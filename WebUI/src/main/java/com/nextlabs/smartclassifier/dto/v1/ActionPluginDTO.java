package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ActionPlugin;
import com.nextlabs.smartclassifier.database.entity.ActionPluginParam;
import com.nextlabs.smartclassifier.dto.BaseDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionPluginDTO
        extends BaseDTO {

    @Expose
    private String className;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private String repositoryType;
    @Expose
    private List<ActionPluginParamDTO> params;

    private transient Map<String, ActionPluginParamDTO> pluginParamMap;

    public ActionPluginDTO() {
        super();
        this.pluginParamMap = new HashMap<>();
    }

    public ActionPluginDTO(ActionPlugin actionPlugin) {
        super();
        this.pluginParamMap = new HashMap<>();
        copy(actionPlugin);
    }

    public void copy(ActionPlugin actionPlugin) {
        if (actionPlugin != null) {
            this.id = actionPlugin.getId();
            this.className = actionPlugin.getClassName();
            this.name = actionPlugin.getDisplayName();
            this.description = actionPlugin.getDescription();
            this.repositoryType = actionPlugin.getRepositoryType();

            if (actionPlugin.getPluginParams() != null && actionPlugin.getPluginParams().size() > 0) {
                this.params = new ArrayList<>();
                for (ActionPluginParam actionPluginParam : actionPlugin.getPluginParams()) {
                    ActionPluginParamDTO actionPluginParamDTO = new ActionPluginParamDTO(actionPluginParam);
                    this.params.add(actionPluginParamDTO);
                    this.pluginParamMap.put(actionPluginParamDTO.getIdentifier(), actionPluginParamDTO);
                }
            }

            this.createdTimestamp = actionPlugin.getCreatedOn();
            this.createdOn = actionPlugin.getCreatedOn().getTime();
            this.modifiedTimestamp = actionPlugin.getModifiedOn();
            this.modifiedOn = actionPlugin.getModifiedOn().getTime();
        }
    }

    public ActionPlugin getEntity() {
        ActionPlugin actionPlugin = new ActionPlugin();

        actionPlugin.setId(this.id);
        actionPlugin.setClassName(this.className);
        actionPlugin.setDisplayName(this.name);
        actionPlugin.setDescription(this.description);
        actionPlugin.setRepositoryType(this.repositoryType);

        if (this.params != null && this.params.size() > 0) {
            for (ActionPluginParamDTO actionPluginParamDTO : this.params) {
                actionPlugin.getPluginParams().add(actionPluginParamDTO.getEntity());
            }
        }

        if (this.createdOn != null
                && this.createdOn > 0) {
            actionPlugin.setCreatedOn(new Date(this.createdOn));
        }

        if (this.modifiedOn != null
                && this.modifiedOn > 0) {
            actionPlugin.setModifiedOn(new Date(this.modifiedOn));
        }

        return actionPlugin;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ActionPluginParamDTO> getParams() {
        return params;
    }

    public void setParams(List<ActionPluginParamDTO> params) {
        this.params = params;
    }

    public Map<String, ActionPluginParamDTO> getPluginParamMap() {
        return pluginParamMap;
    }

    public void setPluginParamMap(Map<String, ActionPluginParamDTO> pluginParamMap) {
        this.pluginParamMap = pluginParamMap;
    }
}
