package com.nextlabs.smartclassifier.service.v1.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;
import com.nextlabs.smartclassifier.database.manager.SourceAuthenticationManager;
import com.nextlabs.smartclassifier.dto.v1.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class SourceAuthenticationService extends Service {

  public SourceAuthenticationService(ServletContext servletContext) {
    super(servletContext);
  }

  public SourceAuthenticationService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse getSourceAuthentications() {
    CollectionResponse response = new CollectionResponse();

    Session session = null;
    Transaction trans = null;

    try {
      session = getSessionFactory().openSession();
      trans = session.beginTransaction();

      SourceAuthenticationManager manager =
          new SourceAuthenticationManager(getSessionFactory(), session);
      List<SourceAuthentication> list = manager.getSourceAuthentications();

      trans.commit();

      if (list != null && list.size() > 0) {
        response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
        response.setMessage(MessageUtil.getMessage("success.data.loaded"));

        List<SourceAuthenticationDTO> data = new ArrayList<>();
        for (SourceAuthentication sa : list) {
          data.add(new SourceAuthenticationDTO(sa));
        }

        response.setData(data);

      } else {
        response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
        response.setMessage(MessageUtil.getMessage("no.data.found"));
      }

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
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
      }
    }

    return response;
  }
}
