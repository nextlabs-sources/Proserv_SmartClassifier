package com.nextlabs.smartclassifier.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EVENT_LOGS")
public class EventLog {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String COMPONENT_NAME = "componentName";
	public static final String STAGE = "stage";
	public static final String FILE_ID = "fileId";
	public static final String REPOSITORY_TYPE = "repositoryType";
	public static final String FILE_PATH = "filePath";
	public static final String FILE_NAME = "fileName";
	public static final String RULE_ID = "ruleId";
	public static final String RULE_NAME = "ruleName";
	public static final String RULE_EXECUTION_ID = "ruleExecutionId";
	public static final String RULE_EXECUTION_TYPE = "ruleExecutionType";
	public static final String ACTION_PLUGIN_ID = "actionPluginId";
	public static final String ACTION_NAME = "actionName";
	public static final String ACTION_ID = "actionId";
	public static final String CATEGORY = "category";
	public static final String STATUS = "status";
	public static final String MESSAGE_CODE = "messageCode";
	public static final String MESSAGE_PARAM = "messageParam";
	public static final String TIMESTAMP = "timestamp";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "COMPONENT_NAME", nullable = false, length = 100)
	private String componentName;
	
	@Column(name = "STAGE", nullable = false, length = 4)
	private String stage;
	
	@Column(name = "FILE_ID", nullable = true, length = 25)
	private String fileId;
	
	@Column(name = "REPOSITORY_TYPE", nullable = true, length = 30)
	private String repositoryType;
	
	@Column(name = "FILE_PATH", nullable = true, length = 1024)
	private String filePath;
	
	@Column(name = "FILE_NAME", nullable = true, length = 450)
	private String fileName;
	
	@Column(name = "RULE_ID", nullable = true)
	private Long ruleId;
	
	@Column(name = "RULE_NAME", nullable = true, length = 320)
	private String ruleName;
	
	@Column(name = "RULE_EXECUTION_ID", nullable = true)
	private Long ruleExecutionId;
	
	@Column(name = "RULE_EXECUTION_TYPE", nullable = true, length = 1)
	private String ruleExecutionType;
	
	@Column(name = "ACTION_PLUGIN_ID", nullable = true)
	private Long actionPluginId;
	
	@Column(name = "ACTION_NAME", nullable = true, length = 100)
	private String actionName;
	
	@Column(name = "ACTION_ID", nullable = true)
	private Long actionId;
	
	@Column(name = "CATEGORY", nullable = false, length = 1)
	private String category;
	
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status;
	
	@Column(name = "MESSAGE_CODE", nullable = false, length = 50)
	private String messageCode;
	
	@Column(name = "MESSAGE_PARAM", nullable = true, length = 4000)
	private String messageParam;
	
	@Column(name = "TIMESTAMP", nullable = false)
	private Long timestamp;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getComponentName() {
		return componentName;
	}
	
	public void setComponentName(String componentName) {
		this.componentName = componentName;
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
	
	public String getStage() {
		return stage;
	}
	
	public void setStage(String stage) {
		this.stage = stage;
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
	
	public String getMessageCode() {
		return messageCode;
	}
	
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
	
	public String getMessageParam() {
		return messageParam;
	}
	
	public void setMessageParam(String messageParam) {
		this.messageParam = messageParam;
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
