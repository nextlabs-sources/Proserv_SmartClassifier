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

import com.nextlabs.smartclassifier.dto.v1.JMSProfileDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.request.DeletionRequest;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.service.v1.jmsprofile.JMSProfileCreationService;
import com.nextlabs.smartclassifier.service.v1.jmsprofile.JMSProfileDeletionService;
import com.nextlabs.smartclassifier.service.v1.jmsprofile.JMSProfileRetrievalService;
import com.nextlabs.smartclassifier.service.v1.jmsprofile.JMSProfileUpdateService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.jmsprofile.JMSProfileCreationValidator;
import com.nextlabs.smartclassifier.validator.v1.jmsprofile.JMSProfileUpdateValidator;

@Path("/v1/jmsProfile")
public class JMSController extends Controller {

  private static final Logger logger = LogManager.getLogger(JMSController.class);

  @POST
  @Path("/add")
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
  @Produces(MediaType.APPLICATION_JSON)
  public String add(String requestBody) {
    try {
      if (StringUtils.isBlank(requestBody)) {
        return error(
            MessageUtil.getMessage("invalid.input.field.required.code"),
            MessageUtil.getMessage("invalid.input.field.required", "JMS profile entity"));
      }

      logger.debug("Request: " + requestBody);

      CreationRequest creationRequest = getCreationRequest(requestBody, JMSProfileDTO.class);

      Validation validation = JMSProfileCreationValidator.validate(creationRequest);
      if (!validation.isValid()) {
        return error(validation.getErrorCode(), validation.getErrorMessage());
      }

      return (new JMSProfileCreationService(servletContext))
          .addJMSProfile(creationRequest)
          .toJson();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    }
  }

  @POST
  @Path("/list")
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
  @Produces(MediaType.APPLICATION_JSON)
  public String list(String requestBody) {
    try {
      return (new JMSProfileRetrievalService(servletContext))
          .getJMSProfiles(getRetrievalRequest(requestBody))
          .toJson();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    }
  }

  @GET
  @Path("/details/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public String details(@PathParam("id") String id) {
    try {
      return (new JMSProfileRetrievalService(servletContext)).getJMSProfile(toLong(id)).toJson();
    } catch (NumberFormatException err) {
      return error(
          MessageUtil.getMessage("invalid.input.field.pattern.code"),
          MessageUtil.getMessage("invalid.input.field.pattern", "JMSProfileID", "number"));
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    }
  }

  @PUT
  @Path("/modify")
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
  @Produces(MediaType.APPLICATION_JSON)
  public String modify(String requestBody) {
    try {
      if (StringUtils.isBlank(requestBody)) {
        return error(
            MessageUtil.getMessage("invalid.input.field.required.code"),
            MessageUtil.getMessage("invalid.input.field.required", "JMS profile entity"));
      }

      logger.debug("Request: " + requestBody);

      // Transform request into process able object
      UpdateRequest updateRequest = getUpdateRequest(requestBody, JMSProfileDTO.class);

      Validation validation = JMSProfileUpdateValidator.validate(updateRequest);
      if (!validation.isValid()) {
        return error(validation.getErrorCode(), validation.getErrorMessage());
      }

      return (new JMSProfileUpdateService(servletContext)).updateJMSProfile(updateRequest).toJson();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    }
  }

  @DELETE
  @Path("/remove/{id}/{lastModifiedDate}")
  @Produces(MediaType.APPLICATION_JSON)
  public String remove(
      @PathParam("id") String id, @PathParam("lastModifiedDate") String lastModifiedDate) {
    try {
      if (StringUtils.isBlank(id)) {
        return error(
            MessageUtil.getMessage("invalid.input.field.required.code"),
            MessageUtil.getMessage("invalid.input.field.required", "JMSProfileID"));
      }

      if (StringUtils.isBlank(lastModifiedDate)) {
        return error(
            MessageUtil.getMessage("invalid.input.field.required.code"),
            MessageUtil.getMessage("invalid.input.field.required", "Last modified date"));
      }

      JMSProfileDTO JMSProfileDTO = new JMSProfileDTO();
      JMSProfileDTO.setId(Long.parseLong(id));
      JMSProfileDTO.setModifiedOn(Long.parseLong(lastModifiedDate));

      DeletionRequest deletionRequest = new DeletionRequest();
      deletionRequest.setData(JMSProfileDTO);

      return (new JMSProfileDeletionService(servletContext))
          .deleteJMSProfile(deletionRequest)
          .toJson();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    }
  }
}
