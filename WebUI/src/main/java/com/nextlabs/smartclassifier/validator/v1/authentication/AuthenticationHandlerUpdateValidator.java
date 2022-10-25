package com.nextlabs.smartclassifier.validator.v1.authentication;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.AuthenticationHandlerDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class AuthenticationHandlerUpdateValidator {
	
	public static Validation validate(UpdateRequest request)
			throws Exception {
		Validation validation = new Validation();
		AuthenticationHandlerDTO authenticationHandlerDTO = (AuthenticationHandlerDTO)request.getData();
		
		// Target record id is missing
		if(authenticationHandlerDTO.getId() == null || authenticationHandlerDTO.getId() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "AuthenticationHandlerID"));
			
			return validation;
		}
		
		// Name should not be blank
		if(StringUtils.isBlank(authenticationHandlerDTO.getName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));
			
			return validation;
		}
		
		// Type should not be blank
		if(StringUtils.isBlank(authenticationHandlerDTO.getType())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Type"));
			
			return validation;
		}
		
		return validation;
	}
}
