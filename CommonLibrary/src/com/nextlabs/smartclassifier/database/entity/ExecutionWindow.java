package com.nextlabs.smartclassifier.database.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "EXECUTION_WINDOWS")
public class ExecutionWindow {

  @Id
  @Column(name = "ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
  @GenericGenerator(
    name = "SequenceGeneratorYYMMDDHH",
    strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
    parameters = {@Parameter(name = "sequence", value = "EXECUTION_WINDOWS_SEQ")}
  )
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "EXECUTION_WINDOW_SET_ID")
  private ExecutionWindowSet executionWindowSet;

  @Column(name = "DISPLAY_ORDER", nullable = false)
  private Integer displayOrder;

  @Column(name = "DAY", nullable = false, length = 15)
  private String day;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "executionWindow", cascade = CascadeType.ALL)
  @OrderBy("displayOrder ASC")
  @NotFound(action = NotFoundAction.IGNORE)
  private Set<ExecutionTimeSlot> executionTimeSlots = new LinkedHashSet<ExecutionTimeSlot>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ExecutionWindowSet getExecutionWindowSet() {
    return executionWindowSet;
  }

  public void setExecutionWindowSet(ExecutionWindowSet executionWindowSet) {
    this.executionWindowSet = executionWindowSet;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public Set<ExecutionTimeSlot> getExecutionTimeSlots() {
    return executionTimeSlots;
  }

  public void setExecutionTimeSlots(Set<ExecutionTimeSlot> executionTimeSlots) {
    this.executionTimeSlots = executionTimeSlots;
  }
}
