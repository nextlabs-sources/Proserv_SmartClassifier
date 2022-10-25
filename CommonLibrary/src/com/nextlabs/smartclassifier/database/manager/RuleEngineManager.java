package com.nextlabs.smartclassifier.database.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.database.dao.ExecutionWindowAssociationDAO;
import com.nextlabs.smartclassifier.database.dao.ExecutionWindowSetDAO;
import com.nextlabs.smartclassifier.database.dao.HeartbeatDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.dao.RuleEngineDAO;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowAssociation;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;
import com.nextlabs.smartclassifier.database.entity.Heartbeat;
import com.nextlabs.smartclassifier.database.entity.RuleEngine;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;

public class RuleEngineManager 
		extends Manager {
	
	private RuleEngineDAO ruleEngineDAO;
	
	public RuleEngineManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.ruleEngineDAO = new RuleEngineDAO(sessionFactory, session);
	}
	
	public List<RuleEngine> getRuleEngines()
			throws ManagerException {
		try {
			return ruleEngineDAO.getAllByOrder(RuleEngine.NAME, "ASC");
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public List<RuleEngine> getRuleEnginesWithLastHeartbeat()
			throws ManagerException {
		try {
			return getLastHeartbeat(new HeartbeatDAO(sessionFactory, session), ruleEngineDAO.getAll());
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public List<RuleEngine> getRuleEngines(List<Criterion> criterion, List<Order> order, PageInfo pageInfo)
			throws ManagerException {
		try {
			List<RuleEngine> ruleEngines = ruleEngineDAO.findByCriteria(criterion, order, pageInfo);
			
			if(ruleEngines != null) {
				HeartbeatDAO heartbeatDAO = new HeartbeatDAO(sessionFactory, session);
				ExecutionWindowAssociationDAO executionWindowAssociationDAO = new ExecutionWindowAssociationDAO(sessionFactory, session);
				
				for(RuleEngine ruleEngine : ruleEngines) {
					List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
					heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, ruleEngine.getId()));
					heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, ComponentType.RULE_ENGINE.getCode()));
					
					List<Heartbeat> heartbeats = heartbeatDAO.findByCriteria(heartbeatCriteria);
					
					if(heartbeats != null
							&& heartbeats.size() > 0) {
						ruleEngine.setLastHeartbeat(heartbeats.get(0).getLastHeartbeat());
					}
					
					List<Criterion> executionWindowCriteria = new ArrayList<Criterion>();
					executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_ID, ruleEngine.getId()));
					executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_TYPE, ComponentType.RULE_ENGINE.getCode()));
					
					List<ExecutionWindowAssociation> executionWindowAssociations = executionWindowAssociationDAO.findByCriteria(executionWindowCriteria);
					
					if(executionWindowAssociations != null && executionWindowAssociations.size() > 0) {
						for(ExecutionWindowAssociation executionWindowAssociation : executionWindowAssociations) {
							ruleEngine.getExecutionWindowSets().add(executionWindowAssociation.getExecutionWindowSet());
						}
					}
				}
			}
			
			return ruleEngines;
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public RuleEngine getRuleEngineById(Long id) 
			throws ManagerException {
		try {
			logger.debug("Get rule engine with id " + id);
			RuleEngine ruleEngine = ruleEngineDAO.get(id);
			
			if(ruleEngine != null) {
				logger.debug("Rule engine found with id " + id);
				
				List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
				heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, id));
				heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, ComponentType.RULE_ENGINE.getCode()));
				
				List<Heartbeat> heartbeats = new HeartbeatDAO(sessionFactory, session).findByCriteria(heartbeatCriteria);
				
				if(heartbeats != null
						&& heartbeats.size() > 0) {
					ruleEngine.setLastHeartbeat(heartbeats.get(0).getLastHeartbeat());
				}
				
				ExecutionWindowAssociationDAO executionWindowAssociationDAO = new ExecutionWindowAssociationDAO(sessionFactory, session);
				
				List<Criterion> executionWindowCriteria = new ArrayList<Criterion>();
				executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_ID, ruleEngine.getId()));
				executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_TYPE, ComponentType.RULE_ENGINE.getCode()));
				
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(Order.asc(ExecutionWindowAssociation.DISPLAY_ORDER));
				
				List<ExecutionWindowAssociation> executionWindowAssociations = executionWindowAssociationDAO.findByCriteria(executionWindowCriteria, orderList);
				
				if(executionWindowAssociations != null && executionWindowAssociations.size() > 0) {
					for(ExecutionWindowAssociation executionWindowAssociation : executionWindowAssociations) {
						ruleEngine.getExecutionWindowSets().add(executionWindowAssociation.getExecutionWindowSet());
					}
				}
				
				return ruleEngine;
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
		
		return null;
	}
	
	public RuleEngine getRuleEngineByHostname(String hostname) 
			throws ManagerException {
		try {
			logger.debug("Get rule engine for hostname " + hostname);
			List<RuleEngine> ruleEngines = ruleEngineDAO.findByCriteria(Restrictions.eq(RuleEngine.HOSTNAME, hostname));
			
			if(ruleEngines != null && ruleEngines.size() > 0) {
				logger.debug("Rule engine found for hostname " + hostname);
				return ruleEngines.get(0);
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
		
		return null;
	}
	
	public void updateConfigLoadedDate(String hostname, Date configLoadedOn) 
			throws ManagerException {
		try {
			logger.debug("Update rule engine configuration loaded for hostname " + hostname + " time to " + configLoadedOn);
			List<RuleEngine> ruleEngines = ruleEngineDAO.findByCriteria(Restrictions.eq(RuleEngine.HOSTNAME, hostname));
			
			if(ruleEngines != null && ruleEngines.size() > 0) {
				RuleEngine ruleEngine = ruleEngines.get(0);
				ruleEngine.setConfigLoadedOn(configLoadedOn);
				
				ruleEngineDAO.saveOrUpdate(ruleEngine);
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public void updateRuleEngine(RuleEngine ruleEngine)
			throws ManagerException, RecordNotFoundException, IllegalArgumentException {
		try {
			logger.debug("Update rule engine configuration for rule engine id " + ruleEngine.getId());
			RuleEngine entity = ruleEngineDAO.get(ruleEngine.getId());
			
			if(entity != null) {
				Date now = new Date();
				entity.setName(ruleEngine.getName());
				entity.setOnDemandInterval(ruleEngine.getOnDemandInterval());
				entity.setScheduledInterval(ruleEngine.getScheduledInterval());
				entity.setOnDemandPoolSize(ruleEngine.getOnDemandPoolSize());
				entity.setScheduledPoolSize(ruleEngine.getScheduledPoolSize());
				entity.setConfigReloadInterval(ruleEngine.getConfigReloadInterval());
				entity.setModifiedOn(now);
				
				// Remove original record for execution window
				ExecutionWindowAssociationDAO executionWindowAssociationDAO = new ExecutionWindowAssociationDAO(sessionFactory, session);
				
				List<Criterion> executionWindowCriteria = new ArrayList<Criterion>();
				executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_ID, entity.getId()));
				executionWindowCriteria.add(Restrictions.eq(ExecutionWindowAssociation.COMPONENT_TYPE, ComponentType.RULE_ENGINE.getCode()));
				
				List<ExecutionWindowAssociation> executionWindowAssociations = executionWindowAssociationDAO.findByCriteria(executionWindowCriteria);
				
				if(executionWindowAssociations != null && executionWindowAssociations.size() > 0) {
					for(ExecutionWindowAssociation executionWindowAssociation : executionWindowAssociations) {
						executionWindowAssociationDAO.delete(executionWindowAssociation);
					}
					
					executionWindowAssociationDAO.flush();
				}
				
				// Set new execution window
				if(ruleEngine.getExecutionWindowSets() != null) {
					ExecutionWindowSetDAO executionWindowSetDAO = new ExecutionWindowSetDAO(sessionFactory, session);
					
					int displayOrder = 1;
					for(ExecutionWindowSet executionWindowSet : ruleEngine.getExecutionWindowSets()) {
						ExecutionWindowAssociation executionWindowAssociation = new ExecutionWindowAssociation();
						executionWindowAssociation.setComponentId(entity.getId());
						executionWindowAssociation.setComponentType(ComponentType.RULE_ENGINE.getCode());
						executionWindowAssociation.setDisplayOrder(displayOrder);
						executionWindowAssociation.setExecutionWindowSet(executionWindowSetDAO.get(executionWindowSet.getId()));
						executionWindowAssociation.setCreatedOn(now);
						executionWindowAssociation.setModifiedOn(now);
						
						executionWindowAssociationDAO.saveOrUpdate(executionWindowAssociation);
						
						displayOrder++;
					}
				}
				
				ruleEngineDAO.saveOrUpdate(entity);
			} else {
				throw new RecordNotFoundException("Rule engine record not found for the given RuleEngineID.");
			}
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public long getRecordCount(List<Criterion> criterion) 
			throws ManagerException {
		try {
			return ruleEngineDAO.getCount(criterion);
		} catch(HibernateException err) {
			throw new ManagerException(err.getMessage(), err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	private List<RuleEngine> getLastHeartbeat(HeartbeatDAO heartbeatDAO, List<RuleEngine> ruleEngines)
			throws Exception {
		if(heartbeatDAO != null 
				&& ruleEngines != null) {
			for(RuleEngine ruleEngine : ruleEngines) {
				List<Criterion> heartbeatCriteria = new ArrayList<Criterion>();
				heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_ID, ruleEngine.getId()));
				heartbeatCriteria.add(Restrictions.eq(Heartbeat.COMPONENT_TYPE, ComponentType.RULE_ENGINE.getCode()));
				
				List<Heartbeat> heartbeats = heartbeatDAO.findByCriteria(heartbeatCriteria);
				
				if(heartbeats != null
						&& heartbeats.size() > 0) {
					ruleEngine.setLastHeartbeat(heartbeats.get(0).getLastHeartbeat());
				}
			}
		}
		
		return ruleEngines;
	}
}
