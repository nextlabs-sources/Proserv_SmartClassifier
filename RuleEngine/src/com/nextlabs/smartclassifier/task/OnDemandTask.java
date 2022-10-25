package com.nextlabs.smartclassifier.task;

import com.nextlabs.smartclassifier.base.Component;
import com.nextlabs.smartclassifier.constant.ExecutionType;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class OnDemandTask 
		extends RuleExecutionFetchingTask {
	
	public OnDemandTask(Component component, ThreadPoolExecutor threadPoolExecutor, Map<Long, Future<?>> ruleExecutionIdToFutureMap) {
		super(component, "[On-demand]", threadPoolExecutor, ruleExecutionIdToFutureMap);
	}
	
	/**
	 * On demand task will execute periodically without limitation of execution window
	 */
	@Override
	public void run() {
		try {
			logger.debug("Checking for on-demand rule execution.");
			addTaskToQueue(fetchRuleForExecution(ExecutionType.ON_DEMAND));
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
	}
}
