package com.nextlabs.smartclassifier.service.v1.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.nextlabs.smartclassifier.database.entity.Rule;
import com.nextlabs.smartclassifier.database.manager.RuleManager;
import com.nextlabs.smartclassifier.dto.v1.RuleDTO;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.DateUtil;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class RuleImportService 
		extends Service {
	
	public RuleImportService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public RuleImportService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse importRule(List<RuleDTO> ruleDTOs) {
		ServiceResponse response = new ServiceResponse();
		
		if(ruleDTOs != null && ruleDTOs.size() > 0) {
			Session session = null;
			Transaction transaction = null;
			List<Rule> rules = new ArrayList<Rule>(ruleDTOs.size());
			
			for(RuleDTO ruleDTO : ruleDTOs) {
				ruleDTO.setName(getImportRuleName(ruleDTO.getName()));
				rules.add(ruleDTO.getEntity());
			}
			
			try {
				session = getSessionFactory().openSession();
				transaction = session.beginTransaction();
				
				new RuleManager(getSessionFactory(), session).importRule(rules);
				
				transaction.commit();
				
				response.setStatusCode(MessageUtil.getMessage("success.data.imported.code"));
				response.setMessage(MessageUtil.getMessage("success.data.imported"));
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
		} else {
			response.setStatusCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
			response.setMessage(MessageUtil.getMessage("invalid.input.empty.collection", "Rules"));
		}
		
		return response;
	}
	
	private String getImportRuleName(String ruleName) {
		if(StringUtils.isNotBlank(ruleName)) {
			String importRuleName = ruleName + " (Imported at " + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm") + ")";
			
			if(importRuleName.length() > 320) {
				return importRuleName.substring(0, 320);
			}
			
			return importRuleName;
		}
		
		return ruleName;
	}
}
