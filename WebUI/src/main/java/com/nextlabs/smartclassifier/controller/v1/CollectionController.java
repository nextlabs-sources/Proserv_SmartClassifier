package com.nextlabs.smartclassifier.controller.v1;

import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.service.v1.common.ActionPluginService;
import com.nextlabs.smartclassifier.service.v1.common.AuthenticationHandlerService;
import com.nextlabs.smartclassifier.service.v1.common.DataProviderService;
import com.nextlabs.smartclassifier.service.v1.common.DocumentMatchingConditionService;
import com.nextlabs.smartclassifier.service.v1.common.DocumentTypeService;
import com.nextlabs.smartclassifier.service.v1.common.ExecutionWindowSetService;
import com.nextlabs.smartclassifier.service.v1.common.JMSProfileService;
import com.nextlabs.smartclassifier.service.v1.common.JMSTypeService;
import com.nextlabs.smartclassifier.service.v1.common.MatchingConditionService;
import com.nextlabs.smartclassifier.service.v1.common.MetadataFieldNameService;
import com.nextlabs.smartclassifier.service.v1.common.MetadataMatchingConditionService;
import com.nextlabs.smartclassifier.service.v1.common.RepositoryTypeService;
import com.nextlabs.smartclassifier.service.v1.common.RuleEngineService;
import com.nextlabs.smartclassifier.service.v1.common.RuleService;
import com.nextlabs.smartclassifier.service.v1.common.SourceAuthenticationService;
import com.nextlabs.smartclassifier.util.MessageUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1/collection")
public class CollectionController extends Controller {

    private static final Logger logger = LogManager.getLogger(CollectionController.class);

    @GET
    @Path("/list/{name}")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@PathParam("name") String name) {
        try {
            if ("DocumentType".equalsIgnoreCase(name)) {
                return getDocumentTypes();
            } else if ("JMSProfile".equalsIgnoreCase(name)) {
                return getJMSProfiles();
            } else if ("JMSType".equalsIgnoreCase(name)) {
                return getJMSType();
            } else if ("RepositoryType".equalsIgnoreCase(name)) {
                return getRepositoryTypes();
            } else if ("DataProvider".equalsIgnoreCase(name)) {
                return getDataProviders();
            } else if ("Rule".equalsIgnoreCase(name)) {
                return getRules();
            } else if ("ActionPlugin".equalsIgnoreCase(name)) {
                return getActionPlugins();
            } else if ("ActionName".equalsIgnoreCase(name)) {
                return getDistinctActionNames();
            } else if ("SharePointPlugin".equalsIgnoreCase(name)) {
                return getSharePointPlugins();
            } else if ("SharedFolderPlugin".equalsIgnoreCase(name)) {
                return getSharedFolderPlugins();
            } else if ("ExecutionWindowSet".equalsIgnoreCase(name)) {
                return getExecutionWindowSets();
            } else if ("RuleEngine".equalsIgnoreCase(name)) {
                return getRuleEngines();
            } else if ("MatchingCondition".equalsIgnoreCase(name)) {
                return getMatchingConditions();
            } else if ("DocumentMatchingCondition".equalsIgnoreCase(name)) {
                return getDocumentMatchingConditions();
            } else if ("MetadataMatchingCondition".equalsIgnoreCase(name)) {
                return getMetadataMatchingConditions();
            } else if ("MetadataFieldName".equalsIgnoreCase(name)) {
                return getMetadataFieldNames();
            } else if ("AuthenticationHandler".equalsIgnoreCase(name)) {
                return getAuthenticationHandlers();
            } else if ("RepositoryCredentials".equalsIgnoreCase(name)) {
                return getSourceAuthentications();
            } else {
                return error(
                        MessageUtil.getMessage("invalid.input.list.unrecognizable.code"),
                        MessageUtil.getMessage("invalid.input.list.unrecognizable", name));
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getDocumentTypes() {
        try {
            return (new DocumentTypeService(servletContext)).getDocumentTypes().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getJMSProfiles() {
        try {
            return (new JMSProfileService(servletContext)).getJMSProfiles().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getJMSType() {
        try {
            return (new JMSTypeService(servletContext)).getJMSTypes().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getRepositoryTypes() {
        try {
            return (new RepositoryTypeService(servletContext)).getRepositoryTypes().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getDataProviders() {
        try {
            return (new DataProviderService(servletContext)).getDataProviders().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getRules() {
        try {
            return (new RuleService(servletContext)).getRules().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getActionPlugins() {
        try {
            return (new ActionPluginService(servletContext)).getActionPlugins().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getDistinctActionNames() {
        try {
            return (new ActionPluginService(servletContext)).getDistinctActionNames().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getExecutionWindowSets() {
        try {
            return (new ExecutionWindowSetService(servletContext)).getExecutionWindowSets().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getRuleEngines() {
        try {
            return (new RuleEngineService(servletContext)).getRuleEngines().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getMatchingConditions() {
        try {
            return (new MatchingConditionService(servletContext)).getMatchingConditions().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getDocumentMatchingConditions() {
        try {
            return (new DocumentMatchingConditionService(servletContext))
                    .getDocumentMatchingConditions()
                    .toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getMetadataMatchingConditions() {
        try {
            return (new MetadataMatchingConditionService(servletContext))
                    .getMetadataMatchingConditions()
                    .toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getMetadataFieldNames() {
        try {
            return (new MetadataFieldNameService(servletContext)).getMetadataFieldNames().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getAuthenticationHandlers() {
        try {
            return (new AuthenticationHandlerService(servletContext))
                    .getAuthenticationHandlers()
                    .toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getSharePointPlugins() {
        try {
            return (new ActionPluginService(servletContext))
                    .getActionPlugins(RepositoryType.SHAREPOINT)
                    .toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getSharedFolderPlugins() {
        try {
            return (new ActionPluginService(servletContext))
                    .getActionPlugins(RepositoryType.SHARED_FOLDER)
                    .toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }

    private String getSourceAuthentications() {
        try {
            return (new SourceAuthenticationService(servletContext)).getSourceAuthentications().toJson();
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            return error(
                    MessageUtil.getMessage("server.error.code"),
                    MessageUtil.getMessage("server.error", err.getMessage()));
        }
    }
}
