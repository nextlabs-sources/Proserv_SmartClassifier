package com.nextlabs.smartclassifier.dto;

import com.google.gson.Gson;
import com.nextlabs.smartclassifier.constant.EventCategory;
import com.nextlabs.smartclassifier.constant.EventStage;
import com.nextlabs.smartclassifier.constant.EventStatus;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.util.FileUtil;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Event {

    protected static final Gson gson = new Gson();

    private String componentName;
    private EventStage stage;
    private String fileId;
    private String repositoryType;
    private String filePath;
    private String fileName;
    private Long ruleId;
    private String ruleName;
    private Long ruleExecutionId;
    private String ruleExecutionType;
    private Long actionPluginId;
    private String actionName;
    private Long actionId;
    private EventCategory category;
    private EventStatus status;
    private String messageCode;
    private List<String> messageParam;
    private Long timestamp;

    public Event() {
        super();
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public EventStage getStage() {
        return stage;
    }

    public void setStage(EventStage stage) {
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

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessageParam() {
        if (this.messageParam != null) {
            return gson.toJson(this.messageParam);
        }

        return null;
    }

    public void addMessageParam(String messageParam) {
        if (this.messageParam == null) {
            this.messageParam = new ArrayList<String>();
        }

        this.messageParam.add(messageParam);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setAbsolutePath(String absolutePath, RepositoryType repositoryType) {
        if (StringUtils.isNotBlank(absolutePath)) {
            if (repositoryType == RepositoryType.SHARED_FOLDER) {
                this.filePath = FileUtil.getFilePath(absolutePath);
                this.fileName = FileUtil.getFileName(absolutePath);
            } else if (repositoryType == RepositoryType.SHAREPOINT) {
                String url = SharePointUtil.getSharePointURL(absolutePath);
                this.filePath = FileUtil.getFilePath(url);
                this.fileName = FileUtil.getFileName(absolutePath);
            }
        }
    }
}
