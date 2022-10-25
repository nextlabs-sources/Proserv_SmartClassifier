package com.nextlabs.smartclassifier.validator.v1.user;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class UserCreationValidator {
	
	public static Validation validate(CreationRequest request) 
			throws Exception {
		Validation validation = new Validation();
		UserDTO userDTO = (UserDTO)request.getData();
		
		if(StringUtils.isBlank(userDTO.getType())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "User type"));
			
			return validation;
		}
		
		if(StringUtils.isBlank(userDTO.getUsername())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Username"));
			
			return validation;
		}
		
		if(StringUtils.isBlank(userDTO.getFirstName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "First name"));
			
			return validation;
		}
		
		if(StringUtils.isBlank(userDTO.getLastName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Last name"));
			
			return validation;
		}
		
		return validation;
	}
}
