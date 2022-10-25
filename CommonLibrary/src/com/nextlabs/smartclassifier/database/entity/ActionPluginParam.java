package com.nextlabs.smartclassifier.database.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(
  name = "ACTION_PLUGIN_PARAMS",
  uniqueConstraints = {@UniqueConstraint(columnNames = {"ACTION_PLUGIN_ID", "IDENTIFIER"})}
)
public class ActionPluginParam {

  @Id
  @Column(name = "ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
  @GenericGenerator(
    name = "SequenceGeneratorYYMMDDHH",
    strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
    parameters = {@Parameter(name = "sequence", value = "ACTION_PLUGIN_PARAMS_SEQ")}
  )
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ACTION_PLUGIN_ID")
  private ActionPlugin actionPlugin;

  @Column(name = "DISPLAY_ORDER", nullable = false)
  private Integer displayOrder;

  @Column(name = "PARAM_TYPE", nullable = false, length = 1)
  private String paramType;

  @Column(name = "COLLECTIONS", nullable = false)
  private Boolean collections;

  @Column(name = "KEY_VALUE", nullable = false)
  private Boolean keyValue;

  @Column(name = "DATA_TYPE", nullable = false, length = 10)
  private String dataType;

  @Column(name = "LABEL", nullable = false, length = 100)
  private String label;

  @Column(name = "IDENTIFIER", nullable = false, length = 100)
  private String identifier;

  @Column(name = "FIXED_PARAMETER", nullable = false)
  private Boolean fixedParameter;

  @Column(name = "FIXED_VALUE", nullable = true, length = 100)
  private String fixedValue;

  @Column(name = "CREATED_ON", nullable = false)
  private Date createdOn;

  @Version
  @Column(name = "MODIFIED_ON", nullable = true)
  private Date modifiedOn;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ActionPlugin getActionPlugin() {
    return actionPlugin;
  }

  public void setActionPlugin(ActionPlugin actionPlugin) {
    this.actionPlugin = actionPlugin;
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

  public String getFixedValue() {
    return fixedValue;
  }

  public void setFixedValue(String fixedValue) {
    this.fixedValue = fixedValue;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public Date getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(Date modifiedOn) {
    this.modifiedOn = modifiedOn;
  }
}
