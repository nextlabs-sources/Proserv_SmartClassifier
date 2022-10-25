package com.nextlabs.smartclassifier.dto.v1;

import static com.nextlabs.smartclassifier.constant.Punctuation.GENERAL_DELIMITER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ExecutionTimeSlot;
import com.nextlabs.smartclassifier.database.entity.ExecutionWindow;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ExecutionWindowDTO extends BaseDTO {

  @Expose private Integer displayOrder;
  @Expose private List<String> day;
  @Expose private List<ExecutionTimeSlotDTO> executionTimeSlots;

  public ExecutionWindowDTO() {
    super();
  }

  public ExecutionWindowDTO(ExecutionWindow executionWindow) {
    super();
    copy(executionWindow);
  }

  public void copy(ExecutionWindow executionWindow) {
    if (executionWindow != null) {
      this.id = executionWindow.getId();
      this.displayOrder = executionWindow.getDisplayOrder();

      if (StringUtils.isNotBlank(executionWindow.getDay())) {
        this.day = Arrays.asList(executionWindow.getDay().split(Pattern.quote(GENERAL_DELIMITER)));
      }

      if (executionWindow.getExecutionTimeSlots() != null
          && executionWindow.getExecutionTimeSlots().size() > 0) {
        this.executionTimeSlots = new ArrayList<ExecutionTimeSlotDTO>();
        for (ExecutionTimeSlot executionTimeSlot : executionWindow.getExecutionTimeSlots()) {
          this.executionTimeSlots.add(new ExecutionTimeSlotDTO(executionTimeSlot));
        }
      }
    }
  }

  public ExecutionWindow getEntity() {
    ExecutionWindow executionWindow = new ExecutionWindow();

    executionWindow.setId(this.id);
    executionWindow.setDisplayOrder(this.displayOrder);

    if (this.day != null) {
      executionWindow.setDay(StringUtils.join(this.day, GENERAL_DELIMITER));
    }

    if (this.executionTimeSlots != null && this.executionTimeSlots.size() > 0) {
      for (ExecutionTimeSlotDTO executionTimeSlotDTO : this.executionTimeSlots) {
        executionWindow.getExecutionTimeSlots().add(executionTimeSlotDTO.getEntity());
      }
    }

    return executionWindow;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public List<String> getDay() {
    return day;
  }

  public void setDay(List<String> day) {
    this.day = day;
  }

  public List<ExecutionTimeSlotDTO> getExecutionTimeSlots() {
    return executionTimeSlots;
  }

  public void setExecutionTimeSlots(List<ExecutionTimeSlotDTO> executionTimeSlots) {
    this.executionTimeSlots = executionTimeSlots;
  }
}
