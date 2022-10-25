package com.nextlabs.smartclassifier.validator.v1.actionplugin;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.ActionPluginDTO;
import com.nextlabs.smartclassifier.dto.v1.ActionPluginParamDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class ActionPluginUpdateValidator {
	
	public static Validation validate(UpdateRequest request)
			throws Exception {
		Validation validation = new Validation();
		ActionPluginDTO actionPluginDTO = (ActionPluginDTO)request.getData();
		
		// Target record id is missing
		if(actionPluginDTO.getId() == null || actionPluginDTO.getId() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "ActionPluginID"));
			
			return validation;
		}
		
		// Fixed plugin parameter
		if(actionPluginDTO.getParams() == null || actionPluginDTO.getParams().size() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.empty.collection.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.empty.collection", "Action plugin parameter"));
			
			return validation;
		} else {
			for(ActionPluginParamDTO actionPluginParamDTO : actionPluginDTO.getParams()) {
				if(actionPluginParamDTO.getId() == null || actionPluginParamDTO.getId() == 0) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "ActionPluginParamID"));
					
					return validation;
				}
				
				if(StringUtils.isBlank(actionPluginParamDTO.getValue())) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Parameter value"));
					
					return validation;
				}
			}
		}
		
		return validation;
	}
}
