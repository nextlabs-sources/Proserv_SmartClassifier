package com.nextlabs.smartclassifier.constant;

public enum RuleExecutionOutcome {
	
	SUCCESS("S", "Success"), 
	PARTIAL_FAILED("P", "Partial failed"),
	FAILED("F", "Failed");
	
	private String code;
	
	private String name;
	
	private RuleExecutionOutcome(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static RuleExecutionOutcome getOutcome(String code) {
		if(code != null) {
			for(RuleExecutionOutcome outcome : RuleExecutionOutcome.values()) {
				if(outcome.code.equals(code)) {
					return outcome;
				}
			}
		}
		
		return null;
	}
}
