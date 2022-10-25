package com.nextlabs.smartclassifier.database.manager;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.database.dao.ExtractorDAO;
import com.nextlabs.smartclassifier.database.dao.JMSProfileDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.dao.WatcherDAO;
import com.nextlabs.smartclassifier.database.entity.Extractor;
import com.nextlabs.smartclassifier.database.entity.JMSProfile;
import com.nextlabs.smartclassifier.database.entity.Watcher;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;

public class JMSProfileManager extends Manager {

  private JMSProfileDAO jmsProfileDAO;

  public JMSProfileManager(SessionFactory sessionFactory, Session session) {
    super(sessionFactory, session);
    this.jmsProfileDAO = new JMSProfileDAO(sessionFactory, session);
  }

  public Long addJMSProfile(JMSProfile jmsProfile) throws ManagerException {
    try {
      logger.debug("Create JMSProfile.");

      Date now = new Date();
      JMSProfile entity = new JMSProfile();

      entity.setDisplayName(jmsProfile.getDisplayName());
      entity.setDescription(jmsProfile.getDescription());
      entity.setType(jmsProfile.getType());
      entity.setProviderURL(jmsProfile.getProviderURL());
      entity.setInitialContextFactory(jmsProfile.getInitialContextFactory());
      entity.setServiceName(jmsProfile.getServiceName());
      entity.setConnectionRetryInterval(jmsProfile.getConnectionRetryInterval());
      if(StringUtils.isNotBlank(jmsProfile.getUsername())) {
      	entity.setUsername(jmsProfile.getUsername());
      	entity.setPassword(jmsProfile.getPassword());
      }
      entity.setCreatedOn(now);
      entity.setModifiedOn(now);

      jmsProfileDAO.saveOrUpdate(entity);

      return entity.getId();
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public List<JMSProfile> getJMSProfiles() throws ManagerException {
    try {
      return jmsProfileDAO.getAll();
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public List<JMSProfile> getJMSProfiles(
      List<Criterion> criterion, List<Order> order, PageInfo pageInfo) throws ManagerException {
    try {
      return jmsProfileDAO.findByCriteria(criterion, order, pageInfo);
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public JMSProfile getJMSProfileById(Long id) throws ManagerException {
    try {
      logger.debug("Get JMS profile with id " + id);
      JMSProfile jmsProfile = jmsProfileDAO.get(id);

      if (jmsProfile != null) {
        logger.debug("JMS profile found with id " + id);
        return jmsProfile;
      }
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }

    return null;
  }

  public void updateJMSProfile(JMSProfile jmsProfile)
      throws ManagerException, RecordNotFoundException, IllegalArgumentException {
    try {
      logger.debug("Update JMS profile configuration for profile id " + jmsProfile.getId());
      JMSProfile entity = jmsProfileDAO.get(jmsProfile.getId());

      if (entity != null) {
        Date now = new Date();
        entity.setDisplayName(jmsProfile.getDisplayName());
        entity.setDescription(jmsProfile.getDescription());
        entity.setType(jmsProfile.getType());
        entity.setProviderURL(jmsProfile.getProviderURL());
        entity.setInitialContextFactory(jmsProfile.getInitialContextFactory());
        entity.setServiceName(jmsProfile.getServiceName());
        entity.setConnectionRetryInterval(jmsProfile.getConnectionRetryInterval());
        if(StringUtils.isNotBlank(jmsProfile.getUsername())) {
        	entity.setUsername(jmsProfile.getUsername());
        	entity.setPassword(jmsProfile.getPassword());
        }
        entity.setModifiedOn(now);

        jmsProfileDAO.saveOrUpdate(entity);
      } else {
        throw new RecordNotFoundException(
            "JMS profile record not found for the given JMSProfileID.");
      }
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }

  public void deleteJMSProfile(JMSProfile jmsProfile)
      throws ManagerException, RecordNotFoundException, RecordInUseException,
          RecordUnmatchedException {
    try {
      logger.debug("Delete JMS profile configuration for profile id " + jmsProfile.getId());
      WatcherDAO watcherDAO = new WatcherDAO(sessionFactory, session);
      ExtractorDAO extractorDAO = new ExtractorDAO(sessionFactory, session);

      if (watcherDAO.getCount(Restrictions.eq(Watcher.JMS_PROFILE_ID, jmsProfile.getId())) > 0
          || extractorDAO.getCount(Restrictions.eq(Extractor.JMS_PROFILE_ID, jmsProfile.getId()))
              > 0) {
        throw new RecordInUseException("Unable to delete. Record is currently referred by others.");
      }

      JMSProfile entity = jmsProfileDAO.get(jmsProfile.getId());

      if (entity != null) {
        if (entity.getModifiedOn().getTime() == jmsProfile.getModifiedOn().getTime()) {
          jmsProfileDAO.delete(entity);
        } else {
          throw new RecordUnmatchedException("Row was updated or deleted by another transaction.");
        }
      } else {
        throw new RecordNotFoundException(
            "JMS profile record not found for the given JMSProfileID.");
      }
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }

  public long getRecordCount(List<Criterion> criterion) throws ManagerException {
    try {
      return jmsProfileDAO.getCount(criterion);
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }
}
