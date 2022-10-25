package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindow;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindowSet;
import com.nextlabs.smartclassifier.dto.BaseDTO;
import com.nextlabs.smartclassifier.util.DateUtil;

public class ExecutionWindowSetDTO extends BaseDTO {

  @Expose private String name;
  @Expose private String description;
  @Expose private String scheduleType;
  @Expose private Long effectiveFrom;
  @Expose private Long effectiveUntil;
  @Expose private List<ExecutionWindowDTO> executionWindows;

  public ExecutionWindowSetDTO() {
    super();
  }

  public ExecutionWindowSetDTO(ExecutionWindowSet executionWindowSet) {
    super();
    copy(executionWindowSet);
  }

  public void copy(ExecutionWindowSet executionWindowSet) {
    if (executionWindowSet != null) {
      this.id = executionWindowSet.getId();
      this.name = executionWindowSet.getName();
      this.description = executionWindowSet.getDescription();
      this.scheduleType = executionWindowSet.getScheduleType();

      if (executionWindowSet.getEffectiveFrom() != null) {
        this.effectiveFrom = executionWindowSet.getEffectiveFrom().getTime();
      }

      if (executionWindowSet.getEffectiveUntil() != null) {
        this.effectiveUntil = executionWindowSet.getEffectiveUntil().getTime();
      }

      if (executionWindowSet.getExecutionWindows() != null
          && executionWindowSet.getExecutionWindows().size() > 0) {
        this.executionWindows = new ArrayList<ExecutionWindowDTO>();
        for (ExecutionWindow executionWindow : executionWindowSet.getExecutionWindows()) {
          this.executionWindows.add(new ExecutionWindowDTO(executionWindow));
        }
      }

      this.createdTimestamp = executionWindowSet.getCreatedOn();
      this.createdOn = executionWindowSet.getCreatedOn().getTime();
      this.modifiedTimestamp = executionWindowSet.getModifiedOn();
      this.modifiedOn = executionWindowSet.getModifiedOn().getTime();
    }
  }

  public ExecutionWindowSet getEntity() {
    ExecutionWindowSet executionWindowSet = new ExecutionWindowSet();

    executionWindowSet.setId(this.id);
    executionWindowSet.setName(this.name);
    executionWindowSet.setDescription(this.description);
    executionWindowSet.setScheduleType(this.scheduleType);

    if (this.effectiveFrom != null && this.effectiveFrom > 0) {
      executionWindowSet.setEffectiveFrom(DateUtil.toStartOfTheDay(this.effectiveFrom));
    }

    if (this.effectiveUntil != null && this.effectiveUntil > 0) {
      executionWindowSet.setEffectiveUntil(DateUtil.toEndOfTheDay(this.effectiveUntil));
    }

    if (this.executionWindows != null && this.executionWindows.size() > 0) {
      for (ExecutionWindowDTO executionWindowDTO : this.executionWindows) {
        executionWindowSet.getExecutionWindows().add(executionWindowDTO.getEntity());
      }
    }

    if (this.createdOn != null && this.createdOn > 0) {
      executionWindowSet.setCreatedOn(new Date(this.createdOn));
    }

    if (this.modifiedOn != null && this.modifiedOn > 0) {
      executionWindowSet.setModifiedOn(new Date(this.modifiedOn));
    }

    return executionWindowSet;
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

  public String getScheduleType() {
    return scheduleType;
  }

  public void setScheduleType(String scheduleType) {
    this.scheduleType = scheduleType;
  }

  public Long getEffectiveFrom() {
    return effectiveFrom;
  }

  public void setEffectiveFrom(Long effectiveFrom) {
    this.effectiveFrom = effectiveFrom;
  }

  public Long getEffectiveUntil() {
    return effectiveUntil;
  }

  public void setEffectiveUntil(Long effectiveUntil) {
    this.effectiveUntil = effectiveUntil;
  }

  public List<ExecutionWindowDTO> getExecutionWindows() {
    return executionWindows;
  }

  public void setExecutionWindows(List<ExecutionWindowDTO> executionWindows) {
    this.executionWindows = executionWindows;
  }
}
