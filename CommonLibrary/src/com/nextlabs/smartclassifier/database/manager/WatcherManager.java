package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.dao.DocumentExtractorDAO;
import com.nextlabs.smartclassifier.database.dao.DocumentTypeAssociationDAO;
import com.nextlabs.smartclassifier.database.dao.HeartbeatDAO;
import com.nextlabs.smartclassifier.database.dao.JMSProfileDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.dao.RepoFolderDAO;
import com.nextlabs.smartclassifier.database.dao.SourceAuthenticationDAO;
import com.nextlabs.smartclassifier.database.dao.WatcherDAO;
import com.nextlabs.smartclassifier.database.entity.DocumentTypeAssociation;
import com.nextlabs.smartclassifier.database.entity.ExcludeRepoFolder;
import com.nextlabs.smartclassifier.database.entity.Heartbeat;
import com.nextlabs.smartclassifier.database.entity.JMSProfile;
import com.nextlabs.smartclassifier.database.entity.RepoFolder;
import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;
import com.nextlabs.smartclassifier.database.entity.Watcher;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WatcherManager extends Manager {

    private WatcherDAO watcherDAO;

    public WatcherManager(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
        this.watcherDAO = new WatcherDAO(sessionFactory, session);
    }

    public List<Watcher> getWatchers() throws ManagerException {
        try {
            logger.debug("Get all watchers.");
            return watcherDAO.getAll();
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public List<Watcher> getWatchersWithLastHeartbeat() throws ManagerException {
        try {
            logger.debug("Get all watchers with heart beat.");
            return getLastHeartbeat(new HeartbeatDAO(sessionFactory, session), watcherDAO.getAll());
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public List<Watcher> getWatchers(List<Criterion> criterion, List<Order> order, PageInfo pageInfo)
            throws ManagerException {
        try {
            return getLastHeartbeat(
                    new HeartbeatDAO(sessionFactory, session),
                    watcherDAO.findByCriteria(criterion, order, pageInfo));
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public Watcher getWatcherById(Long id) throws ManagerException {
        try {
            logger.debug("Get watcher with id " + id);
            Watcher watcher = watcherDAO.get(id);

            if (watcher != null) {
                logger.debug("Watcher found with id " + id);

                List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
                heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, id));
                heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, ComponentType.WATCHER.getCode()));

                List<Heartbeat> heartbeats = new HeartbeatDAO(sessionFactory, session).findByCriteria(heartbeatCriteria);

                if (heartbeats != null
                        && heartbeats.size() > 0) {
                    watcher.setLastHeartbeat(heartbeats.get(0).getLastHeartbeat());
                }

                return watcher;
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        return null;
    }

    public Watcher getWatcherByHostname(String hostname) throws ManagerException {
        try {
            logger.debug("Get watcher for hostname " + hostname);
            List<Watcher> watchers =
                    watcherDAO.findByCriteria(Restrictions.eq(Watcher.HOSTNAME, hostname));

            if (watchers != null && watchers.size() > 0) {
                logger.debug("Watcher found for hostname " + hostname);
                return watchers.get(0);
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        return null;
    }

    public void updateConfigLoadedDate(String hostname, Date configLoadedOn) throws ManagerException {
        try {
            logger.debug(
                    "Updating the Watcher[" + hostname + "] configuration load time to " + configLoadedOn);

            List<Watcher> watchers =
                    watcherDAO.findByCriteria(Restrictions.eq(Watcher.HOSTNAME, hostname));

            if (watchers != null && watchers.size() > 0) {
                Watcher watcher = watchers.get(0);
                watcher.setConfigLoadedOn(configLoadedOn);

                watcherDAO.saveOrUpdate(watcher);
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public void updateWatcher(Watcher watcher)
            throws ManagerException, RecordNotFoundException, IllegalArgumentException {
        try {
            logger.debug("Update watcher configuration for watcher id " + watcher.getId());

            Watcher entity = watcherDAO.get(watcher.getId());

            if (entity != null) {
                JMSProfileDAO jmsProfileDAO = new JMSProfileDAO(sessionFactory, session);
                DocumentTypeAssociationDAO documentTypeAssociationDAO =
                        new DocumentTypeAssociationDAO(sessionFactory, session);
                DocumentExtractorDAO documentExtractorDAO =
                        new DocumentExtractorDAO(sessionFactory, session);
                RepoFolderDAO repoFolderDAO = new RepoFolderDAO(sessionFactory, session);
                SourceAuthenticationDAO srcAuthDAO = new SourceAuthenticationDAO(sessionFactory, session);

                // get the id of the passed watcher
                JMSProfile jmsProfile = jmsProfileDAO.get(watcher.getJMSProfile().getId());
                if (jmsProfile == null) {
                    throw new IllegalArgumentException(
                            "Invalid JMS profile. Unable to retrieve JMS profile for given value.");
                }

                entity.setName(watcher.getName());
                entity.setFileMonitorCount(watcher.getFileMonitorCount());
                entity.setConfigReloadInterval(watcher.getConfigReloadInterval());
                entity.setJMSProfile(jmsProfile);

                // Remove original records for document type association
                if (entity.getDocumentTypeAssociations() != null) {
                    Iterator<DocumentTypeAssociation> iterator =
                            entity.getDocumentTypeAssociations().iterator();

                    while (iterator.hasNext()) {
                        DocumentTypeAssociation documentTypeAssociation = iterator.next();
                        iterator.remove();
                        documentTypeAssociationDAO.delete(documentTypeAssociation);
                    }
                    documentTypeAssociationDAO.flush();
                }

                if (watcher.getDocumentTypeAssociations() != null) {
                    // get new document type associations
                    for (DocumentTypeAssociation docTypeAssociation : watcher.getDocumentTypeAssociations()) {
                        DocumentTypeAssociation documentTypeAssociation = new DocumentTypeAssociation();

                        documentTypeAssociation.setWatcher(entity);
                        documentTypeAssociation.setDocumentExtractor(
                                documentExtractorDAO.get(docTypeAssociation.getDocumentExtractor().getId()));
                        documentTypeAssociation.isInclude(docTypeAssociation.isInclude());

                        // and add them to the entity - new records created.
                        entity.getDocumentTypeAssociations().add(documentTypeAssociation);
                    }
                }

                // Remove original records for repository folder
                if (entity.getRepoFolders() != null) {
                    Iterator<RepoFolder> iterator = entity.getRepoFolders().iterator();

                    while (iterator.hasNext()) {
                        RepoFolder repoFolder = iterator.next();
                        iterator.remove();
                        repoFolderDAO.delete(repoFolder);
                    }

                    repoFolderDAO.flush();
                }

                if (watcher.getRepoFolders() != null) {
                    int i = 1;
                    for (RepoFolder repositoryFolder : watcher.getRepoFolders()) {
                        RepoFolder repoFolder = new RepoFolder();

                        repoFolder.setWatcher(entity);
                        repoFolder.setDisplayOrder(i);
                        repoFolder.setPath(repositoryFolder.getPath());
                        repoFolder.setRepositoryType(repositoryFolder.getRepositoryType());

                        RepositoryType repositoryType = RepositoryType.getRepositoryType(repoFolder.getRepositoryType());

                        if (repositoryType == RepositoryType.SHARED_FOLDER) {
                            repoFolder.setSourceAuthentication(null);
                        } else {
                            SourceAuthentication sa =
                                    srcAuthDAO.get(repositoryFolder.getSourceAuthentication().getId());
                            if (sa == null) {
                                throw new IllegalArgumentException(
                                        "Invalid Repository Credential. Unable to retrieve Repository Credential record for ID = "
                                                + repositoryFolder.getSourceAuthentication().getId());
                            }
                            repoFolder.setSourceAuthentication(sa);
                        }

                        if (repositoryFolder.getExcludeRepoFolders() != null) {
                            int j = 1;

                            for (ExcludeRepoFolder excludeRepositoryFolder :
                                    repositoryFolder.getExcludeRepoFolders()) {
                                ExcludeRepoFolder excludeRepoFolder = new ExcludeRepoFolder();

                                excludeRepoFolder.setRepoFolder(repoFolder);
                                excludeRepoFolder.setDisplayOrder(j);
                                excludeRepoFolder.setPath(excludeRepositoryFolder.getPath());

                                repoFolder.getExcludeRepoFolders().add(excludeRepoFolder);
                            }

                            j++;
                        }

                        entity.getRepoFolders().add(repoFolder);
                        i++;
                    }
                }

                entity.setModifiedOn(new Date());

                watcherDAO.saveOrUpdate(entity);
            } else {
                throw new RecordNotFoundException("Watcher record not found for the given WatcherID.");
            }
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }
    }

    /**
     * Check if folders in given watcher has been watched by other watcher.
     *
     * @param watcher Information of the watcher and folders to check.
     * @return Map of which folder has been watched by which watcher.
     */
    public Map<String, String> checkMonitorFolder(Watcher watcher) throws ManagerException {
        try {
            RepoFolderDAO repoFolderDAO = new RepoFolderDAO(sessionFactory, session);

            for (RepoFolder includedFolder : watcher.getRepoFolders()) {
                String folderEntry = getMachinePath(includedFolder.getPath());
                String lcaseIncludedFolder = includedFolder.getPath().toLowerCase();

                if (StringUtils.isNotBlank(folderEntry)) {
                    List<RepoFolder> repoFolders =
                            repoFolderDAO.findByCriteria(Restrictions.like(RepoFolder.PATH, folderEntry + "%"));

                    if (repoFolders != null && repoFolders.size() > 0) {
                        for (RepoFolder repoFolder : repoFolders) {
                            String lcaseRepoFolder = repoFolder.getPath().toLowerCase();

                            if ((lcaseIncludedFolder.startsWith(lcaseRepoFolder)
                                    || lcaseRepoFolder.startsWith(lcaseIncludedFolder))
                                    && !repoFolder.getWatcher().getId().equals(watcher.getId())) {
                                Map<String, String> monitored = new HashMap<String, String>();
                                monitored.put(repoFolder.getPath(), repoFolder.getWatcher().getName());

                                return monitored;
                            }
                        }
                    }
                }
            }
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }

        return null;
    }

    public long getRecordCount(List<Criterion> criterion) throws ManagerException {
        try {
            return watcherDAO.getCount(criterion);
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }
    }

    private List<Watcher> getLastHeartbeat(HeartbeatDAO heartbeatDAO, List<Watcher> watchers)
            throws Exception {
        if (heartbeatDAO != null && watchers != null) {
            for (Watcher watcher : watchers) {
                List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
                heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, watcher.getId()));
                heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, ComponentType.WATCHER.getCode()));

                List<Heartbeat> heartbeats = heartbeatDAO.findByCriteria(heartbeatCriteria);

                if (heartbeats != null
                        && heartbeats.size() > 0) {
                    watcher.setLastHeartbeat(heartbeats.get(0).getLastHeartbeat());
                }
            }
        }

        return watchers;
    }

    private String getMachinePath(final String repositoryPath) {
        if (StringUtils.isNotBlank(repositoryPath)) {
            int ordinalIndex = StringUtils.ordinalIndexOf(repositoryPath, "\\", 3) + 1;

            if (ordinalIndex > 0) {
                return repositoryPath.substring(0, ordinalIndex);
            }
        }

        return null;
    }
}
