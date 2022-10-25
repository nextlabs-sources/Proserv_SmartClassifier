package com.nextlabs.smartclassifier.validator.v1.report;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.constant.ScheduleType;
import com.nextlabs.smartclassifier.dto.v1.ReportDTO;
import com.nextlabs.smartclassifier.dto.v1.ReportFilterDTO;
import com.nextlabs.smartclassifier.dto.v1.ReportFilterGroupDTO;
import com.nextlabs.smartclassifier.dto.v1.ReportRecipientDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class ReportUpdateValidator {
	
	public static Validation validate(UpdateRequest request)
			throws Exception {
		Validation validation = new Validation();
		ReportDTO reportDTO = (ReportDTO)request.getData();
		
		// Target record id is missing
		if(reportDTO.getId() == null || reportDTO.getId() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "ReportID"));
			
			return validation;
		}
		
		// Name should not be blank
		if(StringUtils.isBlank(reportDTO.getName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));
			
			return validation;
		}
		
		// Type should not be blank
		if(StringUtils.isBlank(reportDTO.getType())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Type"));
			
			return validation;
		}
		
		// If there is report filter defined
		if(reportDTO.getFilterGroups() != null) {
			for(ReportFilterGroupDTO reportFilterGroupDTO : reportDTO.getFilterGroups()) {
				if(reportFilterGroupDTO.getFilters() == null || reportFilterGroupDTO.getFilters().size() == 0) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.empty.collection", "Filters"));
					
					return validation;
				}
				
				for(ReportFilterDTO reportFilterDTO : reportFilterGroupDTO.getFilters()) {
					if(StringUtils.isBlank(reportFilterDTO.getFieldName())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Filter field name"));
						
						return validation;
					}
					
					if(StringUtils.isBlank(reportFilterDTO.getOperator())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Filter operator"));
						
						return validation;
					}
					
					if(StringUtils.isBlank(reportFilterDTO.getValue())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Filter value"));
						
						return validation;
					}
				}
			}
		}
		
		// Report schedule
		if(ExecutionType.SCHEDULED.getCode().equalsIgnoreCase(reportDTO.getType())) {
			// Schedule should not be null
			if(reportDTO.getSchedule() == null) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Schedule"));
				
				return validation;
			}
			
			// Schedule type should not be blank
			if(StringUtils.isBlank(reportDTO.getSchedule().getScheduleType())) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Schedule type"));
				
				return validation;
			}
			
			// Execution frequency should not be null
			if(reportDTO.getSchedule().getExecutionFrequency() == null) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Execution frequency"));
				
				return validation;
			}
			
			if(StringUtils.isBlank(reportDTO.getSchedule().getExecutionFrequency().getTime())) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Time"));
				
				return validation;
			}
			
			if(ScheduleType.WEEKLY.getCode().equalsIgnoreCase(reportDTO.getSchedule().getScheduleType())) {
				if(reportDTO.getSchedule().getExecutionFrequency().getDayOfWeek() == null
						|| reportDTO.getSchedule().getExecutionFrequency().getDayOfWeek().size() == 0) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Day of week"));
					
					return validation;
				}
			}
			
			if(ScheduleType.MONTHLY.getCode().equalsIgnoreCase(reportDTO.getSchedule().getScheduleType())) {
				if(reportDTO.getSchedule().getExecutionFrequency().getDayOfMonth() == null
						|| reportDTO.getSchedule().getExecutionFrequency().getDayOfMonth().size() == 0) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Day of Month"));
					
					return validation;
				}
			}
			
			// Email subject should not be null
			if(StringUtils.isBlank(reportDTO.getSchedule().getEmailSubject())) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Email subject"));
				
				return validation;
			}
			
			// Email content should not be null
			if(StringUtils.isBlank(reportDTO.getSchedule().getEmailContent())) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Email content"));
				
				return validation;
			}
			
			// There must be at least one recipient
			if(reportDTO.getSchedule().getRecipients() == null
					|| reportDTO.getSchedule().getRecipients().size() == 0) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Day of month"));
				
				return validation;
			}
			
			// Recipient information should not be blank
			for(ReportRecipientDTO recipientDTO : reportDTO.getSchedule().getRecipients()) {
				if(StringUtils.isBlank(recipientDTO.getField())) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Email field"));
					
					return validation;
				}
				
				if(StringUtils.isBlank(recipientDTO.getEmail())) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Email address"));
					
					return validation;
				}
			}
		}
		
		return validation;
	}
}
