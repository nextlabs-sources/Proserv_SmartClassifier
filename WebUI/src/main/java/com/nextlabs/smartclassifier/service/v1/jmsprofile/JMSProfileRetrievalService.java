package com.nextlabs.smartclassifier.service.v1.jmsprofile;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;

import com.nextlabs.smartclassifier.database.entity.JMSProfile;
import com.nextlabs.smartclassifier.database.manager.JMSProfileManager;
import com.nextlabs.smartclassifier.dto.v1.JMSProfileDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class JMSProfileRetrievalService extends Service {

  public JMSProfileRetrievalService(ServletContext servletContext) {
    super(servletContext);
  }

  public JMSProfileRetrievalService(
      ServletContext servletContext,
      ServletRequest servletRequest,
      ServletResponse servletResponse) {
    super(servletContext, servletRequest, servletResponse);
  }

  public ServiceResponse getJMSProfiles(RetrievalRequest retrievalRequest) {
    RetrievalResponse response = new RetrievalResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      JMSProfileManager jmsProfileManager = new JMSProfileManager(getSessionFactory(), session);
      List<Criterion> criterionList = getCriterion(retrievalRequest);
      List<JMSProfile> jmsProfiles =
          jmsProfileManager.getJMSProfiles(
              criterionList, getOrder(retrievalRequest), getPageInfo(retrievalRequest));
      response.setTotalNoOfRecords(jmsProfileManager.getRecordCount(criterionList));

      transaction.commit();

      if (jmsProfiles != null && jmsProfiles.size() > 0) {
        response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
        response.setMessage(MessageUtil.getMessage("success.data.loaded"));

        List<JMSProfileDTO> jmsProfileDTOs = new ArrayList<JMSProfileDTO>();
        for (JMSProfile jmsProfile : jmsProfiles) {
          jmsProfileDTOs.add(new JMSProfileDTO(jmsProfile));
        }

        response.setData(jmsProfileDTOs);
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

  public ServiceResponse getJMSProfile(Long jmsProfileId) {
    RetrievalResponse response = new RetrievalResponse();
    Session session = null;
    Transaction transaction = null;

    try {
      session = getSessionFactory().openSession();
      transaction = session.beginTransaction();

      JMSProfile jmsProfile =
          (new JMSProfileManager(getSessionFactory(), session).getJMSProfileById(jmsProfileId));

      transaction.commit();

      if (jmsProfile != null) {
        response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
        response.setMessage(MessageUtil.getMessage("success.data.found"));

        JMSProfileDTO jmsProfileDTO = new JMSProfileDTO(jmsProfile);
        response.setData(jmsProfileDTO);
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
