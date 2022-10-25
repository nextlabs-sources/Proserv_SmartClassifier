package com.nextlabs.smartclassifier.service.v1.jmsprofile;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.manager.JMSProfileManager;
import com.nextlabs.smartclassifier.dto.v1.JMSProfileDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.response.CreationResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class JMSProfileCreationService 
		extends Service {
	
	public JMSProfileCreationService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public JMSProfileCreationService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse addJMSProfile(CreationRequest creationRequest) {
		CreationResponse response = new CreationResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			new JMSProfileManager(getSessionFactory(), session).addJMSProfile(((JMSProfileDTO)creationRequest.getData()).getEntity());
			
			transaction.commit();
			
			response.setStatusCode(MessageUtil.getMessage("success.data.saved.code"));
			response.setMessage(MessageUtil.getMessage("success.data.saved"));
		} catch(ManagerException | Exception err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("server.error.code"));
			response.setMessage(MessageUtil.getMessage("server.error", err.getMessage()));
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch(HibernateException err) {
					// Ignore
				}
			}
		}
		
		return response;
	}
}
