package com.nextlabs.smartclassifier;

import com.nextlabs.smartclassifier.base.SCComponent;
import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.constant.EventCategory;
import com.nextlabs.smartclassifier.constant.EventStage;
import com.nextlabs.smartclassifier.constant.EventStatus;
import com.nextlabs.smartclassifier.constant.MessageFieldName;
import com.nextlabs.smartclassifier.constant.ReportEvent;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.manager.ExtractorManager;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.dto.ExtractionTaskInfo;
import com.nextlabs.smartclassifier.dto.ExtractorProfile;
import com.nextlabs.smartclassifier.dto.TaskParameter;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.jms.QueueMessageConsumer;
import com.nextlabs.smartclassifier.manager.IndexManager;
import com.nextlabs.smartclassifier.task.ExtractDocumentTask;
import com.nextlabs.smartclassifier.util.HTTPClientUtil;
import com.nextlabs.smartclassifier.util.RepositoryUtil;
import com.nextlabs.smartclassifier.util.TimestampUtil;
import com.nextlabs.smartclassifier.util.VFSUtil;

import javax.jms.TextMessage;

import org.apache.commons.configuration.ConfigurationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Extractor
        extends SCComponent {

    private static ExtractorProfile profile;
    private static QueueMessageConsumer messageConsumer;
    private static ThreadPoolExecutor threadPool;
    private static BlockingQueue<Runnable> taskQueue;
    private static TaskParameter taskParameter;
    private static Map<Future<?>, String> taskMap;
    
    private Queue<ExtractionTaskInfo> reprocessingQueue;
    
    /**
     * Constructor.
     *
     * @throws Exception All exceptions will be throw to indicate shutdown of program required.
     */
    public Extractor()
            throws Exception {
        super(ComponentType.EXTRACTOR);

        try {
            logger.info("\n=================================================================================================");
            logger.debug("Content extractor constructor start.");

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
                throw new ConfigurationException("Content extractor unable to load configuration.");
            } else {
                RepositoryUtil.initializeSessionFactory(getSessionFactory());
                RepositoryUtil.reloadRepositories();
                loadMetadataFields();
                loadExcludedMetadataFields();
                initTaskParameter();
                initQueueConsumer();
                initThreadPool();
                reprocessingQueue = new ConcurrentLinkedQueue<>();
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

    /**
     * Attempt to receive message from JMS queue.
     *
     * @return true if there is message fetched successfully from JMS server
     */
    public boolean checkMessage() {
        boolean messageReceived = false;

        try {
            Iterator<?> iterator = taskMap.entrySet().iterator();

            // Perform completed task clean up
            while (iterator.hasNext()) {
                @SuppressWarnings("unchecked")
                Entry<Future<?>, Runnable> task = (Entry<Future<?>, Runnable>) iterator.next();
                Future<?> future = task.getKey();

                if (future.isDone()) {
                    logger.info("Task " + task.getValue() + " completed.");
                    iterator.remove();
                }
            }

            // Only retrieve message from JMS queue if task size is below allowed maximum task size
            if (isWithinExecutionWindow() && (taskMap.size() < Integer.parseInt(systemConfigs.get(SystemConfigKey.EXTRACTOR_MAX_TASK_SIZE)))) {
                TextMessage message = messageConsumer.retrieveMessage(Long.parseLong(systemConfigs.get(SystemConfigKey.JMS_RETRIEVE_MESSAGE_TIMEOUT)) * 1000);
                
                if (message == null) {
                    logger.info("No message received.");
                } else {
                    // get other message properties (pkalra)
                    logger.debug("Message detected");
                    String absoluteFilePath = VFSUtil.getAbsoluteFilePath(message.getStringProperty(MessageFieldName.FILE_NAME));
                    String action = message.getStringProperty(MessageFieldName.ACTION);
                    String messageType = message.getStringProperty(MessageFieldName.FILE_TYPE);
                    String siteURL = message.getStringProperty(MessageFieldName.SITE_URL);
                    String fileID = message.getStringProperty(MessageFieldName.ID);
                    String repoPath = message.getStringProperty(MessageFieldName.REPO_FOLDER_PATH);

                    logger.debug("Message has action = " + action + " and path = " + absoluteFilePath);

                    if (absoluteFilePath != null && action != null) {
                        logger.info("Received the file " + absoluteFilePath + " for " + action);

                        Future<?> future = threadPool.submit(new ExtractDocumentTask(this, taskParameter, fileID, absoluteFilePath, action, messageType, siteURL, repoPath));

                        taskMap.put(future, absoluteFilePath);
                        messageReceived = true;
                    }
                }
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }

        return messageReceived;
    }

    /**
     * Call to shutdown resources gracefully before exit program.
     */
    @Override
    public void shutdown() {
        logger.info("Shutting down extractor.");

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

        logger.info("Content extractor shut down successfully.");
        logger.info("\n=================================================================================================\n");
    }

    /**
     * Reload extractor configuration.
     */
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

                ExtractorProfile upToDateProfile = null;
                ExtractorManager extractorManager = new ExtractorManager(sessionFactory, session);
                com.nextlabs.smartclassifier.database.entity.Extractor upToDateExtractor = extractorManager.getExtractorByHostname(hostname);

                if (upToDateExtractor != null) {
                    upToDateProfile = new ExtractorProfile();
                    upToDateProfile.copy(upToDateExtractor);

                    extractorManager.updateConfigLoadedDate(hostname, now);
                }
                transaction.commit();

                if (upToDateProfile != null) {
                    reloadMessageConsumerConfiguration(upToDateProfile);
                    reloadTaskParameter(upToDateProfile);

                    profile.setConfigLoadedOn(now);
                    profile.setConfigReloadInterval(upToDateProfile.getConfigReloadInterval() / 1000);

                    configurationReloaded = true;
                    logger.info("---- Configuration Reload Completed ----\n");
                } else {
                    logger.warn("Unable to resolve Extractor configuration for " + hostname + ".");
                }
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
     * Re-queue document extraction task for re-processing. 
     * @param extractDocumentTask Task for re-processing
     */
    public void reQueue(Runnable extractDocumentTask) {
    	try {
    		ExtractDocumentTask task = (ExtractDocumentTask) extractDocumentTask;
    		ExtractionTaskInfo info = new ExtractionTaskInfo();
    		
    		info.setDocumentID(task.getDocumentID());
    		info.setAbsolutePath(task.getAbsolutePath());
    		info.setAction(task.getAction());
    		info.setRepositoryType(task.getRepositoryType());
    		info.setSiteURL(task.getSiteURL());
    		info.setRepoPath(task.getRepoPath());
    		info.setRetryAttempt(task.getExtractionAttemptCount() + 1);
    		
    		this.reprocessingQueue.add(info);
    	} catch(Exception err) {
    		logger.error(err.getMessage(), err);
    	}
    }
    
    /**
     * Re-submit document extraction task to threadPool for retry.
     */
    public void processReQueue() {
    	try {
    		while(!reprocessingQueue.isEmpty()) {
    			ExtractionTaskInfo info = reprocessingQueue.poll();
    			ExtractDocumentTask task = new ExtractDocumentTask(this, taskParameter, 
    					info.getDocumentID(), info.getAbsolutePath(), 
    					info.getAction(), info.getRepositoryType(), 
    					info.getSiteURL(), info.getRepoPath());
    			task.setExtractionAttemptCount(info.getRetryAttempt());
    			
    			Future<?> future = threadPool.submit(task);
    			taskMap.put(future, info.getAbsolutePath());
    		}
    	} catch(Exception err) {
    		logger.error(err.getMessage(), err);
    	}
    }
    
    /**
     * Load Extractor's profile from database based on machine hostname.
     */
    @Override
    protected boolean loadProfile() {
        Session session = null;
        Transaction transaction = null;
        boolean profileLoaded = false;

        try {
            logger.info("Loading extractor profile [" + hostname + "] from database.");
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            ExtractorManager extractorManager = new ExtractorManager(sessionFactory, session);
            extractorManager.initDocSizeLimits(hostname);
            com.nextlabs.smartclassifier.database.entity.Extractor extractor = extractorManager.getExtractorByHostname(hostname);

            if (extractor != null) {
                id = extractor.getId();
                Date now = new Date();

                profile = new ExtractorProfile();
                profile.copy(extractor);
                profile.setConfigLoadedOn(now);

                extractorManager.updateConfigLoadedDate(hostname, now);
                profileLoaded = true;
                logger.info("Content extractor profile [" + hostname + "] loaded successfully.");
            } else {
                logger.error("Content extractor profile [" + hostname + "] not found.");
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
     * Try to shutdown resources gracefully
     */
    @Override
    protected void shutdownResources() {
        // Indicate program is shutting down
        shutdown = true;

        logger.info("Shutting down index manager.");
        if (taskParameter != null && taskParameter.getIndexManager() != null) {
            taskParameter.getIndexManager().shutdown();
        }

        logger.info("Shutting down thread pool.");
        if (threadPool != null) {
            threadPool.shutdownNow();
        }

        logger.info("Canceling task.");
        if (taskMap != null) {
            for (Future<?> future : taskMap.keySet()) {
                future.cancel(true);
            }
        }

        logger.info("Terminating thread pool.");
        if (threadPool != null) {
            try {
                while (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    logger.info("Waiting treadpool to shutdown gracefully...");
                }
            } catch (InterruptedException err) {
                // Ignore
            }
        }

        logger.info("Closing JMS connection.");
        if (messageConsumer != null) {
            messageConsumer.shutdown();
            messageConsumer.close();
        }

        // closing httpclient instances
        HTTPClientUtil.closeConnections();
    }

    /**
     * Check for JMS information change and replacement
     *
     * @param upToDateProfile Most recent configuration
     * @throws Exception All kind of exceptions
     */
    protected void reloadMessageConsumerConfiguration(ExtractorProfile upToDateProfile)
            throws Exception {
        if (upToDateProfile != null) {
            if (!messageConsumer.equals(new QueueMessageConsumer(upToDateProfile.getJmsConfig()))) {
                logger.info("JMS configuration changed, updating QueueMessageSender.");
                messageConsumer.updateJMSConfig(upToDateProfile.getJmsConfig());
            }
        }
    }

    /**
     * Reload updated task parameter.
     *
     * @param upToDateProfile Most recent configuration
     * @throws Exception All kind of exceptions
     */
    protected void reloadTaskParameter(ExtractorProfile upToDateProfile)
            throws Exception {
        try {
            if (upToDateProfile != null) {
                profile.setMinHeapMemory(upToDateProfile.getMinHeapMemory());
                profile.getDocumentExtractorByExtn().clear();
                profile.getDocumentExtractorByExtn().putAll(upToDateProfile.getDocumentExtractorByExtn());

                setTaskParameter();
            }
        } catch (Exception err) {
            logger.error("Failed to initailize task parameter.");
        }
    }

    /**
     * Initialize task parameter which needed for extraction operation.
     *
     * @throws Exception Any encounter exception should treated as fatal error and program shutdown is required.
     */
    private void initTaskParameter()
            throws Exception {
        try {
            logger.info("Initializing task parameter.");
            setTaskParameter();
            logger.info("Task parameter initialized.");
        } catch (Exception err) {
            logger.error("Failed to initialize task parameter.");
            throw err;
        }
    }

	private void setTaskParameter() throws Exception {
		if (taskParameter == null) {
			taskParameter = new TaskParameter();
		}

		TimestampUtil.setTimeFormats(systemConfigs.get(SystemConfigKey.SYSTEM_FORMAT_TIME_FORMAT));

		taskParameter.setMinimumHeapMemorySize(profile.getMinHeapMemory());
		taskParameter.setDocumentExtractorByExtn(profile.getDocumentExtractorByExtn());
		taskParameter.setIndexManager(new IndexManager(systemConfigs.get(SystemConfigKey.INDEXER_URL),
				Long.parseLong(systemConfigs.get(SystemConfigKey.INDEXER_INTERVAL_RETRY)) * 1000,
				getMetadataFieldByName(), systemConfigs.get(SystemConfigKey.INDEXER_USERNAME),
				systemConfigs.get(SystemConfigKey.INDEXER_PASSWORD)));

		Map<String, String> rmsProperties = new HashMap<>();
		rmsProperties.put(SystemConfigKey.SKYDRM_APP_ID, systemConfigs.get(SystemConfigKey.SKYDRM_APP_ID));
		rmsProperties.put(SystemConfigKey.SKYDRM_APP_KEY, systemConfigs.get(SystemConfigKey.SKYDRM_APP_KEY));
		rmsProperties.put(SystemConfigKey.SKYDRM_SYSTEM_BUCKET, systemConfigs.get(SystemConfigKey.SKYDRM_SYSTEM_BUCKET));
		rmsProperties.put(SystemConfigKey.SKYDRM_ROUTER_URL, systemConfigs.get(SystemConfigKey.SKYDRM_ROUTER_URL));
		taskParameter.setRmsProperties(rmsProperties);

		taskParameter.setExcludedMetadata(getExcludedMetadata());
	}

    /**
     * Initialize JMS queue consumer to receive message for document extraction task.
     *
     * @throws Exception Any encounter exception should treated as fatal error and program shutdown is required.
     */
    private void initQueueConsumer()
            throws Exception {
        try {
            logger.info("Initializing message consumer.");
            messageConsumer = new QueueMessageConsumer(profile.getJmsConfig());
            messageConsumer.connectToJMS();
            logger.info("Message consumer initialized.");
        } catch (Exception err) {
            logger.error("Failed to initialize queue consumer.");
            throw err;
        }
    }

    /**
     * Initialize thread pool to execute document extraction task.
     *
     * @throws Exception Any encounter exception should treated as fatal error and program shutdown is required.
     */
    private void initThreadPool()
            throws Exception {
        try {
            int workerThreadCount = profile.getDocumentExtractorCount();
            logger.info("Initializing " + workerThreadCount + " document extractor threads.");

            taskMap = new HashMap<>();
            taskQueue = new LinkedBlockingQueue<>();
            threadPool = new ThreadPoolExecutor(workerThreadCount, workerThreadCount,
                    1, TimeUnit.MINUTES, taskQueue);

            logger.info("Thread pool initialized.\n");
        } catch (Exception err) {
            logger.error("Failed to initialize thread pool.");
            throw err;
        }
    }
}
