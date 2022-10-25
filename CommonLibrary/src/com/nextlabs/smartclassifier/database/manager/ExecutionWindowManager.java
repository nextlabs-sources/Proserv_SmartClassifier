package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.database.dao.ExecutionWindowAssociationDAO;
import com.nextlabs.smartclassifier.database.dao.ExecutionWindowDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindow;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowAssociation;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;
import com.nextlabs.smartclassifier.exception.ManagerException;

public class ExecutionWindowManager extends Manager {

  private ExecutionWindowDAO executionWindowDAO;
  private ExecutionWindowAssociationDAO executionWindowAssociationDAO;

  public ExecutionWindowManager(SessionFactory sessionFactory, Session session) {
    super(sessionFactory, session);
    this.executionWindowDAO = new ExecutionWindowDAO(sessionFactory, session);
    this.executionWindowAssociationDAO = new ExecutionWindowAssociationDAO(sessionFactory, session);
  }

  /**
   * Gets the list of execution window sets for a component
   *
   * @param componentId the component ID
   * @param componentType the component type
   * @return return the list of execution window sets
   * @throws ManagerException
   */
  public List<ExecutionWindowSet> getExecutionWindowSets(Long componentId, String componentType) 
		  throws ManagerException {
    try {
      List<Criterion> criterionList = new ArrayList<Criterion>();
      criterionList.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_ID, componentId));
      criterionList.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_TYPE, componentType));

      List<Order> orderList = new ArrayList<Order>();
      orderList.add(Order.asc(ExecutionWindowAssociation.DISPLAY_ORDER));

      List<ExecutionWindowAssociation> associations =
          executionWindowAssociationDAO.findByCriteria(criterionList, orderList);

      if (associations != null && associations.size() > 0) {
        List<ExecutionWindowSet> executionWindowSets = new ArrayList<ExecutionWindowSet>();

        for (ExecutionWindowAssociation association : associations) {
          executionWindowSets.add(association.getExecutionWindowSet());
        }

        return executionWindowSets;
      }
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }

    return null;
  }

  /**
   * Gets the execution windows for a component
   *
   * @param componentId the component ID
   * @param componentType the component type
   * @return
   * @throws ManagerException
   */
  public List<ExecutionWindow> getExecutionWindows(Long componentId, String componentType) 
		  throws ManagerException {
    try {
      List<ExecutionWindowSet> executionWindowSets = getExecutionWindowSets(componentId, componentType);

      if (executionWindowSets != null) {
        List<ExecutionWindow> executionWindows = new ArrayList<ExecutionWindow>();

        for (ExecutionWindowSet executionWindowSet : executionWindowSets) {
          executionWindows.addAll(executionWindowSet.getExecutionWindows());
        }

        return executionWindows;
      }
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }

    return null;
  }

  public List<ExecutionWindow> getExecutionWindows(List<Criterion> criterion, PageInfo pageInfo)
      throws ManagerException {
    try {
      return executionWindowDAO.findByCriteria(criterion, pageInfo);
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public ExecutionWindow getExecutionWindowById(Long id) throws ManagerException {
    try {
      logger.debug("Get execution window with id " + id);
      ExecutionWindow executionWindow = executionWindowDAO.get(id);

      if (executionWindow != null) {
        logger.debug("Execution window found with id " + id);
        return executionWindow;
      }
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }

    return null;
  }

  public long getRecordCount(List<Criterion> criterion) throws ManagerException {
    try {
      return executionWindowDAO.getCount(criterion);
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }
}
