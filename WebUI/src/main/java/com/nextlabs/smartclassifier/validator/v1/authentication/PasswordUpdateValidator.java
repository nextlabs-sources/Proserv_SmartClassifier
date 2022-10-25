package com.nextlabs.smartclassifier.validator.v1.authentication;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class PasswordUpdateValidator {
	
	public static Validation validate(UpdateRequest request)
			throws Exception {
		Validation validation = new Validation();
		UserDTO userDTO = (UserDTO)request.getData();
		
		// Target record id is missing
		if(userDTO.getId() == null || userDTO.getId() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "UserID"));
			
			return validation;
		}
		
		// Check username
		if(StringUtils.isBlank(userDTO.getUsername())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Username"));
			
			return validation;
		}
		
		// Check current password
		if(StringUtils.isBlank(userDTO.getPassword())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Current password"));
			
			return validation;
		}
		
		// Check new password
		if(StringUtils.isBlank(userDTO.getNewPassword())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "New password"));
			
			return validation;
		}
		
		return validation;
	}
}
