package com.nextlabs.smartclassifier.constant;

public enum ExecutionType {
	
	SCHEDULED("S", "Scheduled"),
	ON_DEMAND("O", "On-Demand");
	
	private String code;
	private String name;
	
	private ExecutionType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static ExecutionType getType(String code) {
		if(code != null) {
			for(ExecutionType type : ExecutionType.values()) {
				if(type.code.equals(code)) {
					return type;
				}
			}
		}
		
		return null;
	}
}
