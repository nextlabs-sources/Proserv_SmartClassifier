package com.nextlabs.smartclassifier.service.v1.rule;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.Action;
import com.nextlabs.smartclassifier.database.entity.CriteriaGroup;
import com.nextlabs.smartclassifier.database.entity.Rule;
import com.nextlabs.smartclassifier.database.manager.RuleManager;
import com.nextlabs.smartclassifier.dto.v1.RuleDTO;
import com.nextlabs.smartclassifier.dto.v1.response.RetrievalResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class RuleExportService 
		extends Service {
	
	public RuleExportService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public RuleExportService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse exportRuleToJson() {
		RetrievalResponse response = new RetrievalResponse();
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			RuleManager ruleManager = new RuleManager(getSessionFactory(), session);
			List<Rule> rules = ruleManager.getRules();
			
			if(rules != null && rules.size() > 0) {
				for(Rule rule : rules) {
					if(rule.getCriteriaGroups() != null) {
						for(CriteriaGroup group : rule.getCriteriaGroups()) {
							group.getCriterias();
						}
					}
					
					if(rule.getActions() != null) {
						for(Action action : rule.getActions()) {
							action.getActionParams();
						}
					}
				}
			}
			
			transaction.commit();
			
			if(rules != null && rules.size() > 0) {
				response.setStatusCode(MessageUtil.getMessage("success.data.exported.code"));
				response.setMessage(MessageUtil.getMessage("success.data.exported"));
				
				List<RuleDTO> ruleDTOs = new ArrayList<RuleDTO>();
				for(Rule rule : rules) {
					ruleDTOs.add(new RuleDTO(rule));
				}
				
				response.setData(ruleDTOs);
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
	
	public void export() {
		Session session = null;
		Transaction transaction = null;
		OutputStream outputStream = null;
		
		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			RuleManager ruleManager = new RuleManager(getSessionFactory(), session);
			List<Rule> rules = ruleManager.getRules();

			if(rules != null && rules.size() > 0) {
				for(Rule rule : rules) {
					if(rule.getCriteriaGroups() != null) {
						for(CriteriaGroup group : rule.getCriteriaGroups()) {
							group.getCriterias();
						}
					}
					
					if(rule.getActions() != null) {
						for(Action action : rule.getActions()) {
							action.getActionParams();
						}
					}
				}
			}
			
			transaction.commit();
			
			List<RuleDTO> ruleDTOs = new ArrayList<RuleDTO>();
			for(Rule rule : rules) {
				ruleDTOs.add(new RuleDTO(rule));
			}
			
			HttpServletResponse httpServletResponse = (HttpServletResponse)servletResponse;
			httpServletResponse.setCharacterEncoding("UTF-16");
			httpServletResponse.setContentType("text/plain");
			httpServletResponse.addHeader("Content-Disposition", "attachment;filename=" + getFileName());
			
			outputStream = httpServletResponse.getOutputStream();
			outputStream.write(gson.toJson(ruleDTOs).getBytes(Charset.forName("UTF-16")));
			outputStream.flush();
		} catch(ManagerException | Exception err) {
			if(transaction != null) {
				try {
					transaction.rollback();
				} catch(Exception rollbackErr) {
					logger.error(rollbackErr.getMessage(), rollbackErr);
				}
			}
			
			logger.error(err.getMessage(), err);
		} finally {
			if(session != null) {
				try {
					session.close();
				} catch(HibernateException err) {
					// Ignore
				}
			}
			
			if(outputStream != null) {
				try {
					outputStream.close();
				} catch(Exception err) {
					// Ignore
				}
			}
		}
	}
	
	private String getFileName() {
		return "Rules-" + (new SimpleDateFormat("yyMMDDhhmmss").format(new Date())) + ".json";
	}
}
