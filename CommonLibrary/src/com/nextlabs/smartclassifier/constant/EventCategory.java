package com.nextlabs.smartclassifier.constant;

public enum EventCategory {
	
	CONFIGURATION("C", "Configuration"),
	OPERATION("O", "Operation"),
	MAINTENANCE("M", "Maintenance"),
	USER_INTERFACE("U", "User Interface"),
	RESOURCE("R", "Resource");
	
	private String code;
	
	private String name;
	
	private EventCategory(final String code, final String name) {
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
	
	public static EventCategory getCategory(String categoryCode) {
		if(categoryCode != null) {
			for(EventCategory category : EventCategory.values()) {
				if(category.getCode().equals(categoryCode)) {
					return category;
				}
			}
		}
		
		return null;
	}
}
