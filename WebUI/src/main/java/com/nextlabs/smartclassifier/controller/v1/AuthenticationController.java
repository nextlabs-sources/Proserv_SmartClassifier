package com.nextlabs.smartclassifier.controller.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nextlabs.smartclassifier.dto.v1.UserDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.service.v1.authentication.AuthenticationService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.authentication.PasswordUpdateValidator;

@Path("/v1/authentication")
public class AuthenticationController 
		extends Controller {
	
	private static final Logger logger = LogManager.getLogger(AuthenticationController.class);
	
	@GET
	@Path("/details")
	@Produces(MediaType.APPLICATION_JSON)
	public String details() {
		try {
			return (new AuthenticationService(servletContext, httpServletRequest, httpServletResponse)).details().toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@PUT
	@Path("/changePassword")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String changePassword(String requestBody) {
		try {
			if(StringUtils.isBlank(requestBody)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "User entity"));
			}
			
			logger.debug("Request: " + requestBody);
			
			// Transform request into process able object 
			UpdateRequest updateRequest = getUpdateRequest(requestBody, UserDTO.class);
			
			Validation validation = PasswordUpdateValidator.validate(updateRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new AuthenticationService(servletContext)).updatePassword(updateRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	public String logout(String requestBody) {
		try {
			return (new AuthenticationService(servletContext, httpServletRequest, httpServletResponse)).logout().toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
}
