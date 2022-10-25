package com.nextlabs.smartclassifier.service.v1.dashboard;

import com.nextlabs.smartclassifier.constant.ComponentStatus;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.entity.Extractor;
import com.nextlabs.smartclassifier.database.entity.Rule;
import com.nextlabs.smartclassifier.database.entity.RuleEngine;
import com.nextlabs.smartclassifier.database.entity.Watcher;
import com.nextlabs.smartclassifier.database.manager.DocumentRecordManager;
import com.nextlabs.smartclassifier.database.manager.ExtractorManager;
import com.nextlabs.smartclassifier.database.manager.RuleEngineManager;
import com.nextlabs.smartclassifier.database.manager.RuleManager;
import com.nextlabs.smartclassifier.database.manager.SystemConfigManager;
import com.nextlabs.smartclassifier.database.manager.WatcherManager;
import com.nextlabs.smartclassifier.dto.v1.CodeValueDTO;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.dto.v1.response.SummaryResponse;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummaryService 
		extends Service {
	
	public DashboardSummaryService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public DashboardSummaryService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getWatcherSummary() {
		SummaryResponse response = new SummaryResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			List<Watcher> watchers = new WatcherManager(getSessionFactory(), session).getWatchersWithLastHeartbeat();
			
			transaction.commit();
			
			List<CodeValueDTO> summaries = new ArrayList<CodeValueDTO>();
			int healthy = 0, warning = 0, critical = 0;
			
			if(watchers != null) {
				for(Watcher watcher : watchers) {
					String status = getComponentStatus(watcher.getLastHeartbeat(), SystemConfigKey.WATCHER_INTERVAL_HEARTBEAT);
					
					if(ComponentStatus.HEALTHY.getName().equalsIgnoreCase(status)) {
						healthy++;
					} else if(ComponentStatus.WARNING.getName().equalsIgnoreCase(status)) {
						warning++;
					} else if(ComponentStatus.CRITICAL.getName().equalsIgnoreCase(status)) {
						critical++;
					}
				}
			}
			
			summaries.add(new CodeValueDTO("Total", Integer.toString(watchers.size())));
			summaries.add(new CodeValueDTO(ComponentStatus.HEALTHY.getName(), Integer.toString(healthy)));
			summaries.add(new CodeValueDTO(ComponentStatus.WARNING.getName(), Integer.toString(warning)));
			summaries.add(new CodeValueDTO(ComponentStatus.CRITICAL.getName(), Integer.toString(critical)));
			
			response.setData(summaries);
			try {
				response.setCriticalHeartbeat(Integer.parseInt(getSystemConfig(SystemConfigKey.HEARTBEAT_CRITICAL_FAILED_COUNT)));
			} catch(NumberFormatException err) {
				// Ignore
			}
			response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
			response.setMessage(MessageUtil.getMessage("success.data.loaded"));
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
	
	public ServiceResponse getExtractorSummary() {
		SummaryResponse response = new SummaryResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			List<Extractor> extractors = new ExtractorManager(getSessionFactory(), session).getExtractorsWithLastHeartbeat();
			
			transaction.commit();
			
			List<CodeValueDTO> summaries = new ArrayList<CodeValueDTO>();
			int healthy = 0, warning = 0, critical = 0;
			
			if(extractors != null) {
				for(Extractor extractor : extractors) {
					String status = getComponentStatus(extractor.getLastHeartbeat(), SystemConfigKey.EXTRACTOR_INTERVAL_HEARTBEAT);
					
					if(ComponentStatus.HEALTHY.getName().equalsIgnoreCase(status)) {
						healthy++;
					} else if(ComponentStatus.WARNING.getName().equalsIgnoreCase(status)) {
						warning++;
					} else if(ComponentStatus.CRITICAL.getName().equalsIgnoreCase(status)) {
						critical++;
					}
				}
			}
			
			summaries.add(new CodeValueDTO("Total", Integer.toString(extractors.size())));
			summaries.add(new CodeValueDTO(ComponentStatus.HEALTHY.getName(), Integer.toString(healthy)));
			summaries.add(new CodeValueDTO(ComponentStatus.WARNING.getName(), Integer.toString(warning)));
			summaries.add(new CodeValueDTO(ComponentStatus.CRITICAL.getName(), Integer.toString(critical)));
			
			response.setData(summaries);
			try {
				response.setCriticalHeartbeat(Integer.parseInt(getSystemConfig(SystemConfigKey.HEARTBEAT_CRITICAL_FAILED_COUNT)));
			} catch(NumberFormatException err) {
				// Ignore
			}
			response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
			response.setMessage(MessageUtil.getMessage("success.data.loaded"));
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
	
	public ServiceResponse getRuleEngineSummary() {
		SummaryResponse response = new SummaryResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			List<RuleEngine> ruleEngines = new RuleEngineManager(getSessionFactory(), session).getRuleEnginesWithLastHeartbeat();
			
			transaction.commit();
			
			List<CodeValueDTO> summaries = new ArrayList<CodeValueDTO>();
			int healthy = 0, warning = 0, critical = 0;
			
			if(ruleEngines != null) {
				for(RuleEngine ruleEngine : ruleEngines) {
					String status = getComponentStatus(ruleEngine.getLastHeartbeat(), SystemConfigKey.ENGINE_INTERVAL_HEARTBEAT);
					
					if(ComponentStatus.HEALTHY.getName().equalsIgnoreCase(status)) {
						healthy++;
					} else if(ComponentStatus.WARNING.getName().equalsIgnoreCase(status)) {
						warning++;
					} else if(ComponentStatus.CRITICAL.getName().equalsIgnoreCase(status)) {
						critical++;
					}
				}
			}
			
			summaries.add(new CodeValueDTO("Total", Integer.toString(ruleEngines.size())));
			summaries.add(new CodeValueDTO(ComponentStatus.HEALTHY.getName(), Integer.toString(healthy)));
			summaries.add(new CodeValueDTO(ComponentStatus.WARNING.getName(), Integer.toString(warning)));
			summaries.add(new CodeValueDTO(ComponentStatus.CRITICAL.getName(), Integer.toString(critical)));
			
			response.setData(summaries);
			try {
				response.setCriticalHeartbeat(Integer.parseInt(getSystemConfig(SystemConfigKey.HEARTBEAT_CRITICAL_FAILED_COUNT)));
			} catch(NumberFormatException err) {
				// Ignore
			}
			response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
			response.setMessage(MessageUtil.getMessage("success.data.loaded"));
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
	
	public ServiceResponse getRuleSummary() {
		SummaryResponse response = new SummaryResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			List<Rule> rules = new RuleManager(getSessionFactory(), session).getRules();
			
			transaction.commit();
			
			List<CodeValueDTO> summaries = new ArrayList<CodeValueDTO>();
			int total = 0, active = 0, inactive = 0;
			
			if(rules != null) {
				for(Rule rule : rules) {
					if(!rule.isDeleted()) {
						total++;
						
						if(rule.isActive()) {
							active++;
						} else {
							inactive++;
						}
					}
				}
			}
			
			summaries.add(new CodeValueDTO("Total", Integer.toString(total)));
			summaries.add(new CodeValueDTO("Active", Integer.toString(active)));
			summaries.add(new CodeValueDTO("Inactive", Integer.toString(inactive)));
			
			response.setData(summaries);
			response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
			response.setMessage(MessageUtil.getMessage("success.data.loaded"));
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
	
	public ServiceResponse getDocumentSummary() {
		SummaryResponse response = new SummaryResponse();
		Session session = null;
		Transaction transaction = null;

		try {
			List<CodeValueDTO> summaries = new ArrayList<CodeValueDTO>();
			session = getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			DocumentRecordManager documentRecordManager = new DocumentRecordManager(getSessionFactory(), session);
			SystemConfigManager systemConfigManager = new SystemConfigManager(getSessionFactory(), session);
			long total = 0, success = 0, fail = 0;
			
			total = documentRecordManager.getTotalCount();
			success = documentRecordManager.getSuccessCount();
			fail = total - success;
			
			summaries.add(new CodeValueDTO("Total document", Long.toString(total)));
			summaries.add(new CodeValueDTO("Success extraction", Long.toString(success)));
			summaries.add(new CodeValueDTO("Fail extraction", Long.toString(fail)));
			try {
				summaries.add(new CodeValueDTO("License expiry date", systemConfigManager.getConfig(SystemConfigKey.LICENSE_EXPIRY_DATE)));
			} catch(ManagerException err) {
				summaries.add(new CodeValueDTO("License expiry date", null));
			}
			try{
				summaries.add(new CodeValueDTO("Data size", systemConfigManager.getConfig(SystemConfigKey.LICENSE_DATA_SIZE)));
			} catch(ManagerException err) {
				summaries.add(new CodeValueDTO("Data size", "0"));
			}

			try {
				summaries.add(new CodeValueDTO("License validity", systemConfigManager.getConfig(SystemConfigKey.LICENSE_VALIDITY)));
			} catch(ManagerException err) {
				summaries.add(new CodeValueDTO("License validity", "false"));
			}

			summaries.add(new CodeValueDTO("Used size", Long.toString(documentRecordManager.getVolumeUsed())));
			
			transaction.commit();
			
			response.setData(summaries);
			response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
			response.setMessage(MessageUtil.getMessage("success.data.loaded"));
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
