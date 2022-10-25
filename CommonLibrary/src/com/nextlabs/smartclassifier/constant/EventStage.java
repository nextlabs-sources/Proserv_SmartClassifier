package com.nextlabs.smartclassifier.constant;

public enum EventStage {
	
	COMPONENT_STARTUP("CSTR", "Component startup"),
	COMPONENT_SHUTDOWN("CSTP", "Component shutdown"),
	FILE_MONITORING("FMON", "File monitoring"),
	FILE_EXTRACTION("FEXT", "File extraction"),
	FILE_INDEXING("FIDX", "File indexing"),
	RULE_EXECUTION("REXE", "Rule execution"),
	ACTION_EXECUTION("AEXE", "Action execution");
	
	private String code;
	
	private String name;
	
	private EventStage(final String code, final String name) {
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
	
	public static EventStage getStage(String stageCode) {
		if(stageCode != null) {
			for(EventStage stage : EventStage.values()) {
				if(stage.getCode().equals(stageCode)) {
					return stage;
				}
			}
		}
		
		return null;
	}
}
