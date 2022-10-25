package com.nextlabs.smartclassifier.controller.v1;

import java.util.ArrayList;
import java.util.List;

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

import com.google.gson.reflect.TypeToken;
import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.request.DeletionRequest;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.service.v1.user.UserCreationService;
import com.nextlabs.smartclassifier.service.v1.user.UserDeletionService;
import com.nextlabs.smartclassifier.service.v1.user.UserImportService;
import com.nextlabs.smartclassifier.service.v1.user.UserRetrievalService;
import com.nextlabs.smartclassifier.service.v1.user.UserUpdateService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.user.UserCreationValidator;
import com.nextlabs.smartclassifier.validator.v1.user.UserImportValidator;
import com.nextlabs.smartclassifier.validator.v1.user.UserUpdateValidator;

@Path("/v1/user")
public class UserController 
		extends Controller {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);
	
	@POST
	@Path("/add")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String add(String requestBody) {
		try {
			if(StringUtils.isBlank(requestBody)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "User entity"));
			}
			
			logger.debug("Request: " + requestBody);
			
			CreationRequest creationRequest = getCreationRequest(requestBody, UserDTO.class);
			
			Validation validation = UserCreationValidator.validate(creationRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new UserCreationService(servletContext)).addUser(creationRequest).toJson();
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
			return (new UserRetrievalService(servletContext)).getUsers(getRetrievalRequest(requestBody)).toJson();
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
			return (new UserRetrievalService(servletContext)).getUser(toLong(id)).toJson();
		} catch(NumberFormatException err) {
			return error(MessageUtil.getMessage("invalid.input.field.pattern.code"), MessageUtil.getMessage("invalid.input.field.pattern", "UserID", "number"));
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
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "User entity"));
			}
			
			logger.debug("Request: " + requestBody);
			
			// Transform request into process able object 
			UpdateRequest updateRequest = getUpdateRequest(requestBody, UserDTO.class);
			
			Validation validation = UserUpdateValidator.validate(updateRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new UserUpdateService(servletContext)).updateUser(updateRequest).toJson();
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
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "UserID"));
			}
			
			if(StringUtils.isBlank(lastModifiedDate)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "Last modified date"));
			}
			
			UserDTO userDTO = new UserDTO();
			userDTO.setId(Long.parseLong(id));
			userDTO.setModifiedOn(Long.parseLong(lastModifiedDate));
			
			DeletionRequest deletionRequest = new DeletionRequest();
			deletionRequest.setData(userDTO);
			
			return (new UserDeletionService(servletContext)).deleteUser(deletionRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/import")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String importUser(String requestBody) {
		try {
			if(StringUtils.isBlank(requestBody)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "User entity"));
			}
			
			logger.debug("Request: " + requestBody);
			
			List<UserDTO> userList = gson.fromJson(requestBody, new TypeToken<ArrayList<UserDTO>>(){}.getType());
			
			Validation validation = UserImportValidator.validate(userList);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new UserImportService(servletContext)).importUser(userList).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
}
