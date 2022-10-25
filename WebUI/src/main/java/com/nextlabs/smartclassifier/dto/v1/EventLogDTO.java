package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.EventLog;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class EventLogDTO 
		extends BaseDTO {
	
	@Expose
	private String componentName;
	@Expose
	private String stage;
	@Expose
	private String fileId;
	@Expose
	private String repositoryType;
	@Expose
	private String filePath;
	@Expose
	private String fileName;
	@Expose
	private Long ruleId;
	@Expose
	private String ruleName;
	@Expose
	private Long ruleExecutionId;
	@Expose
	private String ruleExecutionType;
	@Expose
	private Long actionPluginId;
	@Expose
	private String actionName;
	@Expose
	private Long actionId;
	@Expose
	private String category;
	@Expose
	private String status;
	@Expose
	private String message;
	@Expose
	private Long timestamp;
	
	public EventLogDTO() {
		super();
	}
	
	public EventLogDTO(EventLog eventLog) {
		super();
		copy(eventLog);
	}
	
	public void copy(EventLog eventLog) {
		if(eventLog != null) {
			this.componentName = eventLog.getComponentName();
			this.stage = eventLog.getStage();
			this.fileId = eventLog.getFileId();
			this.repositoryType = eventLog.getRepositoryType();
			this.filePath = eventLog.getFilePath();
			this.fileName = eventLog.getFileName();
			this.ruleId = eventLog.getRuleId();
			this.ruleName = eventLog.getRuleName();
			this.ruleExecutionId = eventLog.getRuleExecutionId();
			this.ruleExecutionType = eventLog.getRuleExecutionType();
			this.actionPluginId = eventLog.getActionPluginId();
			this.actionName = eventLog.getActionName();
			this.actionId = eventLog.getActionId();
			this.category = eventLog.getCategory();
			this.status = eventLog.getStatus();
			this.timestamp = eventLog.getTimestamp();
		}
	}
	
	public String getComponentName() {
		return componentName;
	}
	
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	public String getStage() {
		return stage;
	}
	
	public void setStage(String stage) {
		this.stage = stage;
	}
	
	public String getFileId() {
		return fileId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public String getRepositoryType() {
		return repositoryType;
	}
	
	public void setRepositoryType(String repositoryType) {
		this.repositoryType = repositoryType;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public Long getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	public Long getRuleExecutionId() {
		return ruleExecutionId;
	}
	
	public void setRuleExecutionId(Long ruleExecutionId) {
		this.ruleExecutionId = ruleExecutionId;
	}
	
	public String getRuleExecutionType() {
		return ruleExecutionType;
	}
	
	public void setRuleExecutionType(String ruleExecutionType) {
		this.ruleExecutionType = ruleExecutionType;
	}
	
	public Long getActionPluginId() {
		return actionPluginId;
	}
	
	public void setActionPluginId(Long actionPluginId) {
		this.actionPluginId = actionPluginId;
	}
	
	public String getActionName() {
		return actionName;
	}
	
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	public Long getActionId() {
		return actionId;
	}
	
	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
