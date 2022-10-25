package com.nextlabs.smartclassifier.controller.v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Part;
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
import com.nextlabs.smartclassifier.dto.v1.CodeValueDTO;
import com.nextlabs.smartclassifier.dto.v1.RuleDTO;
import com.nextlabs.smartclassifier.dto.v1.request.CreationRequest;
import com.nextlabs.smartclassifier.dto.v1.request.DeletionRequest;
import com.nextlabs.smartclassifier.dto.v1.request.ExecutionRequest;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.dto.v1.response.CollectionResponse;
import com.nextlabs.smartclassifier.service.v1.rule.RuleCreationService;
import com.nextlabs.smartclassifier.service.v1.rule.RuleDeletionService;
import com.nextlabs.smartclassifier.service.v1.rule.RuleExecutionService;
import com.nextlabs.smartclassifier.service.v1.rule.RuleExportService;
import com.nextlabs.smartclassifier.service.v1.rule.RuleImportService;
import com.nextlabs.smartclassifier.service.v1.rule.RuleRetrievalService;
import com.nextlabs.smartclassifier.service.v1.rule.RuleUpdateService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.rule.RuleCreationValidator;
import com.nextlabs.smartclassifier.validator.v1.rule.RuleUpdateValidator;

@Path("/v1/rule")
public class RuleController extends Controller {

  private static final Logger logger = LogManager.getLogger(RuleController.class);

  @GET
  @Path("/sortFields")
  @Produces(MediaType.APPLICATION_JSON)
  public String sortFields() {
    try {
      CollectionResponse response = new CollectionResponse();

      List<CodeValueDTO> codeValueDTOs = new ArrayList<CodeValueDTO>();

      codeValueDTOs.add(new CodeValueDTO("modifiedOn", "Last Updated"));
      codeValueDTOs.add(new CodeValueDTO("createdOn", "Created Date"));
      codeValueDTOs.add(new CodeValueDTO("name", "Name"));
      codeValueDTOs.add(new CodeValueDTO("enabled", "Status"));

      response.setStatusCode(MessageUtil.getMessage("success.data.loaded.code"));
      response.setMessage(MessageUtil.getMessage("success.data.loaded"));
      response.setTotalNoOfRecords(codeValueDTOs.size());
      response.setData(codeValueDTOs);

      return response.toJson();
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
            MessageUtil.getMessage("invalid.input.field.required", "Rule entity"));
      }

      logger.debug("Request: " + requestBody);

      CreationRequest creationRequest = getCreationRequest(requestBody, RuleDTO.class);

      logger.debug("Creation Request = " + creationRequest.getData());

      Validation validation = RuleCreationValidator.validate(creationRequest);
      if (!validation.isValid()) {
        logger.debug("Validation is not valid");
        return error(validation.getErrorCode(), validation.getErrorMessage());
      } else {
        logger.debug("Validation is valid");
      }

      return (new RuleCreationService(servletContext)).addRule(creationRequest).toJson();
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
      return (new RuleRetrievalService(servletContext))
          .getRules(getRetrievalRequest(requestBody))
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
      return (new RuleRetrievalService(servletContext)).getRule(toLong(id)).toJson();
    } catch (NumberFormatException err) {
      return error(
          MessageUtil.getMessage("invalid.input.field.pattern.code"),
          MessageUtil.getMessage("invalid.input.field.pattern", "RuleID", "number"));
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
            MessageUtil.getMessage("invalid.input.field.required", "Rule entity"));
      }

      logger.debug("Request: " + requestBody);

      // Transform request into process able object
      UpdateRequest updateRequest = getUpdateRequest(requestBody, RuleDTO.class);

      Validation validation = RuleUpdateValidator.validate(updateRequest);
      if (!validation.isValid()) {
        return error(validation.getErrorCode(), validation.getErrorMessage());
      }

      return (new RuleUpdateService(servletContext)).updateRule(updateRequest).toJson();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    }
  }

  @DELETE
  @Path("/remove/{id}/{lastModifiedDate}")
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
  @Produces(MediaType.APPLICATION_JSON)
  public String remove(
      @PathParam("id") String id, @PathParam("lastModifiedDate") String lastModifiedDate) {
    try {
      if (StringUtils.isBlank(id)) {
        return error(
            MessageUtil.getMessage("invalid.input.field.required.code"),
            MessageUtil.getMessage("invalid.input.field.required", "RuleID"));
      }

      if (StringUtils.isBlank(lastModifiedDate)) {
        return error(
            MessageUtil.getMessage("invalid.input.field.required.code"),
            MessageUtil.getMessage("invalid.input.field.required", "Last modified date"));
      }

      RuleDTO ruleDTO = new RuleDTO();
      ruleDTO.setId(Long.parseLong(id));
      ruleDTO.setModifiedOn(Long.parseLong(lastModifiedDate));

      DeletionRequest deletionRequest = new DeletionRequest();
      deletionRequest.setData(ruleDTO);

      return (new RuleDeletionService(servletContext)).deleteRule(deletionRequest).toJson();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    }
  }

  @POST
  @Path("/execute/{id}")
  @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
  @Produces(MediaType.APPLICATION_JSON)
  public String execute(@PathParam("id") String id) {
    try {
      if (StringUtils.isBlank(id)) {
        return error(
            MessageUtil.getMessage("invalid.input.field.required.code"),
            MessageUtil.getMessage("invalid.input.field.required", "RuleID"));
      }

      RuleDTO ruleDTO = new RuleDTO();
      ruleDTO.setId(Long.parseLong(id));

      ExecutionRequest executionRequest = new ExecutionRequest();
      executionRequest.setData(ruleDTO);

      return (new RuleExecutionService(servletContext)).executeRule(executionRequest).toJson();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    }
  }

  @POST
  @Path("/import")
  @Produces(MediaType.APPLICATION_JSON)
  public String importRule() {
    BufferedReader reader = null;
    StringBuilder fileContent = new StringBuilder();

    try {
      Part filePart = httpServletRequest.getPart("file");

      if (filePart != null) {
        reader =
            new BufferedReader(
                new InputStreamReader(filePart.getInputStream(), Charset.forName("UTF-16")));
        String currentLine;

        while ((currentLine = reader.readLine()) != null) {
          fileContent.append(currentLine);
        }

        logger.info("Import file content: " + fileContent.toString());
      }

      List<RuleDTO> rules =
          gson.fromJson(fileContent.toString(), new TypeToken<List<RuleDTO>>() {}.getType());

      return (new RuleImportService(servletContext)).importRule(rules).toJson();
    } catch (Exception err) {
      logger.error(err.getMessage(), err);
      return error(
          MessageUtil.getMessage("server.error.code"),
          MessageUtil.getMessage("server.error", err.getMessage()));
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ioErr) {
          // Ignore
        }
      }
    }
  }

	@POST
	@Path("/export")
	public void exportRule(String requestBody) {
		try {
    		(new RuleExportService(servletContext, httpServletRequest, httpServletResponse)).export();
		} catch (Exception err) {
			logger.error(err.getMessage(), err);
		}
	}
}
