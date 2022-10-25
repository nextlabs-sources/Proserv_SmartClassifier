package com.nextlabs.smartclassifier.service.v1.executionwindow;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;

import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;
import com.nextlabs.smartclassifier.database.manager.ExecutionWindowSetManager;
import com.nextlabs.smartclassifier.dto.v1.ExecutionWindowSetDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class ExecutionWindowSetRetrievalService extends Service {

  public ExecutionWindowSetRetrievalService(ServletContext servletContext) {
    super(servletContext);
  }

  public ExecutionWindowSetRetrievalService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse getExecutionWindowSets(RetrievalRequest retrievalRequest) {
    RetrievalResponse response = new RetrievalResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      ExecutionWindowSetManager manager =
          new ExecutionWindowSetManager(getSessionFactory(), session);

      List<Criterion> criterionList = getCriterion(retrievalRequest);
      List<ExecutionWindowSet> executionWindowSets =
          manager.getExecutionWindowSets(
              criterionList, getOrder(retrievalRequest), getPageInfo(retrievalRequest));
      response.setTotalNoOfRecords(manager.getRecordCount(criterionList));

      transaction.commit();

      if (executionWindowSets != null && executionWindowSets.size() > 0) {
        response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
        response.setMessage(MessageUtil.getMessage("success.data.loaded"));

        List<ExecutionWindowSetDTO> executionWindowSetDTOs = new ArrayList<ExecutionWindowSetDTO>();
        for (ExecutionWindowSet executionWindowSet : executionWindowSets) {
          executionWindowSetDTOs.add(new ExecutionWindowSetDTO(executionWindowSet));
        }

        response.setData(executionWindowSetDTOs);
      } else {
        response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
        response.setMessage(MessageUtil.getMessage("no.data.found"));
      }

      response.setPageInfo(getPageInfo(retrievalRequest));
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

  public ServiceResponse getExecutionWindowSet(Long executionWindowSetId) {
    RetrievalResponse response = new RetrievalResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      ExecutionWindowSet executionWindowSet =
          (new ExecutionWindowSetManager(getSessionFactory(), session)
              .getExecutionWindowSetById(executionWindowSetId));

      transaction.commit();

      if (executionWindowSet != null) {
        response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
        response.setMessage(MessageUtil.getMessage("success.data.found"));

        ExecutionWindowSetDTO executionWindowSetDTO = new ExecutionWindowSetDTO(executionWindowSet);
        response.setData(executionWindowSetDTO);
        response.setTotalNoOfRecords(1);
      } else {
        response.setStatusCode(MessageUtil.getMessage("no.data.found.for.criteria.code"));
        response.setMessage(MessageUtil.getMessage("no.data.found.for.criteria"));
      }

      response.setPageInfo(getPageInfo(null));
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
