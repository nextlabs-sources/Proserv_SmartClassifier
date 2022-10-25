package com.nextlabs.smartclassifier.service.v1.sourceauthentication;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;
import com.nextlabs.smartclassifier.database.manager.SourceAuthenticationManager;
import com.nextlabs.smartclassifier.dto.v1.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.request.DeletionRequest;
import com.nextlabs.smartclassifier.dto.v1.response.DeletionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class SourceAuthenticationDeletionService extends Service {

  public SourceAuthenticationDeletionService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
    // TODO Auto-generated constructor stub
  }

  public SourceAuthenticationDeletionService(ServletContext servletContext) {
    super(servletContext);
    // TODO Auto-generated constructor stub
  }

  public ServiceResponse deleteSourceAuthentication(DeletionRequest request) {
    DeletionResponse response = new DeletionResponse();
    Session session = null;
    Transaction trans = null;

    try {
      session = getSessionFactory().openSession();
      trans = session.beginTransaction();

      SourceAuthenticationManager manager =
          new SourceAuthenticationManager(getSessionFactory(), session);

      SourceAuthentication entity = ((SourceAuthenticationDTO) request.getData()).getEntity();
      manager.deleteSourceAuthentication(entity);

      trans.commit();
      response.setStatusCode(MessageUtil.getMessage("success.data.deleted.code"));
      response.setMessage(MessageUtil.getMessage("success.data.deleted"));
    } catch (RecordInUseException e) {
      if (trans != null) {
        try {
          trans.rollback();
        } catch (Exception rollbackErr) {
          logger.error(rollbackErr.getMessage(), rollbackErr);
        }
      }
      logger.error(e.getMessage(), e);
      response.setStatusCode(MessageUtil.getMessage("delete.referred.entity.code"));
      response.setMessage(MessageUtil.getMessage("delete.referred.entity"));
    } catch (RecordNotFoundException e) {
      if (trans != null) {
        try {
          trans.rollback();
        } catch (Exception rollbackErr) {
          logger.error(rollbackErr.getMessage(), rollbackErr);
        }
      }
      logger.error(e.getMessage(), e);
      response.setStatusCode(MessageUtil.getMessage("no.entity.found.delete.code"));
      response.setMessage(
          MessageUtil.getMessage("no.entity.found.delete", "source authentication"));
    } catch (RecordUnmatchedException e) { // TODO Auto-generated catch block
      if (trans != null) {
        try {
          trans.rollback();
        } catch (Exception rollbackErr) {
          logger.error(rollbackErr.getMessage(), rollbackErr);
        }
      }
      logger.error(e.getMessage(), e);
      response.setStatusCode(MessageUtil.getMessage("no.entity.match.for.modification.code"));
      response.setMessage(MessageUtil.getMessage("no.entity.match.for.modification"));
    } catch (ManagerException | Exception e) { // TODO Auto-generated catch block
      if (trans != null) {
        try {
          trans.rollback();
        } catch (Exception rollbackErr) {
          logger.error(rollbackErr.getMessage(), rollbackErr);
        }
      }
      logger.error(e.getMessage(), e);
      response.setStatusCode(MessageUtil.getMessage("server.error.code"));
      response.setMessage(MessageUtil.getMessage("server.error", e.getMessage()));
    } finally {
      if (session != null) {
        try {
          session.close();
        } catch (HibernateException err) {
          logger.error(err.getMessage(), err);
        }
      }
    }

    return response;
  }
}
