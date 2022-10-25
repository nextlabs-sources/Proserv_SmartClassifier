package com.nextlabs.smartclassifier.controller.v1;

import com.nextlabs.smartclassifier.dto.v1.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.request.DeletionRequest;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.service.v1.sourceauthentication.SourceAuthenticationCreationService;
import com.nextlabs.smartclassifier.service.v1.sourceauthentication.SourceAuthenticationDeletionService;
import com.nextlabs.smartclassifier.service.v1.sourceauthentication.SourceAuthenticationModificationService;
import com.nextlabs.smartclassifier.service.v1.sourceauthentication.SourceAuthenticationRetrievalService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.sourceauthentication.SourceAuthenticationCreationValidator;
import com.nextlabs.smartclassifier.validator.v1.sourceauthentication.SourceAuthenticationUpdateValidator;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/repocreds")
public class SourceAuthenticationController extends Controller {

    private static final Logger logger = LogManager.getLogger(SourceAuthenticationController.class);

    @POST
    @Path("/list")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public String list(String requestBody) {
        try {
            return (new SourceAuthenticationRetrievalService(servletContext))
                    .getSourceAuthentications(getRetrievalRequest(requestBody))
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
            return (new SourceAuthenticationRetrievalService(servletContext)
                    .getSourceAuthentication(toLong(id)))
                    .toJson();
        } catch (NumberFormatException e) {
            return error(
                    MessageUtil.getMessage("invalid.input.field.pattern.code"),
                    MessageUtil.getMessage(
                            "invalid.input.field.pattern", "SourceAuthenticationID", "number"));
            // TODO: handle exception
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    @POST
    @Path("/add")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public String add(String requestBody) {
        try {
            if (StringUtils.isBlank(requestBody)) {
                return error(
                        MessageUtil.getMessage("invalid.input.field.required.code"),
                        MessageUtil.getMessage("invalid.input.field.required", "Source authentication entity"));
            }

            logger.debug("Request: " + requestBody);

            CreationRequest creationRequest =
                    getCreationRequest(requestBody, SourceAuthenticationDTO.class);

            Validation validation = SourceAuthenticationCreationValidator.validate(creationRequest);
            if (!validation.isValid()) {
                return error(validation.getErrorCode(), validation.getErrorMessage());
            }

            return new SourceAuthenticationCreationService(servletContext)
                    .addSourceAuthentication(creationRequest)
                    .toJson();
        } catch (Exception err) {
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
            if (StringUtils.isBlank(requestBody)) {
                return error(
                        MessageUtil.getMessage("invalid.input.field.required.code"),
                        MessageUtil.getMessage("invalid.input.field.required", "Source authentication entity"));
            }

            logger.debug("Request: " + requestBody);

            UpdateRequest updateRequest = getUpdateRequest(requestBody, SourceAuthenticationDTO.class);
            Validation validation = SourceAuthenticationUpdateValidator.validate(updateRequest);
            if (!validation.isValid()) {
                return error(validation.getErrorCode(), validation.getErrorMessage());
            }

            return new SourceAuthenticationModificationService(servletContext)
                    .updateSourceAuthentication(updateRequest)
                    .toJson();
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
                        MessageUtil.getMessage("invalid.input.field.required", "SourceAuthenticationID"));
            }

            if (StringUtils.isBlank(lastModifiedDate)) {
                return error(
                        MessageUtil.getMessage("invalid.input.field.required.code"),
                        MessageUtil.getMessage("invalid.input.field.required", "Last modified date"));
            }

            SourceAuthenticationDTO data = new SourceAuthenticationDTO();
            data.setId(Long.parseLong(id));
            data.setModifiedOn(Long.parseLong(lastModifiedDate));

            DeletionRequest request = new DeletionRequest();
            request.setData(data);

            return new SourceAuthenticationDeletionService(servletContext)
                    .deleteSourceAuthentication(request)
                    .toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }
}
