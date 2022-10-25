package com.nextlabs.smartclassifier.service.v1.sourceauthentication;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.manager.SourceAuthenticationManager;
import com.nextlabs.smartclassifier.dto.v1.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.dto.v1.response.UpdateResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class SourceAuthenticationModificationService extends Service {

  public SourceAuthenticationModificationService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public SourceAuthenticationModificationService(ServletContext servletContext) {
    super(servletContext);
  }

  public ServiceResponse updateSourceAuthentication(UpdateRequest updateRequest) {
    UpdateResponse response = new UpdateResponse();
    Session session = null;
    Transaction trans = null;

    try {
      session = getSessionFactory().openSession();
      trans = session.beginTransaction();

      SourceAuthenticationManager manager =
          new SourceAuthenticationManager(getSessionFactory(), session);

      SourceAuthenticationDTO saDTO = (SourceAuthenticationDTO) updateRequest.getData();
      manager.updateSourceAuthentication(saDTO.getEntity());

      trans.commit();

      response.setStatusCode(MessageUtil.getMessage("success.data.modified.code"));
      response.setMessage(MessageUtil.getMessage("success.data.modified"));
    } catch (RecordNotFoundException e) {
      if (trans != null) {
        try {
          trans.rollback();
        } catch (Exception rollbackErr) {
          logger.error(rollbackErr.getMessage(), rollbackErr);
        }
      }
      logger.error(e.getMessage(), e);

      response.setStatusCode(MessageUtil.getMessage("no.entity.found.modify.code"));
      response.setMessage(
          MessageUtil.getMessage("no.entity.found.modify", "Source Authentication"));
    } catch (ManagerException | Exception e) {
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
