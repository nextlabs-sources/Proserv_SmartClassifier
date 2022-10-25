package com.nextlabs.smartclassifier.service.v1.eventlog;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.manager.EventLogManager;
import com.nextlabs.smartclassifier.dto.v1.request.PurgeRequest;
import com.nextlabs.smartclassifier.dto.v1.response.PurgeResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.DateUtil;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class EventLogPurgeService 
		extends Service {
	
	public EventLogPurgeService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public EventLogPurgeService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse purgeEventLog(PurgeRequest purgeRequest) {
		PurgeResponse response = new PurgeResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			new EventLogManager(getSessionFactory(), session).purgeEventLogs(DateUtil.toStartOfTheDay(purgeRequest.getDateInMillis()).getTime());
			
			transaction.commit();
			
			response.setStatusCode(MessageUtil.getMessage("success.data.deleted.code"));
			response.setMessage(MessageUtil.getMessage("success.data.deleted"));
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
