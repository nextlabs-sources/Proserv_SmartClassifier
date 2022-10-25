package com.nextlabs.smartclassifier.controller.v1;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nextlabs.smartclassifier.dto.v1.request.PurgeRequest;
import com.nextlabs.smartclassifier.service.v1.eventlog.EventLogPurgeService;
import com.nextlabs.smartclassifier.util.MessageUtil;

@Path("/v1/eventLog")
public class EventLogController 
		extends Controller {
	
	private static final Logger logger = LogManager.getLogger(EventLogController.class);
	
	@DELETE
	@Path("/purge/{beforeDate}")
	@Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String purge(@PathParam("beforeDate") String beforeDate) {
		try {
			if(StringUtils.isBlank(beforeDate)) {
				return error(MessageUtil.getMessage("invalid.input.field.required.code"), MessageUtil.getMessage("invalid.input.field.required", "Purge till date before"));
			}
			
			PurgeRequest purgeRequest = new PurgeRequest();
			purgeRequest.setDateInMillis(Long.parseLong(beforeDate));
			
			return (new EventLogPurgeService(servletContext)).purgeEventLog(purgeRequest).toJson();
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
			return error(MessageUtil.getMessage("server.error.code"), MessageUtil.getMessage("server.error", err.getMessage()));
		}
	}
}
