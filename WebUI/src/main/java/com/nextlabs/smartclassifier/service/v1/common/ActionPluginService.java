package com.nextlabs.smartclassifier.service.v1.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.entity.ActionPlugin;
import com.nextlabs.smartclassifier.database.manager.ActionPluginManager;
import com.nextlabs.smartclassifier.dto.v1.ActionPluginDTO;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class ActionPluginService extends Service {

  public ActionPluginService(ServletContext servletContext) {
    super(servletContext);
  }

  public ActionPluginService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse getActionPlugins() {
    CollectionResponse response = new CollectionResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      ActionPluginManager actionPluginManager =
          new ActionPluginManager(getSessionFactory(), session);
      List<ActionPlugin> actionPlugins = actionPluginManager.getActionPlugins(false);
      response.setTotalNoOfRecords(actionPluginManager.getRecordCount(null));

      transaction.commit();

      if (actionPlugins != null && actionPlugins.size() > 0) {
        response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
        response.setMessage(MessageUtil.getMessage("success.data.loaded"));

        List<ActionPluginDTO> actionPluginDTOs = new ArrayList<ActionPluginDTO>();
        for (ActionPlugin actionPlugin : actionPlugins) {
          actionPluginDTOs.add(new ActionPluginDTO(actionPlugin));
        }

        response.setData(actionPluginDTOs);
      } else {
        response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
        response.setMessage(MessageUtil.getMessage("no.data.found"));
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
      response.setStatusCode(MessageUtil.getMessage("server.error.code"));
      response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
    } finally {
      if (session != null) {
        try {
          session.close();
        } catch (HibernateException err) {
          // Ignore
        }
      }
    }

    return response;
  }

  public ServiceResponse getDistinctActionNames() {
    CollectionResponse response = new CollectionResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      Set<String> actionNameSet = new HashSet<String>();
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      List<ActionPlugin> actionPlugins =
          new ActionPluginManager(getSessionFactory(), session).getActionPlugins(false);

      transaction.commit();

      if (actionPlugins != null && actionPlugins.size() > 0) {
        response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
        response.setMessage(MessageUtil.getMessage("success.data.loaded"));

        List<ActionPluginDTO> actionPluginDTOs = new ArrayList<ActionPluginDTO>();
        for (ActionPlugin actionPlugin : actionPlugins) {
          if (!actionNameSet.contains(actionPlugin.getDisplayName())) {
            actionPluginDTOs.add(new ActionPluginDTO(actionPlugin));
            actionNameSet.add(actionPlugin.getDisplayName());
          }
        }

        response.setData(actionPluginDTOs);
        response.setTotalNoOfRecords(actionPluginDTOs.size());
      } else {
        response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
        response.setMessage(MessageUtil.getMessage("no.data.found"));
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
      response.setStatusCode(MessageUtil.getMessage("server.error.code"));
      response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
    } finally {
      if (session != null) {
        try {
          session.close();
        } catch (HibernateException err) {
          // Ignore
        }
      }
    }

    return response;
  }

  public ServiceResponse getActionPlugins(RepositoryType repositoryType) {

    CollectionResponse response = new CollectionResponse();

    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      ActionPluginManager manager = new ActionPluginManager(getSessionFactory(), session);

      List<Criterion> criteria = new ArrayList<>();
      criteria.add(Restrictions.eq(ActionPlugin.REPOSITORY_TYPE, repositoryType.getName()));

      List<Order> orderList = new ArrayList<>();
      orderList.add(Order.asc(ActionPlugin.DISPLAY_ORDER));

      List<ActionPlugin> actionPlugins = manager.getActionPlugins(criteria, orderList, false);
      response.setTotalNoOfRecords(manager.getRecordCount(criteria));

      transaction.commit();

      if (actionPlugins != null && actionPlugins.size() > 0) {
        response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
        response.setMessage(MessageUtil.getMessage("success.data.loaded"));

        List<ActionPluginDTO> data = new ArrayList<>();

        for (ActionPlugin actionPlugin : actionPlugins) {
          data.add(new ActionPluginDTO(actionPlugin));
        }

        response.setData(data);
      } else {
        response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
        response.setMessage(MessageUtil.getMessage("no.data.found"));
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
      response.setStatusCode(MessageUtil.getMessage("server.error.code"));
      response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
    } finally {
      if (session != null) {
        try {
          session.close();
        } catch (HibernateException err) {
          // Ignore
        }
      }
    }

    return response;
  }
}
