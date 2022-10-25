package com.nextlabs.smartclassifier.validator.v1.systemconfig;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.SystemConfigDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class SystemConfigUpdateValidator {
	
	public static Validation validate(UpdateRequest request) 
			throws Exception {
		Validation validation = new Validation();
		SystemConfigDTO systemConfigDTO = (SystemConfigDTO)request.getData();
		
		if(systemConfigDTO.getId() == null || systemConfigDTO.getId() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "SystemConfigID"));
			
			return validation;
		}
		
		if(StringUtils.isBlank(systemConfigDTO.getValue())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Value"));
			
			return validation;
		}
		
		return validation;
	}
	
	public static Validation validate(List<SystemConfigDTO> systemConfigDTOs)
			throws Exception {
		Validation validation = new Validation();
		
		if(systemConfigDTOs != null) {
			for(SystemConfigDTO systemConfigDTO : systemConfigDTOs) {
				if(systemConfigDTO.getId() == null || systemConfigDTO.getId() == 0) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "SystemConfigID"));
					
					return validation;
				}
				
				if(StringUtils.isBlank(systemConfigDTO.getValue())) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Value"));
					
					return validation;
				}
			}
		}
		
		return validation;
	}
}
