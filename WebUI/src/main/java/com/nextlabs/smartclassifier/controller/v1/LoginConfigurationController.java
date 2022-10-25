package com.nextlabs.smartclassifier.controller.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.nextlabs.smartclassifier.dto.v1.AuthenticationHandlerDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.request.DeletionRequest;
import com.nextlabs.smartclassifier.dto.v1.request.ExecutionRequest;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.service.v1.authentication.LDAPService;
import com.nextlabs.smartclassifier.service.v1.authentication.AuthenticationHandlerCreationService;
import com.nextlabs.smartclassifier.service.v1.authentication.AuthenticationHandlerDeletionService;
import com.nextlabs.smartclassifier.service.v1.authentication.AuthenticationHandlerRetrievalService;
import com.nextlabs.smartclassifier.service.v1.authentication.AuthenticationHandlerUpdateService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.authentication.AuthenticationHandlerCreationValidator;
import com.nextlabs.smartclassifier.validator.v1.authentication.AuthenticationHandlerUpdateValidator;

@Path("/v1/loginConfiguration")
public class LoginConfigurationController 
		extends Controller {
	
	private static final Logger logger = LogManager.getLogger(LoginConfigurationController.class);
	
	@POST
	@Path("/add")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String add(String requestBody) {
		try {
			if(StringUtils.isBlank(requestBody)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "Login configuration entity"));
			}
			
			logger.debug("Request: " + requestBody);
			
			CreationRequest creationRequest = getCreationRequest(requestBody, AuthenticationHandlerDTO.class);
			
			Validation validation = AuthenticationHandlerCreationValidator.validate(creationRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new AuthenticationHandlerCreationService(servletContext)).addAuthenticationHandler(creationRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/list")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String list(String requestBody) {
		try {
			return (new AuthenticationHandlerRetrievalService(servletContext)).getAuthenticationHandlers(getRetrievalRequest(requestBody)).toJson();
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
			return (new AuthenticationHandlerRetrievalService(servletContext)).getAuthenticationHandler(toLong(id)).toJson();
		} catch(NumberFormatException err) {
			return error(MessageUtil.getMessage("invalid.input.field.pattern.code"), MessageUtil.getMessage("invalid.input.field.pattern", "LoginConfigurationID", "number"));
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
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "Login configuration entity"));
			}
			
			logger.debug("Request: " + requestBody);
			
			// Transform request into process able object 
			UpdateRequest updateRequest = getUpdateRequest(requestBody, AuthenticationHandlerDTO.class);
			
			Validation validation = AuthenticationHandlerUpdateValidator.validate(updateRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new AuthenticationHandlerUpdateService(servletContext)).updateAuthenticationHandler(updateRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@DELETE
	@Path("/remove/{id}/{lastModifiedDate}")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String remove(@PathParam("id") String id, @PathParam("lastModifiedDate") String lastModifiedDate) {
		try {
			if(StringUtils.isBlank(id)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "LoginConfigurationID"));
			}
			
			if(StringUtils.isBlank(lastModifiedDate)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "Last modified date"));
			}
			
			AuthenticationHandlerDTO authenticationHandlerDTO = new AuthenticationHandlerDTO();
			authenticationHandlerDTO.setId(Long.parseLong(id));
			authenticationHandlerDTO.setModifiedOn(Long.parseLong(lastModifiedDate));
			
			DeletionRequest deletionRequest = new DeletionRequest();
			deletionRequest.setData(authenticationHandlerDTO);
			
			return (new AuthenticationHandlerDeletionService(servletContext)).deleteAuthenticationHandler(deletionRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/connect")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String connect(String requestBody) {
		try {
			if(StringUtils.isBlank(requestBody)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "Login configuration info"));
			}
			
			logger.debug("Request: " + requestBody);
			
			ExecutionRequest executionRequest = getExecutionRequest(requestBody, AuthenticationHandlerDTO.class);
			
			return (new LDAPService(servletContext)).connect(executionRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/listUsers")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String listUsers(String requestBody) {
		try {
			ExecutionRequest executionRequest = getExecutionRequest(requestBody, AuthenticationHandlerDTO.class);
			return (new LDAPService(servletContext)).getUsers(executionRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
}
