package com.nextlabs.smartclassifier.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ROLLBACK_ERRORS")
public class RollbackError {
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "REPOSITORY_TYPE", nullable = true, length = 30)
	private String repositoryType;
	
	@Column(name = "BACKUP_ABSOLUTE_FILE_PATH", nullable = true, length = 1024)
	private String backupAbsoluteFilePath;
	
	@Column(name = "TARGET_ABSOLUTE_FILE_PATH", nullable = false, length = 1024)
	private String targetAbsoluteFilePath;
	
	@Column(name = "RULE_ID", nullable = false)
	private Long ruleId;
	
	@Column(name = "RULE_NAME", nullable = false, length = 320)
	private String ruleName;
	
	@Column(name = "RULE_EXECUTION_ID", nullable = false)
	private Long ruleExecutionId;
	
	@Column(name = "RULE_EXECUTION_TYPE", nullable = false, length = 1)
	private String ruleExecutionType;
	
	@Column(name = "ACTION_PLUGIN_ID", nullable = true)
	private Long actionPluginId;
	
	@Column(name = "ACTION_NAME", nullable = true, length = 100)
	private String actionName;
	
	@Column(name = "ACTION_ID", nullable = true)
	private Long actionId;
	
	@Column(name = "ERROR_TYPE", nullable = false, length = 1)
	private String errorType;
	
	@Column(name = "MESSAGE", nullable = true, length = 1024)
	private String message;
	
	@Column(name = "TIMESTAMP", nullable = false)
	private Long timestamp;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getRepositoryType() {
		return repositoryType;
	}
	
	public void setRepositoryType(String repositoryType) {
		this.repositoryType = repositoryType;
	}
	
	public String getBackupAbsoluteFilePath() {
		return backupAbsoluteFilePath;
	}
	
	public void setBackupAbsoluteFilePath(String backupAbsoluteFilePath) {
		this.backupAbsoluteFilePath = backupAbsoluteFilePath;
	}
	
	public String getTargetAbsoluteFilePath() {
		return targetAbsoluteFilePath;
	}
	
	public void setTargetAbsoluteFilePath(String targetAbsoluteFilePath) {
		this.targetAbsoluteFilePath = targetAbsoluteFilePath;
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
	
	public String getErrorType() {
		return errorType;
	}
	
	public void setErrorType(String errorType) {
		this.errorType = errorType;
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
