package com.nextlabs.smartclassifier.service.v1.sourceauthentication;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.entity.SourceAuthentication;
import com.nextlabs.smartclassifier.database.manager.SourceAuthenticationManager;
import com.nextlabs.smartclassifier.dto.v1.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class SourceAuthenticationRetrievalService extends Service {

  public SourceAuthenticationRetrievalService(ServletContext servletContext) {
    super(servletContext);
  }

  public SourceAuthenticationRetrievalService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {

    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse getSourceAuthentications(RetrievalRequest request) {
    RetrievalResponse response = new RetrievalResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      SourceAuthenticationManager manager =
          new SourceAuthenticationManager(getSessionFactory(), session);

      List<Criterion> criteria = getCriterion(request);
      List<Order> orders = getOrder(request);
      PageInfo pageInfo = getPageInfo(request);

      List<SourceAuthentication> sourceAuthentications =
          manager.getSourceAuthentications(criteria, orders, pageInfo);

      response.setTotalNoOfRecords(manager.getRecordCount(criteria));

      transaction.commit();

      if (sourceAuthentications != null && sourceAuthentications.size() > 0) {
        response.setMessage(MessageUtil.getMessage("success.data.loaded"));
        response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));

        List<SourceAuthenticationDTO> data = new ArrayList<>();
        for (SourceAuthentication sa : sourceAuthentications) {
          data.add(new SourceAuthenticationDTO(sa));
        }

        response.setData(data);

      } else {
        response.setMessage(MessageUtil.getMessage("no.data.found"));
        response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
      }

      response.setPageInfo(pageInfo);

    } catch (ManagerException | Exception err) {
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }
      }
      logger.error(err.getMessage(), err);
      response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
      response.setStatusCode(MessageUtil.getMessage("server.error.code"));

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

  public ServiceResponse getSourceAuthentication(Long id) {
    RetrievalResponse response = new RetrievalResponse();
    Session session = null;
    Transaction trans = null;

    try {
      session = getSessionFactory().openSession();
      trans = session.beginTransaction();

      SourceAuthenticationManager manager =
          new SourceAuthenticationManager(getSessionFactory(), session);

      SourceAuthentication sa = manager.getSourceAuthenticationById(id);

      trans.commit();

      if (sa != null) {
        response.setMessage(MessageUtil.getMessage("success.data.found"));
        response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));

        response.setData(new SourceAuthenticationDTO(sa));
        response.setTotalNoOfRecords(1);
      } else {
        response.setMessage(MessageUtil.getMessage("no.data.found"));
        response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
      }

      response.setPageInfo(getPageInfo(null));

    } catch (ManagerException | Exception e) {
      if (trans != null) {
        try {
          trans.rollback();
        } catch (Exception err) {
          logger.error(err.getMessage(), err);
        }
      }
      logger.error(e.getMessage(), e);
      response.setMessage(MessageUtil.getMessage("server.error", e.getMessage()));
      response.setStatusCode("server.error.code");
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
