package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.dao.RepoFolderDAO;
import com.nextlabs.smartclassifier.database.entity.RepoFolder;
import com.nextlabs.smartclassifier.exception.ManagerException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by pkalra on 10/21/2016.
 */
public class RepoFolderManager extends Manager {

    private RepoFolderDAO repoFolderDAO;

    public RepoFolderManager(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
        this.repoFolderDAO = new RepoFolderDAO(sessionFactory, session);
    }

    public List<RepoFolder> getAllRepositories() throws ManagerException {
        try {
            return repoFolderDAO.getAll();
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public List<RepoFolder> getSPRepositories()
            throws ManagerException {
        try {
            return repoFolderDAO.findByCriteria(Restrictions.eq(RepoFolder.REPOSITORY_TYPE, RepositoryType.SHAREPOINT.getName()));
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public List<RepoFolder> getSFRepositories()
            throws ManagerException {
        try {
            return repoFolderDAO.findByCriteria(Restrictions.eq(RepoFolder.REPOSITORY_TYPE, RepositoryType.SHARED_FOLDER.getName()));
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public RepoFolder getSPRepoFolder(String path) throws ManagerException {
        logger.debug("Trying to get the repository with the following path");

        List<RepoFolder> repositories = getSPRepositories();

        for (RepoFolder repository : repositories) {
            if (path.startsWith(repository.getPath())) {
                return repository;
            }
        }
        return null;
    }

    public RepoFolder getSFRepoFolder(String path) throws ManagerException {
        logger.debug("Trying to get the repository with the following path");

        List<RepoFolder> repositories = getSFRepositories();

        for (RepoFolder repository : repositories) {
            if (path.startsWith(repository.getPath())) {
                return repository;
            }
        }
        return null;

    }
}
