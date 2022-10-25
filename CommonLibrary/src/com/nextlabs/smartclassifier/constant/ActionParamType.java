package com.nextlabs.smartclassifier.constant;

public enum ActionParamType {
	
	PROPERTY("P", "Property"),
	TAG("T", "Tag");
	
	private String code;
	
	private String name;
	
	private ActionParamType(String code, String name) {
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
	
	public ActionParamType getType(String code) {
		if(code != null) {
			for(ActionParamType type : ActionParamType.values()) {
				if(type.getCode().equals(code)) {
					return type;
				}
			}
		}
		
		return null;
	}
}
