package com.nextlabs.smartclassifier.validator.v1.ruleengine;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.RuleEngineDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class RuleEngineUpdateValidator {
	
	public static Validation validate(UpdateRequest request)
			throws Exception {
		Validation validation = new Validation();
		RuleEngineDTO ruleEngineDTO = (RuleEngineDTO)request.getData();
		
		if(ruleEngineDTO.getId() == null || ruleEngineDTO.getId() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "RuleEngineID"));
			
			return validation;
		}
		
		if(StringUtils.isBlank(ruleEngineDTO.getName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));
			
			return validation;
		}
		
		if(ruleEngineDTO.getOnDemandInterval() == null || ruleEngineDTO.getOnDemandInterval() <= 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "On demand task checking interval", "1"));
			
			return validation;
		}
		
		if(ruleEngineDTO.getScheduledInterval() == null || ruleEngineDTO.getScheduledInterval() <= 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Schedule task checking interval", "1"));
			
			return validation;
		}
		
		if(ruleEngineDTO.getOnDemandPoolSize() == null || ruleEngineDTO.getOnDemandPoolSize() <= 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "On demand pool size", "1"));
			
			return validation;
		}
		
		if(ruleEngineDTO.getScheduledPoolSize() == null || ruleEngineDTO.getScheduledPoolSize() <= 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Schedule pool size", "1"));
			
			return validation;
		}
		
		if(ruleEngineDTO.getConfigReloadInterval() == null || ruleEngineDTO.getConfigReloadInterval() < 300) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Configuration reload interval", "300"));
			
			return validation;
		}
		
		return validation;
	}
}
