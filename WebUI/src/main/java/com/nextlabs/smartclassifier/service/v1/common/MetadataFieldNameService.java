package com.nextlabs.smartclassifier.service.v1.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.dto.v1.MetadataFieldNameDTO;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.dto.v1.response.ServiceResponse;
import com.nextlabs.smartclassifier.service.v1.Service;
import com.nextlabs.smartclassifier.util.MessageUtil;

public class MetadataFieldNameService 
		extends Service {
	
	public MetadataFieldNameService(ServletContext servletContext) {
		super(servletContext);
	}
	
	public MetadataFieldNameService(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
		super(servletContext, servletRequest, servletResponse);
	}
	
	public ServiceResponse getMetadataFieldNames() {
		CollectionResponse response = new CollectionResponse();
		
		List<MetadataFieldNameDTO> fieldNameDTOs = new ArrayList<MetadataFieldNameDTO>();
		Map<String, MetadataField> metadataFields = getMetadataFields();
		
		if(metadataFields != null) {
			for(Map.Entry<String, MetadataField> entry : metadataFields.entrySet()) {
				if(entry.getValue().isVisible()) {
					fieldNameDTOs.add(new MetadataFieldNameDTO(entry.getValue().getFieldName(), entry.getValue().getDisplayName(), entry.getValue().getUIDataType(), entry.getValue().getStorageDataType()));
				}
			}
		}
		
		if(fieldNameDTOs.size() > 0) {
			response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
			response.setMessage(MessageUtil.getMessage("success.data.loaded"));
		} else {
			response.setStatusCode(MessageUtil.getMessage("no.data.found.code"));
			response.setMessage(MessageUtil.getMessage("no.data.found"));
		}
		
		response.setTotalNoOfRecords(fieldNameDTOs.size());
		response.setData(fieldNameDTOs);
		
		return response;
	}
}
