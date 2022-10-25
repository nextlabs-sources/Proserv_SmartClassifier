package com.nextlabs.smartclassifier.service.v1.actionplugin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;

import com.nextlabs.smartclassifier.database.entity.ActionPlugin;
import com.nextlabs.smartclassifier.database.manager.ActionPluginManager;
import com.nextlabs.smartclassifier.dto.v1.ActionPluginDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class ActionPluginRetrievalService 
		extends Service {
	
	public ActionPluginRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public ActionPluginRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getActionPluginWithFixedParameters(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			ActionPluginManager actionPluginManager = new ActionPluginManager(getSessionFactory(), session);
			List<Criterion> criterionList = getCriterion(retrievalRequest);
			List<ActionPlugin> actionPlugins = actionPluginManager.getActionPluginWithFixedParameters(criterionList, getOrder(retrievalRequest), getPageInfo(retrievalRequest));
			response.setTotalNoOfRecords(actionPluginManager.getFixedParameterPluginRecordCount(criterionList));
			
			transaction.commit();
			
			if(actionPlugins != null && actionPlugins.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<ActionPluginDTO> actionPluginDTOs = new ArrayList<ActionPluginDTO>();
				for(ActionPlugin actionPlugin : actionPlugins) {
					actionPluginDTOs.add(new ActionPluginDTO(actionPlugin));
				}
				response.setData(actionPluginDTOs);
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
	
	public ServiceResponse getActionPluginFixedParameter(Long actionPluginId) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			ActionPlugin actionPlugin = (new ActionPluginManager(getSessionFactory(), session).getActionPluginFixedParameterById(actionPluginId));
			
			transaction.commit();
			
			if(actionPlugin != null) {
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
				
				ActionPluginDTO actionPluginDTO = new ActionPluginDTO(actionPlugin);
				response.setData(actionPluginDTO);
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
