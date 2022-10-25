package com.nextlabs.smartclassifier.controller.v1;

import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nextlabs.smartclassifier.dto.v1.SystemConfigDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.service.v1.systemconfig.SystemConfigRetrievalService;
import com.nextlabs.smartclassifier.service.v1.systemconfig.SystemConfigUpdateService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.systemconfig.SystemConfigUpdateValidator;

@Path("/v1/systemConfig")
public class SystemConfigController 
		extends Controller {
	
	private static final Logger logger = LogManager.getLogger(SystemConfigController.class);
	
	@POST
	@Path("/list")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String list(String requestBody) {
		try {
			return (new SystemConfigRetrievalService(servletContext)).getSystemConfigs(getRetrievalRequest(requestBody)).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@GET
	@Path("/details/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String details(@PathParam("id") String id) {
		try {
			return (new SystemConfigRetrievalService(servletContext)).getSystemConfig(toLong(id)).toJson();
		} catch(NumberFormatException err) {
			return error(MessageUtil.getMessage("invalid.input.field.pattern.code"), MessageUtil.getMessage("invalid.input.field.pattern", "SystemConfigID", "number"));
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@PUT
	@Path("/modify")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String modify(String requestBody) {
		try {
			if(StringUtils.isBlank(requestBody)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "System config entity"));
			}
			
			logger.debug("Request: " + requestBody);
			
			// Transform request into process able object 
			UpdateRequest updateRequest = getUpdateRequest(requestBody, SystemConfigDTO.class);
			
			Validation validation = SystemConfigUpdateValidator.validate(updateRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new SystemConfigUpdateService(servletContext)).updateSystemConfig(updateRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@PUT
	@Path("/modifyAll")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String modifyAll(String requestBody) {
		try {
			if(StringUtils.isBlank(requestBody)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "System config entities"));
			}
			
			logger.debug("Request: " + requestBody);
			
			// Transform request into process able object
			Type collectionType = new TypeToken<List<SystemConfigDTO>>(){}.getType();
			List<SystemConfigDTO> systemConfigDTOs = new Gson().fromJson(requestBody, collectionType);
			
			Validation validation = SystemConfigUpdateValidator.validate(systemConfigDTOs);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new SystemConfigUpdateService(servletContext)).updateSystemConfigs(systemConfigDTOs).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
}
