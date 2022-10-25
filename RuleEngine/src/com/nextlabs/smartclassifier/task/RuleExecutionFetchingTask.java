package com.nextlabs.smartclassifier.task;

import com.nextlabs.smartclassifier.base.Component;
import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.database.entity.RuleExecution;
import com.nextlabs.smartclassifier.database.manager.RuleExecutionManager;
import com.nextlabs.smartclassifier.exception.ManagerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class RuleExecutionFetchingTask 
		implements Runnable {
	
	protected final Logger logger = LogManager.getLogger(getClass());
	protected static final int QUERY_BLOCK_SIZE = 100;
	
	protected final Component component;
	protected final String ruleType;
	protected final ThreadPoolExecutor threadPoolExecutor;
	protected final Map<Long, Future<?>> ruleExecutionIdToFutureMap;
	
	public RuleExecutionFetchingTask(Component component, String ruleType, ThreadPoolExecutor threadPoolExecutor, Map<Long, Future<?>> ruleExecutionIdToFutureMap) {
		super();
		this.component = component;
		this.ruleType = ruleType;
		this.threadPoolExecutor = threadPoolExecutor;
		this.ruleExecutionIdToFutureMap = ruleExecutionIdToFutureMap;
	}
	
	protected List<RuleExecution> fetchRuleForExecution(ExecutionType type) {
		Session session = null;
		Transaction transaction = null;
		List<RuleExecution> ruleExecutions = null;
		
		try {
			session = component.getSessionFactory().openSession();
			transaction = session.beginTransaction();

			ruleExecutions = (new RuleExecutionManager(component.getSessionFactory(), session)).fetchRuleForExecution(component.getHostname(), type);
			
			transaction.commit();
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
					logger.error(err.getMessage(), err);
				}
			}
		}
		
		return ruleExecutions;
	}
	
	protected void addTaskToQueue(List<RuleExecution> ruleExecutions) {
		if(ruleExecutions != null) {
			for(RuleExecution ruleExecution : ruleExecutions) {
				try {
					Future<?> future = threadPoolExecutor.submit(new RuleExecutionTask(component, ruleExecution));
					
					ruleExecutionIdToFutureMap.put(ruleExecution.getId(), future);
				} catch(Exception err) {
					logger.error(err.getMessage(), err);
				}
			}
		}
	}
}
