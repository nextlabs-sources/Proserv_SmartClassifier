package com.nextlabs.smartclassifier.service.v1.executionwindow;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.manager.ExecutionWindowSetManager;
import com.nextlabs.smartclassifier.dto.v1.ExecutionWindowSetDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.response.CreationResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class ExecutionWindowSetCreationService extends Service {

  public ExecutionWindowSetCreationService(ServletContext servletContext) {
    super(servletContext);
  }

  public ExecutionWindowSetCreationService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse addExecutionWindowSet(CreationRequest creationRequest) {
    CreationResponse response = new CreationResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      new ExecutionWindowSetManager(getSessionFactory(), session)
          .addExecutionWindowSet(((ExecutionWindowSetDTO) creationRequest.getData()).getEntity());

      transaction.commit();

      response.setStatusCode(MessageUtil.getMessage("success.data.saved.code"));
      response.setMessage(MessageUtil.getMessage("success.data.saved"));
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
