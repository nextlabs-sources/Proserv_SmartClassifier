package com.nextlabs.smartclassifier.service.v1.rule;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.database.manager.RuleExecutionManager;
import com.nextlabs.smartclassifier.dto.v1.RuleDTO;
import com.nextlabs.smartclassifier.dto.v1.request.ExecutionRequest;
import com.nextlabs.smartclassifier.dto.v1.response.DeletionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class RuleExecutionService 
		extends Service {
	
	public RuleExecutionService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public RuleExecutionService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse executeRule(ExecutionRequest executionRequest) {
		DeletionResponse response = new DeletionResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();		
			
			new RuleExecutionManager(getSessionFactory(), session).scheduleExecution(((RuleDTO)executionRequest.getData()).getId(), ExecutionType.ON_DEMAND.getCode());
			
			transaction.commit();
			
			response.setStatusCode(MessageUtil.getMessage("success.data.saved.code"));
			response.setMessage(MessageUtil.getMessage("success.data.saved"));
		} catch(RecordNotFoundException err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
			response.setMessage(MessageUtil.getMessage("no.data.found"));
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
