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
  name = "EXECUTION_WINDOW_ASSOCIATIONS",
  uniqueConstraints = {@UniqueConstraint(columnNames = {"COMPONENT_ID", "COMPONENT_TYPE", "EXECUTION_WINDOW_SET_ID"})}
)
public class ExecutionWindowAssociation {

  // Field name for search criteria construction
  public static final String ID = "id";
  public static final String COMPONENT_ID = "componentId";
  public static final String COMPONENT_TYPE = "componentType";
  public static final String DISPLAY_ORDER = "displayOrder";
  public static final String EXECUTION_WINDOW_SET_ID = "executionWindowSet.id";
  public static final String CREATED_ON = "createdOn";
  public static final String MODIFIED_ON = "modifiedOn";

  @Id
  @Column(name = "ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
  @GenericGenerator(
    name = "SequenceGeneratorYYMMDDHH",
    strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
    parameters = {@Parameter(name = "sequence", value = "EXECUTION_WINDOW_ASSOCIATIONS_SEQ")}
  )
  private Long id;

  @Column(name = "COMPONENT_ID", nullable = false)
  private Long componentId;
  
  @Column(name = "COMPONENT_TYPE", nullable = false, length = 1)
  private String componentType;
  
  @Column(name = "DISPLAY_ORDER", nullable = false)
  private Integer displayOrder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EXECUTION_WINDOW_SET_ID")
  private ExecutionWindowSet executionWindowSet;

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

  public Long getComponentId() {
    return componentId;
  }

  public void setComponentId(Long componentId) {
    this.componentId = componentId;
  }
  
  public String getComponentType() {
	return componentType;
  }
  
  public void setComponentType(String componentType) {
	this.componentType = componentType;
  }
  
  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public ExecutionWindowSet getExecutionWindowSet() {
    return executionWindowSet;
  }

  public void setExecutionWindowSet(ExecutionWindowSet executionWindowSet) {
    this.executionWindowSet = executionWindowSet;
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
