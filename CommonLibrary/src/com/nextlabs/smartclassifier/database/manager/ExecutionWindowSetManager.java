package com.nextlabs.smartclassifier.database.manager;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.database.dao.ExecutionWindowAssociationDAO;
import com.nextlabs.smartclassifier.database.dao.ExecutionWindowDAO;
import com.nextlabs.smartclassifier.database.dao.ExecutionWindowSetDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.entity.ExecutionTimeSlot;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindow;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowAssociation;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;

public class ExecutionWindowSetManager extends Manager {

  private ExecutionWindowSetDAO executionWindowSetDAO;

  public ExecutionWindowSetManager(SessionFactory sessionFactory, Session session) {
    super(sessionFactory, session);
    this.executionWindowSetDAO = new ExecutionWindowSetDAO(sessionFactory, session);
  }

  public Long addExecutionWindowSet(ExecutionWindowSet executionWindowSet) throws ManagerException {
    try {
      logger.debug("Create execution window set.");

      Date now = new Date();
      ExecutionWindowSet entity = new ExecutionWindowSet();

      entity.setName(executionWindowSet.getName());
      entity.setDescription(executionWindowSet.getDescription());
      entity.setScheduleType(executionWindowSet.getScheduleType());
      entity.setEffectiveFrom(executionWindowSet.getEffectiveFrom());
      entity.setEffectiveUntil(executionWindowSet.getEffectiveUntil());
      entity.setCreatedOn(now);
      entity.setModifiedOn(now);

      if (executionWindowSet.getExecutionWindows() != null) {
        for (ExecutionWindow execWindow : executionWindowSet.getExecutionWindows()) {
          ExecutionWindow executionWindow = new ExecutionWindow();

          executionWindow.setExecutionWindowSet(entity);
          executionWindow.setDisplayOrder(execWindow.getDisplayOrder());
          executionWindow.setDay(execWindow.getDay());

          if (execWindow.getExecutionTimeSlots() != null) {
            for (ExecutionTimeSlot execTimeSlot : execWindow.getExecutionTimeSlots()) {
              ExecutionTimeSlot executionTimeSlot = new ExecutionTimeSlot();

              executionTimeSlot.setExecutionWindow(executionWindow);
              executionTimeSlot.setDisplayOrder(execTimeSlot.getDisplayOrder());
              executionTimeSlot.setStartTime(execTimeSlot.getStartTime());
              executionTimeSlot.setEndTime(execTimeSlot.getEndTime());

              executionWindow.getExecutionTimeSlots().add(executionTimeSlot);
            }
          }

          entity.getExecutionWindows().add(executionWindow);
        }
      }

      executionWindowSetDAO.saveOrUpdate(entity);

      return entity.getId();
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public List<ExecutionWindowSet> getExecutionWindowSets() throws ManagerException {
    try {
      return executionWindowSetDAO.getAllByOrder(ExecutionWindowSet.NAME, "ASC");
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public List<ExecutionWindowSet> getExecutionWindowSets(
      List<Criterion> criterion, List<Order> order, PageInfo pageInfo) throws ManagerException {
    try {
      return executionWindowSetDAO.findByCriteria(criterion, order, pageInfo);
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }
  }

  public ExecutionWindowSet getExecutionWindowSetById(Long id) throws ManagerException {
    try {
      logger.debug("Get execution window set with id " + id);
      ExecutionWindowSet executionWindowSet = executionWindowSetDAO.get(id);

      if (executionWindowSet != null) {
        logger.debug("Execution window set found with id " + id);
        return executionWindowSet;
      }
    } catch (HibernateException err) {
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      throw new ManagerException(err);
    }

    return null;
  }

  public void updateExecutionWindowSet(ExecutionWindowSet executionWindowSet)
      throws ManagerException, RecordNotFoundException, IllegalArgumentException {
    try {
      logger.debug(
          "Update execution window set configuration for execution window set id "
              + executionWindowSet.getId());
      ExecutionWindowSet entity = executionWindowSetDAO.get(executionWindowSet.getId());

      if (entity != null) {
        entity.setName(executionWindowSet.getName());
        entity.setDescription(executionWindowSet.getDescription());
        entity.setScheduleType(executionWindowSet.getScheduleType());
        entity.setEffectiveFrom(executionWindowSet.getEffectiveFrom());
        entity.setEffectiveUntil(executionWindowSet.getEffectiveUntil());
        entity.setModifiedOn(new Date());

        ExecutionWindowDAO executionWindowDAO = new ExecutionWindowDAO(sessionFactory, session);
        // Remove original records of execution window
        if (entity.getExecutionWindows() != null) {
          Iterator<ExecutionWindow> iterator = entity.getExecutionWindows().iterator();

          while (iterator.hasNext()) {
            ExecutionWindow executionWindow = iterator.next();
            iterator.remove();
            executionWindowDAO.delete(executionWindow);
          }

          executionWindowDAO.flush();
        }

        // Add new records of execution window
        if (executionWindowSet.getExecutionWindows() != null
            && executionWindowSet.getExecutionWindows().size() > 0) {
          for (ExecutionWindow execWindow : executionWindowSet.getExecutionWindows()) {
            ExecutionWindow executionWindow = new ExecutionWindow();

            executionWindow.setExecutionWindowSet(entity);
            executionWindow.setDisplayOrder(execWindow.getDisplayOrder());
            executionWindow.setDay(execWindow.getDay());

            if (execWindow.getExecutionTimeSlots() != null
                && execWindow.getExecutionTimeSlots().size() > 0) {
              for (ExecutionTimeSlot execTimeSlot : execWindow.getExecutionTimeSlots()) {
                ExecutionTimeSlot executionTimeSlot = new ExecutionTimeSlot();

                executionTimeSlot.setExecutionWindow(executionWindow);
                executionTimeSlot.setDisplayOrder(execTimeSlot.getDisplayOrder());
                executionTimeSlot.setStartTime(execTimeSlot.getStartTime());
                executionTimeSlot.setEndTime(execTimeSlot.getEndTime());

                executionWindow.getExecutionTimeSlots().add(executionTimeSlot);
              }
            }

            entity.getExecutionWindows().add(executionWindow);
          }
        }

        executionWindowSetDAO.saveOrUpdate(entity);
      } else {
        throw new RecordNotFoundException(
            "Execution window set record not found for the given ExecutionWindowSetID.");
      }
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }

  public void deleteExecutionWindowSet(ExecutionWindowSet executionWindowSet)
      throws ManagerException, RecordNotFoundException, RecordInUseException,
          RecordUnmatchedException {
    try {
      logger.debug(
          "Delete execution window set configuration for execution window set id "
              + executionWindowSet.getId());

      ExecutionWindowAssociationDAO executionWindowAssociationDAO =
          new ExecutionWindowAssociationDAO(sessionFactory, session);

      if (executionWindowAssociationDAO.getCount(
              Restrictions.eq(
                  ExecutionWindowAssociation.EXECUTION_WINDOW_SET_ID, executionWindowSet.getId()))
          > 0) {
        throw new RecordInUseException("Unable to delete. Record is currently referred by others.");
      }

      ExecutionWindowSet entity = executionWindowSetDAO.get(executionWindowSet.getId());

      if (entity != null) {
        if (entity.getModifiedOn().getTime() == executionWindowSet.getModifiedOn().getTime()) {
          executionWindowSetDAO.delete(entity);
        } else {
          throw new RecordUnmatchedException("Row was updated or deleted by another transaction.");
        }
      } else {
        throw new RecordNotFoundException(
            "Execution window set record not found for the given ExecutionWindowSetID.");
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
      return executionWindowSetDAO.getCount(criterion);
    } catch (HibernateException err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err.getMessage(), err);
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      throw new ManagerException(err);
    }
  }
}
