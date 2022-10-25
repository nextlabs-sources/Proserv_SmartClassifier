package com.nextlabs.smartclassifier.validator.v1.reportquery;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.ReportFilterDTO;
import com.nextlabs.smartclassifier.dto.v1.ReportFilterGroupDTO;
import com.nextlabs.smartclassifier.dto.v1.request.ReportQueryRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class ReportQueryValidator {
	
	public static Validation validate(ReportQueryRequest request) {
		Validation validation = new Validation();
		
		if(request.getRange() == null) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Range"));
			
			return validation;
		}
		
		List<ReportFilterGroupDTO> filterGroupDTOs = request.getFilterGroups();
		// Check filter
		if(filterGroupDTOs != null) {
			for(ReportFilterGroupDTO reportFilterGroupDTO : filterGroupDTOs) {
				if(reportFilterGroupDTO.getFilters() == null) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.empty.collection", "Filters"));
					
					return validation;
				}
				
				for(ReportFilterDTO filterDTO : reportFilterGroupDTO.getFilters()) {
					if(StringUtils.isBlank(filterDTO.getFieldName())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Field name"));
						
						return validation;
					}
					
					if(StringUtils.isBlank(filterDTO.getOperator())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Operator"));
						
						return validation;
					}
					
					if(StringUtils.isBlank(filterDTO.getValue())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Value"));
						
						return validation;
					}
				}
			}
		}
		
		List<ReportFilterGroupDTO> refineGroupDTOs = request.getRefineGroups();
		// Check refine filter
		if(refineGroupDTOs != null) {
			for(ReportFilterGroupDTO refineGroupDTO : refineGroupDTOs) {
				if(refineGroupDTO.getFilters() == null) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.empty.collection", "Refine filters"));
					
					return validation;
				}
				
				for(ReportFilterDTO filterDTO : refineGroupDTO.getFilters()) {
					if(StringUtils.isBlank(filterDTO.getFieldName())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Refine field name"));
						
						return validation;
					}
					
					if(StringUtils.isBlank(filterDTO.getOperator())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Refine operator"));
						
						return validation;
					}
					
					if(StringUtils.isBlank(filterDTO.getValue())) {
						validation.isValid(false);
						validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
						validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Value"));
						
						return validation;
					}
				}
			}
		}
		
		// Check status
		if(StringUtils.isBlank(request.getEventStatus())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Event status"));
			
			return validation;
		}
		
		return validation;
	}
}
