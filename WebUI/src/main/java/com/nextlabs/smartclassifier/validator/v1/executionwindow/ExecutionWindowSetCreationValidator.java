package com.nextlabs.smartclassifier.validator.v1.executionwindow;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.ExecutionTimeSlotDTO;
import com.nextlabs.smartclassifier.dto.v1.ExecutionWindowDTO;
import com.nextlabs.smartclassifier.dto.v1.ExecutionWindowSetDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class ExecutionWindowSetCreationValidator {

  public static Validation validate(CreationRequest request) throws Exception {
    Validation validation = new Validation();
    ExecutionWindowSetDTO executionWindowSetDTO = (ExecutionWindowSetDTO) request.getData();

    // Name should not be blank
    if (StringUtils.isBlank(executionWindowSetDTO.getName())) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
      validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));

      return validation;
    }

    // Schedule type should not be blank
    if (StringUtils.isBlank(executionWindowSetDTO.getScheduleType())) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
      validation.setErrorMessage(
          MessageUtil.getMessage("invalid.input.field.blank", "Schedule type"));

      return validation;
    }

    if ((executionWindowSetDTO.getEffectiveFrom() != null && executionWindowSetDTO.getEffectiveFrom() > 0)
        && (executionWindowSetDTO.getEffectiveUntil() != null && executionWindowSetDTO.getEffectiveUntil() > 0)) {
      if (executionWindowSetDTO.getEffectiveUntil() < executionWindowSetDTO.getEffectiveFrom()) {
        validation.isValid(false);
        validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
        validation.setErrorMessage(
            MessageUtil.getMessage(
                "invalid.input.field.minvalue", "Effective end date", "effective start date"));

        return validation;
      }
    }

    // Must has one or more execution window
    if (executionWindowSetDTO.getExecutionWindows() == null
        || executionWindowSetDTO.getExecutionWindows().size() == 0) {
      validation.isValid(false);
      validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
      validation.setErrorMessage(
          MessageUtil.getMessage("invalid.input.empty.collection", "Execution window"));
    } else {
      // Validate execution window
      for (ExecutionWindowDTO executionWindowDTO : executionWindowSetDTO.getExecutionWindows()) {
        // Must have display order
        if (executionWindowDTO.getDisplayOrder() <= 0) {
          validation.isValid(false);
          validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
          validation.setErrorMessage(
              MessageUtil.getMessage(
                  "invalid.input.field.minvalue", "Execution window display order", "1"));

          return validation;
        }

        // Must have execution day
        if (executionWindowDTO.getDay() == null || executionWindowDTO.getDay().size() == 0) {
          validation.isValid(false);
          validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
          validation.setErrorMessage(
              MessageUtil.getMessage("invalid.input.empty.collection", "Execution day"));

          return validation;
        }

        // Execution time slot cannot be empty
        if (executionWindowDTO.getExecutionTimeSlots() == null
            || executionWindowDTO.getExecutionTimeSlots().size() == 0) {
          validation.isValid(false);
          validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
          validation.setErrorMessage(
              MessageUtil.getMessage("invalid.input.empty.collection", "Execution time slot"));

          return validation;
        } else {
          // Validate execution time slot
          for (ExecutionTimeSlotDTO executionTimeSlotDTO :
              executionWindowDTO.getExecutionTimeSlots()) {
            // Must have display order
            if (executionTimeSlotDTO.getDisplayOrder() <= 0) {
              validation.isValid(false);
              validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
              validation.setErrorMessage(
                  MessageUtil.getMessage(
                      "invalid.input.field.minvalue", "Execution time slot display order", "1"));

              return validation;
            }

            // Must have start time
            if (StringUtils.isBlank(executionTimeSlotDTO.getStartTime())) {
              validation.isValid(false);
              validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
              validation.setErrorMessage(
                  MessageUtil.getMessage("invalid.input.field.blank", "Start time"));

              return validation;
            }

            // Must have end time
            if (StringUtils.isBlank(executionTimeSlotDTO.getEndTime())) {
              validation.isValid(false);
              validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
              validation.setErrorMessage(
                  MessageUtil.getMessage("invalid.input.field.blank", "End time"));

              return validation;
            }
          }
        }
      }
    }

    return validation;
  }
}
