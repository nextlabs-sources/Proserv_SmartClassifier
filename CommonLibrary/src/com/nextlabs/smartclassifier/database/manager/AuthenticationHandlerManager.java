package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.database.dao.AuthenticationHandlerDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.dao.UserDAO;
import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;
import com.nextlabs.smartclassifier.database.entity.User;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;

public class AuthenticationHandlerManager extends Manager {

  private AuthenticationHandlerDAO authenticationHandlerDAO;

  public AuthenticationHandlerManager(SessionFactory sessionFactory, Session session) {
    super(sessionFactory, session);
    this.authenticationHandlerDAO = new AuthenticationHandlerDAO(sessionFactory, session);
  }

  public Long addAuthenticationHandler(AuthenticationHandler authenticationHandler)
      throws ManagerException {
    try {
      logger.debug("Create authentication handler.");

      Date now = new Date();
      AuthenticationHandler entity = new AuthenticationHandler();

      entity.setName(authenticationHandler.getName());
      entity.setType(authenticationHandler.getType());
      entity.setConfigurationData(authenticationHandler.getConfigurationData());
      entity.setUserAttributeMapping(authenticationHandler.getUserAttributeMapping());
      entity.setCreatedOn(now);
      entity.setModifiedOn(now);

      authenticationHandlerDAO.saveOrUpdate(entity);

      return entity.getId();
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public List<AuthenticationHandler> getAuthenticationHandlers() throws ManagerException {
    try {
      return authenticationHandlerDAO.getAllByOrder(AuthenticationHandler.NAME, PageInfo.ASC);
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public List<AuthenticationHandler> getAuthenticationHandlers(
      List<Criterion> criterion, List<Order> order, PageInfo pageInfo) throws ManagerException {
    try {
      if (order == null) {
        order = new ArrayList<Order>();
      }

      if (order.size() == 0) {
        order.add(Order.asc(AuthenticationHandler.NAME));
      }

      return authenticationHandlerDAO.findByCriteria(criterion, order, pageInfo);
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public AuthenticationHandler getAuthenticationHandlerById(Long id) throws ManagerException {
    try {
      logger.debug("Get authentication handler with id " + id);
      AuthenticationHandler authenticationHandler = authenticationHandlerDAO.get(id);

      if (authenticationHandler != null) {
        logger.debug("Authentication handler found with id " + id);
        return authenticationHandler;
      }
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }

    return null;
  }

  public void updateAuthenticationHandler(AuthenticationHandler authenticationHandler)
      throws ManagerException, RecordNotFoundException, IllegalArgumentException {
    try {
      logger.debug(
          "Update authentication handler configuration for handler id "
              + authenticationHandler.getId());
      AuthenticationHandler entity = authenticationHandlerDAO.get(authenticationHandler.getId());

      if (entity != null) {
        entity.setName(authenticationHandler.getName());
        entity.setType(authenticationHandler.getType());
        entity.setConfigurationData(authenticationHandler.getConfigurationData());
        entity.setUserAttributeMapping(authenticationHandler.getUserAttributeMapping());
        entity.setModifiedOn(new Date());

        authenticationHandlerDAO.saveOrUpdate(entity);
      } else {
        throw new RecordNotFoundException(
            "Authentication handler record not found for the given AuthenticationHandlerID.");
      }
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }

  public void deleteAuthenticationHandler(AuthenticationHandler authenticationHandler)
      throws ManagerException, RecordNotFoundException, RecordInUseException,
          RecordUnmatchedException {
    try {
      logger.debug(
          "Delete authentication handler configuration for handler id "
              + authenticationHandler.getId());
      UserDAO userDAO = new UserDAO(sessionFactory, session);

      if (userDAO.getCount(
              Restrictions.eq(User.AUTHENTICATION_HANDLER_ID, authenticationHandler.getId()))
          > 0) {
        throw new RecordInUseException("Unable to delete. Record is currently referred by others.");
      }

      AuthenticationHandler entity = authenticationHandlerDAO.get(authenticationHandler.getId());

      if (entity != null) {
        if (entity.getModifiedOn().getTime() == authenticationHandler.getModifiedOn().getTime()) {
          authenticationHandlerDAO.delete(entity);
        } else {
          throw new RecordUnmatchedException("Row was updated or deleted by another transaction.");
        }
      } else {
        throw new RecordNotFoundException(
            "Authentication handler record not found for the given AuthenticationHandlerID.");
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
      return authenticationHandlerDAO.getCount(criterion);
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }
}
