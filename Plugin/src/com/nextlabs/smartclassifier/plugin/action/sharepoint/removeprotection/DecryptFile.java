package com.nextlabs.smartclassifier.plugin.action.sharepoint.removeprotection;

import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import com.nextlabs.nxl.RightsManager;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.RollbackErrorType;
import com.nextlabs.smartclassifier.exception.RollbackException;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharePointAction;
import com.nextlabs.smartclassifier.solr.SolrDocumentInfo;
import com.nextlabs.smartclassifier.util.FileUtil;

/**
 * Created by pkalra on 11/21/2016.
 */
public class DecryptFile extends SharePointAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(DecryptFile.class);
    private static final String ACTION_NAME = "DECRYPT_FILE (SHAREPOINT)";	
    private String decFileSRURL;
    private String decFileSiteURL;

    public DecryptFile() {
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

    	logger.info("Received file " + docInfo.getDocumentPath() + " for " + ACTION_NAME);

    	initHTTPClient(docInfo.getRepoPath());

    	File inputFile = new File(docInfo.getDocumentPath());

    	RightsManager rm = setupSkyDRMSDK();
    	if (rm == null) {
    		logger.error(ACTION_NAME + ": Cannot initialize the Rights Manager. Action cannot be performed");
    		actionOutcome.setResult(ActionResult.FAIL);
    		actionOutcome.setMessage("Cannot initialize the Rights Manager");
    		return actionOutcome;
    	}

    	try {
    		if (!rm.isNXL(inputFile.toString())) {
    			logger.error(ACTION_NAME + ": The input file is not a NXL file. Action cannot be performed");
    			actionOutcome.setResult(ActionResult.SUCCESS);
    			actionOutcome.setMessage("The file is already decrypted and will be skipped");
    			return actionOutcome;
    		}
    	} catch (Exception e) {
    		logger.error(ACTION_NAME + ": Failed to check if the file is a NXL file: " + e.getMessage(), e);
    		actionOutcome.setResult(ActionResult.FAIL);
    		actionOutcome.setMessage("Failed to check if the file is a NXL file " + e.getMessage());
    		return actionOutcome;
    	}

    	boolean overwrite = Boolean.valueOf(getParameterByKey("overwrite"));

    	try {
    		String outputFileName;
    		String outputPath;
    		if (overwrite) {
    			outputFileName = FilenameUtils.removeExtension(docInfo.getDocumentName());
    		} else {
    			outputFileName = FileUtil.getNonDuplicateFilename(docInfo.getDirectory(),
    					FilenameUtils.removeExtension(docInfo.getDocumentName()));
    		}
    		outputPath = new File(docInfo.getDirectory(), outputFileName).getAbsolutePath();

    		logger.debug(ACTION_NAME + ": Destination of encryption " + outputPath);
    		logger.info(ACTION_NAME + ": Start decrypting file " + inputFile);
    		
    		rm.decrypt(inputFile.toString(), outputPath);

    		logger.info("Decryption done");
    		

    		decFileSRURL = docInfo.getServerRelativeURL().substring(0,
    				docInfo.getServerRelativeURL().lastIndexOf("/") + 1) + outputFileName;
    		decFileSiteURL = docInfo.getSiteURL();

    		// checkInFile(decFileSiteURL, decFileSRURL, "DECRYPTED_FILE");

    		actionPerformed = true;

    		logger.info("Decryption done");
    	} catch (Exception e) {
    		logger.error(ACTION_NAME + ":Error decrypting file at " + docInfo.getServerRelativeURL() + " under "
    				+ docInfo.getSiteURL() + ": " + e.getMessage(), e);

    		actionOutcome.setResult(ActionResult.FAIL);
    		actionOutcome.setMessage("Error decrypting file " + e.getMessage());
    		return actionOutcome;
    	}

    	boolean deleteOriginalFile = false;
    	try {
    		deleteOriginalFile = Boolean.parseBoolean(getParameterByKey("delete-original-file"));
    	} catch (Exception e) {
    		logger.error(
    				ACTION_NAME
    				+ ": Cannot parse parameter deleteOriginalFile. Default value as False will be set instead",
    				e);
    	}

    	if (deleteOriginalFile) {

    		logger.debug("Deleting original file " + docInfo.getServerRelativeURL());

    		try {
    			if (!deleteFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL())) {
    				logger.error(
    						ACTION_NAME + ": Cannot delete the original file at " + docInfo.getServerRelativeURL());

    				actionOutcome.setResult(ActionResult.SUCCESS);
    				actionOutcome.setMessage("File encrypted successfully but failed to delete the original file");
    				return actionOutcome;
    			}
    		} catch (Exception e) {
    			logger.error(ACTION_NAME + ": Cannot delete the original file at " + docInfo.getServerRelativeURL()
    			+ " : " + e.getMessage(), e);

    			actionOutcome.setResult(ActionResult.SUCCESS);
    			actionOutcome.setMessage(
    					"File encrypted successfully but failed to delete the original file " + e.getMessage());
    			return actionOutcome;
    		}
    	}

    	actionOutcome.setResult(ActionResult.SUCCESS);
    	actionOutcome.setMessage(ACTION_NAME + ": File decrypted successfully.");

    	return actionOutcome;
    }

    public void rollback() throws RollbackException {

        if (actionPerformed) {

            logger.info(ACTION_NAME + ": Rollback: Trying to delete the decrypted file at " + decFileSRURL);
            try {
                deleteFile(decFileSiteURL, decFileSRURL);
            } catch (Exception e) {
                logger.error("Rollback " + ACTION_NAME + " : Could not delete the file at " + decFileSRURL + " during rollback. " + e.getMessage(), e);

                RollbackException exception = new RollbackException(e.getMessage());
                exception.setRollbackErrorType(RollbackErrorType.DELETE_FAILED);
                exception.setTargetAbsoluteFilePath(decFileSRURL);
                throw exception;
            }
            logger.debug(ACTION_NAME + ": Deleted the decrypted file at " + decFileSRURL + " under the site " + docInfo.getSiteURL());
            docInfo = null;
            actionPerformed = false;

        } else {
            logger.debug("No " + ACTION_NAME + " was performed before. Nothing to rollback.");
        }
    }
}
