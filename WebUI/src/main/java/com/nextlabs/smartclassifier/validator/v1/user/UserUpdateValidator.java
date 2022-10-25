package com.nextlabs.smartclassifier.validator.v1.user;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class UserUpdateValidator {
	
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
		
		// Username should not be blank
		if(StringUtils.isBlank(userDTO.getUsername())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Username"));
			
			return validation;
		}
		
		// First name should not be blank
		if(StringUtils.isBlank(userDTO.getFirstName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "First name"));
			
			return validation;
		}
		
		// Last name should not be blank
		if(StringUtils.isBlank(userDTO.getLastName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Last name"));
			
			return validation;
		}
		
		return validation;
	}
}
