package com.nextlabs.smartclassifier.controller.v1;

import java.net.URLDecoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nextlabs.smartclassifier.dto.v1.request.ReportQueryRequest;
import com.nextlabs.smartclassifier.service.v1.report.ReportQueryService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import com.nextlabs.smartclassifier.validator.v1.reportquery.ReportQueryValidator;

@Path("/v1/reportQuery")
public class ReportQueryController 
		extends Controller {
	
	private static final Logger logger = LogManager.getLogger(ReportQueryController.class);
	
	@POST
	@Path("/search")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String search(String requestBody) {
		try {
			logger.debug("Request: " + requestBody);
			ReportQueryRequest queryRequest = getReportQueryRequest(requestBody);
			
			Validation validation = ReportQueryValidator.validate(queryRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new ReportQueryService(servletContext)).search(queryRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/loadDocument")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String loadDocument(String requestBody) {
		try {
			logger.debug("Request: " + requestBody);
			ReportQueryRequest queryRequest = getReportQueryRequest(requestBody);
			
			Validation validation = ReportQueryValidator.validate(queryRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new ReportQueryService(servletContext)).loadDocument(queryRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/loadDocumentEvent")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String loadDocumentEvent(String requestBody) {
		try {
			logger.debug("Request: " + requestBody);
			ReportQueryRequest queryRequest = getReportQueryRequest(requestBody);
			
			Validation validation = ReportQueryValidator.validate(queryRequest);
			if(!validation.isValid()) {
				return error(validation.getErrorCode(), validation.getErrorMessage());
			}
			
			return (new ReportQueryService(servletContext)).loadDocumentEvent(queryRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/export")
	public void export(String requestBody) {
		try {
			if(StringUtils.isNotBlank(requestBody)) {
				requestBody = URLDecoder.decode(requestBody, "UTF-8");
				if(!requestBody.startsWith("{") && requestBody.indexOf("{") > 0) {
					requestBody = requestBody.substring(requestBody.indexOf("{"));
				} else {
					logger.error("Request: " + requestBody);
					logger.error("Invalid request parameter!");
				}
			} else {
				requestBody = httpServletRequest.getParameter("exportQuery");
			}
			
			ReportQueryRequest queryRequest = getReportQueryRequest(requestBody);
			
			Validation validation = ReportQueryValidator.validate(queryRequest);
			if(!validation.isValid()) {
				logger.error(validation.getErrorMessage());
				return;
			}
			
			(new ReportQueryService(servletContext, httpServletRequest, httpServletResponse)).export(queryRequest);
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
	}
}
