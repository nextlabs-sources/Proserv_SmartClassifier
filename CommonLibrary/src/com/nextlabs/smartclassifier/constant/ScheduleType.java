package com.nextlabs.smartclassifier.constant;

public enum ScheduleType {
	
	DAILY("D", "Daily"),
	WEEKLY("W", "Weekly"),
	MONTHLY("M", "Monthly");
	
	private String code;
	
	private String name;
	
	private ScheduleType(final String code, final String name) {
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
	
	public static ScheduleType getType(final String typeCode) {
		if(typeCode != null) {
			for(ScheduleType type : ScheduleType.values()) {
				if(type.getCode().equals(typeCode)) {
					return type;
				}
			}
		}
		
		return null;
	}
}
