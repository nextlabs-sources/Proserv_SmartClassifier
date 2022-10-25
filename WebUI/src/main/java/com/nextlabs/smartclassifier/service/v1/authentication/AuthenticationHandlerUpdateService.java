package com.nextlabs.smartclassifier.service.v1.authentication;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.manager.AuthenticationHandlerManager;
import com.nextlabs.smartclassifier.dto.v1.AuthenticationHandlerDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.dto.v1.response.UpdateResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class AuthenticationHandlerUpdateService 
		extends Service {
	
	public AuthenticationHandlerUpdateService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public AuthenticationHandlerUpdateService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse updateAuthenticationHandler(UpdateRequest updateRequest) {
		UpdateResponse response = new UpdateResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			new AuthenticationHandlerManager(getSessionFactory(), session).updateAuthenticationHandler(((AuthenticationHandlerDTO)updateRequest.getData()).getEntity());
			
			transaction.commit();
			
			response.setStatusCode(MessageUtil.getMessage("success.data.modified.code"));
			response.setMessage(MessageUtil.getMessage("success.data.modified"));
		} catch(RecordNotFoundException err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("no.entity.found.modify.code"));
			response.setMessage(MessageUtil.getMessage("no.entity.found.modify", "Authentication handler"));
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
