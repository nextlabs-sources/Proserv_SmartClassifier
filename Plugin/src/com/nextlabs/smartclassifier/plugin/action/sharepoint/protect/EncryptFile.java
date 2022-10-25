package com.nextlabs.smartclassifier.plugin.action.sharepoint.protect;

import com.nextlabs.common.shared.JsonProject;
import com.nextlabs.common.shared.Constants.TokenGroupType;
import com.nextlabs.nxl.RightsManager;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.RollbackErrorType;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.exception.RollbackException;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharePointAction;
import com.nextlabs.smartclassifier.solr.SolrDocumentInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pkalra on 11/21/2016.
 */
public class EncryptFile extends SharePointAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(EncryptFile.class);
    private static final String ACTION_NAME = "ENCRYPT_FILE (SHAREPOINT)";
    private String encFileSRURL;
    private String encFileSiteURL;

    public EncryptFile() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument solrDocument) throws Exception {

        ActionOutcome actionOutcome = new ActionOutcome();
        actionOutcome.setResult(ActionResult.FAIL);
        actionOutcome.setMessage(ACTION_NAME + ": Operation started.");

        initializeFields();
        docInfo = new SolrDocumentInfo(solrDocument);
        docInfo.printDocumentInfo();

        logger.info("Received file " + docInfo.getDocumentPath() + " for encrypt action");

        initHTTPClient(docInfo.getRepoPath());

        RightsManager rm = setupSkyDRMSDK();
        if (rm == null) {
            logger.error(ACTION_NAME + ": Cannot initialize the Right Manager. Action cannot be performed");
            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Cannot initialize the Rights Manager");
            return actionOutcome;
        }

        Map<String, Set<String>> tagsByName = getTagsByName();
        Map<String, List<String>> tags = new HashMap<>();
        if (tagsByName != null) {
            for (Map.Entry<String, Set<String>> entry : tagsByName.entrySet()) {
                List<String> values = new ArrayList<>();
                values.addAll(entry.getValue());
                tags.put(entry.getKey(), values);
            }
        }

        File inputFile = new File(docInfo.getDocumentPath());
        

        try {
            if (rm.isNXL(inputFile.toString())) {
                logger.error(ACTION_NAME + ": The input file is already a NXL file. Action cannot be performed");
                actionOutcome.setResult(ActionResult.SUCCESS);
                actionOutcome.setMessage("File is already encrypted and will be skipped");
                return actionOutcome;
            }
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Failed to check if the input file is a NXL file: " + e.getMessage(), e);
            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Failed to check if the input file is a NXL file");
            return actionOutcome;
        }

        try {
            String outputFileName = docInfo.getDocumentName().concat(".nxl");
            String outputPath = new File(docInfo.getDirectory(), outputFileName).getAbsolutePath();
            logger.debug(ACTION_NAME + ": Destination of encryption = " + outputPath);

            logger.debug(ACTION_NAME + ": Start encrypting file " + inputFile);
            
            String projectName = getParameterByKey("project-name");
			
			String projectTenantName = null;
			
			if(!projectName.equalsIgnoreCase("system")) {
							
				JsonProject project = rm.getProjectMetadata(null, projectName, null);
				
		        if (null == project) {
		        	actionOutcome.setResult(ActionResult.FAIL);
		        	actionOutcome.setMessage("Not able to locate Project, check your configuration!");
					return actionOutcome;
		        }
		        
		        logger.info("Project tenant name for project is : "+ project.getParentTenantName());
		        
		        projectTenantName = project.getParentTenantName();
			}
			else {
				projectTenantName = getSystemConfigs().get(SystemConfigKey.SKYDRM_SYSTEM_BUCKET);
				logger.info("Using system bucket for encryption :" + projectTenantName);
			}
			
            rm.encrypt(inputFile.toString(), outputPath, tags, null, null, projectTenantName, TokenGroupType.TOKENGROUP_SYSTEMBUCKET);
                       
            encFileSRURL = docInfo.getServerRelativeURL().substring(0, docInfo.getServerRelativeURL().lastIndexOf("/") + 1) + outputFileName;
            encFileSiteURL = docInfo.getSiteURL();

            // check-in the encrypted file
            //checkInFile(encFileSiteURL, encFileSRURL, "ENCRYPTED_FILE");

            actionPerformed = true;

            logger.info("Encryption done");
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Error encrypting file " + docInfo.getDocumentPath() + ":" + e.getMessage(), e);

            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Error encrypting file "+ e.getMessage());
            return actionOutcome;
        }

        boolean deleteOriginalFile = false;
        try {
            deleteOriginalFile = Boolean.parseBoolean(getParameterByKey("delete-original-file"));
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Cannot parse parameter deleteOriginalFile. Default value as False will be set instead", e);
        }

        if (deleteOriginalFile) {
            logger.info("Deleting original file at " + docInfo.getServerRelativeURL());

            try {
                if (!deleteFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL())) {
                    logger.error(ACTION_NAME + ": Cannot delete the original file at " + docInfo.getServerRelativeURL());

                    actionOutcome.setResult(ActionResult.SUCCESS);
                    actionOutcome.setMessage("File encrypted successfully but failed to delete the original file");
                    return actionOutcome;
                }
            } catch (Exception e) {
                logger.error(ACTION_NAME + ": Cannot delete the original file at " + docInfo.getServerRelativeURL());

                actionOutcome.setResult(ActionResult.SUCCESS);
                actionOutcome.setMessage("File encrypted successfully but failed to delete the original file. Error " + e.getMessage());
                return actionOutcome;
            }
        }

        actionOutcome.setResult(ActionResult.SUCCESS);
        actionOutcome.setMessage("File encrypted successfully.");

        return actionOutcome;
    }

    public void rollback() throws RollbackException {

        if (actionPerformed) {

            logger.info(ACTION_NAME + ": Rollback: Trying to delete the encrypted file at " + encFileSRURL);
            try {
                deleteFile(encFileSiteURL, encFileSRURL);
            } catch (Exception e) {
                logger.error("Rollback " + ACTION_NAME + " : Could not delete the file at " + encFileSRURL + " during rollback. " + e.getMessage(), e);

                RollbackException rollbackException = new RollbackException(e.getMessage());
                rollbackException.setRollbackErrorType(RollbackErrorType.DELETE_FAILED);
                rollbackException.setTargetAbsoluteFilePath(encFileSRURL);
                throw rollbackException;
            }
            logger.debug("Rollback " + ACTION_NAME + " : Deleted File " + encFileSRURL + " under site " + encFileSiteURL);
            docInfo = null;
            actionPerformed = false;
        } else {
            logger.debug("No " + ACTION_NAME + " was performed before. Nothing to rollback.");
        }
    }

}
