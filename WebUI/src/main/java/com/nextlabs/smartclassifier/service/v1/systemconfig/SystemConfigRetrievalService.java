package com.nextlabs.smartclassifier.service.v1.systemconfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.SystemConfig;
import com.nextlabs.smartclassifier.database.entity.SystemConfigGroup;
import com.nextlabs.smartclassifier.database.manager.SystemConfigGroupManager;
import com.nextlabs.smartclassifier.database.manager.SystemConfigManager;
import com.nextlabs.smartclassifier.dto.v1.SystemConfigDTO;
import com.nextlabs.smartclassifier.dto.v1.SystemConfigGroupDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class SystemConfigRetrievalService 
		extends Service {
	
	public SystemConfigRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public SystemConfigRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getSystemConfigs(RetrievalRequest retrievalRequest) {
		CollectionResponse response = new CollectionResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			SystemConfigGroupManager systemConfigGroupManager = new SystemConfigGroupManager(getSessionFactory(), session);
			List<SystemConfigGroup> systemConfigGroups = systemConfigGroupManager.getSystemConfigGroups();
			response.setTotalNoOfRecords(systemConfigGroupManager.getRecordCount(null));
			
			transaction.commit();
			
			if(systemConfigGroups != null && systemConfigGroups.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<SystemConfigGroupDTO> systemConfigGroupDTOs = new ArrayList<SystemConfigGroupDTO>();
				for(SystemConfigGroup systemConfigGroup : systemConfigGroups) {
					systemConfigGroupDTOs.add(new SystemConfigGroupDTO(systemConfigGroup));
				}
				response.setData(systemConfigGroupDTOs);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
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
	
	public ServiceResponse getSystemConfig(Long systemConfigId) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			SystemConfig systemConfig = (new SystemConfigManager(getSessionFactory(), session).getSystemConfigById(systemConfigId));
			
			transaction.commit();
			
			if(systemConfig != null) {
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
				
				SystemConfigDTO systemConfigDTO = new SystemConfigDTO(systemConfig);
				response.setData(systemConfigDTO);
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
