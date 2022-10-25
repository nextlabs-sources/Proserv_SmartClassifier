/*
	 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nextlabs.smartclassifier;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.util.VFSUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.CacheStrategy;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileMonitor;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.cache.NullFilesCache;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.AbstractFileSystem;
import org.apache.commons.vfs2.provider.local.DefaultLocalFileProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.nextlabs.smartclassifier.constant.Punctuation.FORWARD_SLASH;

/**
 * A polling {@link FileMonitor} implementation.<br />
 * <br />
 * The DefaultFileMonitor is a Thread based polling file system monitor with a 1
 * second delay.<br />
 * <br />
 * <b>Design:</b>
 * <p>
 * There is a Map of monitors known as FileMonitorAgents. With the thread running,
 * each FileMonitorAgent object is asked to "check" on the file it is
 * responsible for.
 * To do this check, the cache is cleared.
 * </p>
 * <ul>
 * <li>If the file existed before the refresh and it no longer exists, a delete
 * event is fired.</li>
 * <li>If the file existed before the refresh and it still exists, check	 the
 * last modified timestamp to see if that has changed.</li>
 * <li>If it has, fire a change event.</li>
 * </ul>
 * <p>
 * With each file delete, the FileMonitorAgent of the parent is asked to
 * re-build its
 * list of children, so that they can be accurately checked when there are new
 * children.<br/>
 * New files are detected during each "check" as each file does a check for new
 * children.
 * If new children are found, create events are fired recursively if recursive
 * descent is
 * enabled.
 * </p>
 * <p>
 * For performance reasons, added a delay that increases as the number of files
 * monitored
 * increases. The default is a delay of 1 second for every 1000 files processed.
 * </p>
 * <br /><b>Example usage:</b><br /><pre>
 * FileSystemManager fsManager = VFS.getManager();
 * FileObject listendir = fsManager.resolveFile("/home/username/monitored/");
 * <p/>
 * DefaultFileMonitor fm = new DefaultFileMonitor(new CustomFileListener());
 * fm.setRecursive(true);
 * fm.addFile(listendir);
 * fm.start();
 * </pre>
 * <i>(where CustomFileListener is a class that implements the FileListener
 * interface.)</i>
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public class NextlabsFileMonitor implements Runnable, FileMonitor {
    public static final String TEMP_FILE = "~$";
    private static final Log logger = LogFactory.getLog(NextlabsFileMonitor.class);
    private static final long DEFAULT_DELAY = 1000;
    private static final long DEFAULT_SPLIT = 15000;
    private static final int DEFAULT_MAX_FILES = 1000;
    private static final String URL_SCHEME = "file";
    private static final String DAVWWWROOT = "davwwwroot";
    static DefaultFileSystemManager fileSystemManager;
    /**
     * File objects to be removed from the monitor map.
     */
    private final Stack<FileObject> deleteStack = new Stack<>();
    /**
     * File objects to be added to the monitor map.
     */
    private final Stack<FileObject> addStack = new Stack<>();
    /**
     * A listener object that if set, is notified on file creation and deletion.
     */
    private final FileListener listener;
    /**
     * Remove List Stack means
     */
    private final Stack<String> removeListStack = new Stack<>();
    /**
     * Map from FileName to FileObject being monitored.
     */
    private final Map<String, NextLabsFileObject> fileNameToNextLabsFileObjectMap = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
    /**
     * The low priority thread used for checking the files being monitored.
     */
    private Thread monitorThread;
    /**
     * A flag used to determine if the monitor thread should be running. If set to false, the monitor thread stops
     */
    private volatile boolean shouldRun = true; // used for inter-thread communication
    /**
     * A flag used to determine if adding files to be monitored should be recursive.
     */
    private boolean recursive;
    /**
     * Set the delay between checks
     */
    private long delay = DEFAULT_DELAY;
    /**
     * Set the split time before conclude file has been updated
     */
    private long split = DEFAULT_SPLIT;
    /**
     * Set the number of files to check until a delay will be inserted
     */
    private int checksPerRun = DEFAULT_MAX_FILES;
    private Set<String> includeFileType = null;
    /**
     * Remove Paths stands for excluded paths
     */
    private List<String> removePaths = new ArrayList<>();
    private String monitorThreadName;
    /**
     * Watcher object this belongs to
     */
    private Watcher watcher;

    public NextlabsFileMonitor(final Watcher watcher, final FileListener listener, final String monitorThreadName) {
        this.watcher = watcher;
    	this.listener = listener;
        this.monitorThreadName = monitorThreadName;
        initFileManager();
    }

    private void initFileManager() {
        fileSystemManager = new DefaultFileSystemManager();
        try {
            fileSystemManager.addProvider(URL_SCHEME, new DefaultLocalFileProvider());
            fileSystemManager.setCacheStrategy(CacheStrategy.ON_CALL);
            fileSystemManager.setFilesCache(new NullFilesCache());
            fileSystemManager.init();
        } catch (FileSystemException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Access method to get the recursive setting when adding files for monitoring.
     *
     * @return true if monitoring is enabled for children.
     */
    public boolean isRecursive() {
        return this.recursive;
    }

    /**
     * Access method to set the recursive setting when adding files for monitoring.
     *
     * @param newRecursive true if monitoring should be enabled for children.
     */
    public void setRecursive(final boolean newRecursive) {
        this.recursive = newRecursive;
    }

    public Set<String> getIncludeFileType() {
        return includeFileType;
    }

    public void setIncludeFileType(final Set<String> includeFileType) {
        this.includeFileType = includeFileType;
    }

    public List<String> getRemovePaths() {
        return removePaths;
    }

    public void setRemovePaths(final List<String> removePathList) {
        this.removePaths = removePathList;
    }

    /**
     * Access method to get the current FileListener object notified when there
     * are changes with the files added.
     *
     * @return The FileListener.
     */
    FileListener getFileListener() {
        return this.listener;
    }

    /**
     * Adds a file to be monitored.
     *
     * @param file The FileObject to monitor.
     */
    public void addFile(final FileObject file) {
        doAddFile(file);
    }

    public void addFile(final NextLabsFileObject nxlFileObject) {
    	try {
    		String fileName = nxlFileObject.getsFileName();
    		synchronized (this.fileNameToNextLabsFileObjectMap) {
    			if (!this.fileNameToNextLabsFileObjectMap.containsKey(fileName)) {
    				this.fileNameToNextLabsFileObjectMap.put(fileName, nxlFileObject);
    			}
    		}
    	} catch(Exception ex) {
    		logger.error(ex.toString(), ex);
    	}
    }
    
    /**
     * Adds a file to be monitored.
     *
     * @param file The FileObject to add.
     */
    private void doAddFile(FileObject file) {

        try {
            String fileName = file.getName().toString();

            synchronized (this.fileNameToNextLabsFileObjectMap) {
                if (!this.fileNameToNextLabsFileObjectMap.containsKey(fileName)) {
                    NextLabsFileObject nxlObj;

                    if (fileName.toLowerCase().contains(DAVWWWROOT)) {

                        // trying to get the repository url
                        boolean isAFolder = (file.getType() == FileType.FOLDER);

                        String absolutePathString = isAFolder ? VFSUtil.getAbsoluteFilePath(file) : VFSUtil.getAbsoluteFilePath(file.getParent());
                        Path key = Paths.get(absolutePathString);
                        String repositoryURL = Watcher.getSiteURL(key);

                        nxlObj = new NextLabsFileObject(file, RepositoryType.SHAREPOINT, repositoryURL);
                    } else {
                        nxlObj = new NextLabsFileObject(file, RepositoryType.SHARED_FOLDER, null);
                    }

                    resetChildrenList(nxlObj);
                    this.fileNameToNextLabsFileObjectMap.put(fileName, nxlObj);
                }
            }

        } catch (Exception ex) {
            logger.error(ex.toString(), ex);
        }
    }

    /**
     * Removes a file from being monitored.
     *
     * @param file The FileObject to remove from monitoring.
     */
    public void removeFile(final FileObject file) {
        String fileName = file.getName().toString();

        // Only proceed to remove file if this is not a root entry
        if(!watcher.isRootEntry(fileName)) {
	        synchronized (this.fileNameToNextLabsFileObjectMap) {
	            if (this.fileNameToNextLabsFileObjectMap.containsKey(fileName)) {
	            	if(watcher.isRootEntryAccessible(fileName, true)) {
		                FileObject parent;
		                try {
		                    parent = file.getParent();
		                } catch (FileSystemException | NullPointerException fse) {
		                    parent = null;
		                }

		                this.fileNameToNextLabsFileObjectMap.remove(fileName);
		
		                if (parent != null) { // Not the root
		                    NextLabsFileObject parentNextLabsObject = this.fileNameToNextLabsFileObjectMap.get(parent.getName().toString());
		                    if (parentNextLabsObject != null) {
		                        parentNextLabsObject.getChildren().remove(fileName);
		                    }
		                }
	            	} else {
	            		logger.debug("Network connection issue, ignoring " + fileName + " get deleted event.");
	            	}
	            }
	        }
        } else {
        	logger.info("Keep root entry " + fileName);
        }
    }

    /**
     * Queues a file for removal from being monitored.
     *
     * @param file The FileObject to be removed from being monitored.
     */
    protected void queueRemoveFile(final FileObject file) {
        this.deleteStack.push(file);
    }

    /**
     * Get the delay between runs.
     *
     * @return The delay period.
     */
    public long getDelay() {
        return delay;
    }

    /**
     * Set the delay between runs.
     *
     * @param delay The delay period.
     */
    public void setDelay(long delay) {
        if (delay > 0) {
            this.delay = delay;
        } else {
            this.delay = DEFAULT_DELAY;
        }
    }

    public long getSplit() {
        return split;
    }

    public void setSplit(long split) {
        if (split > 0) {
            this.split = split;
        } else {
            this.split = DEFAULT_SPLIT;
        }
    }


    /**
     * get the number of files to check per run.
     *
     * @return The number of files to check per iteration.
     */
    public int getChecksPerRun() {
        return checksPerRun;
    }

    /**
     * set the number of files to check per run.
     * a additional delay will be added if there are more files to check
     *
     * @param checksPerRun a value less than 1 will disable this feature
     */
    public void setChecksPerRun(int checksPerRun) {
        this.checksPerRun = checksPerRun;
    }

    /**
     * Queues a file for addition to be monitored.
     *
     * @param file The FileObject to add.
     */
    protected void queueAddFile(final FileObject file) {
        this.addStack.push(file);
    }


    public void queueAddRemovePath(String sPath) {
        this.removeListStack.push(sPath);
    }


    /**
     * Starts monitoring the files that have been added.
     */
    public void start() {
        if (this.monitorThread == null) {
            this.monitorThread = new Thread(this, monitorThreadName);
            this.monitorThread.setDaemon(true);
            this.monitorThread.setPriority(Thread.MIN_PRIORITY);
        }
        this.monitorThread.start();
    }

    /**
     * Stops monitoring the files that have been added.
     */
    public void stop() {
        this.shouldRun = false;
    }

    public long getMonitorMapSize() {
        return this.fileNameToNextLabsFileObjectMap.size();
    }

    /**
     * Asks the agent for each file being monitored to check its file for changes.
     */
    public void run() {

        mainloop:
        while (!monitorThread.isInterrupted() && this.shouldRun) {

            try {
                while (!this.deleteStack.empty()) {
                    this.removeFile(this.deleteStack.pop());
                }

                int iterator = 0;

                removePaths.clear();
                while (!this.removeListStack.empty()) {
                    removePaths.add(this.removeListStack.pop());
                }

                // for all the files/folders in this map
                for (String fileName : fileNameToNextLabsFileObjectMap.keySet()) {

                    // why is this needed?
                    while (!this.deleteStack.empty()) {
                        this.removeFile(this.deleteStack.pop());
                    }

                    NextLabsFileObject nextLabsFileObject;

                    synchronized (this.fileNameToNextLabsFileObjectMap) {
                        nextLabsFileObject = this.fileNameToNextLabsFileObjectMap.get(fileName);
                    }
                    if (nextLabsFileObject != null) {
                        check(nextLabsFileObject);
                    }

                    // every 1000 files scanned, sleep for 1 second
                    if (getChecksPerRun() > 0) {
                        if ((iterator % getChecksPerRun()) == 0) {
                            try {
                                Thread.sleep(getDelay());
                                iterator = 0;
                            } catch (InterruptedException e) {
                                // Woke up.
                            }
                        }
                    }

                    // if interrupted or stopped, jump to loop start and quit. don't "check()" any more files
                    if (monitorThread.isInterrupted() || !this.shouldRun) {
                        continue mainloop;
                    }

                    iterator++;
                }

                while (!this.addStack.empty()) {
                    FileObject f = this.addStack.pop();

                    if (includeFileType.contains(f.getName().getExtension().toLowerCase()) && (!f.getName().getBaseName().startsWith(TEMP_FILE)))
                        this.addFile(f);
                }

                try {
                    Thread.sleep(getDelay());
                } catch (InterruptedException e) {
                    // continue();
                }

            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        this.shouldRun = true;
    }

    public Map<String, NextLabsFileObject> getFileObjects() {
        return this.fileNameToNextLabsFileObjectMap;
    }

    void check(NextLabsFileObject nextLabsFileObject) {

        FileObject fileObj;
        try {
            fileObj = fileSystemManager.resolveFile(nextLabsFileObject.getsFileName());
        } catch (FileSystemException e) {
        	if(!nextLabsFileObject.isRecoveredObject()) {
        		logger.error(e.getLocalizedMessage(), e);
        	}
            return;
        }

        nextLabsFileObject.setRecoveredObject(false);
        try {

            // If the file/folder existed then and now it doesn't - it was deleted
            if (nextLabsFileObject.exists() && !fileObj.exists()) {
            	if(watcher.isRootEntryAccessible(nextLabsFileObject.getsFileName(), true)) {
	                nextLabsFileObject.setExists(fileObj.exists());
	
	                // Add our listener before firing event
	                if (fileObj.getFileSystem() != null) {
	                    fileObj.getFileSystem().addListener(fileObj, this.listener);
	                }
	
	                // Fire delete event
	                ((AbstractFileSystem) fileObj.getFileSystem()).fireFileDeleted(fileObj);
	
	                // Remove listener in case file is re-created. Don't want to fire twice.
	                // pkalra: This says in case the file is recreated, we will add the listener then. Need to say if we do this.
	                if (fileObj.getFileSystem() != null) {
	                    fileObj.getFileSystem().removeListener(fileObj, this.listener);
	                }
	
	                // Remove from map - this will update the map used in "run()"
	                queueRemoveFile(fileObj);
            	} else {
            		logger.debug("Network connection issue, ignoring " + nextLabsFileObject.getsFileName() + " get deleted event.");
            	}
            } else if (nextLabsFileObject.exists() && fileObj.exists()) { // update action ?
                if (!isInRemovePaths(fileObj)) {
                    // Check the timestamp to see if it has been modified
                    if (nextLabsFileObject.getTimestamp() != fileObj.getContent().getLastModifiedTime()
                            && (System.currentTimeMillis() - fileObj.getContent().getLastModifiedTime()) >= this.split) {
                        nextLabsFileObject.setTimestamp(fileObj.getContent().getLastModifiedTime());
                        // Fire change event
                        // Don't fire if it's a folder because new file children
                        // and deleted files in a folder have their own event triggered.
                        if (!fileObj.getType().hasChildren()) {
                            // Add listener to fire event
                            if (fileObj.getFileSystem() != null) {
                                fileObj.getFileSystem().addListener(fileObj, this.listener);
                            }

                            ((AbstractFileSystem)
                                    fileObj.getFileSystem()).fireFileChanged(fileObj);

                            // Remove listener in case file is re-created. Don't want to fire twice.
                            if (fileObj.getFileSystem() != null) {
                                fileObj.getFileSystem().removeListener(fileObj, this.listener);
                            }
                        }
                    }
                } else { // File is now in excluded paths - delete it

                    nextLabsFileObject.setExists(false);

                    // Add listener to fire event
                    if (fileObj.getFileSystem() != null) {
                        fileObj.getFileSystem().addListener(fileObj, this.listener);
                    }

                    // Fire delete event
                    ((AbstractFileSystem)
                            fileObj.getFileSystem()).fireFileDeleted(fileObj);

                    // Remove listener in case file is re-created. Don't want to fire twice.
                    if (fileObj.getFileSystem() != null) {
                        fileObj.getFileSystem().removeListener(fileObj, this.listener);
                    }

                    // Remove from map
                    queueRemoveFile(fileObj);
                }

            } else if (!nextLabsFileObject.exists() && fileObj.exists()) //New file detected
            {
                nextLabsFileObject.setExists(fileObj.exists());
                nextLabsFileObject.setTimestamp(fileObj.getContent().getLastModifiedTime());
                // Don't fire if it's a folder because new file children
                // and deleted files in a folder have their own event triggered.
                if (!fileObj.getType().hasChildren()) {
                    // Add listener to fire event
                    if (fileObj.getFileSystem() != null) {
                        fileObj.getFileSystem().addListener(fileObj, this.listener);
                    }
                    ((AbstractFileSystem)
                            fileObj.getFileSystem()).fireFileCreated(fileObj);

                    // Remove listener in case file is re-created. Don't want to fire twice.
                    if (fileObj.getFileSystem() != null) {
                        fileObj.getFileSystem().removeListener(fileObj, this.listener);
                    }
                }
            }

            if (fileObj.getType().hasChildren()) {
                if (!isInRemovePaths(fileObj)) {
                    this.checkForNewChildren(nextLabsFileObject);
                }
            }

            fileObj = null;

        } catch (FileSystemException fse) {
            logger.error(fse.getLocalizedMessage(), fse);
        }
    }

    /**
     * Only checks for new children. If children are removed, they'll
     * eventually be checked.
     */
    private void checkForNewChildren(NextLabsFileObject nxlFileObj) {

        FileObject fileObj = null;
        try {
            fileObj = fileSystemManager.resolveFile(nxlFileObj.getsFileName());
        } catch (FileSystemException e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        try {
            FileObject[] newChildren = (FileObject[]) fileObj.getChildren();

            if (nxlFileObj.getChildren() != null) {
                // See which new children are not listed in the current children
                // map.
                Set<String> newChildrenMap = new TreeSet<>(
                        String.CASE_INSENSITIVE_ORDER);
                Stack<FileObject> missingChildren = new Stack<FileObject>();

                for (int i = 0; i < newChildren.length; i++) {
                    String sFileName = newChildren[i].getName().toString();

                    if (newChildren[i].getType().hasChildren()) {

                        newChildrenMap.add(sFileName);

                        // If the child's not there
                        if (!nxlFileObj.getChildren().contains(sFileName)) {
                            missingChildren.push(newChildren[i]);
                        }
                    } else if (getIncludeFileType().contains(
                            newChildren[i].getName().getExtension()
                                    .toLowerCase())
                            && (!newChildren[i].getName().getBaseName()
                            .startsWith(TEMP_FILE))) {
                        newChildrenMap.add(sFileName);
                        // If the child's not there
                        if (!nxlFileObj.getChildren().contains(sFileName)) {
                            missingChildren.push(newChildren[i]);
                        }
                    }
                }
                nxlFileObj.setChildren(newChildrenMap);
                // If there were missing children
                if (!missingChildren.empty()) {
                    logger.trace("New Children Map = " + Arrays.toString(newChildrenMap.toArray()));
                    logger.trace("missingChildren =  " + Arrays.toString(missingChildren.toArray()));
                    while (!missingChildren.empty()) {
                        FileObject child = (FileObject) missingChildren.pop();
                        this.fireAllCreate(child);
                    }
                }

            } else {
                // First set of children - Break out the cigars
                if (newChildren.length > 0) {
                    nxlFileObj.setChildren(new TreeSet<String>(
                            String.CASE_INSENSITIVE_ORDER));

                    for (int i = 0; i < newChildren.length; i++) {
                        if (newChildren[i].getType().hasChildren()) {
                            nxlFileObj.getChildren().add(newChildren[i].getName()
                                    .toString());
                            this.fireAllCreate(newChildren[i]);
                        } else if (getIncludeFileType().contains(
                                newChildren[i].getName().getExtension()
                                        .toLowerCase())
                                && (!newChildren[i].getName().getBaseName()
                                .startsWith(TEMP_FILE))) {
                            nxlFileObj.getChildren().add(newChildren[i].getName()
                                    .toString());
                            this.fireAllCreate(newChildren[i]);
                        }
                    }
                }

            }

        } catch (FileSystemException fse) {
            logger.error(fse.getLocalizedMessage(), fse);
        }
    }

    /**
     * Recursively fires create events for all children if recursive descent is
     * enabled. Otherwise the create event is only fired for the initial
     * FileObject.
     *
     * @param fileObject The child to add.
     */
    private void fireAllCreate(FileObject fileObject) {
        // Add listener so that it can be triggered
        if (this.listener != null) {
            fileObject.getFileSystem().addListener(fileObject, this.listener);
        }

        ((AbstractFileSystem) fileObject.getFileSystem()).fireFileCreated(fileObject);

        if (this.listener != null) {
            fileObject.getFileSystem().removeListener(fileObject, this.listener);
        }

        queueAddFile(fileObject); // Add

        try {
            if (this.isRecursive()) {
                if (fileObject.getType().hasChildren()) {
                    FileObject[] newChildren = fileObject.getChildren();
                    for (FileObject newChild : newChildren) {
                        fireAllCreate(newChild);
                    }
                }
            }

        } catch (FileSystemException fse) {
            logger.error(fse.getLocalizedMessage(), fse);
        }
    }


    /**
     * Verify if given fileObject is in excluded path list.
     *
     * @param fileObject File Object for checking
     * @return true when filePath starts with excluded path list's value.
     * @throws FileSystemException
     */
    private boolean isInRemovePaths(FileObject fileObject) throws FileSystemException {

        String fileName = fileObject.getName().toString().toLowerCase();

        if (fileObject.getType().hasChildren()) {
            fileName = fileName + FORWARD_SLASH;
        }

        for (String excludedPath : getRemovePaths()) {
            if (fileName.startsWith(excludedPath.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Register children for folder
     *
     * @param nextLabsFileObject
     */
    private void resetChildrenList(NextLabsFileObject nextLabsFileObject) {
        try {
            FileObject fileObj;
            try {
                fileObj = fileSystemManager.resolveFile(nextLabsFileObject.getsFileName());
            } catch (FileSystemException e) {
                logger.error(e.getLocalizedMessage(), e);
                return;
            }

            // if the fileObj is a folder
            if (fileObj != null && fileObj.getType().hasChildren()) {

                nextLabsFileObject.setChildren(new TreeSet<>(String.CASE_INSENSITIVE_ORDER));
                FileObject[] childrenList = fileObj.getChildren();

                for (FileObject child : childrenList) {
                    // we only add file names, not the file to the children set.
                    if (child.getType().hasChildren()) {
                        nextLabsFileObject.getChildren().add(child.getName().toString());
                    } else if (getIncludeFileType().contains(child.getName().getExtension().toLowerCase()) && (!child.getName().getBaseName().startsWith(TEMP_FILE))) {
                        nextLabsFileObject.getChildren().add(child.getName().toString());
                    }
                }
            }
        } catch (FileSystemException fse) {
            nextLabsFileObject.setChildren(null);
        }
    }
}
