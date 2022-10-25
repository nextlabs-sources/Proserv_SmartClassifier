package com.nextlabs.smartclassifier.plugin.action;

import com.nextlabs.smartclassifier.constant.ActionParamType;
import com.nextlabs.smartclassifier.constant.DataType;

public class ActionPluginParameter {

    private ActionParamType paramType;

    private boolean collections;

    private DataType dataType;

    private String label;

    private String identifier;

    private boolean fixedParameter;

    private String fixedValue;

    public ActionPluginParameter() {
        super();
    }

    public ActionPluginParameter(ActionParamType paramType, boolean collections, DataType dataType,
                                 String label, String identifier, boolean fixedParameter, String fixedValue) {
        super();
        this.paramType = paramType;
        this.collections = collections;
        this.dataType = dataType;
        this.label = label;
        this.identifier = identifier;
        this.fixedParameter = fixedParameter;
        this.fixedValue = fixedValue;
    }

    public ActionParamType getParamType() {
        return paramType;
    }

    public void setParamType(ActionParamType paramType) {
        this.paramType = paramType;
    }

    public boolean isCollections() {
        return collections;
    }

    public void setCollections(boolean collections) {
        this.collections = collections;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isFixedParameter() {
        return fixedParameter;
    }

    public void setFixedParameter(boolean fixedParameter) {
        this.fixedParameter = fixedParameter;
    }

    public String getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(String fixedValue) {
        this.fixedValue = fixedValue;
    }
}
