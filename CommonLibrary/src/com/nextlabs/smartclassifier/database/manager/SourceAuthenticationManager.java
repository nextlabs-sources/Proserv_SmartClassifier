package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.dao.RepoFolderDAO;
import com.nextlabs.smartclassifier.database.dao.SourceAuthenticationDAO;
import com.nextlabs.smartclassifier.database.entity.RepoFolder;
import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SourceAuthenticationManager extends Manager {

    private SourceAuthenticationDAO saDAO;

    public SourceAuthenticationManager(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
        this.saDAO = new SourceAuthenticationDAO(sessionFactory, session);
    }

    /**
     * Get all source authentications
     *
     * @return the list of source authentication records
     * @throws ManagerException
     */
    public List<SourceAuthentication> getSourceAuthentications() throws ManagerException {
        try {
            return saDAO.getAll();
        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }
    }

    /**
     * Gets the source authentication records
     *
     * @param criteria the criteria list
     * @param orders   the order list
     * @param pageInfo the page information
     * @return the list of source authentication records
     * @throws ManagerException
     */
    public List<SourceAuthentication> getSourceAuthentications(
            List<Criterion> criteria, List<Order> orders, PageInfo pageInfo) throws ManagerException {
        try {
            return saDAO.findByCriteria(criteria, orders, pageInfo);
        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }
    }

    /**
     * Get source authentication by its id
     *
     * @param id the source authentication id
     * @return source authentication record with this <code>id</code>.
     * @throws ManagerException
     */
    public SourceAuthentication getSourceAuthenticationById(Long id) throws ManagerException {
        try {
            logger.debug("Trying to get source authentication with id = " + id);

            SourceAuthentication sa = saDAO.get(id);

            if (sa != null) {
                logger.debug("Source Authentication with ID = " + id + " is FOUND!");
                return sa;
            }

        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }
        return null;
    }

    /**
     * Add a new source authentication record
     *
     * @param sourceAuthentication
     * @return
     * @throws ManagerException
     */
    public Long addSourceAuthentication(SourceAuthentication sourceAuthentication)
            throws ManagerException {
        try {
            logger.info("Creating a new source authentication");

            Date now = new Date();

            SourceAuthentication entity = new SourceAuthentication();

            entity.setName(sourceAuthentication.getName());
            entity.setDomainName(sourceAuthentication.getDomainName());
            entity.setUsername(sourceAuthentication.getUsername());
            entity.setPassword(sourceAuthentication.getPassword());
            entity.setCreatedOn(now);
            entity.setModifiedOn(now);

            saDAO.saveOrUpdate(entity);

            return entity.getId();
        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }
    }

    /**
     * Updates SourceAuthentication record
     *
     * @param sa SourceAuthentication entity
     * @throws RecordNotFoundException if the entity is not found
     * @throws ManagerException
     */
    public void updateSourceAuthentication(SourceAuthentication sa)
            throws RecordNotFoundException, ManagerException {

        try {
            SourceAuthentication entity = saDAO.get(sa.getId());

            if (entity != null) {
                logger.debug("Trying to update the SourceAuthentication record with ID = " + sa.getId());

                entity.setName(sa.getName());
                entity.setDomainName(sa.getDomainName());
                entity.setUsername(sa.getUsername());
                entity.setPassword(sa.getPassword());
                entity.setModifiedOn(new Date());

                saDAO.saveOrUpdate(entity);
            } else {
                throw new RecordNotFoundException(
                        "SourceAuthentication record not found for ID = " + sa.getId());
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        }
    }

    public long getRecordCount(List<Criterion> criteria) throws ManagerException {
        try {
            return saDAO.getCount(criteria);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ManagerException(e.getMessage(), e);
        }
    }

    public void deleteSourceAuthentication(SourceAuthentication sa)
            throws RecordInUseException, RecordNotFoundException, RecordUnmatchedException,
            ManagerException {
        try {

            long id = sa.getId();

            logger.debug("Trying to delete source authentication with id = " + id);

            logger.debug("Checking if any repo folders are using this source authentication record");
            RepoFolderDAO repoFolderDAO = new RepoFolderDAO(sessionFactory, session);
            long dependencies =
                    repoFolderDAO.getCount(Restrictions.eq(RepoFolder.SOURCE_AUTHENTICATION_ID, sa.getId()));
            if (dependencies > 0) {
                throw new RecordInUseException(
                        "Unable to delete this source authentication record. It is currently used by RepoFolders.");
            }

            SourceAuthentication entity = saDAO.get(id);
            if (entity != null) {
                if (entity.getModifiedOn().getTime() == sa.getModifiedOn().getTime()) {
                    saDAO.delete(id);
                } else {
                    throw new RecordUnmatchedException(
                            "Concurrent Modification: Record was updated by another transaction.");
                }
            } else {
                throw new RecordNotFoundException(
                        "Source Authentication with ID = " + id + " was not found!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ManagerException(e.getMessage(), e);
        }
    }

    public boolean hasSameUsername(String domain, String username) throws ManagerException {
        try {

            logger.debug("Checking if there are any source authentication with domain = " + domain + " and username = " + username);

            List<Criterion> criteria = new ArrayList<>();
            criteria.add(Restrictions.eq(SourceAuthentication.DOMAIN_NAME, domain));
            criteria.add(Restrictions.eq(SourceAuthentication.USERNAME, username));

            List<SourceAuthentication> sourceAuthenticationList = saDAO.findByCriteria(criteria);

            if (sourceAuthenticationList.size() > 0) {
                logger.debug("Found an existing record with domain = " + domain + " and username = " + username);
                logger.debug(Arrays.toString(sourceAuthenticationList.toArray()));
            } else {
                logger.debug("No existing record with domain = " + domain + " and username = " + username + " was found!");
            }
            return sourceAuthenticationList.size() > 0;
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        }
    }
}
