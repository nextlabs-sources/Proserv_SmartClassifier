package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;
import com.nextlabs.smartclassifier.database.entity.RuleEngine;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class RuleEngineDTO 
		extends BaseDTO {
	
	@Expose
	private String name;
	@Expose
	private String hostname;
	@Expose
	private Long onDemandInterval;
	@Expose
	private Long scheduledInterval;
	@Expose
	private Integer onDemandPoolSize;
	@Expose
	private Integer scheduledPoolSize;
	@Expose
	private Long configLoadedOn;
	@Expose
	private Integer configReloadInterval;
	@Expose
	private List<ExecutionWindowSetDTO> executionWindowSets;
	@Expose
	private String status;
	
	public RuleEngineDTO() {
		super();
	}
	
	public RuleEngineDTO(RuleEngine ruleEngine) {
		super();
		copy(ruleEngine);
	}
	
	public void copy(RuleEngine ruleEngine) {
		if(ruleEngine != null) {
			this.id = ruleEngine.getId();
			this.name = ruleEngine.getName();
			this.hostname = ruleEngine.getHostname();
			this.onDemandInterval = ruleEngine.getOnDemandInterval();
			this.scheduledInterval = ruleEngine.getScheduledInterval();
			this.onDemandPoolSize = ruleEngine.getOnDemandPoolSize();
			this.scheduledPoolSize = ruleEngine.getScheduledPoolSize();
			this.configReloadInterval = ruleEngine.getConfigReloadInterval();
			
			if(ruleEngine.getConfigLoadedOn() != null) {
				this.configLoadedOn = ruleEngine.getConfigLoadedOn().getTime();
			}
			
			if(ruleEngine.getExecutionWindowSets() != null && ruleEngine.getExecutionWindowSets().size() > 0) {
				this.executionWindowSets = new ArrayList<ExecutionWindowSetDTO>();
				for(ExecutionWindowSet executionWindowSet : ruleEngine.getExecutionWindowSets()) {
					this.executionWindowSets.add(new ExecutionWindowSetDTO(executionWindowSet));
				}
			}
			
			this.createdTimestamp = ruleEngine.getCreatedOn();
			this.createdOn = ruleEngine.getCreatedOn().getTime();
			this.modifiedTimestamp = ruleEngine.getModifiedOn();
			this.modifiedOn = ruleEngine.getModifiedOn().getTime();
		}
	}
	
	public RuleEngine getEntity() {
		RuleEngine ruleEngine = new RuleEngine();
		
		ruleEngine.setId(this.id);
		ruleEngine.setName(this.name);
		ruleEngine.setHostname(this.hostname);
		ruleEngine.setOnDemandInterval(this.onDemandInterval);
		ruleEngine.setScheduledInterval(this.scheduledInterval);
		ruleEngine.setOnDemandPoolSize(this.onDemandPoolSize);
		ruleEngine.setScheduledPoolSize(this.scheduledPoolSize);
		ruleEngine.setConfigReloadInterval(this.configReloadInterval);
		
		if(this.executionWindowSets != null && this.executionWindowSets.size() > 0) {
			for(ExecutionWindowSetDTO executionWindowSetDTO : this.executionWindowSets) {
				ruleEngine.getExecutionWindowSets().add(executionWindowSetDTO.getEntity());
			}
		}
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			ruleEngine.setCreatedOn(new Date(this.createdOn));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			ruleEngine.setModifiedOn(new Date(this.modifiedOn));
		}
		
		return ruleEngine;
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
	
	public Long getOnDemandInterval() {
		return onDemandInterval;
	}
	
	public void setOnDemandInterval(Long onDemandInterval) {
		this.onDemandInterval = onDemandInterval;
	}
	
	public Long getScheduledInterval() {
		return scheduledInterval;
	}
	
	public void setScheduledInterval(Long scheduledInterval) {
		this.scheduledInterval = scheduledInterval;
	}
	
	public Integer getOnDemandPoolSize() {
		return onDemandPoolSize;
	}
	
	public void setOnDemandPoolSize(Integer onDemandPoolSize) {
		this.onDemandPoolSize = onDemandPoolSize;
	}
	
	public Integer getScheduledPoolSize() {
		return scheduledPoolSize;
	}
	
	public void setScheduledPoolSize(Integer scheduledPoolSize) {
		this.scheduledPoolSize = scheduledPoolSize;
	}
	
	public Long getConfigLoadedOn() {
		return configLoadedOn;
	}
	
	public void setConfigLoadedOn(Long configLoadedOn) {
		this.configLoadedOn = configLoadedOn;
	}
	
	public Integer getConfigReloadInterval() {
		return configReloadInterval;
	}
	
	public void setConfigReloadInterval(Integer configReloadInterval) {
		this.configReloadInterval = configReloadInterval;
	}
	
	public List<ExecutionWindowSetDTO> getExecutionWindowSets() {
		return executionWindowSets;
	}
	
	public void setExecutionWindowSets(
			List<ExecutionWindowSetDTO> executionWindowSets) {
		this.executionWindowSets = executionWindowSets;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
