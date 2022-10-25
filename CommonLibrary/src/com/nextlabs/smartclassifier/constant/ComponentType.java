package com.nextlabs.smartclassifier.constant;

public enum ComponentType {
	
	WATCHER("W", "File Watcher"),
	EXTRACTOR("E", "File Extractor"),
	RULE_ENGINE("R", "Rule Engine"),
	INDEX_SERVER("I", "File Index Server"),
	JMS_SERVER("J", "Java Messaging Service Server");
	
	private String code;
	
	private String name;
	
	private ComponentType(final String code, final String name) {
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
	
	public static ComponentType getType(final String componentCode) {
		if(componentCode != null) {
			for(ComponentType component : ComponentType.values()) {
				if(component.getCode().equals(componentCode)) {
					return component;
				}
			}
		}
		
		return null;
	}
}
