package com.nextlabs.smartclassifier.constant;

public enum ReportExecutionStatus {
	
	QUEUE("Q", "Queue"),
	EXECUTING("E", "Executing"),
	COMPLETED("C", "Execution Completed"),
	EXPIRED("X", "Disabled/Expired for Execution"),
	INTERRUPTED("I", "Execution Interrupted"),
	DELETED("D", "Deleted");
	
	private String code;
	
	private String name;
	
	private ReportExecutionStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public ReportExecutionStatus getStatus(String code) {
		if(code != null) {
			for(ReportExecutionStatus ruleStatus : ReportExecutionStatus.values()) {
				if(ruleStatus.getCode().equals(code)) {
					return ruleStatus;
				}
			}
		}
		
		return null;
	}
}
