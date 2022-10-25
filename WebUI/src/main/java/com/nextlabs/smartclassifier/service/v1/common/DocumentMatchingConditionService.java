package com.nextlabs.smartclassifier.service.v1.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.nextlabs.smartclassifier.constant.DocumentMatchingCondition;
import com.nextlabs.smartclassifier.dto.v1.CodeValueDTO;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class DocumentMatchingConditionService 
		extends Service {
	
	public DocumentMatchingConditionService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public DocumentMatchingConditionService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getDocumentMatchingConditions() {
		CollectionResponse response = new CollectionResponse();
		
		List<CodeValueDTO> codeValueDTOs = new ArrayList<CodeValueDTO>();
		
		for(DocumentMatchingCondition condition : DocumentMatchingCondition.values()) {
			codeValueDTOs.add(new CodeValueDTO(condition.getCode(), condition.getName()));
		}
		
		if(codeValueDTOs.size() > 0) {
			response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
			response.setMessage(MessageUtil.getMessage("success.data.loaded"));
		} else {
			response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
			response.setMessage(MessageUtil.getMessage("no.data.found"));
		}
		
		response.setTotalNoOfRecords(DocumentMatchingCondition.values().length);
		response.setData(codeValueDTOs);
		
		return response;
	}
}
