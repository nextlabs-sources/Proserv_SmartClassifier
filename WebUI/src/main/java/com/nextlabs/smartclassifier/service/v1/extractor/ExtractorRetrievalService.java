package com.nextlabs.smartclassifier.service.v1.extractor;

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
import com.nextlabs.smartclassifier.database.entity.Extractor;
import com.nextlabs.smartclassifier.database.manager.ExtractorManager;
import com.nextlabs.smartclassifier.dto.v1.ExtractorDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class ExtractorRetrievalService 
		extends Service {
	
	public ExtractorRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public ExtractorRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getExtractors(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			ExtractorManager extractorManager = new ExtractorManager(getSessionFactory(), session);
			List<Criterion> criterionList = getCriterion(retrievalRequest);
			List<Extractor> extractors = extractorManager.getExtractors(criterionList, getOrder(retrievalRequest), getPageInfo(retrievalRequest));
			response.setTotalNoOfRecords(extractorManager.getRecordCount(criterionList));
			
			transaction.commit();
			
			if(extractors != null && extractors.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<ExtractorDTO> extractorDTOs = new ArrayList<ExtractorDTO>();
				for(Extractor extractor : extractors) {
					ExtractorDTO extractorDTO = new ExtractorDTO(extractor);
					extractorDTO.setStatus(getComponentStatus(extractor.getLastHeartbeat(), SystemConfigKey.EXTRACTOR_INTERVAL_HEARTBEAT));
					extractorDTOs.add(extractorDTO);
				}
				response.setData(extractorDTOs);
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
	
	public ServiceResponse getExtractor(Long extractorId) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Extractor extractor = (new ExtractorManager(getSessionFactory(), session).getExtractorById(extractorId));
			
			transaction.commit();
			
			if(extractor != null) {
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
				
				ExtractorDTO extractorDTO = new ExtractorDTO(extractor);
				response.setData(extractorDTO);
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
