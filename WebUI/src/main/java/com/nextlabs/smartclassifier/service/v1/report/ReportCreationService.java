package com.nextlabs.smartclassifier.service.v1.report;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.Report;
import com.nextlabs.smartclassifier.database.manager.ReportManager;
import com.nextlabs.smartclassifier.dto.v1.ReportDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.response.CreationResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class ReportCreationService 
		extends Service {
	
	public ReportCreationService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public ReportCreationService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse addReport(CreationRequest creationRequest) {
		CreationResponse response = new CreationResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Report reportEntity = ((ReportDTO)creationRequest.getData()).getEntity();
			reportEntity.isDeleted(false);
			reportEntity.setCreatedBy(getUserName());
			
			Long entityId = new ReportManager(getSessionFactory(), session).addReport(reportEntity);
			
			transaction.commit();
			
			response.setEntityId(entityId);
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
