package com.nextlabs.smartclassifier.util;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.entity.RepoFolder;
import com.nextlabs.smartclassifier.database.manager.RepoFolderManager;
import com.nextlabs.smartclassifier.dto.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.exception.ManagerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pkalra on 12/9/2016.
 */
public final class RepositoryUtil {

    private static final Logger logger = LogManager.getLogger(RepositoryUtil.class);
    private static final Map<String, SourceAuthenticationDTO> credentialsByRepoPath = new HashMap<>();
    private static SessionFactory sessionFactory;

    public static void initializeSessionFactory(SessionFactory sessionFactory1) {
        sessionFactory = sessionFactory1;
    }

    public static synchronized void reloadRepositories() {

        Session session = null;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            List<RepoFolder> allRepositories = new RepoFolderManager(sessionFactory, session).getAllRepositories();

            for (RepoFolder repoFolder : allRepositories) {
                RepositoryType repositoryType = RepositoryType.getRepositoryType(repoFolder.getRepositoryType());
                if(repositoryType == RepositoryType.SHAREPOINT) {
                    SourceAuthenticationDTO sourceAuthentication = new SourceAuthenticationDTO(repoFolder.getSourceAuthentication());
                    credentialsByRepoPath.put(repoFolder.getPath(), sourceAuthentication);
                } else {
                    credentialsByRepoPath.put(repoFolder.getPath(), null);
                }
            }

            logger.debug("Repository and Credentials Information loaded successfully!");
            logger.debug(Arrays.toString(credentialsByRepoPath.entrySet().toArray()));
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

    public static synchronized SourceAuthenticationDTO getSourceAuthentication(String repoPath) {
        if(credentialsByRepoPath.containsKey(repoPath)) {
            return credentialsByRepoPath.get(repoPath);
        } else {
            logger.debug("Could not find the source authentication for " + repoPath + " in the following..");
            logger.debug(Arrays.toString(credentialsByRepoPath.entrySet().toArray()));

            reloadRepositories();

            logger.debug("Updated RepoPath to Credentials : ");
            logger.debug(Arrays.toString(credentialsByRepoPath.entrySet().toArray()));

            return credentialsByRepoPath.get(repoPath);
        }
    }

    public static synchronized String getRepoPath(String path) {

        for (String repoPath : credentialsByRepoPath.keySet()) {
            if (path.toLowerCase().startsWith(repoPath.toLowerCase())) {
                return repoPath;
            }
        }

        logger.debug("Could not find " +  path + " in the following..");
        logger.debug(Arrays.toString(credentialsByRepoPath.keySet().toArray()));

        reloadRepositories();

        logger.debug("Updated Paths : ");
        logger.debug(Arrays.toString(credentialsByRepoPath.keySet().toArray()));

        for (String repoPath : credentialsByRepoPath.keySet()) {
            if (path.toLowerCase().startsWith(repoPath.toLowerCase())) {
                return repoPath;
            }
        }

        return null;
    }
}
