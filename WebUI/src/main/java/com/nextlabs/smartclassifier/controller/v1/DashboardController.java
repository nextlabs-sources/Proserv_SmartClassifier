package com.nextlabs.smartclassifier.controller.v1;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nextlabs.smartclassifier.service.v1.dashboard.DashboardSummaryService;
import com.nextlabs.smartclassifier.util.MessageUtil;

@Path("/v1/dashboard")
public class DashboardController 
		extends Controller {
	
	private static final Logger logger = LogManager.getLogger(DashboardController.class);
	
	@POST
	@Path("/watcherSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public String watcherSummary() {
		try {
			return (new DashboardSummaryService(servletContext)).getWatcherSummary().toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/extractorSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public String extractorSummary() {
		try {
			return (new DashboardSummaryService(servletContext)).getExtractorSummary().toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/ruleEngineSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public String ruleEngineSummary() {
		try {
			return (new DashboardSummaryService(servletContext)).getRuleEngineSummary().toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/ruleSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public String ruleSummary() {
		try {
			return (new DashboardSummaryService(servletContext)).getRuleSummary().toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
	
	@POST
	@Path("/documentSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public String documentSummary() {
		try {
			return (new DashboardSummaryService(servletContext)).getDocumentSummary().toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
}
