package com.nextlabs.smartclassifier.plugin.action.sharepoint.copy;

import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.Punctuation;
import com.nextlabs.smartclassifier.constant.RollbackErrorType;
import com.nextlabs.smartclassifier.exception.RollbackException;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharePointAction;
import com.nextlabs.smartclassifier.solr.SolrDocumentInfo;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import com.nextlabs.smartclassifier.util.StringFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import java.net.URI;

/**
 * Created by pkalra on 11/21/2016.
 */
public class CopyFile extends SharePointAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(CopyFile.class);
    private static final String ACTION_NAME = "COPY_FILE (SHAREPOINT)";
    private String copiedFileSRURL;
    private String copiedFileSiteURL;

    public CopyFile() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument solrDocument) throws Exception {

        ActionOutcome actionOutcome = new ActionOutcome();
        actionOutcome.setResult(ActionResult.FAIL);
        actionOutcome.setMessage("Copy File operation started ");

        initializeFields();
        docInfo = new SolrDocumentInfo(solrDocument);
        docInfo.printDocumentInfo();

        logger.debug(ACTION_NAME + ": Received " + docInfo.getDocumentPath() + " for " + ACTION_NAME);

        initHTTPClient(docInfo.getRepoPath());

        boolean overwrite = Boolean.valueOf(getParameterByKey("overwrite"));

        String targetURL = getParameterByKey("target");
        targetURL = StringFunctions.removeWhiteSpacesInURL(targetURL);

        try {
            if (targetURL != null) {

                if (SharePointUtil.isAValidFolder(httpClient, targetURL)) {

                    String targetSite = SharePointUtil.getSiteByFolder(httpClient, targetURL);
                    if (targetSite != null) {
                        if (targetSite.equals(docInfo.getSiteURL())) {
                            this.copiedFileSRURL = (new URI(targetURL)).getPath() + Punctuation.FORWARD_SLASH + docInfo.getDocumentName();

                            logger.debug(ACTION_NAME + ": Trying to copy file from " + docInfo.getServerRelativeURL() + " to " + copiedFileSRURL);

                            if (SharePointUtil.copyFile(httpClient, docInfo.getSiteURL(), docInfo.getServerRelativeURL(), copiedFileSRURL, overwrite)) {
                                logger.debug(ACTION_NAME + ": Copied the file from " + docInfo.getServerRelativeURL() + " to => " + copiedFileSRURL);

                                actionPerformed = true;
                                this.copiedFileSiteURL = docInfo.getSiteURL();

                                actionOutcome.setResult(ActionResult.SUCCESS);
                                actionOutcome.setMessage("Copied file from " + docInfo.getServerRelativeURL() + " to => " + copiedFileSRURL);
                            } else {
                                actionOutcome.setResult(ActionResult.FAIL);
                                actionOutcome.setMessage("Copy failed.");
                            }
                        } else {
                            logger.error(ACTION_NAME + ": FILES CAN ONLY BE COPIED WITHIN THE SAME SITE");

                            actionOutcome.setResult(ActionResult.FAIL);
                            actionOutcome.setMessage("Files can ONLY BE copied within the same site.");
                        }
                    } else {
                        logger.error(ACTION_NAME + ": COPY COULD NOT BE PERFORMED AS THE SITE FOR " + targetURL + " COULD NOT BE FOUND!");

                        actionOutcome.setResult(ActionResult.FAIL);
                        actionOutcome.setMessage("Copy could not be performed as the site for the " + targetURL + " could not be found!");
                    }
                } else {
                    logger.error(ACTION_NAME + ": Target destination is not a valid sharepoint folder.");

                    actionOutcome.setResult(ActionResult.FAIL);
                    actionOutcome.setMessage("Target destination is not a valid sharepoint folder.");
                }
            } else {
                logger.error(ACTION_NAME + ": TARGET DESTINATION IS NULL!");

                actionOutcome.setResult(ActionResult.FAIL);
                actionOutcome.setMessage("Target destination is null.");
            }
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Error trying to copy the file from " + docInfo.getServerRelativeURL() + " to " + copiedFileSRURL + " : " + e.getMessage(), e);

            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Error trying to copy the file " + e.getMessage());
        }

        return actionOutcome;
    }

    public void rollback() throws RollbackException {

        if (actionPerformed) {

            logger.debug(ACTION_NAME + ": Rollback: Trying to delete the copied file at " + copiedFileSRURL);

            try {
                SharePointUtil.deleteFile(httpClient, copiedFileSiteURL, copiedFileSRURL);
            } catch (Exception e) {
                logger.error("Rollback " + ACTION_NAME + " : Could not delete the file at " + copiedFileSRURL + " during rollback. " + e.getMessage(), e);

                RollbackException exception = new RollbackException(e.getMessage());
                exception.setRollbackErrorType(RollbackErrorType.DELETE_FAILED);
                exception.setTargetAbsoluteFilePath(copiedFileSRURL);
                throw exception;
            }
            logger.debug("Rollback " + ACTION_NAME + " : Deleted the File " + copiedFileSRURL + " under site " + copiedFileSiteURL);
            actionPerformed = false;
            docInfo = null;
        } else {
            logger.debug("Rollback " + ACTION_NAME + " : No " + ACTION_NAME + " was performed before. Nothing to rollback.");
        }
    }
}
