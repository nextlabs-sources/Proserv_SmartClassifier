package com.nextlabs.smartclassifier.dto.v1;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ActionPluginParam;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ActionPluginParamDTO extends BaseDTO {

  @Expose private Integer displayOrder;
  @Expose private String paramType;
  @Expose private Boolean collections;
  @Expose private Boolean keyValue;
  @Expose private String dataType;
  @Expose private String label;
  @Expose private String identifier;
  @Expose private Boolean fixedParameter;
  @Expose private String value;

  public ActionPluginParamDTO() {
    super();
  }

  public ActionPluginParamDTO(ActionPluginParam actionPluginParam) {
    super();
    copy(actionPluginParam);
  }

  public void copy(ActionPluginParam actionPluginParam) {
    if (actionPluginParam != null) {
      this.id = actionPluginParam.getId();
      this.displayOrder = actionPluginParam.getDisplayOrder();
      this.paramType = actionPluginParam.getParamType();
      this.collections = actionPluginParam.isCollections();
      this.keyValue = actionPluginParam.isKeyValue();
      this.dataType = actionPluginParam.getDataType();
      this.label = actionPluginParam.getLabel();
      this.identifier = actionPluginParam.getIdentifier();
      this.fixedParameter = actionPluginParam.isFixedParameter();
      this.value = actionPluginParam.getFixedValue();
      this.createdTimestamp = actionPluginParam.getCreatedOn();
      this.createdOn = actionPluginParam.getCreatedOn().getTime();
      this.modifiedTimestamp = actionPluginParam.getModifiedOn();
      this.modifiedOn = actionPluginParam.getModifiedOn().getTime();
    }
  }

  public ActionPluginParam getEntity() {
    ActionPluginParam actionPluginParam = new ActionPluginParam();

    actionPluginParam.setId(this.id);
    actionPluginParam.setDisplayOrder(this.displayOrder);
    actionPluginParam.setParamType(this.paramType);
    actionPluginParam.isCollections(this.collections);
    actionPluginParam.isKeyValue(this.keyValue);
    actionPluginParam.setDataType(this.dataType);
    actionPluginParam.setLabel(this.label);
    actionPluginParam.setIdentifier(this.identifier);
    actionPluginParam.isFixedParameter(this.fixedParameter);
    actionPluginParam.setFixedValue(this.value);

    if (this.createdOn != null && this.createdOn > 0) {
      actionPluginParam.setCreatedOn(new Date(this.createdOn));
    }

    if (this.modifiedOn != null && this.modifiedOn > 0) {
      actionPluginParam.setModifiedOn(new Date(this.modifiedOn));
    }

    return actionPluginParam;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public String getParamType() {
    return paramType;
  }

  public void setParamType(String paramType) {
    this.paramType = paramType;
  }

  public Boolean isCollections() {
    return collections;
  }

  public void isCollections(Boolean collections) {
    this.collections = collections;
  }

  public Boolean isKeyValue() {
    return keyValue;
  }

  public void isKeyValue(Boolean keyValue) {
    this.keyValue = keyValue;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
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

  public Boolean isFixedParameter() {
    return fixedParameter;
  }

  public void isFixedParameter(Boolean fixedParameter) {
    this.fixedParameter = fixedParameter;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
