package com.nextlabs.smartclassifier;

import static com.nextlabs.smartclassifier.constant.Punctuation.FORWARD_SLASH;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.base.SCComponent;
import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.constant.EventCategory;
import com.nextlabs.smartclassifier.constant.EventStage;
import com.nextlabs.smartclassifier.constant.EventStatus;
import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import com.nextlabs.smartclassifier.constant.Punctuation;
import com.nextlabs.smartclassifier.constant.ReportEvent;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.constant.SCConstant;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.manager.WatcherManager;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.dto.WatcherProfile;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.jms.QueueMessageSender;
import com.nextlabs.smartclassifier.solr.QueryEngine;
import com.nextlabs.smartclassifier.util.DateUtil;
import com.nextlabs.smartclassifier.util.HTTPClientUtil;
import com.nextlabs.smartclassifier.util.RepositoryUtil;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import com.nextlabs.smartclassifier.util.VFSUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public final class Watcher extends SCComponent {

    private static final String URL_SCHEME = "file";
    private static final String INCLUDE_FOLDER_START_TAG = "<IncludeFolder>";
    private static final String INCLUDE_FOLDER_END_TAG = "</IncludeFolder>";
    private static final String EXCLUDE_FOLDER_START_TAG = "<ExcludeFolder>";
    private static final String EXCLUDE_FOLDER_END_TAG = "</ExcludeFolder>";
    private static final String ROOT_FOLDER_START_TAG = "<RootFolder>";
    private static final String ROOT_FOLDER_END_TAG = "</RootFolder>";
    private static final String FILE_MONITOR_START_TAG = "<FileMonitor>";
    private static final String FILE_MONITOR_END_TAG = "</FileMonitor>";
    private static final String FILE_OBJECT_START_TAG = "<FileObject>";
    private static final String FILE_OBJECT_END_TAG = "</FileObject>";
    
    private static final String DAVWWWROOT = "davwwwroot";

    private static WatcherProfile profile;
    private static QueueMessageSender messageSender;
    private static QueryEngine indexQueryEngine;
    private static DefaultFileSystemManager fileSystemManager;
    private static MyFileMonitor[] fileMonitors;
    private static Set<String> includePaths;
    
    // This flag control if recovery dump creation is needed
    // By default, always createRecoveryDump upon shutdown
    private static boolean createRecoveryDump = true;

    /**
     * This is used to record which file monitor is monitoring the main included
     * folder entry.
     */
    private static Map<String, MyFileMonitor> fileMonitorByFolderName;

    private static Integer registeredFolderCount = 0;
    private static volatile Long encounteredFileCount = 0L;
    private static volatile Long registeredFileCount = 0L;

    private static long splitTime;

    /**
     * Constructor.
     *
     * @throws Exception All exceptions will be throw to indicate shutdown of program
     *                   required.
     */
    public Watcher(boolean startFileMonitor) throws Exception {
        super(ComponentType.WATCHER);

        logger.info("\n=================================================================================================");
        logger.debug("File watcher constructor start.");

        if (!loadProfile()) {
        	// Do not create dump file, this may cause data lost
        	createRecoveryDump = false;
        	
            Event profileNotFoundEvent = new Event();
            profileNotFoundEvent.setStage(EventStage.COMPONENT_STARTUP);
            profileNotFoundEvent.setCategory(EventCategory.MAINTENANCE);
            profileNotFoundEvent.setStatus(EventStatus.FAIL);
            profileNotFoundEvent.setMessageCode(ReportEvent.COMPONENT_START_FAIL.getMessageCode());
            profileNotFoundEvent.addMessageParam("Unable to load configuration.");
            profileNotFoundEvent.setTimestamp(System.currentTimeMillis());

            log(profileNotFoundEvent);
            shutdown();
            throw new ConfigurationException("Watcher unable to load configuration.");
        } else {
            splitTime = Long.parseLong(getSystemConfig(SystemConfigKey.WATCHER_SPLIT_LAST_MODIFIED_DATE));
            initMessageSender();
            initIndexQueryEngine();
            initFileSystemManager();
            initFileMonitor(startFileMonitor);
            RepositoryUtil.initializeSessionFactory(getSessionFactory());
            RepositoryUtil.reloadRepositories();
        }

        beat();

        Event startupEvent = new Event();
        startupEvent.setStage(EventStage.COMPONENT_STARTUP);
        startupEvent.setCategory(EventCategory.MAINTENANCE);
        startupEvent.setStatus(EventStatus.SUCCESS);
        startupEvent.setMessageCode(ReportEvent.COMPONENT_START_SUCCESS.getMessageCode());
        startupEvent.setTimestamp(System.currentTimeMillis());

        log(startupEvent);
        
        includePaths = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Recover to last execution stage via data dump file upon last successful
     * shutdown.
     *
     * @throws IllegalStateException if data dump file doesn't match, or watcher's configuration
     *                               has changed
     * @throws IOException           if failed to operate on data dump file
     * @throws Exception             all exceptions will be throw to indicate shutdown of program
     *                               required.
     */
    public void recover() throws Exception {
        CSVReader csvReader = null;

        try {
        	// When performing recovery, do not create dump in case program is shut down during this phase
        	createRecoveryDump = false;
            Date startTime = new Date();
            logger.info("Recovering File Watcher from data dump. Time: " + startTime + "\n");
            csvReader = new CSVReader(new InputStreamReader(new FileInputStream(NextLabsConstant.NXL_DATA_DUMP_PATH.concat(NextLabsConstant.DATA_DUMP_FILE)), Charset.forName("UTF-8").newDecoder()), Punctuation.GENERAL_DELIMITER.charAt(0), Punctuation.DOUBLE_QUOTE.charAt(0));
            validateDumpFile(csvReader.readNext()[0], csvReader.readNext()[0]);

            String[] nextLine;
            Segment dataSegment = null;
            MyFileMonitor targetFileMonitor = null;
            long counter = 0;

            // Record what is found in dump to cross check against profile
            Set<String> recoveredIncludeFolders = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
            Set<String> recoveredExcludeFolders = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
            Map<String, String> recoveredRootFolders = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
            Map<String, NextLabsFileObject> recoveredMonitorEntries = new LinkedHashMap<>();
                      
            while ((nextLine = csvReader.readNext()) != null) {
            	if(shutdown) {
            		return;
            	}
            	
                counter++;
                if (dataSegment == null) {
                    switch (nextLine[0]) {
                        case INCLUDE_FOLDER_START_TAG:
                            dataSegment = Segment.INCLUDE_FOLDER;
                            logger.debug("Include folder path segment starts.");
                            break;

                        case EXCLUDE_FOLDER_START_TAG:
                            dataSegment = Segment.EXCLUDE_FOLDER;
                            logger.debug("Exclude folder path segment starts.");
                            break;

                        case ROOT_FOLDER_START_TAG:
                            dataSegment = Segment.ROOT_FOLDER;
                            logger.debug("Root folder path segment starts.");
                            break;

                        case FILE_MONITOR_START_TAG:
                            dataSegment = Segment.FILE_MONITOR;
                            targetFileMonitor = selectFileMonitor();
                            logger.debug("File monitor segment starts.");
                            break;
                        
                        default:
                            logger.warn("Skip unrecognizable data segment [" + nextLine[0] + "]");
                            break;
                    }
                } else {
                    switch (nextLine[0]) {
                        case INCLUDE_FOLDER_START_TAG:         // Embedded segment found
                        case EXCLUDE_FOLDER_START_TAG:         // Something went wrong for
                        case ROOT_FOLDER_START_TAG:            // the dump file
                        case FILE_MONITOR_START_TAG:           // Quit loading and exit program
                            throw new IllegalStateException("Unexpected start of segment " + nextLine[0] + " within [" + dataSegment + "].");

                        case INCLUDE_FOLDER_END_TAG:
                            dataSegment = null;
                            logger.debug("Include folder path segment ended.");
                            profile.setIncludedFolders(recoveredIncludeFolders);
                            includePaths.addAll(recoveredIncludeFolders);
                            break;

                        case EXCLUDE_FOLDER_END_TAG:
                            dataSegment = null;
                            logger.debug("Exclude folder path segment ended.");
                            profile.setExcludedFolders(recoveredExcludeFolders);
                            break;

                        case ROOT_FOLDER_END_TAG:
                            dataSegment = null;
                            logger.debug("Root folder path segment ended.");
                            profile.setParentByExcludedFolder(recoveredRootFolders);
                            break;

                        case FILE_MONITOR_END_TAG:
                            dataSegment = null;

                            for (Entry<String, NextLabsFileObject> entry : recoveredMonitorEntries.entrySet()) {
                            	logger.debug("Recovering entry " + entry.getKey());
                            	targetFileMonitor.addFile(entry.getValue());
                            }
                            recoveredMonitorEntries.clear();
                            logger.debug("File monitor segment ended.");
                            break;

                        case FILE_OBJECT_START_TAG:
                            if (dataSegment != Segment.FILE_MONITOR) {
                                throw new IllegalStateException("Unexpected start of segment " + nextLine[0] + " within [" + dataSegment + "].");
                            }
                            dataSegment = Segment.FILE_OBJECT;
                            logger.debug("File object segment starts.");
                            break;

                        case FILE_OBJECT_END_TAG:
                            dataSegment = Segment.FILE_MONITOR;
                            logger.debug("File object segment ended.");
                            break;

                        default:
                            try {
                                recoverObject(dataSegment, nextLine, targetFileMonitor,
                                        recoveredIncludeFolders, recoveredExcludeFolders, recoveredRootFolders, recoveredMonitorEntries);
                            } catch (FileSystemException err) {
                                logger.error(err.getMessage(), err);
                            }
                            break;
                    }
                }

                if (counter == 500000) {
                    beat(); // Send heart beat every 500000 line processed and
                    // reset counter
                    counter = 0;
                }
            }

            printSummary();
            
            for(MyFileMonitor fileMonitor : fileMonitors) {
            	fileMonitor.start();
            	logger.info("File monitor " + fileMonitor.getMonitorId() + " started.");
            }
            
        	// Recover stage completed, should dump upon shut down
            // only if there is at least one root entry recovered 
        	createRecoveryDump = true;
            Date endTime = new Date();
            logger.info("File Watcher recovered from data dump. Time: " + endTime 
            		+ "\nRecover data lapsed time: " + DateUtil.getTimeElapsed(startTime, endTime));
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException err) {
                    logger.error(err.getMessage(), err);
                }
            }
        }
    }

    /**
     * Full scan through all folders and files for registered main entry.
     */
    public void crawl() {
        if (profile.getIncludedFolders() != null) {
            try {
                Date startTime = new Date();
                logger.info("Registering folders and files for startup. Time: " + startTime + "\n");
                
                includePaths.addAll(profile.getIncludedFolders());
                
                for (String folderPath : profile.getIncludedFolders()) {
                	crawlFolder(folderPath);
                }
                
                Date endTime = new Date();
                logger.info("Completed folders and files registration for startup. Time: " + endTime 
                		+ "\nCrawl folder lapsed time: " + DateUtil.getTimeElapsed(startTime, endTime));
                
                printSummary();
            } catch (Exception err) {
                logger.error(err.getMessage(), err);
            }
        } else {
            logger.warn("File path for monitor not configured.");
        }
    }

    /**
     * Full scan through all folders and files for registered main entry.
     * Add the folderPath into skippedRootEntries if encounter error or it is imaginary type upon resolution
     *
     * @param folderPath          file path in include folder configuration
     */
    public void crawlFolder(String folderPath) {
        try {
            if (StringUtils.isNotBlank(folderPath)) {
                logger.debug("Start register folder " + folderPath + " Time: " + new Date() + "\n");

                try {
                	FileObject fileObject = fileSystemManager.resolveFile(folderPath);
                    if(fileObject != null) {
                    	if(FileType.IMAGINARY != fileObject.getType()) {
                    		processFile(fileObject, NextLabsConstant.ACTION_ADD, true);
                        	includePaths.remove(folderPath);
                        	createRecoveryDump = true;
                    	}
                    }
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }

                logger.debug("Completed folder registration. Time: " + new Date() + "\n");
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }
    }

    /**
     * Trigger of file scanning request if require. Upon program startup where
     * file monitor object need to be determine by included folder entry.
     *
     * @param file                File object which is the processing target.
     * @param action              Action to take for given file object.
     * @param registerFileMonitor true indicate require to register into folder versus file
     *                            monitor map
     * @see #crawl()
     */
    public void processFile(final FileObject file, final String action, final boolean registerFileMonitor) {
        try {
            if (file != null) {
                String filename = URLDecoder.decode(file.toString().replaceAll(Pattern.quote("+"), "%2B"), "UTF-8");
                if (file.getType() == FileType.FOLDER && !filename.endsWith(FORWARD_SLASH)) {
                    filename += FORWARD_SLASH;
                }

                if (!isWithinFolders(profile.getExcludedFolders(), filename)) {
                    MyFileMonitor fileMonitor = selectFileMonitor();

                    if (file.getType() == FileType.FOLDER && registerFileMonitor) {
                        // Record file monitor which contains the main include
                        // folder entry
                        fileMonitorByFolderName.put(filename, fileMonitor);
                    }

                    processFile(fileMonitor, file, action, true);
                }
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }
    }

    /**
     * @param fileMonitor        the fileMonitor for the file
     * @param file               the file object to be processed
     * @param action             the action to be performed
     * @param checkExcludedPaths flag to check if the excluded paths need to checked for
     */
    public void processFile(final MyFileMonitor fileMonitor, final FileObject file, final String action, final boolean checkExcludedPaths) {
    	
    	StringBuffer sBuf = new StringBuffer();
    	
        try {
            if (file != null) {
                String filename = URLDecoder.decode(file.toString().replaceAll(Pattern.quote("+"), "%2B"), "UTF-8");
                if (file.getType() == FileType.FOLDER && !filename.endsWith(FORWARD_SLASH)) {
                    filename += FORWARD_SLASH;
                }

                String absoluteFilePath = getAbsoluteFilePath(filename);
                
                sBuf.append("[").append(fileMonitor.getMonitorId()).append("]: Received file object ").append(absoluteFilePath)
                		.append("::").append(file.getType().toString()).append(" for ").append(action);
                
                logger.info(sBuf);
                
                sBuf.setLength(0);
                
                // Check for exclude file path
                if (checkExcludedPaths && isWithinFolders(profile.getExcludedFolders(), filename)) {
                    logger.info("[" + fileMonitor.getMonitorId() + "]: Path " + absoluteFilePath + " is in excluded paths, skipping..");
                    return;
                }

                // Different handling required for folder versus file
                if (file.getType() == FileType.FOLDER) {
                    if (action.equals(NextLabsConstant.ACTION_ADD)) {
                        synchronized (registeredFolderCount) {
                            registeredFolderCount++;
                        }
                        fileMonitor.addFile(file);
                        logger.info("[" + fileMonitor.getMonitorId() + "]: Added folder " + absoluteFilePath + " into the watcher monitoring map.");
                        logger.info("[" + fileMonitor.getMonitorId() + "]: File monitor map size: " + fileMonitor.getMonitorMapSize() + "\n");

                        // Crawl through sub folders
                        FileObject[] childrenList = file.getChildren();
                        if (childrenList != null && childrenList.length > 0) {
                            for (FileObject child : childrenList) {
                                // Register all children folders and files under
                                // same file monitor object
                                processFile(fileMonitor, child, action, true);
                                beat();
                            }
                        }
                    } else if (action.equals(NextLabsConstant.ACTION_DELETE)) {
                        synchronized (registeredFolderCount) {
                            registeredFolderCount--;
                        }
                        fileMonitor.removeFile(file);
                        logger.info("[" + fileMonitor.getMonitorId() + "]: Removing folder " + absoluteFilePath + " from the monitoring map.");
                        logger.info("[" + fileMonitor.getMonitorId() + "]: File monitor map size: " + fileMonitor.getMonitorMapSize() + "\n");
                    }
                } else { // it is a file
                    synchronized (encounteredFileCount) {
                        encounteredFileCount++;
                    }
                    // Create message to file extractor to process the file
                    if (profile.getIncludedExtns().contains(file.getName().getExtension().toLowerCase())
                            && (!file.getName().getBaseName().startsWith(MyFileListener.TEMP_FILE))) {
                        if (action.equals(NextLabsConstant.ACTION_ADD)) {
                            synchronized (registeredFileCount) {
                                registeredFileCount++;
                            }
                            fileMonitor.addFile(file);
                            
                            sBuf.append("[").append(fileMonitor.getMonitorId()).append("]: Adding the file ").append(absoluteFilePath).append(" into the monitoring map.");
                            
                            logger.info(sBuf);
                            
                            sBuf.setLength(0);
                            
                            if (Boolean.parseBoolean(getSystemConfig(SystemConfigKey.WATCHER_CHECK_LASTMODIFIED))) {
	                            // Only perform file processed check for ADD action
	                            if (!isDocumentAlreadyProcessed(absoluteFilePath, file.getContent().getLastModifiedTime())) {
	                                sendToJMS(file, action);
	                            } else {
	                                logger.info("Skip processed file " + file.getName() + ".");
	                            }
                            }
                            else {
                            	sendToJMS(file, action);
                            	logger.info("send file " + file.getName() + " to queue");
                            }
                        } else {
                            if (action.equals(NextLabsConstant.ACTION_DELETE)) {
                                synchronized (registeredFileCount) {
                                    registeredFileCount--;
                                }
                                fileMonitor.removeFile(file);
                                logger.info(
                                        "[" + fileMonitor.getMonitorId() + "]: Removing the file " + absoluteFilePath + " from the monitoring map.");
                            }

                            sendToJMS(file, action);
                        }

                        logger.debug("[" + fileMonitor.getMonitorId() + "]: File monitor map size: " + fileMonitor.getMonitorMapSize() + "\n");
                    } else {
                        logger.info("Skip non-monitor file or temp file " + absoluteFilePath + ".\n");
                    }
                }
            }
        } catch (Exception err) {
            logger.debug(err.getMessage(), err);
        }
    }

    /**
     * Call to shutdown resources gracefully before exit program.
     */
    @Override
    public void shutdown() {
        logger.info("Shutting down file watcher.");

        Event shutdownEvent = new Event();
        shutdownEvent.setStage(EventStage.COMPONENT_SHUTDOWN);
        shutdownEvent.setCategory(EventCategory.MAINTENANCE);
        shutdownEvent.setStatus(EventStatus.SUCCESS);
        shutdownEvent.setMessageCode(ReportEvent.COMPONENT_STOP_SUCCESS.getMessageCode());
        shutdownEvent.setTimestamp(System.currentTimeMillis());

        log(shutdownEvent);

        shutdownResources();
        logger.info("All resources shutdown succesfully");
        
        // Only create dump if watcher is not shut down in the middle of recovery process
        if(createRecoveryDump) {
        	dumpToFile();
        }

        // Give time for eventLogger to flush messages into database before
        // program exit
        int counter = 0;
        while (eventLogger.isAlive() && counter < 5) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException err) {
                // Ignore
            }

            counter++;
        }

        logger.info("File watcher shut down successfully.");
        logger.info("\n=================================================================================================\n");
    }

    /**
     * Reload latest configuration of watcher and replace Java object if there
     * is change detected.
     */
    @Override
    public boolean reloadConfigs() {
        Session session = null;
        Transaction transaction = null;
        boolean configurationReloaded = false;
        Date now = new Date();
                
        try {
            if ((now.getTime() - profile.getConfigLoadedOn().getTime()) >= profile.getConfigReloadInterval()) {
            	
            	logger.info("Starting  to reload config..");
            	
                session = sessionFactory.openSession();
                transaction = session.beginTransaction();

                reloadSystemConfiguration(session);
                if (splitTime != Long.parseLong(getSystemConfig(SystemConfigKey.WATCHER_SPLIT_LAST_MODIFIED_DATE))) {
                    splitTime = Long.parseLong(getSystemConfig(SystemConfigKey.WATCHER_SPLIT_LAST_MODIFIED_DATE));
                    for (MyFileMonitor fileMonitor : fileMonitors) {
                        fileMonitor.setSplit(splitTime * 1000);
                    }
                }

                RepositoryUtil.reloadRepositories();

                WatcherProfile upToDateProfile = null;
                WatcherManager watcherManager = new WatcherManager(sessionFactory, session);
                com.nextlabs.smartclassifier.database.entity.Watcher upToDateWatcher = watcherManager.getWatcherByHostname(hostname);

                if (upToDateWatcher != null) {
                    upToDateProfile = new WatcherProfile();
                    upToDateProfile.copy(upToDateWatcher);
                    watcherManager.updateConfigLoadedDate(hostname, now);
                }
                transaction.commit();

                if (upToDateProfile != null) {
                    reloadDocTypes(upToDateProfile);
                    reloadRepoFolderConfig(upToDateProfile);
                    reloadMessageSenderConfig(upToDateProfile);

                    profile.setConfigLoadedOn(now);
                    profile.setConfigReloadInterval(upToDateProfile.getConfigReloadInterval() / 1000);

                    configurationReloaded = true;
                    logger.info("---- Configuration Reload Completed ----\n");
                } else {
                    logger.warn("Unable to resolve Watcher configuration for " + hostname + ".");
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

    public void retryImaginaryPaths() {
    	try {
	    	for(String path : includePaths) {
	    		logger.debug("Attempt to process " + path);
	    		if(!shutdown) {
	    			crawlFolder(path);
	    		}
	    	}
    	} catch(Exception err) {
    		logger.warn("Encounter " + err.getMessage() + " when trying to re-process failed paths.", err);
    	}
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
     * Check if given file name is a root folder entry
     */
    public boolean isRootEntry(String fileName) {
    	return profile.getIncludedFolders().contains((fileName.endsWith(FORWARD_SLASH) ? fileName : fileName + FORWARD_SLASH));
    }
    
    /**
     * Retrieve document's configured root entry folder value 
     * @param fileName File name to check
     * @return root entry folder string if found
     */
    public String getRootEntry(String fileName) {
    	if(fileName != null) {
    		String toCheck = (fileName + FORWARD_SLASH).toLowerCase();
	    	Iterator<String> rootEntryIterator = profile.getIncludedFolders().iterator();
	
	    	while(rootEntryIterator.hasNext()) {
	    		String rootEntry = rootEntryIterator.next().toLowerCase();
	
	    		if(toCheck.startsWith(rootEntry)) {
	    			return rootEntry;
	    		}
	    	}
    	}

    	logger.warn("Root entry not found for " + fileName);
    	return null;
    }
    
    /**
     * Check if configured root entry is accessible before decide to remove file from monitoring list.
     * This is to counter network disconnect issue
     * @param fileName File name to be remove
     * @param notFoundDefault Default value to return if root entry is not found
     * @return If root entry is accessible
     */
    public boolean isRootEntryAccessible(String fileName, boolean notFoundDefault) {
    	String rootEntry = getRootEntry(fileName);

    	if(rootEntry != null) {
	        try {
	        	FileObject root = fileSystemManager.resolveFile(rootEntry);
	        	return (root != null && root.exists());
	        } catch(FileSystemException e) {
        		return false;
	        } catch (Exception e) {
	        	logger.error(e.getMessage(), e);
	        	return false;
	        }
    	}

    	return notFoundDefault;
    }
    
    /**
     * Load Watcher's profile from database based on machine hostname.
     *
     * @return true if watcher profile found<br>
     * false if unable to find watcher's profile
     */
    @Override
    protected boolean loadProfile() {
        Session session = null;
        Transaction transaction = null;
        boolean profileLoaded = false;

        try {
            logger.info("Loading watcher profile [" + hostname + "] from database.");

            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            WatcherManager watcherManager = new WatcherManager(sessionFactory, session);
            com.nextlabs.smartclassifier.database.entity.Watcher watcher = watcherManager.getWatcherByHostname(hostname);

            if (watcher != null) {
                id = watcher.getId();
                Date now = new Date();

                profile = new WatcherProfile();
                profile.copy(watcher);
                profile.setConfigLoadedOn(now);

                watcherManager.updateConfigLoadedDate(hostname, now);
                profileLoaded = true;
                logger.info("Watcher profile [" + hostname + "] loaded successfully.");
            } else {
                logger.error("Watcher profile [" + hostname + "] not found.");
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

        if (fileSystemManager != null) {
            fileSystemManager.close();
        }

        if (fileMonitors != null) {
            for (NextlabsFileMonitor fileMonitor : fileMonitors) {
                fileMonitor.stop();
            }
        }

        if (messageSender != null) {
            messageSender.shutdown();
            messageSender.close();
        }
        // closing httpclient instances
        HTTPClientUtil.closeConnections();
    }

    /**
     * Check for added and removed document type.
     *
     * @param upToDateProfile Most recent configuration
     * @throws Exception All kind of exceptions
     */
    protected void reloadDocTypes(WatcherProfile upToDateProfile) throws Exception {
        if (upToDateProfile != null && upToDateProfile.getIncludedExtns() != null) {
            if (!profile.getIncludedExtns().equals(upToDateProfile.getIncludedExtns())) {
                logger.debug("Document type change detected.");

                // use this to collect any document extensions which are to be
                // removed from the original set
                Set<String> removeDocumentTypes = new HashSet<>();

                // initially add all documents types.
                removeDocumentTypes.addAll(profile.getIncludedExtns());

                for (String includedExtension : upToDateProfile.getIncludedExtns()) {
                    // if the extension is still present in the new profile,
                    // remove it from remove document types
                    if (profile.getIncludedExtns().contains(includedExtension)) {
                        // the document type is in the new profile, it should
                        // not be removed from the original profile
                        removeDocumentTypes.remove(includedExtension);
                    } else {
                        // a new document type is found, add it
                        profile.getIncludedExtns().add(includedExtension);
                        // TODO: Crawl through entries process newly added
                        // extension
                    }
                }

                // These extensions are not in up to date document type
                for (String documentTypeToRemove : removeDocumentTypes) {
                    profile.getIncludedExtns().remove(documentTypeToRemove);
                }

                // Update file monitors' include file type
                for (MyFileMonitor fileMonitor : fileMonitors) {
                    fileMonitor.setIncludeFileType(profile.getIncludedExtns());
                }

                // Print updated document type
                for (String extension : profile.getIncludedExtns()) {
                    logger.debug("Included document type : " + extension);
                }
            }
        }
    }

    /**
     * Check for added and removed directories.
     *
     * @param upToDateProfile Most recent configuration
     * @throws Exception All kinds of exceptions
     */
    protected void reloadRepoFolderConfig(WatcherProfile upToDateProfile) throws Exception {
        if (upToDateProfile != null) {

            reloadRepoTypeByPath(upToDateProfile);

            // Map<Folder, Site>
            reloadSiteByFolder(upToDateProfile);

            // reload siteURL To RepoFolder Path Map
            reloadRepoFolderBySite(upToDateProfile);

            // reload excluded folders
            reloadExcludedFolders(upToDateProfile);

            // reload included folders
            reloadIncludedFolders(upToDateProfile);

            // reload excluded folders to parent Included Folders Map
            reloadParentByExcludedFolder(upToDateProfile);

        }
    }

    private void reloadRepoTypeByPath(WatcherProfile upToDateProfile) {

        logger.debug("Trying to reload RepoFolderByPath");

        Set<String> currentKeys = new HashSet<>(profile.getRepoTypeByPath().keySet());
        Set<String> latestKeys = new HashSet<>(upToDateProfile.getRepoTypeByPath().keySet());

        // Keys in currentMap and not in latestMap - remove these entries
        // from the current map
        Set<String> keysNotInLatest = new HashSet<>(currentKeys);
        keysNotInLatest.removeAll(latestKeys);

        for (String keyToRemove : keysNotInLatest) {
            profile.getRepoTypeByPath().remove(keyToRemove);
        }

        profile.getRepoTypeByPath().putAll(upToDateProfile.getRepoTypeByPath());

    }

    private void reloadRepoFolderBySite(WatcherProfile upToDateProfile) {

        logger.debug("Trying to reload RepoFolderBySite");

        HashMap<String, String> currentMap = profile.getRepoPathBySite();
        HashMap<String, String> latestMap = upToDateProfile.getRepoPathBySite();

        if (!latestMap.equals(currentMap)) {

            logger.debug("RepoFolderBySite has changed. Before update :");
            SharePointUtil.HashMapUtil.printMap(currentMap);

            Set<String> currentKeys = new HashSet<>(currentMap.keySet());
            Set<String> latestKeys = new HashSet<>(latestMap.keySet());

            // Keys common to both maps - simply update the entries in the
            // current map
            Set<String> commonKeys = new HashSet<>(currentKeys);
            commonKeys.retainAll(latestKeys);

            for (String commonKey : commonKeys) {
                currentMap.put(commonKey, latestMap.get(commonKey));
            }

            // Keys in latestMap and not in currentMap - simply add these to the
            // current map
            Set<String> newKeysInLatest = new HashSet<>(latestKeys);
            newKeysInLatest.removeAll(currentKeys);

            for (String newKeyInLatest : newKeysInLatest) {
                currentMap.put(newKeyInLatest, latestMap.get(newKeyInLatest));
            }

            // Keys in currentMap and not in latestMap - remove these entries
            // from the current map
            Set<String> keysNotInLatest = new HashSet<>(currentKeys);
            keysNotInLatest.removeAll(latestKeys);

            // currently we do not remove entries from the map

			/*
             * for (String keyNotInLatest : keysNotInLatest) { String repoFolder
			 * = profile.getRepoPathBySite().get(keyNotInLatest); if
			 * (upToDateProfile.getRepoFolderLoadingStatus().get(repoFolder) !=
			 * null) { // repoFolder is there, and loaded correctly if
			 * (upToDateProfile.getRepoFolderLoadingStatus().get(repoFolder)) {
			 * // This only indicates that maybe sub-site is removed from SP.
			 * logger.debug("The " + repoFolder +
			 * " still exists in the new profile, but the " + keyNotInLatest +
			 * " does not. This means probably sub-site removed from SP.");
			 * currentMap.remove(keyNotInLatest); } else { // repoFolder is
			 * there, but not loaded correctly logger.debug("The " + repoFolder
			 * +
			 * " still exists in the new profile but wasn't loaded correctly. Hence we do not remove "
			 * + keyNotInLatest + " from the map"); } } else { // repoFolder is
			 * no longer there -> remove this SiteURL
			 * currentMap.remove(keyNotInLatest); } }
			 */

            logger.debug("RepoFolderBySite after update :");
            SharePointUtil.HashMapUtil.printMap(currentMap);
        } else {
            logger.debug("RepoFolderBySite has not changed!");
        }
        logger.debug("Reload of RepoFolderBySite completed.\n");
    }

    private void reloadParentByExcludedFolder(WatcherProfile upToDateProfile) {

        logger.debug("Trying to reload ParentByExcludedFolder");

        Map<String, String> latestMap = upToDateProfile.getParentByExcludedFolder();
        Map<String, String> currentMap = profile.getParentByExcludedFolder();

        if (!latestMap.equals(currentMap)) {

            logger.debug("ParentByExcludedFolder has changed. Before update - ");
            SharePointUtil.HashMapUtil.printMap(currentMap);

            Set<String> keysInCurrent = new HashSet<>(currentMap.keySet());
            Set<String> keysInLatest = new HashSet<>(latestMap.keySet());

            // Keys common to both maps - simply update the entries in the
            // current map
            Set<String> commonKeys = new HashSet<>(keysInCurrent);
            commonKeys.retainAll(keysInLatest);

            for (String commonKey : commonKeys) {
                currentMap.put(commonKey, latestMap.get(commonKey));
            }

            // Keys in latestMap and not in currentMap - simply add these to the
            // current map
            Set<String> newKeysInLatest = new HashSet<>(keysInLatest);
            newKeysInLatest.removeAll(keysInCurrent);

            for (String newKeyInLatest : newKeysInLatest) {
                currentMap.put(newKeyInLatest, latestMap.get(newKeyInLatest));
            }

            // Keys in currentMap and not in latestMap - remove these entries
            // from the current map
            Set<String> keysNotInLatest = new HashSet<>(keysInCurrent);
            keysNotInLatest.removeAll(keysInLatest);

            for (String keyNotInLatest : keysNotInLatest) {
                /*
                 * It might occur that SharePoint server was down. In that case
				 * the folders are not loaded correctly. We must not disturb the
				 * old configuration. We are careful about removal. Not so much
				 * about addition
				 *
				 * Before removal, we first get the sharepoint site this folder
				 * belongs to. Then we check which Repo Folder this site belongs
				 * to. Then we check whether this repo folder was loaded
				 * correctly or not. If it was not loaded correctly. we do not
				 * remove.
				 *
				 * We go from this Path -> Site -> Repo-Folder -> Loading Status
				 */

                String folderPath;
                try {
                    folderPath = VFSUtil.getAbsoluteFilePath(keyNotInLatest);
                } catch (Exception e) {
                    logger.error("Could not convert included folder to absolute path !!!");
                    continue;
                }

                String siteURLForThisFolder;
                if ((siteURLForThisFolder = profile.getSiteByFolders().get(Paths.get(folderPath))) != null) { // Site
                    // sharepoint section
                    String repoFolderPath = profile.getRepoPathBySite().get(siteURLForThisFolder); // Repo-Folder
                    // check if this repo-folder is also present in the new
                    // profile and if it was loaded correctly
                    if (upToDateProfile.getRepoFolderLoadingStatus().get(repoFolderPath) != null) { // Loading
                        // Status
                        if (upToDateProfile.getRepoFolderLoadingStatus().get(repoFolderPath)) {
                            // maybe the excluded folder is removed.
                            logger.debug("Maybe the " + keyNotInLatest
                                    + " was removed as an excluded folder. Removing it from the ExcludedFolderToParentIncludedFolderMap");
                            currentMap.remove(keyNotInLatest);
                        } else {
                            logger.debug(repoFolderPath + " wasn't loaded correctly. Hence not removing " + keyNotInLatest
                                    + " from ExcludedFolderToParentIncludedFolderMap");
                        }
                    } else {
                        logger.debug("The new profile does not contain this repo-folder. Hence removing " + keyNotInLatest
                                + " from ExcludedFolderToParentIncludedFolderMap");
                        currentMap.remove(keyNotInLatest);
                    }
                } else {
                    // shared folder section
                    logger.debug(
                            "The folder " + keyNotInLatest + " is a shared folder. Removing it from the ExcludedFolderToParentIncludedFolderMap");
                    currentMap.remove(keyNotInLatest);
                }
                /*
                 * Here we check whether the new profile also contains this same
				 * repo folder and if it does, was it successfully loaded? If
				 * not, we will not remove the folder.
				 */
            }

            logger.debug("ParentByExcludedFolder after update - ");

            SharePointUtil.HashMapUtil.printMap(currentMap);
        } else {
            logger.debug("ParentByExcludedFolder has not been changed ");
        }
        logger.debug("Reload of ParentByExcludedFolder completed.\n");
    }

    /**
     * Compare the difference between the current configuration's
     * sharePointFolderToSiteURLMap and the most recent configuration's
     * sharePointFolderToSiteURLMap and update the current
     * sharePointFolderToSiteURLMap to the latest configuration.
     *
     * @param upToDateProfile Most recent configuration
     */
    protected void reloadSiteByFolder(WatcherProfile upToDateProfile) {

        logger.debug("Trying to reload SiteByFolders");
        /*
         * Test Conditions For sharePoint: 1) Either the include folder was
		 * changed. 2) or, the folder was not loaded correctly.
		 */
        Map<Path, String> oldSiteByFolder = profile.getSiteByFolders();
        Map<Path, String> newSiteByFolder = upToDateProfile.getSiteByFolders();

        if (!newSiteByFolder.equals(oldSiteByFolder)) {

            logger.debug("SiteByFolder map has changed. Before update, size = " + oldSiteByFolder.size());
            SharePointUtil.HashMapUtil.printMap(oldSiteByFolder);

            Set<Path> oldKeys = new HashSet<>(oldSiteByFolder.keySet());
            Set<Path> newKeys = new HashSet<>(newSiteByFolder.keySet());

            // Keys common to both maps - simply update the entries in the
            // current map
            Set<Path> commonKeys = new HashSet<>(oldKeys);
            commonKeys.retainAll(newKeys);

            for (Path commonKey : commonKeys) {
                oldSiteByFolder.put(commonKey, newSiteByFolder.get(commonKey));
            }

            // Keys in latestMap and not in currentMap
            Set<Path> exclKeysInNew = new HashSet<>(newKeys);
            exclKeysInNew.removeAll(oldKeys);

            // ; simply add these to the current map
            for (Path newKeyInLatest : exclKeysInNew) {
                oldSiteByFolder.put(newKeyInLatest, newSiteByFolder.get(newKeyInLatest));
            }

            // Keys in currentMap and not in latestMap - remove these entries
            // from the current map
            Set<Path> exclKeysInOld = new HashSet<>(oldKeys);
            exclKeysInOld.removeAll(newKeys);

            // We currently do not remove from this map
            /*
             * for (Path exclKeyInOld : exclKeysInOld) {
			 *//*
                 * It might occur that SharePoint server(s) was(re) down. In
				 * that case the folders are not loaded correctly. We must not
				 * disturb the old configuration. We are careful about removal.
				 * Not so much about addition
				 *
				 * Before removal, we first get the site this folder belongs to.
				 * Then we check which Repo Folder this site belongs to. Then we
				 * check whether this repo folder was loaded correctly or not.
				 * If it was not loaded correctly. we do not remove.
				 *
				 * We go from this Path - > Site -> Repo-Folder - > Loading
				 * Status
				 *//*
                 * String siteForMissingFolder =
				 * profile.getSiteByFolders().get(exclKeyInOld); // site
				 *
				 * String repoForMissingFolder =
				 * profile.getRepoPathBySite().get(siteForMissingFolder); //
				 * Repo-Folder
				 *
				 * if (upToDateProfile.getRepoFolderLoadingStatus().get(
				 * repoForMissingFolder) != null) { if
				 * (upToDateProfile.getRepoFolderLoadingStatus().get(
				 * repoForMissingFolder)) {
				 * oldSiteByFolder.remove(exclKeyInOld); } else {
				 * logger.debug(repoForMissingFolder +
				 * " wasn't loaded correctly. Hence NOT removing " +
				 * exclKeyInOld + " from SiteByFolders"); } } else {
				 * oldSiteByFolder.remove(exclKeyInOld); } }
				 */

            logger.debug("SiteByFolders after update, size = " + oldSiteByFolder.size());
            SharePointUtil.HashMapUtil.printMap(oldSiteByFolder);
        } else {
            logger.debug("SiteByFolders has not changed! Nothing to do...");
        }
        logger.debug("Reload of SiteByFolders completed.\n");
    }

    /**
     * Check for JMS information change and replacement
     *
     * @param upToDateProfile Most recent configuration
     * @throws Exception All kind of exceptions
     */
    protected void reloadMessageSenderConfig(WatcherProfile upToDateProfile) throws Exception {
        if (upToDateProfile != null) {
            if (!messageSender.equals(new QueueMessageSender(this, upToDateProfile.getJmsConfig()))) {
                logger.info("JMS configuration changed, updating QueueMessageSender.");
                messageSender.updateJMSConfig(upToDateProfile.getJmsConfig());
            } else {
                logger.debug("JMS configuration has not changed. Nothing to update !!");
            }
        }
    }

    /**
     * Initialize JMS queue message sender to send message for document
     * extraction task.
     *
     * @throws Exception should treated as fatal error and program shutdown is
     *                   required.
     * @throws Exception All kind of exceptions
     */
    private void initMessageSender() throws Exception {
        try {
            logger.info("Initializing message sender.");
            messageSender = new QueueMessageSender(this, profile.getJmsConfig());
            messageSender.connectToJMS();

            logger.info("Message sender initialized.");
        } catch (Exception err) {
            logger.error("Failed to initialize message sender.");
            throw err;
        }
    }

    /**
     * Initialize Solr index query engine.
     *
     * @throws Exception should treated as fatal error and program shutdown is required.
     */
    private void initIndexQueryEngine()
            throws Exception {
        try {
            logger.info("Initializing index query engine.");
            indexQueryEngine = new QueryEngine(getSystemConfig(SystemConfigKey.INDEXER_URL), 
            		getSystemConfigs().get(SystemConfigKey.INDEXER_USERNAME),
            		getSystemConfigs().get(SystemConfigKey.INDEXER_PASSWORD));

            logger.info("Index query engine initialized.");
        } catch (Exception err) {
            logger.error("Failed to initialize index query engine.");
            throw err;
        }
    }

    private void initFileSystemManager() throws Exception {
        try {
            fileSystemManager = new DefaultFileSystemManager();
            fileSystemManager.addProvider(URL_SCHEME, new DefaultLocalFileProvider());
            // never cache a file
            fileSystemManager.setFilesCache(new NullFilesCache());
            // get a file object from system every time a method on it is called
            fileSystemManager.setCacheStrategy(CacheStrategy.ON_CALL);
            fileSystemManager.init();
        } catch (FileSystemException err) {
            logger.error("Failed to initialize file system manager.");
            throw err;
        }
    }

    public static String getSiteURL(Path path) {
        if (profile != null && profile.getSiteByFolders() != null) {
            if (profile.getSiteByFolders().containsKey(path)) {
                return profile.getSiteByFolders().get(path);
            } else {
                profile.reloadSharePointFolders(sessionFactory);
                return profile.getSiteByFolders().get(path);
            }
        }

        return null;
    }

    /**
     * Initialize file monitor pool to observe the included folder(s).
     *
     * @throws Exception should treated as fatal error and program shutdown is
     *                   required.
     */
    private void initFileMonitor(boolean startFileMonitor) throws Exception {
        try {
            int fileMonitorCount = profile.getFileMonitorCount();

            // Use minimum 1 file monitor
            if (fileMonitorCount <= 0) {
                fileMonitorCount = 1;
            }

            logger.info("Initializing " + fileMonitorCount + " threads for file monitor");

            fileMonitors = new MyFileMonitor[fileMonitorCount];
            fileMonitorByFolderName = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

            for (int i = 0; i < fileMonitorCount; i++) {
                MyFileListener listener = new MyFileListener(this);

                fileMonitors[i] = new MyFileMonitor(this, listener, "FileMonitor-" + i);
                fileMonitors[i].setSplit(splitTime * 1000);
                fileMonitors[i].setRecursive(false);
                fileMonitors[i].setIncludeFileType(profile.getIncludedExtns());

                listener.setFileMonitor(fileMonitors[i]);

                if(startFileMonitor) {
                	fileMonitors[i].start();
                	logger.info("File monitor " + fileMonitors[i].getMonitorId() + " started.");
                }
            }

            logger.info("File monitors initialized.");
        } catch (Exception err) {
            logger.error("Failed to initialize file monitor.");
            throw err;
        }
    }

    /**
     * Find the difference between current and new profile's include folder and
     * update Java object.
     *
     * @param upToDateProfile Most recent configuration
     */
    private void reloadIncludedFolders(WatcherProfile upToDateProfile) throws Exception {

        logger.debug("Trying to reload IncludedFolders set");

        Set<String> oldFolders = profile.getIncludedFolders();
        Set<String> newFolders = upToDateProfile.getIncludedFolders();

        if (!oldFolders.equals(newFolders)) {
            //dumpAtShutdown = false;
            logger.debug("Included folder set has changed.");

            logger.debug(
                    String.format("Included folder set before update[%s]= " + profile.getIncludedFolders(), profile.getIncludedFolders().size()));

            Set<String> newlyAddedFolders = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
            //Add new/updated profile include folders
            newlyAddedFolders.addAll(newFolders);
            //Remove old profile include folders
            newlyAddedFolders.removeAll(oldFolders);

            for (String newlyAddedFolder : newlyAddedFolders) {
                logger.debug("Adding newly Included folder " + newlyAddedFolder);

                oldFolders.add(newlyAddedFolder);
                crawlFolder(newlyAddedFolder);
            }

            // remove these folders in the end
            Set<String> foldersToRemove = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
            //Add old profile include folders
            foldersToRemove.addAll((oldFolders));
            //remove new profile include folders
            foldersToRemove.removeAll(newFolders);

            // These folders are not in the up to date included folder list
            for (String folderToRemove : foldersToRemove) {
                /*
                 * It might occur that SharePoint server was down. In that case
				 * the folders are not loaded correctly. We must not disturb the
				 * old configuration. We are careful about removal. Not so much
				 * about addition
				 *
				 * Before removal, we first get the sharepoint site this folder
				 * belongs to. Then we check which Repo Folder this site belongs
				 * to. Then we check whether this repo folder was loaded
				 * correctly or not. If it was not loaded correctly. we do not
				 * remove.
				 *
				 * We go from this Path - > Site -> Repo-Folder - > Loading
				 * Status
				 */
                String folderPath;
                try {
                    folderPath = VFSUtil.getAbsoluteFilePath(folderToRemove);
                } catch (Exception e) {
                    logger.error("Could not convert included folder to absolute path !!!");
                    continue;
                }

                String site = profile.getSiteByFolders().get(Paths.get(folderPath));

                if (site != null) {
                    String repoFolderPath = profile.getRepoPathBySite().get(site);

                    // check if this repo-folder is also present in the new
                    // profile and if it was loaded correctly
                    if (upToDateProfile.getRepoFolderLoadingStatus().get(repoFolderPath) != null) {
                        if (upToDateProfile.getRepoFolderLoadingStatus().get(repoFolderPath)) { // Loading
                            // Status
                            // repo-folder was loaded successfully. Hence its
                            // subfolder can be removed
                            // Maybe subfolder was removed from SP instance.
                            profile.getIncludedFolders().remove(folderToRemove);
                            includePaths.remove(folderToRemove);
                        } else {
                            logger.debug("The " + repoFolderPath + " was not loaded successfully. Hence we do not remove " + folderToRemove
                                    + " from the includedFolders set.");
                            continue;
                        }
                    } else {
                        profile.getIncludedFolders().remove(folderToRemove);
                        includePaths.remove(folderToRemove);
                    }
                } else {
                    profile.getIncludedFolders().remove(folderToRemove);
                    includePaths.remove(folderToRemove);
                }

                if (fileMonitorByFolderName.containsKey(folderToRemove)) {

                    // telling the file monitor to remove this path
                    fileMonitorByFolderName.get(folderToRemove).queueAddRemovePath(folderToRemove);

                    // remove from the map
                    fileMonitorByFolderName.remove(folderToRemove);
                } else {
                    // Call to file monitor and remove entries
                    // Ask all file monitor to remove this included path
                    for (MyFileMonitor fileMonitor : fileMonitors) {
                        fileMonitor.queueAddRemovePath(folderToRemove);
                    }
                }
            }

            logger.debug(String.format("Include Folders updated[%s]= " + profile.getIncludedFolders(), profile.getIncludedFolders().size()));

            for (Entry<String, MyFileMonitor> mainEntry : fileMonitorByFolderName.entrySet()) {
                logger.debug("Folder entry: " + mainEntry.getKey() + "; Monitor: " + mainEntry.getValue().getMonitorId());
            }

        }
        logger.debug("Reload of IncludedFolders set completed.");
    }

    /**
     * Find the difference between current and new profile's exclude folder and
     * update Java object.
     *
     * @param upToDateProfile Most recent configuration
     */
    private void reloadExcludedFolders(WatcherProfile upToDateProfile) throws Exception {

        logger.debug("Trying to reload ExcludedFolders");

        Set<String> oldFolders = profile.getExcludedFolders();
        Set<String> newFolders = upToDateProfile.getExcludedFolders();

        if (!oldFolders.equals(newFolders)) {

            logger.debug("Excluded folders have changed.");

            logger.debug(
                    String.format("Exclude folder set before update[%s] = " + profile.getExcludedFolders(), profile.getExcludedFolders().size()));

            // simply add these new folders
            Set<String> newlyAddedFolders = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
            newlyAddedFolders.addAll(newFolders);
            newlyAddedFolders.removeAll(oldFolders);

            for (String newlyAddedFolder : newlyAddedFolders) {

                oldFolders.add(newlyAddedFolder);

                Map<String, String> parentByExcludedFolder = upToDateProfile.getParentByExcludedFolder();
                String parentFolder = parentByExcludedFolder.get(newlyAddedFolder);

                if (parentFolder != null) {
                    if (fileMonitorByFolderName.containsKey(parentFolder)) {
                        // coming here means that, for the newly excluded
                        // folders -> include parent is already present.
                        logger.debug(String.format(
                                "Parent folder (%s) for the newly excluded Folder(%s) is already in fileMonitorByFolderName. Trying to add the excluded folder to the file monitor that is monitoring its parent",
                                parentFolder, newlyAddedFolder));
                        // consider this a 2 step process - > 1) get the file
                        // monitor for the include parent, 2) add an excluded
                        // path to it.
                        fileMonitorByFolderName.get(parentFolder).queueAddRemovePath(newlyAddedFolder);
                    }
                } else {
                    // Crawl through registered map and remove files
                    // if the parent isn't present, add to all - this happens
                    // since excluded folders are loaded before included
                    // folders.
                    for (MyFileMonitor fileMonitor : fileMonitors) {
                        fileMonitor.queueAddRemovePath(newlyAddedFolder);
                    }
                }
            }

            // remove these folders in the end
            Set<String> foldersToRemove = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
            foldersToRemove.addAll(oldFolders);
            foldersToRemove.removeAll(newFolders);

            for (String folderToRemove : foldersToRemove) {
                /*
                 * It might occur that SharePoint server was down. In that case
				 * the folders are not loaded correctly. We must not disturb the
				 * old configuration. We are careful about removal. Not so much
				 * about addition
				 *
				 * Before removal, we first get the sharepoint site this folder
				 * belongs to. Then we check which Repo Folder this site belongs
				 * to. Then we check whether this repo folder was loaded
				 * correctly or not. If it was not loaded correctly. we do not
				 * remove.
				 *
				 * We go from this Path - > Site -> Repo-Folder - > Loading
				 * Status
				 */
                String folderPath;
                try {
                    folderPath = VFSUtil.getAbsoluteFilePath(folderToRemove);
                } catch (Exception e) {
                    logger.error("Could not convert included folder to absolute path !!!");
                    continue;
                }

                String site = profile.getSiteByFolders().get(Paths.get(folderPath));

                if (site != null) {
                    String repoFolderPath = profile.getRepoPathBySite().get(site);

                    // check if this repo-folder is also present in the new
                    // profile and if it was loaded correctly
                    if (upToDateProfile.getRepoFolderLoadingStatus().get(repoFolderPath) != null) {
                        if (upToDateProfile.getRepoFolderLoadingStatus().get(repoFolderPath)) { // Loading
                            // Status
                            // repo-folder was loaded successfully. Hence its
                            // subfolder can be removed
                            // Either the excluded folder was removed from
                            // configuration, or from SP instance.
                            profile.getExcludedFolders().remove(folderToRemove);
                        } else {
                            logger.debug(repoFolderPath + " wasn't loaded correctly. Hence not removing " + folderToRemove + " from excludedFolders");
                            continue;
                        }
                    } else {
                        profile.getExcludedFolders().remove(folderToRemove);
                    }
                } else {
                    profile.getExcludedFolders().remove(folderToRemove);
                }

                // Parent still remain included in updated profile
                /*
                 * eg A - include folder, a - exclude folder. If you remove 'a'
				 * from exclude folders, it becomes an include folder. Hence we
				 * crawl it
				 */

				/*
                 * pkalra: The below logic is added for SharePoint to function
				 * correctly. Removing the second condition after and will cause
				 * problems with SharePoint eg. Suppose watched folder are from
				 * http://sp2013w2k12r2 and excluded folder are
				 * http://sp2013w2k12r2/HR In-case, we remove
				 * http://sp2013w2k12r2/HR now, the folders from this site will
				 * go in as newly included folders.
				 *
				 * However, if we have added http://sp2013w2k12r2 as included
				 * folder and excluded folder are
				 * http://sp2013w2k12r2/PuneetsLibrary/Inside as excluded, then
				 * we check if we are watching the parent included folder in
				 * this profile. Also are we wathcing the folder in the new
				 * profile. If yes is the answer in both cases, we go ahead and
				 * process the folder.
				 *
				 */
                if (isWithinFolders(profile.getIncludedFolders(), folderToRemove)
                        && isWithinFolders(upToDateProfile.getIncludedFolders(), folderToRemove)) {
                    logger.debug(String.format("Parent folder of (%s) is still within the included folder list. Folder about to be crawled %s",
                            folderToRemove, folderToRemove));
                    processFile(resolveFileMonitor(folderToRemove), fileSystemManager.resolveFile(folderToRemove), NextLabsConstant.ACTION_ADD, true);
                }
            }

            logger.debug(String.format("Excluded Folders updated[%s] = " + profile.getExcludedFolders(), profile.getExcludedFolders().size()));
        }
        logger.debug("Reload of ExcludedFolders set completed.");
    }

    /**
     * File path loaded from database configuration contains extra header, strip
     * it off
     *
     * @param filename which is in apache vfs file object format
     * @return absolute file name which stripped off apache vfs specific
     * information
     */
    private String getAbsoluteFilePath(String filename) {
        if (filename.startsWith(SCConstant.FILE_PREFIX)) {
            return filename.substring(SCConstant.FILE_PREFIX.length());
        }

        return filename;
    }

    /**
     * Check if given path is sub folder of a collection of parent folder path
     *
     * @param folderList Parent folder path
     * @param path       Folder path to check if it is a child folder
     * @return true if it belongs to child of any parent folder
     */
    private boolean isWithinFolders(Set<String> folderList, String path) {
        for (String parentFolder : folderList) {
            if (path.toLowerCase().startsWith(parentFolder.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check processing status of given file path in Solr indexer.
     *
     * @param filePath                for checking. Primary key in Solr indexer.
     * @param lastModifiedMillisecond of filePath object.
     * @return true if given file path exist in database with same
     * lastModifiedDate.
     */
    private boolean isDocumentAlreadyProcessed(String filePath, long lastModifiedMillisecond) {
        logger.debug("Checking process status for " + filePath + " with last modified timestamp " + lastModifiedMillisecond);
        boolean isProcessed = false;

        try {
            long indexerLastModified = indexQueryEngine.getLastModifiedDateMillisecond(filePath);
            logger.debug("Indexer last modified date: " + indexerLastModified);
            logger.debug("File last modified date:    " + lastModifiedMillisecond);
            isProcessed = (indexerLastModified == lastModifiedMillisecond);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }

        logger.debug("Is processed: " + isProcessed);
        return isProcessed;
    }

    protected void sendToJMS(FileObject fileObject, String action) throws Exception {
    	
    	StringBuffer sBuf = new StringBuffer();

        Path parentPath = Paths.get(VFSUtil.getAbsoluteFilePath(fileObject.getParent()));

        if (isASharePointFolder(parentPath)) {

            String siteURL = profile.getSiteByFolders().get(parentPath);

            if (siteURL == null) {
                profile.reloadSharePointFolders(sessionFactory);
                siteURL = profile.getSiteByFolders().get(parentPath);
                if (siteURL == null) {
                    throw new Exception("siteURL was not found for " + parentPath);
                }
            }

            String repoFolderPath = profile.getRepoPathBySite().get(siteURL);
            if (repoFolderPath == null) {
                throw new Exception("Repo path was not found for " + siteURL);
            }
            sBuf.append("Sending the file ").append(fileObject.getName()).append(" of type SHAREPOINT to JMS queue for ").append(action).append(".\n");
            logger.info(sBuf);
            sBuf.setLength(0);
            messageSender.send(fileObject, action, RepositoryType.SHAREPOINT, siteURL, repoFolderPath);
        } else {
            String parentPathString = parentPath.toString();
            if (parentPathString.charAt(parentPathString.length() - 1) != File.separatorChar) {
                parentPathString += File.separator;
            }
            
            sBuf.append("Sending the file ").append(fileObject.getName()).append(" of type SHARED_FOLDER to JMS queue for ").append(action).append(".\n");
            logger.info(sBuf);
            sBuf.setLength(0);
            messageSender.send(fileObject, action, RepositoryType.SHARED_FOLDER, null, RepositoryUtil.getRepoPath(parentPathString));
        }
    }

    private boolean isASharePointFolder(Path parentPath) {
        return parentPath.toString().toLowerCase().contains(DAVWWWROOT);
    }
    
    private boolean isASharePointFolder(String path) {
        return path.toLowerCase().contains(DAVWWWROOT);
    }

    protected void printSummary() throws Exception {
        logger.info("Total included folder entry  : " + profile.getIncludedFolders().size());
        logger.info("Total excluded folder entry  : " + profile.getExcludedFolders().size());
        logger.info("Total imaginary folder entry : " + includePaths.size());
        logger.info("Total folders registered     : " + registeredFolderCount);
        logger.info("Total files registered       : " + registeredFileCount);
        logger.info("Total files encountered      : " + encounteredFileCount);

        for(String imaginaryPath : includePaths) {
        	logger.info("Imaginary path: " + imaginaryPath);
        }
        
        for (MyFileMonitor fileMonitor : fileMonitors) {
            logger.info("[" + fileMonitor.getMonitorId() + "]: File monitor map size: " + fileMonitor.getMonitorMapSize());
        }

        for (Entry<String, MyFileMonitor> fileMonitorEntry : fileMonitorByFolderName.entrySet()) {
            logger.debug("Entry path: " + fileMonitorEntry.getKey() + "; FileMonitor: " + fileMonitorEntry.getValue().getMonitorId());
        }
    }

    /**
     * Select the file monitor with the least monitoring items to load balance
     *
     * @return File monitor to be use.
     */
    protected MyFileMonitor selectFileMonitor() {
        if (fileMonitors != null && fileMonitors.length > 0) {
            MyFileMonitor candidate = fileMonitors[0];

            for (MyFileMonitor fileMonitor : fileMonitors) {
                if (fileMonitor.getMonitorMapSize() < candidate.getMonitorMapSize()) {
                    candidate = fileMonitor;
                }
            }

            return candidate;
        }

        return null;
    }

    /**
     * Get file monitor from registered include path mapping
     *
     * @param filePath File path to be added into file monitor
     * @return file monitor which monitoring the entry start with the given file
     * path <br>
     * null if no file monitor object available
     */
    protected MyFileMonitor resolveFileMonitor(String filePath) {
        for (String includePath : fileMonitorByFolderName.keySet()) {
            if (filePath.toLowerCase().startsWith(includePath.toLowerCase())) {
                return fileMonitorByFolderName.get(includePath);
            }
        }

        return null;
    }

    protected synchronized void dumpToFile() {
        CSVWriter csvWriter = null;

        try {
            logger.info("Start writing recovery data dump: " + new Date());
            String dumpFileName = NextLabsConstant.NXL_DATA_DUMP_PATH.concat("dataDump.temp");
            File tempFile = new File(dumpFileName);
            File dataDumpFile = new File(NextLabsConstant.NXL_DATA_DUMP_PATH.concat(NextLabsConstant.DATA_DUMP_FILE));
            tempFile.getParentFile().mkdir();

            if (tempFile.exists()) {
                tempFile.delete();
            }
            if (dataDumpFile.exists()) {
                dataDumpFile.delete();
            }

            csvWriter = new CSVWriter(new OutputStreamWriter(new FileOutputStream(tempFile), Charset.forName("UTF-8").newEncoder()),
                    Punctuation.GENERAL_DELIMITER.charAt(0), Punctuation.DOUBLE_QUOTE.charAt(0));
            csvWriter.writeNext(new String[]{"ComponentType:FileWatcher"}, true);
            csvWriter.writeNext(new String[]{"Hostname:" + getHostname()}, true);

            dumpRepositoryInfo(csvWriter);
            dumpFileMonitorInfo(csvWriter);

            csvWriter.flush();
            csvWriter.close();
            csvWriter = null;

            logger.info("Rename data dump file successful? " + tempFile.renameTo(dataDumpFile));
            logger.info("Data dump file complete: " + new Date());
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        } finally {
            if (csvWriter != null) {
                try {
                    csvWriter.close();
                } catch (IOException err) {
                    logger.error(err.getMessage(), err);
                }
            }
        }
    }

    protected void dumpRepositoryInfo(CSVWriter csvWriter) 
    		throws IOException {
        String[] tag = new String[]{INCLUDE_FOLDER_START_TAG};
        csvWriter.writeNext(tag, true);
        if (profile != null && profile.getIncludedFolders() != null && profile.getIncludedFolders().size() > 0) {
            String[] info = new String[1];
            for (String includeFolderPath : profile.getIncludedFolders()) {
                info[0] = includeFolderPath;
                csvWriter.writeNext(info, true);
            }
        }
        tag[0] = INCLUDE_FOLDER_END_TAG;
        csvWriter.writeNext(tag, true);

        tag[0] = EXCLUDE_FOLDER_START_TAG;
        csvWriter.writeNext(tag, true);
        if (profile != null && profile.getExcludedFolders() != null && profile.getExcludedFolders().size() > 0) {
            String[] info = new String[1];
            for (String excludeFolderPath : profile.getExcludedFolders()) {
                info[0] = excludeFolderPath;
                csvWriter.writeNext(info, true);
            }
        }
        tag[0] = EXCLUDE_FOLDER_END_TAG;
        csvWriter.writeNext(tag, true);

        tag[0] = ROOT_FOLDER_START_TAG;
        csvWriter.writeNext(tag, true);
        if (profile != null && profile.getParentByExcludedFolder() != null && profile.getParentByExcludedFolder().size() > 0) {
            String[] info = new String[2];
            for (Entry<String, String> entry : profile.getParentByExcludedFolder().entrySet()) {
                info[0] = entry.getKey();
                info[1] = entry.getValue();
                csvWriter.writeNext(info, true);
            }
        }
        tag[0] = ROOT_FOLDER_END_TAG;
        csvWriter.writeNext(tag, true);
    }

    protected void dumpFileMonitorInfo(CSVWriter csvWriter) 
    		throws IOException {
        if (fileMonitors != null) {
            String[] fileMonitorStartTag = new String[]{FILE_MONITOR_START_TAG};
            String[] fileMonitorEndTag = new String[]{FILE_MONITOR_END_TAG};
            String[] mainPathStartTag = new String[]{FILE_OBJECT_START_TAG};
            String[] mainPathEndTag = new String[]{FILE_OBJECT_END_TAG};
            String[] fileInfo = new String[3];

            for (MyFileMonitor fileMonitor : fileMonitors) {
                if (fileMonitor.getFileObjects().size() > 0) {
                    logger.info("Monitor object size: " + fileMonitor.getFileObjects().size());
                    csvWriter.writeNext(fileMonitorStartTag, true);
                    for (Entry<String, MyFileMonitor> fileMonitorEntry : fileMonitorByFolderName.entrySet()) {
                        if (fileMonitorEntry.getValue().getMonitorId().equals(fileMonitor.getMonitorId())) {
                            csvWriter.writeNext(new String[]{fileMonitorEntry.getKey()}, true);
                            includePaths.remove(fileMonitorEntry.getKey());
                        }
                    }

                    csvWriter.writeNext(mainPathStartTag, true);
                    Iterator<Entry<String, NextLabsFileObject>> fileIterator = fileMonitor.getFileObjects().entrySet().iterator();
                    while (fileIterator.hasNext()) {
                        Entry<String, NextLabsFileObject> fileObject = fileIterator.next();
                        fileInfo[0] = fileObject.getValue().getFileType().name();
                        fileInfo[1] = fileObject.getKey();
                        fileInfo[2] = Long.toString(fileObject.getValue().getTimestamp());

                        csvWriter.writeNext(fileInfo, true);
                    }
                    csvWriter.writeNext(mainPathEndTag, true);
                    csvWriter.writeNext(fileMonitorEndTag, true);
                }
            }
            
            for (String includePath : includePaths){
            	csvWriter.writeNext(fileMonitorStartTag, true);
            	csvWriter.writeNext(new String[]{includePath}, true);
            	csvWriter.writeNext(mainPathStartTag, true);
            	csvWriter.writeNext(new String[]{includePath}, true);
            	csvWriter.writeNext(mainPathEndTag, true);
                csvWriter.writeNext(fileMonitorEndTag, true);
            }
        }
    }
    
    protected void validateDumpFile(String componentType, String hostname) 
    		throws IllegalStateException {
        if (!("ComponentType:FileWatcher".equals(componentType) && ("Hostname:" + getHostname()).equals(hostname))) {
            throw new IllegalStateException("Invalid data dump file. Component type or hostname doesn't match.");
        }
    }

    protected void recoverObject(Segment dataSegment, String[] data, MyFileMonitor fileMonitor, Set<String> recoveredIncludeFolders,
                                 Set<String> recoveredExcludeFolders, Map<String, String> recoveredRootFolders, Map<String, NextLabsFileObject> childrenByParentFile) 
            throws Exception {
        switch (dataSegment) {
            case INCLUDE_FOLDER:
                recoveredIncludeFolders.add(data[0]);
                break;

            case EXCLUDE_FOLDER:
                recoveredExcludeFolders.add(data[0]);
                break;

            case ROOT_FOLDER:
                recoveredRootFolders.put(data[0], data[1]);
                break;

            case FILE_MONITOR:
                fileMonitorByFolderName.put(data[0], fileMonitor);
                break;

            case FILE_OBJECT:
                if (data.length == 3) {
                	try {
                		logger.debug("Recovering entry " + data[1]);

            			try {
                			FileObject fileObject = fileSystemManager.resolveFile(data[1]);
                			FileType fileType = FileType.valueOf(data[0]);
                			RepositoryType repoType = isASharePointFolder(data[1]) ? RepositoryType.SHAREPOINT : RepositoryType.SHARED_FOLDER;
                			NextLabsFileObject nxlFileObject;
                			
                			if(RepositoryType.SHAREPOINT.equals(repoType)) {
                                String absolutePathString = fileType.equals(FileType.FOLDER) ? 
                                		VFSUtil.getAbsoluteFilePath(fileObject) : 
                                			VFSUtil.getAbsoluteFilePath(fileObject.getParent());
                                String repositoryURL = getSiteURL(Paths.get(absolutePathString));
                                nxlFileObject = new NextLabsFileObject(fileObject, repoType, repositoryURL, fileType, Long.parseLong(data[2]));
                			} else {
                				nxlFileObject = new NextLabsFileObject(fileObject, repoType, null, fileType, Long.parseLong(data[2]));
                			}
                			
                			childrenByParentFile.put(data[1], nxlFileObject);
               				
                    		if(fileType.equals(FileType.FOLDER)) {
                    			includePaths.remove(data[1].endsWith(FORWARD_SLASH) ? data[1] : data[1] + FORWARD_SLASH);
                                synchronized (registeredFolderCount) {
                                    registeredFolderCount++;
                                }
                    		} else if(fileType.equals(FileType.FILE)) {
                                synchronized (encounteredFileCount) {
                                    encounteredFileCount++;
                                }

                                synchronized (registeredFileCount) {
                                    registeredFileCount++;
                                }
                    		} else if(fileType.equals(FileType.IMAGINARY)) {
                    			includePaths.remove(data[1].endsWith(FORWARD_SLASH) ? data[1] : data[1] + FORWARD_SLASH);
                    		}
                    		
                			// To handle when the parent is host itself for example \\BAESC01W12R2\share
                    		try {
	                			if(fileObject.getParent() != null) {
	                    			String parent = fileObject.getParent().getName().toString();
									if(childrenByParentFile.containsKey(parent)) {
	                    				childrenByParentFile.get(parent).addChild(data[1]);
									}
	                			}
                    		} catch(NullPointerException npe) {
                    			// Ignore this NPE since this is expected
                    		}
            			} catch(Exception err) {
            				logger.error(err.getMessage(), err);
            			}
            			
                	} catch(Exception fse) {
                		logger.warn("Unable to process entry " + data[1]);
                	}
                	
                	logger.debug("Recovered entry " + data[1]);
                }
                break;
            
            default:
                break;
        }
    }
}

class MyFileMonitor extends NextlabsFileMonitor {

    private String monitorId;

    public MyFileMonitor(Watcher watcher, FileListener listener, String monitorId) {
        super(watcher, listener, monitorId);
        this.monitorId = monitorId;
    }

    public String getMonitorId() {
        return this.monitorId;
    }
}

class MyFileListener implements FileListener {
    private static final Logger logger = LogManager.getLogger(MyFileListener.class);

    static final String TEMP_FILE = "~$";

    private Watcher watcher;
    private MyFileMonitor fileMonitor;

    public MyFileListener(final Watcher watcher) {
        super();
        this.watcher = watcher;
    }

    public void setFileMonitor(MyFileMonitor fileMonitor) {
        this.fileMonitor = fileMonitor;
    }

    @Override
    public void fileChanged(FileChangeEvent event) throws Exception {
        FileObject file = event.getFile();

        if (!file.getName().getBaseName().startsWith(TEMP_FILE)) {
            logger.debug(file.getName().getURI() + " changed.");
            watcher.processFile(fileMonitor, file, NextLabsConstant.ACTION_UPDATE, false);
        }
    }

    @Override
    public void fileCreated(FileChangeEvent event) throws Exception {
        FileObject file = event.getFile();

        if (!file.getName().getBaseName().startsWith(TEMP_FILE)) {
            logger.debug(file.getName().getURI() + " created.");
            watcher.processFile(fileMonitor, file, NextLabsConstant.ACTION_ADD, true);
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent event) throws Exception {
        FileObject file = event.getFile();

        if (!file.getName().getBaseName().startsWith(TEMP_FILE)
        		&& this.watcher.isRootEntryAccessible(file.getName().toString(), true)) {
            logger.debug(file.getName().getURI() + " deleted.");
            watcher.processFile(fileMonitor, file, NextLabsConstant.ACTION_DELETE, false);
        }
    }
}

enum Segment {
    INCLUDE_FOLDER, EXCLUDE_FOLDER, ROOT_FOLDER, FILE_MONITOR, FILE_OBJECT
}
