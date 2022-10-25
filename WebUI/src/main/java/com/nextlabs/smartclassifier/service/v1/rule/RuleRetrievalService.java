package com.nextlabs.smartclassifier.service.v1.rule;

import com.nextlabs.smartclassifier.constant.RuleExecutionOutcome;
import com.nextlabs.smartclassifier.database.entity.Rule;
import com.nextlabs.smartclassifier.database.entity.RuleExecution;
import com.nextlabs.smartclassifier.database.manager.RuleExecutionManager;
import com.nextlabs.smartclassifier.database.manager.RuleManager;
import com.nextlabs.smartclassifier.dto.v1.RuleDTO;
import com.nextlabs.smartclassifier.dto.v1.request.RetrievalRequest;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

public class RuleRetrievalService 
		extends Service {
	
	public RuleRetrievalService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public RuleRetrievalService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getRules(RetrievalRequest retrievalRequest) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			RuleManager ruleManager = new RuleManager(getSessionFactory(), session);
			RuleExecutionManager ruleExecutionManager = new RuleExecutionManager(getSessionFactory(), session);
			List<Criterion> criterionList = getCriterion(retrievalRequest);
			List<Rule> rules = ruleManager.getRules(criterionList, getOrder(retrievalRequest), getPageInfo(retrievalRequest));
			
			if(rules != null) {
				for(Rule rule : rules) {
					RuleExecution lastExecution = ruleExecutionManager.getLastExecution(rule.getId());
					
					if(lastExecution != null) {
						rule.setLastExecutionDate(lastExecution.getStartTime());
						rule.setLastExecutionOutcome(RuleExecutionOutcome.getOutcome(lastExecution.getOutcome()));
					}
				}
			}
			
			response.setTotalNoOfRecords(ruleManager.getRecordCount(criterionList));
			
			transaction.commit();
			
			if(rules != null && rules.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
				response.setMessage(MessageUtil.getMessage("success.data.loaded"));
				
				List<RuleDTO> ruleDTOs = new ArrayList<RuleDTO>();
				for(Rule rule : rules) {
					ruleDTOs.add(new RuleDTO(rule));
				}
				
				response.setData(ruleDTOs);
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
	
	public ServiceResponse getRule(Long ruleId) {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Rule rule = (new RuleManager(getSessionFactory(), session).getRuleById(ruleId));
			
			transaction.commit();
			
			if(rule != null) {
				response.setStatusCode(MessageUtil.getMessage("success.data.found.code"));
				response.setMessage(MessageUtil.getMessage("success.data.found"));
				
				RuleDTO ruleDTO = new RuleDTO(rule);
				response.setData(ruleDTO);
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
