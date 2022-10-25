package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class ActionParamDTO {

  @Expose private Boolean collections;
  @Expose private Boolean keyValue;
  @Expose private String dataType;
  @Expose private String label;
  @Expose private String identifier;
  @Expose private List<KeyValueDTO> values;

  public ActionParamDTO() {
    super();
    this.values = new ArrayList<KeyValueDTO>();
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

  public List<KeyValueDTO> getValues() {
    return values;
  }

  public void setValues(List<KeyValueDTO> values) {
    this.values = values;
  }
}
