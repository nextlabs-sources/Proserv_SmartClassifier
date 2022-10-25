package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import com.nextlabs.smartclassifier.database.dao.ActionPluginDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.entity.ActionPlugin;
import com.nextlabs.smartclassifier.database.entity.ActionPluginParam;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;

public class ActionPluginManager extends Manager {

  private ActionPluginDAO actionPluginDAO;

  private long fixedParamActionPluginCount;

  public ActionPluginManager(SessionFactory sessionFactory, Session session) {
    super(sessionFactory, session);
    this.actionPluginDAO = new ActionPluginDAO(sessionFactory, session);
  }

  public List<ActionPlugin> getActionPlugins() throws ManagerException {
    try {
      List<ActionPlugin> actionPlugins = actionPluginDAO.getAll();

      if (actionPlugins != null && actionPlugins.size() > 0) {
        for (ActionPlugin actionPlugin : actionPlugins) {
          actionPlugin.getPluginParams();
        }
      }

      return actionPlugins;
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public List<ActionPlugin> getActionPlugins(List<Criterion> criteria, List<Order> orderList)
      throws ManagerException {
    try {
      List<ActionPlugin> actionPlugins = actionPluginDAO.findByCriteria(criteria, orderList);

      if (actionPlugins != null && actionPlugins.size() > 0) {
        for (ActionPlugin actionPlugin : actionPlugins) {
          actionPlugin.getPluginParams();
        }
      }

      return actionPlugins;
    } catch (Exception err) {
      throw new ManagerException(err.getMessage(), err);
    }
  }

  public List<ActionPlugin> getActionPlugins(boolean fixedParameterOnly) throws ManagerException {
    try {
      List<ActionPlugin> actionPlugins =
          actionPluginDAO.getAllByOrder(ActionPlugin.DISPLAY_ORDER, "ASC");

      if (actionPlugins != null && actionPlugins.size() > 0) {
        for (ActionPlugin actionPlugin : actionPlugins) {
          if (fixedParameterOnly) {
            getFixedParameterActionPlugin(actionPlugin);
          } else {
            getDynamicParameterActionPlugin(actionPlugin);
          }
        }
      }

      return actionPlugins;
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public List<ActionPlugin> getActionPlugins(
      List<Criterion> criteria, List<Order> orderList, boolean fixedParameterOnly)
      throws ManagerException {
    try {
      List<ActionPlugin> actionPlugins = actionPluginDAO.findByCriteria(criteria, orderList);

      if (actionPlugins != null && actionPlugins.size() > 0) {
        for (ActionPlugin actionPlugin : actionPlugins) {
          if (fixedParameterOnly) {
            getFixedParameterActionPlugin(actionPlugin);
          } else {
            getDynamicParameterActionPlugin(actionPlugin);
          }
        }
      }

      return actionPlugins;
    } catch (Exception err) {
      throw new ManagerException(err.getMessage(), err);
    }
  }

  public List<ActionPlugin> getActionPluginWithFixedParameters(
      List<Criterion> criterion, List<Order> order, PageInfo pageInfo) throws ManagerException {
    try {
      List<ActionPlugin> actionPlugins = actionPluginDAO.findByCriteria(criterion, order);
      List<ActionPlugin> filteredActionPlugins = new ArrayList<>();
      List<ActionPlugin> pagedActionPlugins = new ArrayList<>();

      if (actionPlugins != null) {
        for (ActionPlugin actionPlugin : actionPlugins) {
          if (actionPlugin.hasFixedParameter()) {
            filteredActionPlugins.add(getFixedParameterActionPlugin(actionPlugin));
          }
        }
      }

      fixedParamActionPluginCount = filteredActionPlugins.size();
      int startIndex = (pageInfo.getPageNumber() - 1) * pageInfo.getSize();

      for (int i = startIndex; i < startIndex + pageInfo.getSize(); i++) {
        if (i >= filteredActionPlugins.size()) {
          break;
        }

        pagedActionPlugins.add(filteredActionPlugins.get(i));
      }

      return pagedActionPlugins;
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public long getFixedParameterPluginRecordCount(List<Criterion> criterion)
      throws ManagerException {
    return fixedParamActionPluginCount;
  }

  public void updateActionPluginFixedParameter(ActionPlugin actionPlugin)
      throws ManagerException, RecordNotFoundException, IllegalArgumentException {
    try {
      logger.debug("Update action plugin fixed value for action plugin id " + actionPlugin.getId());
      ActionPlugin entity = actionPluginDAO.get(actionPlugin.getId());

      if (entity == null) {
        throw new RecordNotFoundException(
            "Action plugin record not found for the given ActionPluginID.");
      }

      if (entity.getPluginParams() != null && actionPlugin.getPluginParams() != null) {
        Date now = new Date();

        entity.setDescription(actionPlugin.getDescription());
        entity.setModifiedOn(now);

        for (ActionPluginParam updatedActionPluginParam : actionPlugin.getPluginParams()) {
          for (ActionPluginParam actionPluginParam : entity.getPluginParams()) {
            if (actionPluginParam.isFixedParameter()
                && actionPluginParam.getId().equals(updatedActionPluginParam.getId())) {
              actionPluginParam.setFixedValue(updatedActionPluginParam.getFixedValue());
              actionPluginParam.setModifiedOn(now);
            }
          }
        }
      }

      actionPluginDAO.saveOrUpdate(entity);
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
      return actionPluginDAO.getCount(criterion);
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }

  public ActionPlugin getActionPluginFixedParameterById(Long id) throws ManagerException {
    try {
      logger.debug("Get action plugin with id " + id);
      ActionPlugin actionPlugin = actionPluginDAO.get(id);

      if (actionPlugin != null) {
        return getFixedParameterActionPlugin(actionPlugin);
      }
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }

    return null;
  }

  private ActionPlugin getDynamicParameterActionPlugin(ActionPlugin actionPlugin) {
    if (actionPlugin != null && actionPlugin.getPluginParams() != null) {
      Iterator<ActionPluginParam> paramIterator = actionPlugin.getPluginParams().iterator();

      while (paramIterator.hasNext()) {
        if (paramIterator.next().isFixedParameter()) {
          paramIterator.remove();
        }
      }
    }

    return actionPlugin;
  }

  private ActionPlugin getFixedParameterActionPlugin(ActionPlugin actionPlugin) {
    if (actionPlugin != null && actionPlugin.getPluginParams() != null) {
      Iterator<ActionPluginParam> paramIterator = actionPlugin.getPluginParams().iterator();

      while (paramIterator.hasNext()) {
        if (!paramIterator.next().isFixedParameter()) {
          paramIterator.remove();
        }
      }
    }

    return actionPlugin;
  }
}
