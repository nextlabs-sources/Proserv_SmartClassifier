package com.nextlabs.smartclassifier.constant;

public enum RuleExecutionStatus {
	
	QUEUE("Q", "Queue"),
	PENDING("P", "Pending Execution"),
	EXECUTING("E", "Executing"),
	COMPLETED("C", "Execution Completed"),
	EXPIRED("X", "Disabled/Expired for Execution"),
	INTERRUPTED("I", "Execution Interrupted"),
	DELETED("D", "Deleted");
	
	private String code;
	
	private String name;
	
	private RuleExecutionStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public RuleExecutionStatus getStatus(String code) {
		if(code != null) {
			for(RuleExecutionStatus ruleStatus : RuleExecutionStatus.values()) {
				if(ruleStatus.getCode().equals(code)) {
					return ruleStatus;
				}
			}
		}
		
		return null;
	}
}
