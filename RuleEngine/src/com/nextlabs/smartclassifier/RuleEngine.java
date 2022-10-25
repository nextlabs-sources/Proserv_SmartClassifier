package com.nextlabs.smartclassifier;

import com.nextlabs.smartclassifier.base.SCComponent;
import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.constant.EventCategory;
import com.nextlabs.smartclassifier.constant.EventStage;
import com.nextlabs.smartclassifier.constant.EventStatus;
import com.nextlabs.smartclassifier.constant.ReportEvent;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.manager.RuleEngineManager;
import com.nextlabs.smartclassifier.database.manager.RuleExecutionManager;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.dto.RuleEngineProfile;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.mail.MailService;
import com.nextlabs.smartclassifier.mail.MailServiceConfig;
import com.nextlabs.smartclassifier.task.LicenseCheckTask;
import com.nextlabs.smartclassifier.task.OnDemandTask;
import com.nextlabs.smartclassifier.task.ScheduledTask;
import com.nextlabs.smartclassifier.util.HTTPClientUtil;
import com.nextlabs.smartclassifier.util.RepositoryUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RuleEngine
        extends SCComponent {

    private static RuleEngineProfile profile;
    private static ScheduledExecutorService onDemandRuleScheduledExecutorService;
    private static ScheduledExecutorService scheduledRuleScheduledExecutorService;
    private static ScheduledExecutorService licenceCheckScheduledExecutorService;

    private static Map<Long, Future<?>> onDemandRuleExecutionIdToFutureMap;
    private static Map<Long, Future<?>> scheduledRuleExecutionIdToFutureMap;
    private static ThreadPoolExecutor onDemandThreadPoolExecutor;
    private static ThreadPoolExecutor scheduledThreadPoolExecutor;

    private static boolean running = false;

    /**
     * Constructor.
     *
     * @throws Exception All exceptions will be throw to indicate shutdown of program required.
     */
    public RuleEngine()
            throws Exception {
        super(ComponentType.RULE_ENGINE);

        try {
            logger.info("\n=================================================================================================");
            logger.debug("Rule engine constructor start.");

            if (!loadProfile()) {
                Event noProfileEvent = new Event();
                noProfileEvent.setStage(EventStage.COMPONENT_STARTUP);
                noProfileEvent.setCategory(EventCategory.MAINTENANCE);
                noProfileEvent.setStatus(EventStatus.FAIL);
                noProfileEvent.setMessageCode(ReportEvent.COMPONENT_START_FAIL.getMessageCode());
                noProfileEvent.addMessageParam("Unable to load configuration.");
                noProfileEvent.setTimestamp(System.currentTimeMillis());

                log(noProfileEvent);
                shutdown();
                throw new ConfigurationException("Rule engine unable to load configuration.");
            } else {
                RepositoryUtil.initializeSessionFactory(getSessionFactory());
                RepositoryUtil.reloadRepositories();
                loadMetadataFields();
                initMailService();
                initLicenceCheckExecutor();
                recoverPreviousRuleExecutions();
                initRuleExecutor();
            }

            beat();

            Event startupEvent = new Event();
            startupEvent.setStage(EventStage.COMPONENT_STARTUP);
            startupEvent.setCategory(EventCategory.MAINTENANCE);
            startupEvent.setStatus(EventStatus.SUCCESS);
            startupEvent.setMessageCode(ReportEvent.COMPONENT_START_SUCCESS.getMessageCode());
            startupEvent.setTimestamp(System.currentTimeMillis());

            log(startupEvent);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw err;
        }
    }

    public void removeCompletedTask() {
        try {
            Iterator<?> onDemandTaskIterator = onDemandRuleExecutionIdToFutureMap.entrySet().iterator();
            while (onDemandTaskIterator.hasNext()) {
                @SuppressWarnings("unchecked")
                Entry<Integer, Future<?>> task = (Entry<Integer, Future<?>>) onDemandTaskIterator.next();
                Future<?> future = task.getValue();

                if (future.isDone() || future.isCancelled()) {
                    onDemandTaskIterator.remove();
                }
            }

            Iterator<?> scheduledTaskIterator = scheduledRuleExecutionIdToFutureMap.entrySet().iterator();
            while (scheduledTaskIterator.hasNext()) {
                @SuppressWarnings("unchecked")
                Entry<Integer, Future<?>> task = (Entry<Integer, Future<?>>) scheduledTaskIterator.next();
                Future<?> future = task.getValue();

                if (future.isDone() || future.isCancelled()) {
                    scheduledTaskIterator.remove();
                }
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }
    }

    /**
     * This method should only be call by license check task.
     *
     * @return Running flag set by license check task.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * This method should only be call by license check task.
     *
     * @param runningValue Value to store.
     */
    public void setRunning(boolean runningValue) {
        running = runningValue;
    }

    /**
     * Call to shutdown resources gracefully before exit program.
     */
    @Override
    public void shutdown() {
        logger.info("Shutting down rule engine.");

        Event shutdownEvent = new Event();
        shutdownEvent.setStage(EventStage.COMPONENT_SHUTDOWN);
        shutdownEvent.setCategory(EventCategory.MAINTENANCE);
        shutdownEvent.setStatus(EventStatus.SUCCESS);
        shutdownEvent.setMessageCode(ReportEvent.COMPONENT_STOP_SUCCESS.getMessageCode());
        shutdownEvent.setTimestamp(System.currentTimeMillis());

        log(shutdownEvent);
        shutdownResources();

        // Give time for eventLogger to flush messages into database before program exit
        int counter = 0;
        while (eventLogger.isAlive() && counter < 5) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException err) {
                // Ignore
            }

            counter++;
        }

        logger.info("Rule engine shut down successfully.");
        logger.info("\n=================================================================================================\n");
    }

    /**
     * Reload rule engine configuration.
     */
    @Override
    public boolean reloadConfigs() {
        Session session = null;
        Transaction transaction = null;
        boolean configurationReloaded = false;
        Date now = new Date();

        try {
            if ((now.getTime() - profile.getConfigLoadedOn().getTime()) >= profile.getConfigReloadInterval()) {
                session = sessionFactory.openSession();
                transaction = session.beginTransaction();

                reloadSystemConfiguration(session);
                reloadExecutionWindow(session);
                RepositoryUtil.reloadRepositories();

                RuleEngineProfile upToDateProfile = null;
                RuleEngineManager ruleEngineManager = new RuleEngineManager(sessionFactory, session);
                com.nextlabs.smartclassifier.database.entity.RuleEngine upToDateRuleEngine = ruleEngineManager.getRuleEngineByHostname(hostname);

                if (upToDateRuleEngine != null) {
                    upToDateProfile = new RuleEngineProfile();
                    upToDateProfile.copy(upToDateRuleEngine);
                    ruleEngineManager.updateConfigLoadedDate(hostname, now);
                }
                transaction.commit();

                profile.setConfigLoadedOn(now);
                profile.setConfigReloadInterval(upToDateProfile.getConfigReloadInterval() / 1000);

                reloadRuleExecutor(upToDateProfile);
                reloadMailService();

                configurationReloaded = true;
                logger.info("---- Configuration Reload Completed ----\n");
            }
        } catch (ManagerException | Exception err) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
            logger.error(err.getMessage(), err);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore
                }
            }
        }

        return configurationReloaded;
    }

    /**
     * Name of this component.
     *
     * @return Name of this component from its profile
     */
    @Override
    public String getName() {
        if (profile != null) {
            return profile.getName();
        }

        return "Unknown";
    }

    /**
     * Load rule engine configuration based on hostname.
     */
    @Override
    protected boolean loadProfile() {
        Session session = null;
        Transaction transaction = null;
        boolean profileLoaded = false;

        try {
            logger.info("Loading rule engine profile [" + hostname + "] from database.");
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            RuleEngineManager ruleEngineManager = new RuleEngineManager(sessionFactory, session);
            com.nextlabs.smartclassifier.database.entity.RuleEngine ruleEngine = ruleEngineManager.getRuleEngineByHostname(hostname);

            if (ruleEngine != null) {
                id = ruleEngine.getId();
                Date now = new Date();

                profile = new RuleEngineProfile();
                profile.copy(ruleEngine);
                profile.setConfigLoadedOn(now);

                ruleEngineManager.updateConfigLoadedDate(hostname, now);
                profileLoaded = true;
                logger.info("Rule engine profile [" + hostname + "] loaded successfully.");
            } else {
                logger.error("Rule engine profile [" + hostname + "] not found.");
            }

            transaction.commit();
        } catch (ManagerException | Exception err) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
            logger.error(err.getMessage(), err);
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore
                }
            }
        }

        return profileLoaded;
    }

    /**
     * Try to shut down resources gracefully
     */
    @Override
    protected void shutdownResources() {
        // Indicate program is shutting down
        shutdown = true;

        logger.info("Shutting down license check executor.");
        if (licenceCheckScheduledExecutorService != null) {
            licenceCheckScheduledExecutorService.shutdownNow();
        }

        logger.info("Shutting down on demand thread pool.");
        if (onDemandThreadPoolExecutor != null) {
            onDemandThreadPoolExecutor.shutdownNow();
        }

        logger.info("Shutting down on demand rule executor.");
        if (onDemandRuleScheduledExecutorService != null) {
            onDemandRuleScheduledExecutorService.shutdownNow();
        }

        logger.info("Canceling on demand tasks.");
        if (onDemandRuleExecutionIdToFutureMap != null) {
            for (Future<?> future : onDemandRuleExecutionIdToFutureMap.values()) {
                future.cancel(true);
            }
        }

        logger.info("Shutting down scheduled thread pool.");
        if (scheduledThreadPoolExecutor != null) {
            scheduledThreadPoolExecutor.shutdownNow();
        }

        logger.info("Shutting down scheduled rule executor.");
        if (scheduledRuleScheduledExecutorService != null) {
            scheduledRuleScheduledExecutorService.shutdownNow();
        }

        logger.info("Canceling scheduled tasks.");
        if (scheduledRuleExecutionIdToFutureMap != null) {
            for (Future<?> future : scheduledRuleExecutionIdToFutureMap.values()) {
                future.cancel(true);
            }
        }

        logger.info("Terminating license check executor.");
        if (licenceCheckScheduledExecutorService != null) {
            try {
                while (!licenceCheckScheduledExecutorService.awaitTermination(15, TimeUnit.SECONDS)) {
                    logger.info("Waiting licence check executor to shutdown gracefully...");
                }
            } catch (InterruptedException err) {
                // Ignore
            }
        }

        logger.info("Terminating on demand rule executor.");
        if (onDemandRuleScheduledExecutorService != null) {
            try {
                while (!onDemandRuleScheduledExecutorService.awaitTermination(15, TimeUnit.SECONDS)) {
                    logger.info("Waiting on-demand rule executor to shutdown gracefully...");
                }
            } catch (InterruptedException err) {
                // Ignore
            }
        }

        logger.info("Terminating on demand thread pool.");
        if (onDemandThreadPoolExecutor != null) {
            try {
                while (!onDemandThreadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.info("Waiting on-demand thread pool to shutdown gracefully...");
                }
            } catch (InterruptedException err) {
                // Ignore
            }
        }

        logger.info("Terminating scheduled rule executor.");
        if (scheduledRuleScheduledExecutorService != null) {
            try {
                while (!scheduledRuleScheduledExecutorService.awaitTermination(15, TimeUnit.SECONDS)) {
                    logger.info("Waiting scheduled rule executor to shutdown gracefully...");
                }
            } catch (InterruptedException err) {
                // Ignore
            }
        }

        logger.info("Terminating scheduled thread pool.");
        if (scheduledThreadPoolExecutor != null) {
            try {
                while (!scheduledThreadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.info("Waiting scheduled thread pool to shutdown gracefully...");
                }
            } catch (InterruptedException err) {
                // Ignore
            }
        }

        // closing httpclient instances
        HTTPClientUtil.closeConnections();
    }

    /**
     * Initialize licence check executor thread and schedule it to execute with 1 day interval.
     *
     * @throws Exception Licence check executor initialization exception.
     */
    private void initLicenceCheckExecutor()
            throws Exception {
        try {
            logger.info("Initializing licence check executor.");

            licenceCheckScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            licenceCheckScheduledExecutorService.scheduleWithFixedDelay(new LicenseCheckTask(this), 0, 1, TimeUnit.DAYS);

            logger.info("Licence check executor initialized.");
        } catch (Exception err) {
            logger.error("Failed to initialize licence check executor.");
            throw err;
        }
    }

    /**
     * Rule execution might get interrupted. Recover rule execution for this host.
     *
     * @throws Exception Unpredictable exception during recovery.
     */
    private void recoverPreviousRuleExecutions()
            throws Exception {
        Session session = null;
        Transaction transaction = null;

        try {
            logger.info("Start recovery of previous rule executions.");
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            (new RuleExecutionManager(sessionFactory, session)).recoverPreviousRuleExecutions(hostname);

            transaction.commit();
            logger.info("Completely recovered previous rule executions.");
        } catch (ManagerException err) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
            logger.error(err.getMessage(), err);
            logger.error("Failed to recover previous rule executions.");
        } catch (Exception err) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
            logger.error(err.getMessage(), err);
            logger.error("Failed to recover previous rule executions.");

            throw err;
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Initialize rule executors and schedule them to execute with configured interval.
     *
     * @throws Exception Rule executor initialization exception.
     */
    private void initRuleExecutor()
            throws Exception {
        try {
            logger.info("Initializing rule executor.");

            if (profile.getOnDemandInterval() > 0 && profile.getOnDemandPoolSize() > 0) {
                logger.info("Initializing on-demand task executor pool.");
                onDemandRuleExecutionIdToFutureMap = new ConcurrentHashMap<>();
                onDemandThreadPoolExecutor = new ThreadPoolExecutor(profile.getOnDemandPoolSize(), profile.getOnDemandPoolSize(), 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
                onDemandRuleScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                onDemandRuleScheduledExecutorService.scheduleWithFixedDelay(new OnDemandTask(this, onDemandThreadPoolExecutor, onDemandRuleExecutionIdToFutureMap), 5, profile.getOnDemandInterval(), TimeUnit.SECONDS);
            } else {
                logger.warn("On demand task execution configuration is incorrect. On demand task will not be execute.");
            }

            if (profile.getScheduledInterval() > 0 && profile.getScheduledPoolSize() > 0) {
                logger.info("Initializing scheduled task executor pool.");
                scheduledRuleExecutionIdToFutureMap = new ConcurrentHashMap<Long, Future<?>>();
                scheduledThreadPoolExecutor = new ThreadPoolExecutor(profile.getScheduledPoolSize(), profile.getScheduledPoolSize(), 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
                scheduledRuleScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                scheduledRuleScheduledExecutorService.scheduleWithFixedDelay(new ScheduledTask(this, scheduledThreadPoolExecutor, scheduledRuleExecutionIdToFutureMap), 5, profile.getScheduledInterval(), TimeUnit.SECONDS);
            } else {
                logger.warn("Scheduled task execution configuration is incorrect. Scheduled task will not be execute.");
            }

            logger.info("Rule executor initialized.");
        } catch (Exception err) {
            logger.error("Failed to initialize rule executor.");
            throw err;
        }
    }

    /**
     * Initialize mail service for future use.
     *
     * @throws Exception Mail service initialization exception.
     */
    private void initMailService()
            throws Exception {
        try {
            logger.info("Initializing mail service.");

            MailService.init(constructMailServiceConfig());

            logger.info("Mail service initialized.");
        } catch (Exception err) {
            logger.error("Failed to initialize mail service.");
            throw err;
        }
    }

    /**
     * Update rule executor object if rule executor configuration has changed.
     *
     * @param upToDateProfile Most current configuration of this rule engine.
     * @throws Exception ThreadPool exceptions
     */
    private void reloadRuleExecutor(RuleEngineProfile upToDateProfile)
            throws Exception {
        try {
            if (profile.getOnDemandPoolSize() != upToDateProfile.getOnDemandPoolSize()) {
                profile.setOnDemandPoolSize(upToDateProfile.getOnDemandPoolSize());

                if (onDemandThreadPoolExecutor != null
                        && profile.getOnDemandPoolSize() > 0) {
                    onDemandThreadPoolExecutor.setCorePoolSize(profile.getOnDemandPoolSize());
                    onDemandThreadPoolExecutor.setMaximumPoolSize(profile.getOnDemandPoolSize());
                    logger.debug("On-demand thread pool updated.");
                }
            }

            if (profile.getScheduledPoolSize() != upToDateProfile.getScheduledPoolSize()) {
                profile.setScheduledPoolSize(upToDateProfile.getScheduledPoolSize());

                if (scheduledThreadPoolExecutor != null
                        && profile.getScheduledPoolSize() > 0) {
                    scheduledThreadPoolExecutor.setCorePoolSize(profile.getScheduledPoolSize());
                    scheduledThreadPoolExecutor.setMaximumPoolSize(profile.getScheduledPoolSize());
                    logger.debug("Scheduled thread pool updated.");
                }
            }

            if (profile.getOnDemandInterval() != upToDateProfile.getOnDemandInterval()) {
                profile.setOnDemandInterval(upToDateProfile.getOnDemandInterval());

                if (profile.getOnDemandInterval() > 0
                        && onDemandThreadPoolExecutor != null) {
                    if (onDemandRuleExecutionIdToFutureMap == null) {
                        onDemandRuleExecutionIdToFutureMap = new ConcurrentHashMap<Long, Future<?>>();
                    }

                    if (onDemandRuleScheduledExecutorService != null) {
                        onDemandRuleScheduledExecutorService.shutdownNow();

                        try {
                            while (!onDemandRuleScheduledExecutorService.awaitTermination(15, TimeUnit.SECONDS)) {
                                logger.info("Waiting on-demand rule executor to shutdown gracefully...");
                            }
                        } catch (InterruptedException err) {
                            // Ignore
                        }
                    }

                    onDemandRuleScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                    onDemandRuleScheduledExecutorService.scheduleWithFixedDelay(new OnDemandTask(this, onDemandThreadPoolExecutor, onDemandRuleExecutionIdToFutureMap), 5, profile.getOnDemandInterval(), TimeUnit.SECONDS);
                    logger.debug("On-demand rule executor reloaded.");
                }
            }

            if (profile.getScheduledInterval() != upToDateProfile.getScheduledInterval()) {
                profile.setScheduledInterval(upToDateProfile.getScheduledInterval());

                if (profile.getScheduledInterval() > 0
                        && scheduledThreadPoolExecutor != null) {
                    if (scheduledRuleExecutionIdToFutureMap == null) {
                        scheduledRuleExecutionIdToFutureMap = new ConcurrentHashMap<Long, Future<?>>();
                    }

                    if (scheduledRuleScheduledExecutorService != null) {
                        scheduledRuleScheduledExecutorService.shutdownNow();

                        try {
                            while (!scheduledRuleScheduledExecutorService.awaitTermination(15, TimeUnit.SECONDS)) {
                                logger.info("Waiting scheduled rule executor to shutdown gracefully...");
                            }
                        } catch (InterruptedException err) {
                            // Ignore
                        }
                    }

                    scheduledRuleScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                    scheduledRuleScheduledExecutorService.scheduleWithFixedDelay(new ScheduledTask(this, scheduledThreadPoolExecutor, scheduledRuleExecutionIdToFutureMap), 5, profile.getScheduledInterval(), TimeUnit.SECONDS);
                    logger.debug("Scheduled rule executor reloaded.");
                }
            }
        } catch (Exception err) {
            logger.error("Failed to reload rule executor.");
            throw err;
        }
    }

    /**
     * Update mail service if the configuration has changed.
     *
     * @throws Exception Mail service initialization exception.
     */
    private void reloadMailService()
            throws Exception {
        try {
            MailServiceConfig mailServiceConfig = constructMailServiceConfig();

            // Only replace if the configuration are different
            if (!MailService.getMailServiceConfig().equals(mailServiceConfig)) {
                MailService.init(mailServiceConfig);
                logger.debug("Mail service configuration reloaded.");
            }
        } catch (Exception err) {
            logger.error("Failed to reload mail service.");
            throw err;
        }
    }

    /**
     * Construct email service configuration from system configuration.
     *
     * @return Mail service configuration.
     * @throws Exception Address exception
     */
    private MailServiceConfig constructMailServiceConfig()
            throws Exception {
        MailServiceConfig mailServiceConfig = new MailServiceConfig();

        mailServiceConfig.setDebug(systemConfigs.get(SystemConfigKey.EMAIL_DEBUG));
        mailServiceConfig.setHost(systemConfigs.get(SystemConfigKey.EMAIL_SERVER_URL));
        mailServiceConfig.setPort(systemConfigs.get(SystemConfigKey.EMAIL_SERVER_PORT_NUMBER));
        mailServiceConfig.setAuthentication(systemConfigs.get(SystemConfigKey.EMAIL_AUTH_REQUIRED));
        mailServiceConfig.setStartTLS(systemConfigs.get(SystemConfigKey.EMAIL_STARTTLS_ENABLED));
        mailServiceConfig.setCheckServerIdentity(systemConfigs.get(SystemConfigKey.EMAIL_SERVER_IDENTITY_CHECK));
        mailServiceConfig.setSender(systemConfigs.get(SystemConfigKey.EMAIL_DEFAULT_SENDER));
        mailServiceConfig.setUsername(systemConfigs.get(SystemConfigKey.EMAIL_AUTH_USERNAME));
        mailServiceConfig.setPassword(systemConfigs.get(SystemConfigKey.EMAIL_AUTH_PASSWORD));

        return mailServiceConfig;
    }
}
