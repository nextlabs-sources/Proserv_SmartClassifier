package com.nextlabs.smartclassifier.service.v1.authentication;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;

import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;
import com.nextlabs.smartclassifier.database.manager.AuthenticationHandlerManager;
import com.nextlabs.smartclassifier.dto.v1.AuthenticationHandlerDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class AuthenticationHandlerRetrievalService 
		extends Service {
	
	public AuthenticationHandlerRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public AuthenticationHandlerRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getAuthenticationHandlers(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			AuthenticationHandlerManager authenticationHandlerManager = new AuthenticationHandlerManager(getSessionFactory(), session);
			List<Criterion> criterionList = getCriterion(retrievalRequest);
			List<AuthenticationHandler> authenticationHandlers = authenticationHandlerManager.getAuthenticationHandlers(criterionList, getOrder(retrievalRequest), getPageInfo(retrievalRequest));
			
			response.setTotalNoOfRecords(authenticationHandlerManager.getRecordCount(criterionList));
			
			transaction.commit();
			
			if(authenticationHandlers != null && authenticationHandlers.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<AuthenticationHandlerDTO> authenticationHandlerDTOs = new ArrayList<AuthenticationHandlerDTO>();
				for(AuthenticationHandler authenticationHandler : authenticationHandlers) {
					authenticationHandlerDTOs.add(new AuthenticationHandlerDTO(authenticationHandler));
				}
				
				response.setData(authenticationHandlerDTOs);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
			
			response.setPageInfo(getPageInfo(retrievalRequest));
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
	
	public ServiceResponse getAuthenticationHandler(Long authenticationHandlerId) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			AuthenticationHandler authenticationHandler = (new AuthenticationHandlerManager(getSessionFactory(), session)
					.getAuthenticationHandlerById(authenticationHandlerId));
			
			transaction.commit();
			
			if(authenticationHandler != null) {
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
				
				AuthenticationHandlerDTO authenticationHandlerDTO = new AuthenticationHandlerDTO(authenticationHandler);
				response.setData(authenticationHandlerDTO);
				response.setTotalNoOfRecords(1);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.for.criteria.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found.for.criteria"));
			}
			
			response.setPageInfo(getPageInfo(null));
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
