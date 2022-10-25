package com.nextlabs.smartclassifier.validator.v1.authentication;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.AuthenticationHandlerDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class AuthenticationHandlerCreationValidator {
	
	public static Validation validate(CreationRequest request)
			throws Exception {
		Validation validation = new Validation();
		AuthenticationHandlerDTO authenticationHandlerDTO = (AuthenticationHandlerDTO)request.getData();
		
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
