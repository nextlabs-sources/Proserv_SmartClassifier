package com.nextlabs.smartclassifier.validator.v1.extractor;

import org.apache.commons.lang.StringUtils;

import com.nextlabs.smartclassifier.dto.v1.DocumentSizeLimitDTO;
import com.nextlabs.smartclassifier.dto.v1.ExtractorDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;

public class ExtractorUpdateValidator {
	
	public static Validation validate(UpdateRequest request) 
			throws Exception {
		Validation validation = new Validation();
		ExtractorDTO extractorDTO = (ExtractorDTO)request.getData();
		
		if(extractorDTO.getId() == null || extractorDTO.getId() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "ExtractorID"));
			
			return validation;
		}
		
		if(StringUtils.isBlank(extractorDTO.getName())) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));
			
			return validation;
		}
		
		if(extractorDTO.getJMSProfile() == null 
				|| extractorDTO.getJMSProfile().getId() == null 
				||extractorDTO.getJMSProfile().getId() == 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "JMS profile"));
			
			return validation;
		}
		
		if(extractorDTO.getDocumentExtractorCount() == null || extractorDTO.getDocumentExtractorCount() <= 0) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Document extractor count", "1"));
			
			return validation;
		}
		
		if(extractorDTO.getMinimumHeapMemory() == null || extractorDTO.getMinimumHeapMemory() < 64) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Minimum heap memory", "64"));
			
			return validation;
		}
		
		if(extractorDTO.getConfigReloadInterval() == null || extractorDTO.getConfigReloadInterval() < 300) {
			validation.isValid(false);
			validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
			validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Configuration reload interval", "300"));
			
			return validation;
		}
		
		if(extractorDTO.getDocumentSizeLimits() != null) {
			for(DocumentSizeLimitDTO documentSizeLimitDTO : extractorDTO.getDocumentSizeLimits()) {
				if(documentSizeLimitDTO.getDocumentExtractor() == null || documentSizeLimitDTO.getDocumentExtractor().getId() == 0) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "DocumentExtractorId"));
					
					return validation;
				}
				
				if(documentSizeLimitDTO.getMaxFileSize() == null || documentSizeLimitDTO.getMaxFileSize() < 0) {
					validation.isValid(false);
					validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
					validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Document size limit", "0"));
					
					return validation;
				}
			}
		}
		
		return validation;
	}
}
