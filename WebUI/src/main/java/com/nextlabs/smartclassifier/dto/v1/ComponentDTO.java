package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.ComponentStatus;
import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.database.entity.Extractor;
import com.nextlabs.smartclassifier.database.entity.RuleEngine;
import com.nextlabs.smartclassifier.database.entity.Watcher;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ComponentDTO 
		extends BaseDTO {
	
	@Expose
	private String type;
	@Expose
	private String name;
	@Expose
	private String hostname;
	@Expose
	private Long configLoadedTime;
	@Expose
	private String status;
	
	public ComponentDTO() {
		super();
	}
	
	public ComponentDTO(Watcher watcher, int warningCount, int criticalCount, long heartbeatInterval) {
		super();
//		copy(watcher, warningCount, criticalCount, heartbeatInterval);
	}
	
	public ComponentDTO(Extractor extractor, int warningCount, int criticalCount, long heartbeatInterval) {
		super();
//		copy(extractor, warningCount, criticalCount, heartbeatInterval);
	}
	
	public ComponentDTO(RuleEngine ruleEngine, int warningCount, int criticalCount, long heartbeatInterval) {
		super();
//		copy(ruleEngine, warningCount, criticalCount, heartbeatInterval);
	}
	
	public void copy(Watcher watcher, long lastHeartbeat, int warningCount, int criticalCount, long heartbeatInterval) {
		if(watcher != null) {
			this.id = watcher.getId();
			this.type = ComponentType.WATCHER.getName();
			this.name = watcher.getName();
			this.hostname = watcher.getHostname();
			this.configLoadedTime = watcher.getConfigLoadedOn().getTime();
			this.status = getStatus(lastHeartbeat, warningCount, criticalCount, heartbeatInterval);
			this.createdTimestamp = watcher.getCreatedOn();
			this.createdOn = watcher.getCreatedOn().getTime();
			this.modifiedTimestamp = watcher.getModifiedOn();
			this.modifiedOn = watcher.getModifiedOn().getTime();
		}
	}
	
	public void copy(Extractor extractor, long lastHeartbeat, int warningCount, int criticalCount, long heartbeatInterval) {
		if(extractor != null) {
			this.id = extractor.getId();
			this.type = ComponentType.EXTRACTOR.getName();
			this.name = extractor.getName();
			this.hostname = extractor.getHostname();
			this.configLoadedTime = extractor.getConfigLoadedOn().getTime();
			this.status = getStatus(lastHeartbeat, warningCount, criticalCount, heartbeatInterval);
			this.createdTimestamp = extractor.getCreatedOn();
			this.createdOn = extractor.getCreatedOn().getTime();
			this.modifiedTimestamp = extractor.getModifiedOn();
			this.modifiedOn = extractor.getModifiedOn().getTime();
		}
	}
	
	public void copy(RuleEngine ruleEngine, long lastHeartbeat, int warningCount, int criticalCount, long heartbeatInterval) {
		if(ruleEngine != null) {
			this.id = ruleEngine.getId();
			this.type = ComponentType.RULE_ENGINE.getName();
			this.name = ruleEngine.getName();
			this.hostname = ruleEngine.getHostname();
			this.status = getStatus(lastHeartbeat, warningCount, criticalCount, heartbeatInterval);
			this.createdTimestamp = ruleEngine.getCreatedOn();
			this.createdOn = ruleEngine.getCreatedOn().getTime();
			this.modifiedTimestamp = ruleEngine.getModifiedOn();
			this.modifiedOn = ruleEngine.getModifiedOn().getTime();
		}
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public Long getConfigLoadedTime() {
		return configLoadedTime;
	}
	
	public void setConfigLoadedTime(Long configLoadedTime) {
		this.configLoadedTime = configLoadedTime;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	private String getStatus(long lastHeartbeat, int warningCount, int criticalCount, long heartbeatInterval) {
		long timeLapsed = System.currentTimeMillis() - lastHeartbeat;
		long missedHeartbeatCount = timeLapsed/heartbeatInterval;
		
		if(missedHeartbeatCount >= criticalCount) {
			return ComponentStatus.CRITICAL.getName();
		} else if(missedHeartbeatCount >= warningCount) {
			return ComponentStatus.WARNING.getName();
		}
		
		return ComponentStatus.HEALTHY.getName();
	}
}
