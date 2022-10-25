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
import com.nextlabs.smartclassifier.dto.v1.request.DeletionRequest;
import com.nextlabs.smartclassifier.dto.v1.response.DeletionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class ReportDeletionService 
		extends Service {
	
	public ReportDeletionService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public ReportDeletionService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse deleteReport(DeletionRequest deletionRequest) {
		DeletionResponse response = new DeletionResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Report reportEntity = ((ReportDTO)deletionRequest.getData()).getEntity();
			reportEntity.setModifiedBy(getUserName());
			
			new ReportManager(getSessionFactory(), session).deleteReport(reportEntity);
			
			transaction.commit();
			
			response.setStatusCode(MessageUtil.getMessage("success.data.deleted.code"));
			response.setMessage(MessageUtil.getMessage("success.data.deleted"));
		} catch(RecordInUseException err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("delete.referred.entity.code"));
			response.setMessage(MessageUtil.getMessage("delete.referred.entity"));
		} catch(RecordUnmatchedException err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("no.entity.match.for.modification.code"));
			response.setMessage(MessageUtil.getMessage("no.entity.match.for.modification"));
		} catch(RecordNotFoundException err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			logger.error(err.getMessage(), err);
			response.setStatusCode(MessageUtil.getMessage("no.entity.found.delete.code"));
			response.setMessage(MessageUtil.getMessage("no.entity.found.delete", "rule"));
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
