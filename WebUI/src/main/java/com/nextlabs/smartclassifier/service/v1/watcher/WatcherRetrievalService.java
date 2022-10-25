package com.nextlabs.smartclassifier.service.v1.watcher;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;

import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.entity.Watcher;
import com.nextlabs.smartclassifier.database.manager.WatcherManager;
import com.nextlabs.smartclassifier.dto.v1.WatcherDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class WatcherRetrievalService 
		extends Service {
	
	public WatcherRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public WatcherRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getWatchers(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			WatcherManager watcherManager = new WatcherManager(getSessionFactory(), session);
			List<Criterion> criterionList = getCriterion(retrievalRequest);
			List<Watcher> watchers = watcherManager.getWatchers(criterionList, getOrder(retrievalRequest), getPageInfo(retrievalRequest));
			response.setTotalNoOfRecords(watcherManager.getRecordCount(criterionList));
			
			transaction.commit();
			
			if(watchers != null && watchers.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<WatcherDTO> watcherDTOs = new ArrayList<WatcherDTO>();
				for(Watcher watcher : watchers) {
					WatcherDTO watcherDTO = new WatcherDTO(watcher);
					watcherDTO.setStatus(getComponentStatus(watcher.getLastHeartbeat(), SystemConfigKey.WATCHER_INTERVAL_HEARTBEAT));
					watcherDTOs.add(watcherDTO);
				}
				response.setData(watcherDTOs);
			} else {
				response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
				response.setMessage(MessageUtil.getMessage("no.data.found"));
			}
			
			response.setPageInfo(getPageInfo(retrievalRequest));
		} catch(ManagerException err) {
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
		} catch(Exception err) {
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
	
	public ServiceResponse getWatcher(Long watcherId) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Watcher watcher = (new WatcherManager(getSessionFactory(), session).getWatcherById(watcherId));
			
			transaction.commit();
			
			if(watcher != null) {
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
				
				WatcherDTO watcherDTO = new WatcherDTO(watcher);
				response.setData(watcherDTO);
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
