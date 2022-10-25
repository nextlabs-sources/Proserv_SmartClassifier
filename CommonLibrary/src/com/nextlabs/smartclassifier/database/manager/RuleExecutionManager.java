package com.nextlabs.smartclassifier.database.manager;

import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.constant.RuleExecutionOutcome;
import com.nextlabs.smartclassifier.constant.RuleExecutionStatus;
import com.nextlabs.smartclassifier.database.dao.RuleDAO;
import com.nextlabs.smartclassifier.database.dao.RuleExecutionDAO;
import com.nextlabs.smartclassifier.database.entity.*;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.LockAcquisitionException;

import java.text.ParseException;
import java.util.*;

public class RuleExecutionManager 
		extends Manager {
	
	private RuleExecutionDAO ruleExecutionDAO;
	
	public RuleExecutionManager(SessionFactory sessionFactory, Session session) {
		super(sessionFactory, session);
		this.ruleExecutionDAO = new RuleExecutionDAO(sessionFactory, session);
	}
	
	public RuleExecution getLastExecution(Long ruleId) 
			throws ManagerException {
		if(ruleId != null && ruleId > 0) {
			try {
				List<Criterion> criterion = new ArrayList<Criterion>();
				criterion.add(Restrictions.eq(RuleExecution.RULE_ID, ruleId));
				criterion.add(Restrictions.disjunction()
										 .add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.COMPLETED.getCode()))
										 .add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.EXECUTING.getCode()))
										 .add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.INTERRUPTED.getCode())));
				
				List<Order> orders = new ArrayList<Order>();
				orders.add(Order.desc(RuleExecution.START_TIME));
				
				List<RuleExecution> lastExecutions = ruleExecutionDAO.findByCriteria(criterion, orders, 1);
				
				if(lastExecutions != null && lastExecutions.size() > 0) {
					return lastExecutions.get(0);
				}
			} catch(Exception err) {
				throw new ManagerException(err);
			}
		}
		
		return null;
	}

	/**
	 * This method is used at startup to recover previous rule executions. In-case some rule did not execute last time or was interrupted, we try to recover it at startup
	 * @param hostname Hostname of the current machine
	 * @throws ManagerException
	 */
	public void recoverPreviousRuleExecutions(String hostname)
			throws ManagerException {
		if(StringUtils.isNotBlank(hostname)) {
			try {
				logger.debug("Recovering rule execution for host [" + hostname + "].");
				
				List<RuleExecution> ruleExecutions = ruleExecutionDAO.findByCriteria(
															Restrictions.disjunction()
																		.add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.PENDING.getCode()))
																		.add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.EXECUTING.getCode())));
				
				logger.debug("Execution record for recovery found: " + ruleExecutions.size());
				if(ruleExecutions.size() > 0) {
					for(RuleExecution ruleExecution : ruleExecutions) {
						if(ruleExecution.getRule().getRuleEngine() == null) {
							updateRuleExecutionStatus(ruleExecution, RuleExecutionStatus.DELETED, null);
						} else {
							if(ruleExecution.getRule().getRuleEngine().getHostname().equals(hostname)) {
								// Not Yet started for execution, re-queue for execution
								if(ruleExecution.getStatus().equals(RuleExecutionStatus.PENDING.getCode())) {
									updateRuleExecutionStatus(ruleExecution, RuleExecutionStatus.QUEUE, null);
								} else {
									// Mark it as partial success and failed
									updateRuleExecutionStatus(ruleExecution, RuleExecutionStatus.INTERRUPTED, RuleExecutionOutcome.PARTIAL_FAILED);
								}
							}
						}
					}
				}
				
				logger.debug("Rule execution for host [" + hostname + "] recovered.");
			} catch(Exception err) {
				throw new ManagerException(err);
			}
		}
	}
	
	public synchronized void updateRuleExecutionStatus(RuleExecution ruleExecution, RuleExecutionStatus status, RuleExecutionOutcome outcome)
			throws ManagerException, LockAcquisitionException {
		if(ruleExecution != null && status != null) {
			logger.debug("Updating " + ruleExecution.getId() + " to status " + status.getName() + ".");
			
			try {
				RuleExecution executionRecord = ruleExecutionDAO.get(ruleExecution.getId());
				
				if(executionRecord != null) {
					Date now = new Date();
					executionRecord.setStatus(status.getCode());
					// There is no execution outcome for the time being
					if(outcome != null) {
						executionRecord.setOutcome(outcome.getCode());
					}
					executionRecord.setModifiedOn(now);
					
					if(status == RuleExecutionStatus.EXECUTING) {
						executionRecord.setStartTime(now);
					} else if(status == RuleExecutionStatus.COMPLETED
								|| status == RuleExecutionStatus.INTERRUPTED) {
						executionRecord.setEndTime(now);
					}
					
					ruleExecutionDAO.saveOrUpdate(executionRecord);
					
					if(executionRecord.getType().equals(ExecutionType.SCHEDULED.getCode()) 
							&& (status == RuleExecutionStatus.COMPLETED || status == RuleExecutionStatus.INTERRUPTED)) {
						logger.debug("Re-scheduling rule " + executionRecord.getRule().getId() + " for execution.");
						scheduleExecution(executionRecord.getRule(), executionRecord.getType());
					}
				} else {
					logger.warn("Rule execution record " + ruleExecution.getId() + " not found.");
				}
			} catch(LockAcquisitionException err) {
				throw err;
			} catch(Exception err) {
				throw new ManagerException(err);
			}
		}
	}
	
	public synchronized List<RuleExecution> fetchRuleForExecution(String hostname, ExecutionType type) 
			throws ManagerException {
		try {
			Date now = new Date();
			
			Map<String, String> aliases = new HashMap<String, String>();
			aliases.put("ruleExecution.rule", "rule");
			aliases.put("rule.ruleEngine", "ruleEngine");
			
			List<Criterion> criterionList = new ArrayList<Criterion>();
			criterionList.add(Restrictions.eq("ruleEngine.hostname", hostname));
			criterionList.add(Restrictions.eq(RuleExecution.TYPE, type.getCode()));
			criterionList.add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.QUEUE.getCode()));
			criterionList.add(Restrictions.le(RuleExecution.PLAN_TIME, now));
			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(Order.asc(RuleExecution.PLAN_TIME));
			
			List<RuleExecution> ruleExecutions = ruleExecutionDAO.findByCriteria(aliases, criterionList, orderList);
			
			if(ruleExecutions != null && ruleExecutions.size() > 0) {
				for(RuleExecution ruleExecution : ruleExecutions) {
					logger.debug("Rule execution id: " + ruleExecution.getId());
					ruleExecution.setStatus(RuleExecutionStatus.PENDING.getCode());
					ruleExecution.setRuleVersion(ruleExecution.getRule().getVersion());
					ruleExecution.setStartTime(now);
					ruleExecution.setModifiedOn(now);
					ruleExecutionDAO.saveOrUpdate(ruleExecution);
					
					// Access to lazy fetch records which is required for action to fire
					if(ruleExecution.getRule().getCriteriaGroups() != null) {
						for(CriteriaGroup criteriaGroup : ruleExecution.getRule().getCriteriaGroups()) {
							if(criteriaGroup.getCriterias() != null) {
								for(Criteria criteria : criteriaGroup.getCriterias()) {
									criteria.getId();
								}
							}
						}
					}
					
					for(Action action : ruleExecution.getRule().getActions()) {
						if(action.getActionParams() != null) {
							for(ActionParam actionParam : action.getActionParams()) {
								actionParam.getId();
							}
						}
						
						if(action.getActionPlugin().getPluginParams() != null) {
							for(ActionPluginParam pluginParam : action.getActionPlugin().getPluginParams()) {
								pluginParam.getId();
							}
						}
					}
				}
			}

			if(ruleExecutions != null) {
				logger.debug("Rule queued for execution count: " + ruleExecutions.size());
			}

			return ruleExecutions;
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public void scheduleExecution(Long ruleId, String type) 
			throws ManagerException, RecordNotFoundException {
		try {
			Rule rule = new RuleDAO(sessionFactory, session).get(ruleId);
			
			if(rule == null) {
				throw new RecordNotFoundException("Rule record not found for the given RuleID.");
			}
			
			scheduleExecution(rule, type);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public synchronized void scheduleExecution(Rule rule, String type) 
			throws ManagerException, LockAcquisitionException {
		try {
			// Check for uniqueness
			List<Criterion> criterionList = new ArrayList<Criterion>();
			criterionList.add(Restrictions.eq(RuleExecution.RULE_ID, rule.getId()));
			criterionList.add(Restrictions.eq(RuleExecution.TYPE, type));
			criterionList.add(Restrictions.disjunction()
										  .add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.QUEUE.getCode()))
										  .add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.PENDING.getCode()))
										  .add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.EXECUTING.getCode())));
			
			if(ruleExecutionDAO.getCount(criterionList) > 0) {
				logger.info("Rule " + rule.getName() + " already scheduled for execution/executing.");
			} else {
				Date now = new Date();
				
				if(type.equals(ExecutionType.ON_DEMAND.getCode())) {
					RuleExecution ruleExecution = new RuleExecution();
					
					ruleExecution.setRule(rule);
					ruleExecution.setType(type);
					ruleExecution.setStatus(RuleExecutionStatus.QUEUE.getCode());
					ruleExecution.setPlanTime(now);
					ruleExecution.setCreatedOn(now);
					ruleExecution.setModifiedOn(now);
					
					ruleExecutionDAO.saveOrUpdate(ruleExecution);
				} else {
					Date nextScheduleTime = rule.getNextScheduleTime();
					
					if(nextScheduleTime != null) {
						RuleExecution ruleExecution = new RuleExecution();
						
						ruleExecution.setRule(rule);
						ruleExecution.setType(type);
						ruleExecution.setStatus(RuleExecutionStatus.QUEUE.getCode());
						ruleExecution.setPlanTime(nextScheduleTime);
						ruleExecution.setCreatedOn(now);
						ruleExecution.setModifiedOn(now);
						
						ruleExecutionDAO.saveOrUpdate(ruleExecution);
					} else {
						logger.info("Rule is not schedule to be execute.");
					}
				}
			}
		} catch(ParseException err) {
			throw new ManagerException(err);
		} catch(LockAcquisitionException err) {
			throw err;
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
	
	public synchronized void updateExecution(Rule rule)
			throws ManagerException {
		try {
			// Get next execution time
			Date nextScheduleTime = rule.getNextScheduleTime();
			// Check for uniqueness
			List<Criterion> criterionList = new ArrayList<Criterion>();
			criterionList.add(Restrictions.eq(RuleExecution.RULE_ID, rule.getId()));
			criterionList.add(Restrictions.eq(RuleExecution.TYPE, ExecutionType.SCHEDULED.getCode()));
			criterionList.add(Restrictions.disjunction()
										  .add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.QUEUE.getCode()))
										  .add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.PENDING.getCode())));
			
			List<RuleExecution> ruleExecutions = ruleExecutionDAO.findByCriteria(criterionList);
			
			// No longer need for execution, update scheduled record
			if(nextScheduleTime == null) {
				logger.info("Rule is not schedule to be execute. Cancelling schedule if exist.");
				
				if(ruleExecutions != null && ruleExecutions.size() > 0) {
					// Only update queue status. Record of pending status will be update before rule execution starts.
					for(RuleExecution ruleExecution : ruleExecutions) {
						if(RuleExecutionStatus.QUEUE.getCode().equalsIgnoreCase(ruleExecution.getStatus())) {
							ruleExecution.setStatus(rule.isDeleted() ? RuleExecutionStatus.DELETED.getCode() : RuleExecutionStatus.EXPIRED.getCode());
							ruleExecution.setModifiedOn(new Date());
						}
					}
					
					ruleExecutionDAO.saveOrUpdateAll(ruleExecutions);
				}
			} else {
				Date now = new Date();
				// Update or create 
				if(ruleExecutions != null && ruleExecutions.size() > 0) {
					for(RuleExecution ruleExecution : ruleExecutions) {
						// Only update queue status. Record of pending status has been picked for execution.
						if(RuleExecutionStatus.QUEUE.getCode().equalsIgnoreCase(ruleExecution.getStatus())
								&& ruleExecution.getPlanTime().getTime() != nextScheduleTime.getTime()) {
							ruleExecution.setPlanTime(nextScheduleTime);
							ruleExecution.setModifiedOn(now);
						}
					}
					
					ruleExecutionDAO.saveOrUpdateAll(ruleExecutions);
				} else {
					RuleExecution ruleExecution = new RuleExecution();
					
					ruleExecution.setRule(rule);
					ruleExecution.setType(ExecutionType.SCHEDULED.getCode());
					ruleExecution.setStatus(RuleExecutionStatus.QUEUE.getCode());
					ruleExecution.setPlanTime(nextScheduleTime);
					ruleExecution.setCreatedOn(now);
					ruleExecution.setModifiedOn(now);
					
					ruleExecutionDAO.saveOrUpdate(ruleExecution);
				}
			}
		} catch(ParseException err) {
			throw new ManagerException(err);
		} catch(HibernateException err) {
			throw new ManagerException(err);
		} catch(Exception err) {
			throw new ManagerException(err);
		}
	}
}
