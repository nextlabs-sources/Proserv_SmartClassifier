package com.nextlabs.smartclassifier.dto;

import java.util.Date;

import com.nextlabs.smartclassifier.database.entity.RuleEngine;

public class RuleEngineProfile 
		extends BaseDTO {
	
	private String name;
	private String hostname;
	private long onDemandInterval;
	private long scheduledInterval;
	private int onDemandPoolSize;
	private int scheduledPoolSize;
	private Date configLoadedOn;
	private long configReloadInterval;
	
	public void copy(RuleEngine ruleEngine) {
		if(ruleEngine != null) {
			this.id = ruleEngine.getId();
			this.name = ruleEngine.getName();
			this.hostname = ruleEngine.getHostname();
			this.onDemandInterval = ruleEngine.getOnDemandInterval();
			this.scheduledInterval = ruleEngine.getScheduledInterval();
			this.onDemandPoolSize = ruleEngine.getOnDemandPoolSize();
			this.scheduledPoolSize = ruleEngine.getScheduledPoolSize();
			this.configLoadedOn = ruleEngine.getConfigLoadedOn();
			this.configReloadInterval = ruleEngine.getConfigReloadInterval();
			this.createdTimestamp = ruleEngine.getCreatedOn();
			this.modifiedTimestamp = ruleEngine.getModifiedOn();
		}
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
	
	public long getOnDemandInterval() {
		return onDemandInterval;
	}
	
	public void setOnDemandInterval(long onDemandInterval) {
		this.onDemandInterval = onDemandInterval;
	}
	
	public long getScheduledInterval() {
		return scheduledInterval;
	}
	
	public void setScheduledInterval(long scheduledInterval) {
		this.scheduledInterval = scheduledInterval;
	}
	
	public int getOnDemandPoolSize() {
		return onDemandPoolSize;
	}
	
	public void setOnDemandPoolSize(int onDemandPoolSize) {
		this.onDemandPoolSize = onDemandPoolSize;
	}
	
	public int getScheduledPoolSize() {
		return scheduledPoolSize;
	}
	
	public void setScheduledPoolSize(int scheduledPoolSize) {
		this.scheduledPoolSize = scheduledPoolSize;
	}
	
	public Date getConfigLoadedOn() {
		return configLoadedOn;
	}
	
	public void setConfigLoadedOn(Date configLoadedOn) {
		this.configLoadedOn = configLoadedOn;
	}
	
	public long getConfigReloadInterval() {
		return configReloadInterval * 1000;
	}
	
	public void setConfigReloadInterval(long configReloadInterval) {
		this.configReloadInterval = configReloadInterval;
	}
}
