package com.nextlabs.smartclassifier.validator.v1.user;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class UserImportValidator {
	
	public static Validation validate(List<UserDTO> userList) {
		Validation validation = new Validation();
		
		for(UserDTO userDTO : userList) {
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
			
			if(userDTO.getAuthenticationHandlerId() == null || userDTO.getAuthenticationHandlerId() == 0) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Authentication handler"));
				
				return validation;
			}
			
			if(StringUtils.isBlank(userDTO.getFirstName()) && StringUtils.isBlank(userDTO.getLastName())) {
				validation.isValid(false);
				validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.displayname.code"));
				validation.setErrorMessage(MessageUtil.getMessage("invalid.input.empty.displayname", userDTO.getUsername()));
				
				return validation;
			}
		}
		
		return validation;
	}
}
