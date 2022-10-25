package com.nextlabs.smartclassifier.constant;

public enum ComponentStatus {
	
	HEALTHY("H", "Healthy"),
	WARNING("W", "Warning"),
	CRITICAL("C", "Critical");
	
	private String code;
	
	private String name;
	
	private ComponentStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static ComponentStatus getStatus(String code) {
		if(code != null) {
			for(ComponentStatus status : ComponentStatus.values()) {
				if(status.code.equals(code)) {
					return status;
				}
			}
		}
		
		return null;
	}
}
