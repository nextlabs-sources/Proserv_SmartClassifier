package com.nextlabs.smartclassifier.service.v1.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;
import com.nextlabs.smartclassifier.database.manager.AuthenticationHandlerManager;
import com.nextlabs.smartclassifier.dto.v1.AuthenticationHandlerDTO;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class AuthenticationHandlerService extends Service {

  public AuthenticationHandlerService(ServletContext servletContext) {
    super(servletContext);
  }

  public AuthenticationHandlerService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse getAuthenticationHandlers() {
    CollectionResponse response = new CollectionResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      AuthenticationHandlerManager manager =
          new AuthenticationHandlerManager(getSessionFactory(), session);
      List<AuthenticationHandler> authenticationHandlers =
          manager.getAuthenticationHandlers();
      response.setTotalNoOfRecords(manager.getRecordCount(null));

      transaction.commit();

      if (authenticationHandlers != null && authenticationHandlers.size() > 0) {
        response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
        response.setMessage(MessageUtil.getMessage("success.data.loaded"));

        List<AuthenticationHandlerDTO> authenticationHandlerDTOs =
            new ArrayList<AuthenticationHandlerDTO>();
        for (AuthenticationHandler authenticationHandler : authenticationHandlers) {
          authenticationHandlerDTOs.add(new AuthenticationHandlerDTO(authenticationHandler));
        }
        response.setData(authenticationHandlerDTOs);
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
