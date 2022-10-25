package com.nextlabs.smartclassifier.service.v1.systemconfig;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.manager.SystemConfigManager;
import com.nextlabs.smartclassifier.dto.v1.SystemConfigDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.dto.v1.response.UpdateResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class SystemConfigUpdateService 
		extends Service {
	
	public SystemConfigUpdateService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public SystemConfigUpdateService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse updateSystemConfig(UpdateRequest updateRequest) {
		UpdateResponse response = new UpdateResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			SystemConfigManager systemConfigManager = new SystemConfigManager(getSessionFactory(), session);
			systemConfigManager.updateSystemConfig(((SystemConfigDTO)updateRequest.getData()).getEntity());
			// Update system configuration into servlet context
			servletContext.setAttribute("SystemConfigs", systemConfigManager.loadConfigs());
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
			response.setMessage(MessageUtil.getMessage("no.entity.found.modify", "system config"));
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
	
	public ServiceResponse updateSystemConfigs(List<SystemConfigDTO> systemConfigDTOs) {
		UpdateResponse response = new UpdateResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			if(systemConfigDTOs != null && systemConfigDTOs.size() > 0) {
				SystemConfigManager systemConfigManager = new SystemConfigManager(getSessionFactory(), session);
				for(SystemConfigDTO systemConfigDTO : systemConfigDTOs) {
					systemConfigManager.updateSystemConfig(systemConfigDTO.getEntity());
				}
				// Update system configuration into servlet context
				servletContext.setAttribute("SystemConfigs", systemConfigManager.loadConfigs());
			}
			
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
			response.setMessage(MessageUtil.getMessage("no.entity.found.modify", "system config"));
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
