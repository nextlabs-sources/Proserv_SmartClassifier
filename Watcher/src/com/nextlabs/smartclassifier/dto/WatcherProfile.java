package com.nextlabs.smartclassifier.dto;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.entity.DocumentTypeAssociation;
import com.nextlabs.smartclassifier.database.entity.ExcludeRepoFolder;
import com.nextlabs.smartclassifier.database.entity.RepoFolder;
import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;
import com.nextlabs.smartclassifier.database.entity.Watcher;
import com.nextlabs.smartclassifier.database.manager.RepoFolderManager;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.util.HTTPClientUtil;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import com.nextlabs.smartclassifier.util.VFSUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class WatcherProfile extends BaseDTO {

    private static final Logger logger = LogManager.getLogger(WatcherProfile.class);
    /**
     * This contains the mapping from all sharepoint sites to the RepoFolder in watcher For example
     * many sites and sub sites may be contained within one Repo Folder mapping.
     */
    private final HashMap<String, String> repoPathBySite = new HashMap<>();
    /**
     * This contains the success status of loading for any repo folder of watcher. This is used
     * primarily to detect unavailable sharepoint directories. If unavailable, we should not remove
     * all the folders being watched. We remove them only when they are available and removed, when
     * unavailable, we let it be.
     */
    private final Map<String, Boolean> repoFolderLoadingStatus = new HashMap<>();
    private String name;
    private String hostname;
    private int fileMonitorCount;
    private Date configLoadedOn;
    private long configReloadInterval;
    private JMSConfig jmsConfig = new JMSConfig();
    private Set<String> includeDocumentExtensions =
            new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
    // new data structures for SharePoint
    private Set<String> includedFolders = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
    private Set<String> excludedFolders = new ConcurrentSkipListSet<>(String.CASE_INSENSITIVE_ORDER);
    private Map<String, String> parentByExcludedFolder =
            new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

    private Map<String, RepositoryType> repoTypeByPath = new ConcurrentSkipListMap<>();

    /**
     * This contains the mapping of all folders to their container site
     */
    private TreeMap<Path, String> siteByFolders = new TreeMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getFileMonitorCount() {
        return fileMonitorCount;
    }

    public void setFileMonitorCount(int fileMonitorCount) {
        this.fileMonitorCount = fileMonitorCount;
    }

    public Date getConfigLoadedOn() {
        return configLoadedOn;
    }

    public void setConfigLoadedOn(Date configLoadedOn) {
        this.configLoadedOn = configLoadedOn;
    }

    public long getConfigReloadInterval() {
        return configReloadInterval * 1000;
    }

    public void setConfigReloadInterval(long configReloadInterval) {
        this.configReloadInterval = configReloadInterval;
    }

    public JMSConfig getJmsConfig() {
        return jmsConfig;
    }

    public void setJmsConfig(JMSConfig jmsConfig) {
        this.jmsConfig = jmsConfig;
    }

    public Set<String> getIncludedExtns() {
        return includeDocumentExtensions;
    }

    public void setIncludeDocumentExtensions(Set<String> includeDocumentExtensions) {
        this.includeDocumentExtensions = includeDocumentExtensions;
    }

    public Set<String> getIncludedFolders() {
        return includedFolders;
    }

    public void setIncludedFolders(Set<String> includedFolders) {
        this.includedFolders = includedFolders;
    }

    public Set<String> getExcludedFolders() {
        return excludedFolders;
    }

    public void setExcludedFolders(Set<String> excludedFolders) {
        this.excludedFolders = excludedFolders;
    }

    public Map<Path, String> getSiteByFolders() {
        return siteByFolders;
    }

    public void setSiteByFolders(TreeMap<Path, String> siteByFolders) {
        this.siteByFolders = siteByFolders;
    }

    public Map<String, String> getParentByExcludedFolder() {
        return parentByExcludedFolder;
    }

    public void setParentByExcludedFolder(Map<String, String> parentByExcludedFolder) {
        this.parentByExcludedFolder = parentByExcludedFolder;
    }

    public HashMap<String, String> getRepoPathBySite() {
        return repoPathBySite;
    }

    public Map<String, Boolean> getRepoFolderLoadingStatus() {
        return repoFolderLoadingStatus;
    }

    public void copy(Watcher watcher) {

        if (watcher != null) {
            this.id = watcher.getId();
            this.name = watcher.getName();
            this.hostname = watcher.getHostname();
            this.fileMonitorCount = watcher.getFileMonitorCount() > 0 ? watcher.getFileMonitorCount() : 1;
            this.jmsConfig.copy(watcher.getJMSProfile());
            this.configLoadedOn = watcher.getConfigLoadedOn();
            this.configReloadInterval = watcher.getConfigReloadInterval();

            // included document extensions
            if (watcher.getDocumentTypeAssociations() != null) {
                for (DocumentTypeAssociation association : watcher.getDocumentTypeAssociations()) {
                    if (association.isInclude()) {
                        includeDocumentExtensions.add(association.getDocumentExtractor().getExtension());
                    }
                }
            }

            if (watcher.getRepoFolders() != null) {

                for (RepoFolder repoFolder : watcher.getRepoFolders()) {

                    RepositoryType repositoryType = RepositoryType.getRepositoryType(repoFolder.getRepositoryType());

                    repoTypeByPath.put(repoFolder.getPath(), repositoryType);

                    if (repositoryType == RepositoryType.SHAREPOINT) {

                        logger.debug(repoFolder.getPath() + " is a SharePoint Folder");

                        // get credentials for this sharepoint repository
                        SourceAuthentication sourceAuthentication = repoFolder.getSourceAuthentication();
                        CloseableHttpClient httpClient = HTTPClientUtil.getHTTPClient(sourceAuthentication.getUsername(), NxlCryptoUtil.decrypt(sourceAuthentication.getPassword()), sourceAuthentication.getDomainName());

                        String sharePointIncludedURL = repoFolder.getPath();
                        TreeMap<Path, String> sharePointFoldersToSiteURL = new TreeMap<>();
                        boolean isOperationSuccessful = true;

                        if (SharePointUtil.getSPFolders(
                                httpClient, sharePointIncludedURL, sharePointFoldersToSiteURL)) {
                            logger.debug("Successfully got the sharepoint folders");

                            // update the siteByFolders which will be a collection for all the repo folders
                            siteByFolders.putAll(sharePointFoldersToSiteURL);

                            // get all the sites and sub sites and map them to this repo folder
                            HashSet<String> sharePointSites = new HashSet<>(sharePointFoldersToSiteURL.values());
                            for (String siteURL : sharePointSites) {
                                repoPathBySite.put(siteURL, repoFolder.getPath());
                            }

                            logger.debug(
                                    "The <Site URL, Repo Folder> Map is as follows: " + repoPathBySite.size());
                            SharePointUtil.HashMapUtil.printMap(repoPathBySite);

                            TreeMap<Path, String> siteByDL = SharePointUtil.removeSubPathsFromMap(siteByFolders);

                            logger.debug("The <Document_Library, Site> Map is as follows: " + siteByDL.size());
                            SharePointUtil.HashMapUtil.printMap(siteByDL);

                            Set<Path> sharePointFoldersToBeIncluded = new TreeSet<>(siteByDL.keySet());
                            Set<Path> sharePointFoldersToBeExcluded = new TreeSet<>();

                            if (repoFolder.getExcludeRepoFolders() != null) {

                                // for all excluded folders, get the url, get the list of excluded folders, add it to the set
                                for (ExcludeRepoFolder excludeRepoFolder : repoFolder.getExcludeRepoFolders()) {
                                    logger.debug(
                                            "Trying to get excluded folders from URL = " + excludeRepoFolder.getPath());

                                    String sharePointExcludeURL = excludeRepoFolder.getPath();
                                    TreeMap<Path, String> excludedFoldersToSiteURLMap = new TreeMap<>();
                                    if (SharePointUtil.getSPFolders(httpClient, sharePointExcludeURL, excludedFoldersToSiteURLMap)) {
                                        logger.debug("Successfully got the sharepoint folders (Excluded folders)");

                                        sharePointFoldersToBeExcluded.addAll(excludedFoldersToSiteURLMap.keySet());
                                    } else {
                                        logger.error("Could not load excluded folders for " + repoFolder);
                                        repoFolderLoadingStatus.put(repoFolder.getPath(), false);
                                        isOperationSuccessful = false;
                                        break;
                                    }
                                }
                                if (!isOperationSuccessful) {
                                    continue;
                                }
                                // update included path set
                                sharePointFoldersToBeIncluded.removeAll(sharePointFoldersToBeExcluded);
                            }

                            for (Path includedFolderPath : sharePointFoldersToBeIncluded) {
                                String vfsIncludeFolderPath =
                                        VFSUtil.convert2VFSFormat(includedFolderPath.toString());
                                includedFolders.add(vfsIncludeFolderPath);
                            }

                            for (Path excludedFolderPath : sharePointFoldersToBeExcluded) {
                                String vfsExcludedFolderPath =
                                        VFSUtil.convert2VFSFormat(excludedFolderPath.toString());
                                excludedFolders.add(vfsExcludedFolderPath);
                            }

                            for (Path excludedFolderPath : sharePointFoldersToBeExcluded) {
                                for (Path includedFolderPath : sharePointFoldersToBeIncluded) {
                                    if (excludedFolderPath.startsWith(includedFolderPath)) {
                                        String vfsIncludeFolderPath =
                                                VFSUtil.convert2VFSFormat(includedFolderPath.toString());
                                        String vfsExcludedFolderPath =
                                                VFSUtil.convert2VFSFormat(excludedFolderPath.toString());
                                        parentByExcludedFolder.put(vfsExcludedFolderPath, vfsIncludeFolderPath);
                                    }
                                }
                            }

                            logger.debug("Successfully loaded " + repoFolder.getPath());
                            repoFolderLoadingStatus.put(repoFolder.getPath(), true);
                        } else {
              /*do not load anything*/
                            logger.error("Could not load " + repoFolder.getPath());
                            repoFolderLoadingStatus.put(repoFolder.getPath(), false);
              /* this indicates that operation returned false - because of a 400/500 HTTP status in which case
              do not reload configuration*/
                        }
                    } else if (repositoryType == RepositoryType.SHARED_FOLDER) {
                        logger.debug(repoFolder.getPath() + " is a Shared Windows Folder");
                        String vfsIncludeFolderPath = VFSUtil.convert2VFSFormat(repoFolder.getPath());
                        includedFolders.add(vfsIncludeFolderPath);
                        if (repoFolder.getExcludeRepoFolders() != null) {
                            for (ExcludeRepoFolder excludeRepoFolder : repoFolder.getExcludeRepoFolders()) {
                                String vfsExcludeFolderPath =
                                        VFSUtil.convert2VFSFormat(excludeRepoFolder.getPath());
                                parentByExcludedFolder.put(vfsExcludeFolderPath, vfsIncludeFolderPath);
                                excludedFolders.add(vfsExcludeFolderPath);
                            }
                        }
                        logger.debug("Successfully loaded " + repoFolder.getPath());
                        repoFolderLoadingStatus.put(repoFolder.getPath(), true);
                    }
                }

                logger.debug(String.format("Included_Folders[%s] = " + includedFolders, includedFolders.size()));
                logger.debug(String.format("Excluded_Folders[%s] = " + excludedFolders, excludedFolders.size()));
                logger.debug("The < Excluded_Folder, Parent_Folder > Map is as follows: " + parentByExcludedFolder.size());
                SharePointUtil.HashMapUtil.printMap(parentByExcludedFolder);
            }

            this.createdTimestamp = watcher.getCreatedOn();
            this.modifiedTimestamp = watcher.getModifiedOn();
        }
    }

    public void reloadSharePointFolders(SessionFactory sessionFactory) {

        Session session = null;
        Transaction transaction = null;

        logger.debug("Trying to reload SharePoint folders");

        for (String repoPath : repoTypeByPath.keySet()) {

            RepositoryType repositoryType = repoTypeByPath.get(repoPath);

            if (repositoryType == RepositoryType.SHAREPOINT) {

                logger.debug(repoPath + " is a SharePoint Folder");
                try {

                    session = sessionFactory.openSession();
                    transaction = session.beginTransaction();

                    RepoFolder repoFolder = new RepoFolderManager(sessionFactory, session).getSPRepoFolder(repoPath);
                    if (repoFolder == null) {
                        throw new Exception("repoFolder is null");
                    }
                    SourceAuthentication sourceAuthentication = repoFolder.getSourceAuthentication();
                    if (sourceAuthentication == null) {
                        throw new Exception("sourceAuthentication is null");
                    }
                    transaction.commit();

                    CloseableHttpClient httpClient = HTTPClientUtil.getHTTPClient(sourceAuthentication.getUsername(), NxlCryptoUtil.decrypt(sourceAuthentication.getPassword()), sourceAuthentication.getDomainName());

                    String includeURL = repoFolder.getPath();

                    TreeMap<Path, String> siteBySharePointFolders = new TreeMap<>();

                    if (SharePointUtil.getSPFolders(httpClient, includeURL, siteBySharePointFolders)) {
                        logger.debug("Successfully got the sharepoint folders");

                        // update the siteByFolders which will be a collection for all the repo folders
                        siteByFolders.putAll(siteBySharePointFolders);

                        // get all the sites and sub sites and map them to this repo folder
                        HashSet<String> spSites = new HashSet<>(siteBySharePointFolders.values());

                        for (String site : spSites) {
                            repoPathBySite.put(site, repoFolder.getPath());
                        }
                    }
                } catch (ManagerException | Exception e) {
                    if (transaction != null) {
                        try {
                            transaction.rollback();
                        } catch (Exception rollbackErr) {
                            logger.error(rollbackErr.getMessage(), rollbackErr);
                        }
                    }
                    logger.error(e.getMessage(), e);
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
        }
    }

    public Map<String, RepositoryType> getRepoTypeByPath() {
        return repoTypeByPath;
    }
}
