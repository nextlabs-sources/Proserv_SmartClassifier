package com.nextlabs.smartclassifier.controller.v1;

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

import com.nextlabs.smartclassifier.dto.v1.ExtractorDTO;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.service.v1.extractor.ExtractorRetrievalService;
import com.nextlabs.smartclassifier.service.v1.extractor.ExtractorUpdateService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.extractor.ExtractorUpdateValidator;

@Path("/v1/extractor")
public class ExtractorController 
		extends Controller {
	
	private static final Logger logger = LogManager.getLogger(ExtractorController.class);
	
	@POST
	@Path("/list")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String list(String requestBody) {
		try {
			return (new ExtractorRetrievalService(servletContext)).getExtractors(getRetrievalRequest(requestBody)).toJson();
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
			return (new ExtractorRetrievalService(servletContext)).getExtractor(toLong(id)).toJson();
		} catch(NumberFormatException err) {
			return error(MessageUtil.getMessage("invalid.input.field.pattern.code"), MessageUtil.getMessage("invalid.input.field.pattern", "ExtractorID", "number"));
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
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "Extractor entity"));
			}

			logger.debug("Request: " + requestBody);
			
			// Transform request into process able object 
			UpdateRequest updateRequest = getUpdateRequest(requestBody, ExtractorDTO.class);

			Validation validation = ExtractorUpdateValidator.validate(updateRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new ExtractorUpdateService(servletContext)).updateExtractor(updateRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
}
