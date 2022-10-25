package com.nextlabs.smartclassifier.constant;

public enum EventLevel {
	
	CRITICAL("C", "Critical", "Indicates that a failure has occurred from which the application or component that triggered the event cannot automatically recover."),
	ERROR("E", "Error", "Indicates that a problem has occurred, which might impact functionality that is external to the application or component that triggered the event."),
	WARNING("W", "Warning", "Indicates that an issue has occurred that can impact service or result in a more serious problem if action is not taken."),
	INFORMATION("I", "Information", "Indicates that a change in an application or component has occurred, such as an operation has successfully completed, a resource has been created, or a service started."),
	SUCCESS_AUDIT("S", "Success Audit", "Indicates that the exercise of a user right has succeeded."),
	FAILURE_AUDIT("F", "Failure Audit", "Indicates that the exercise of a user right has failed.");
	
	private String code;
	
	private String name;
	
	private String description;
	
	private EventLevel(final String code, final String name, final String description) {
		this.code = code;
		this.name = name;
		this.description = description;
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public static EventLevel getLevel(String levelCode) {
		if(levelCode != null) {
			for(EventLevel level : EventLevel.values()) {
				if(level.getCode().equals(levelCode)) {
					return level;
				}
			}
		}
		
		return null;
	}
}
