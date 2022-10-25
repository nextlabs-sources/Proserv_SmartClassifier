package com.nextlabs.smartclassifier.validator.v1.jmsprofile;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.JMSProfileDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class JMSProfileCreationValidator {
	
	public static Validation validate(CreationRequest request) 
			throws Exception {
		Validation validation = new Validation();
		JMSProfileDTO jmsProfileDTO = (JMSProfileDTO)request.getData();
		
		// Name should not be blank
		if(StringUtils.isBlank(jmsProfileDTO.getDisplayName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));
			
			return validation;
		}
		
		// Type should not be blank
		if(StringUtils.isBlank(jmsProfileDTO.getType())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Type"));
			
			return validation;
		}
		
		// Provider URL should not be blank
		if(StringUtils.isBlank(jmsProfileDTO.getProviderURL())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Provider URL"));
			
			return validation;
		}
		
		// Initial context factory should not be blank
		if(StringUtils.isBlank(jmsProfileDTO.getInitialContextFactory())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Initial context factory"));
			
			return validation;
		}
		
		// Service name should not be blank
		if(StringUtils.isBlank(jmsProfileDTO.getServiceName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Service name"));
			
			return validation;
		}
		
		// Connection retry interval cannot be lesser or equal to 5
		if(jmsProfileDTO.getConnectionRetryInterval() == null || jmsProfileDTO.getConnectionRetryInterval() < 5) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Connection retry interval", "5"));
			
			return validation;
		}
		
		return validation;
	}
}
