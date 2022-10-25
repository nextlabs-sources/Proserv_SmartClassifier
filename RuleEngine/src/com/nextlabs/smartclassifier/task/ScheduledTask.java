package com.nextlabs.smartclassifier.task;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.nextlabs.smartclassifier.base.Component;
import com.nextlabs.smartclassifier.constant.ExecutionType;

public class ScheduledTask 
		extends RuleExecutionFetchingTask {
	
	public ScheduledTask(Component scComponent, ThreadPoolExecutor taskPool, Map<Long, Future<?>> taskMap) {
		super(scComponent, "[Scheduled]", taskPool, taskMap);
	}
	
	@Override
	public void run() {
		try {
			logger.debug("Checking for scheduled rule execution.");
			
			if(!component.isWithinExecutionWindow()) {
				logger.info(ruleType + " not within execution window. Not pulling rule from queue...");
				return;
			}
			
			addTaskToQueue(fetchRuleForExecution(ExecutionType.SCHEDULED));
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
	}
}
