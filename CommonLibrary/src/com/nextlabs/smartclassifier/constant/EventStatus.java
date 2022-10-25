package com.nextlabs.smartclassifier.constant;

public enum EventStatus {
	
	ALL("A", "All"),
	SUCCESS("S", "Success"),
	FAIL("F", "Fail"),
	ROLLBACK("R", "Roll-back");
	
	private String code;
	
	private String name;
	
	private EventStatus(final String code, final String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public static EventStatus getStatus(String statusCode) {
		if(statusCode != null) {
			for(EventStatus status : EventStatus.values()) {
				if(status.getCode().equals(statusCode)) {
					return status;
				}
			}
		}
		
		return null;
	}
}
