package com.nextlabs.smartclassifier.base;

import static com.nextlabs.smartclassifier.constant.Punctuation.GENERAL_DELIMITER;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.constant.DayOfWeek;
import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.entity.ExecutionTimeSlot;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindow;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;
import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.database.entity.RollbackError;
import com.nextlabs.smartclassifier.database.manager.ExcludedMetadataFieldManager;
import com.nextlabs.smartclassifier.database.manager.ExecutionWindowManager;
import com.nextlabs.smartclassifier.database.manager.HeartbeatManager;
import com.nextlabs.smartclassifier.database.manager.MetadataFieldManager;
import com.nextlabs.smartclassifier.database.manager.RollbackErrorManager;
import com.nextlabs.smartclassifier.database.manager.SystemConfigManager;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.util.FileUtil;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;

public abstract class SCComponent implements Component {

  protected final Logger logger = LogManager.getLogger(getClass());

  protected static SessionFactory sessionFactory;

  protected final ComponentType type;
  protected final String SLEEP_INTERVAL_KEY;
  protected final String HEARTBEAT_INTERVAL_KEY;

  protected final String hostname;

  protected Long id = 0L;
  protected boolean shutdown = false;
  protected DateFormat formatter;
  protected Map<String, String> systemConfigs;
  protected Map<String, MetadataField> metadataFieldByName;
  protected List<String> excludedMetadata;
  protected EventLogger eventLogger;

  private long lastHeartbeat = 0L;
  private long lastSystemConfigLoaded = 0L;
  private boolean executionWindowControl = false;
  private boolean reloadingExecutionWindow = false;
  private Map<DayOfWeek, Set<TimeSlot>> executableWindows;

  protected SCComponent(ComponentType type) throws UnknownHostException {
    super();

    try {
      logger.info("SCComponent constructor start.");
      
      this.hostname = InetAddress.getLocalHost().getCanonicalHostName();
      logger.info("Hostname is : "+ hostname);
      this.type = type;
      this.SLEEP_INTERVAL_KEY = type.getCode().toLowerCase() + ".interval.sleep";
      this.HEARTBEAT_INTERVAL_KEY = type.getCode().toLowerCase() + ".interval.heartbeat";

      formatter = new SimpleDateFormat("HHmm");
      eventLogger = new EventLogger(this);
      initHibernate();
      loadSystemConfiguration();
      setupExecutionWindow();

      logger.info("SCComponent constructor end.");
    } catch (UnknownHostException err) {
      logger.error(err.getMessage(), err);
      throw err;
    }
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  @Override
  public String getHostname() {
    return this.hostname;
  }

  @Override
  public boolean isShuttingDown() {
    return shutdown;
  }

  /**
   * Make a copy of system configuration so it become immutable. Only Component object can change
   * the value of systemConfig.
   */
  public Map<String, String> getSystemConfigs() {
    return Collections.unmodifiableMap(systemConfigs);
  }

  public String getSystemConfig(String key) {
    if (systemConfigs != null) {
      return systemConfigs.get(key);
    }

    return null;
  }

  public Map<String, MetadataField> getMetadataFieldByName() {
    return Collections.unmodifiableMap(metadataFieldByName);
  }

  public List<String> getExcludedMetadata() {
    if (excludedMetadata.size() == 0) {
      loadExcludedMetadataFields();
    }
    return new ArrayList<>(excludedMetadata);
  }

  @Override
  public synchronized void beat() {
    Session session = null;
    Transaction transaction = null;

    try {
      long now = System.currentTimeMillis();

      // Only beat when required interval reached
      if ((now - lastHeartbeat)
          >= (Long.parseLong(systemConfigs.get(HEARTBEAT_INTERVAL_KEY)) * 1000)) {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        (new HeartbeatManager(sessionFactory, session)).beat(id, type.getCode());
        lastHeartbeat = now;

        transaction.commit();
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
  }

  @Override
  public boolean isWithinExecutionWindow() {
    // Control flag is on, perform execution window check
    if (executionWindowControl) {
      Calendar calendar = Calendar.getInstance();
      DayOfWeek currentDay =
          DayOfWeek.getDayOfWeek(Integer.toString((calendar.get(Calendar.DAY_OF_WEEK) - 1)));
      Date now = new Date();
      String currentTime = formatter.format(now);

      logger.debug("Current day: " + currentDay.getName() + ", current time: " + currentTime);

      while (reloadingExecutionWindow) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException err) {
          logger.error(err.getMessage(), err);
        }
      }

      if (executableWindows != null && executableWindows.containsKey(currentDay)) {
        for (TimeSlot timeSlot : executableWindows.get(currentDay)) {
          logger.debug("Examining time slot: " + timeSlot.toString());
          if (timeSlot.isSatisfy(now, currentTime)) {
            logger.debug("Currently is executable window.");
            return true;
          }
        }
      }

      // Currently is not in executable day
      logger.debug("Currently is not executable window.");
      return false;
    }

    // Execution time frame disabled, always can execute
    logger.debug("Execution window not set => currently is executable window.");
    return true;
  }

  @Override
  public ComponentType getType() {
    return this.type;
  }

  public String getComponentCode() {
    return this.type.getCode();
  }

  public String getComponentName() {
    return this.type.getName();
  }

  @Override
  public void log(Event event) {
    eventLogger.log(event);
  }
  
  @Override
  public void log(RollbackError rollbackError) {
	Session session = null;
	Transaction transaction = null;
	
    try {
      session = sessionFactory.openSession();
      transaction = session.beginTransaction();

      (new RollbackErrorManager(sessionFactory, session)).logRollbackError(rollbackError);

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
  }
  
  @Override
  public void reQueue(Runnable task) {
	  // Blank implementation. Concrete class should override the implementation of task requeue feature.
	  logger.error("Component needs to implement reQueue(Runnable task) method.");
  }
  
  public int getSleepTime() {
    int sleepTime = Integer.parseInt(systemConfigs.get(SLEEP_INTERVAL_KEY)) * 1000;

    if (sleepTime <= 0) {
      return 5000;
    }

    return sleepTime;
  }

  /**
   * Reload system configuration configuration from database
   *
   * @param session Database operation session
   */
  protected void reloadSystemConfiguration(Session session) throws ManagerException, Exception {
    try {
      long now = System.currentTimeMillis();

      Map<String, String> upToDateSystemConfig =
          (new SystemConfigManager(sessionFactory, session)).loadConfigs();
      // Skip loading from database if loaded time is < reload split time
      if (upToDateSystemConfig != null
          && Boolean.valueOf(
              upToDateSystemConfig.get(SystemConfigKey.SYSTEM_CONFIGURATION_RELOAD_ENABLE))
          && ((now - lastSystemConfigLoaded)
              >= (Long.parseLong(
                      upToDateSystemConfig.get(
                          SystemConfigKey.SYSTEM_CONFIGURATION_RELOAD_SPLIT_TIME))
                  * 1000))) {
        systemConfigs = upToDateSystemConfig;
        lastSystemConfigLoaded = now;
        logger.debug("Total system configuration reloaded: " + systemConfigs.size());
        return;
      }

      logger.debug("System configuration not require to reload.");
      upToDateSystemConfig = null;
    } catch (ManagerException | Exception err) {
      throw err;
    }
  }

  /**
   * @param session Database operation session
   * @throws ManagerException Database manager exceptions
   * @throws Exception All kind of exceptions
   */
  protected void reloadExecutionWindow(Session session) throws ManagerException, Exception {
    try {
      reloadingExecutionWindow = true;
      if (executableWindows != null) {
        synchronized (executableWindows) {
          logger.debug("Reloading execution windows.");
          buildExecutionWindow(
              (new ExecutionWindowManager(sessionFactory, session)).getExecutionWindowSets(id, type.getCode()));
        }
      } else {
        buildExecutionWindow(
            (new ExecutionWindowManager(sessionFactory, session)).getExecutionWindowSets(id, type.getCode()));
      }
      reloadingExecutionWindow = false;
    } catch (ManagerException | Exception err) {
      reloadingExecutionWindow = false;
      throw err;
    }
  }

  /** Load list of Solr metadata fields from database */
  protected void loadMetadataFields() {
    logger.info("Start loading metadata fields.");
    Session session = null;
    Transaction transaction = null;

    try {
      session = sessionFactory.openSession();
      transaction = session.beginTransaction();

      metadataFieldByName = (new MetadataFieldManager(sessionFactory, session)).loadFields();

      transaction.commit();
      logger.info("Metadata fields loaded.");
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
  }

  /** Load Excluded Metadata Fields from the database */
  protected void loadExcludedMetadataFields() {
    logger.info("Start loading excluded metadata fields.");
    Session session = null;
    Transaction transaction = null;

    try {
      session = sessionFactory.openSession();
      transaction = session.beginTransaction();

      excludedMetadata =
          (new ExcludedMetadataFieldManager(sessionFactory, session)).getAllExcludedFieldNames();

      transaction.commit();
      logger.info("Excluded Metadata fields loaded.");
    } catch (Exception err) {
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
  }

  // Load file based configuration and create hibernate session factory
  private void initHibernate() {
    try {
      logger.info("Initializing hibernate configuration.");

      String configFolder = FileUtil.getConfigFolder();

      Path hibernateConfigFilePath =
          Paths.get(configFolder, NextLabsConstant.HIBERNATE_CONFIG_FILE).normalize();

      logger.info("Hibernate configuration file: " + hibernateConfigFilePath);

      // Re-write hibernate.cfg.xml file header if it is missing
      try {
        File hibernateConfigFile = hibernateConfigFilePath.toFile();

        logger.info("Does hibernate configuration file exist ? : " + hibernateConfigFile.exists());

        Document document =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(hibernateConfigFile);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        DOMImplementation domImpl = document.getImplementation();
        DocumentType doctype =
            domImpl.createDocumentType(
                "doctype",
                "-//Hibernate/Hibernate Configuration DTD 3.0//EN",
                "classpath://org/hibernate/hibernate-configuration-3.0.dtd");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

        logger.info("Re-writing hibernate configuration to file.");

        //StreamResult result = new StreamResult(new File(hibernateConfigFilePath));
        StreamResult result = new StreamResult(hibernateConfigFilePath.toFile());
        transformer.transform(new DOMSource(document), result);
      } catch (MalformedURLException malformedErr) {
    	logger.warn(malformedErr.getMessage());
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }

      logger.info("Initializing hibernate session factory...");

      // Initialize session factory
      Configuration hibernateConfiguration = new Configuration();

      hibernateConfiguration.configure(hibernateConfigFilePath.toFile());
      hibernateConfiguration.setProperty(
          "hibernate.connection.password",
          NxlCryptoUtil.decrypt(
              hibernateConfiguration.getProperty("hibernate.connection.password")));

      sessionFactory =
          hibernateConfiguration.buildSessionFactory(
              new StandardServiceRegistryBuilder()
                  .applySettings(hibernateConfiguration.getProperties())
                  .build());

      logger.info("Hibernate Session factory initialized.");
    } catch (Exception err) {
      err.printStackTrace();
      logger.error(err.getMessage(), err);
      throw new ExceptionInInitializerError(err);
    }
  }

  // Load system configuration
  private void loadSystemConfiguration() {
    logger.info("Start loading system configuration.");

    Session session = null;
    Transaction transaction = null;
    long now = System.currentTimeMillis();

    try {
      session = sessionFactory.openSession();
      transaction = session.beginTransaction();

      systemConfigs = (new SystemConfigManager(sessionFactory, session)).loadConfigs();

      transaction.commit();
      logger.debug("Total configuration loaded: " + systemConfigs.size());
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

    lastSystemConfigLoaded = now;
    logger.info("End loading system configuration.");
  }

  /**
   * Retrieve execution window configuration from database for this component. If there is no
   * execution window configure, component can run all the time.
   */
  private void setupExecutionWindow() {
    logger.info("Start loading component execution window.");

    Session session = null;
    Transaction transaction = null;

    try {
      session = sessionFactory.openSession();
      transaction = session.beginTransaction();

      buildExecutionWindow(
          (new ExecutionWindowManager(sessionFactory, session)).getExecutionWindowSets(id, type.getCode()));

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
  }

  /**
   * Construct
   *
   * @param executionWindowSets
   */
  private void buildExecutionWindow(List<ExecutionWindowSet> executionWindowSets) {
    try {
      executionWindowControl = false;

      if (executionWindowSets != null && executionWindowSets.size() > 0) {
        logger.debug("Execution window set count: " + executionWindowSets.size());
        executableWindows = new HashMap<DayOfWeek, Set<TimeSlot>>();
        Date now = new Date();

        // Skip expired set to reduce checking loop in future
        for (ExecutionWindowSet executionWindowSet : executionWindowSets) {
          logger.debug("Found execution window set: " + executionWindowSet.getName());
          if (!isExecutionWindowSetExpired(now, executionWindowSet)) {
            executionWindowControl = true;

            for (ExecutionWindow executionWindow : executionWindowSet.getExecutionWindows()) {
              List<DayOfWeek> days = getDays(executionWindow.getDay());
              for (DayOfWeek day : days) {
                logger.debug("Adding day: " + day.getName() + " with: ");
                if (executableWindows.containsKey(day)) {
                  Set<TimeSlot> timeSlots = executableWindows.get(day);

                  for (ExecutionTimeSlot executionTimeSlot :
                      executionWindow.getExecutionTimeSlots()) {
                    TimeSlot timeSlot =
                        new TimeSlot(
                            executionWindowSet.getEffectiveFrom(),
                            executionWindowSet.getEffectiveUntil(),
                            executionTimeSlot.getStartTime(),
                            executionTimeSlot.getEndTime());

                    timeSlots.add(timeSlot);
                    logger.debug("\n\tSlot: " + timeSlot.toString());
                  }
                } else {
                  Set<TimeSlot> timeSlots = new LinkedHashSet<TimeSlot>();

                  for (ExecutionTimeSlot executionTimeSlot :
                      executionWindow.getExecutionTimeSlots()) {
                    TimeSlot timeSlot =
                        new TimeSlot(
                            executionWindowSet.getEffectiveFrom(),
                            executionWindowSet.getEffectiveUntil(),
                            executionTimeSlot.getStartTime(),
                            executionTimeSlot.getEndTime());

                    timeSlots.add(timeSlot);
                    logger.debug("\n\tSlot: " + timeSlot.toString());
                  }

                  executableWindows.put(day, timeSlots);
                }
              }
            }
          } else {
            logger.debug(executionWindowSet.getName() + " expired.\n");
          }
        }

        logger.info("Component execution window loaded successfully.");
      } else {
        logger.info("Component does not restrict to any execution window.");
      }
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
    }
  }

  private boolean isExecutionWindowSetExpired(
      Date currentDate, ExecutionWindowSet executionWindowSet) {
    if (executionWindowSet != null) {
      if (executionWindowSet.getEffectiveUntil() != null
          && executionWindowSet.getEffectiveUntil().before(currentDate)) {
        return true;
      }

      return false;
    }

    return true;
  }

  private List<DayOfWeek> getDays(String day) {
    List<DayOfWeek> days = new ArrayList<DayOfWeek>();

    if (StringUtils.isNotBlank(day)) {
      String[] dayOfWeekCodes = day.split(Pattern.quote(GENERAL_DELIMITER));

      for (String dayOfWeekCode : dayOfWeekCodes) {
        DayOfWeek dayOfWeek = DayOfWeek.getDayOfWeek(dayOfWeekCode);

        if (dayOfWeek != null) {
          days.add(dayOfWeek);
        }
      }
    }

    return days;
  }

  public abstract boolean reloadConfigs();

  protected abstract boolean loadProfile();

  protected abstract void shutdown();

  protected abstract void shutdownResources();
}

class TimeSlot {

  private Date effectiveFrom;
  private Date effectiveUntil;
  private String startTime;
  private String endTime;

  public TimeSlot(Date effectiveFrom, Date effectiveUntil, String startTime, String endTime) {
    super();
    this.effectiveFrom = effectiveFrom;
    this.effectiveUntil = effectiveUntil;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public boolean isSatisfy(Date dateToCompare, String timeToCompare) {
    // Yet effective date
    if (effectiveFrom != null && effectiveFrom.after(dateToCompare)) {
      return false;
    }

    // Date expired
    if (effectiveUntil != null && effectiveUntil.before(dateToCompare)) {
      return false;
    }

    // Yet start time
    if (startTime != null && startTime.compareTo(timeToCompare) > 0) {
      return false;
    }

    // Passed end time
    if (endTime != null && endTime.compareTo(timeToCompare) < 0) {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder
        .append("\n\t\tDate: ")
        .append(effectiveFrom == null ? "Undefine" : effectiveFrom)
        .append(" ~ ")
        .append(effectiveUntil == null ? "Undefine" : effectiveUntil);
    builder
        .append("\n\t\tTime: ")
        .append(startTime == null ? "0000" : startTime)
        .append(" ~ ")
        .append(endTime == null ? "2359" : endTime)
        .append("\n");

    return builder.toString();
  }
}
