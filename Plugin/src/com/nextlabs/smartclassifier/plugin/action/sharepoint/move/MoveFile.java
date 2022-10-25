package com.nextlabs.smartclassifier.plugin.action.sharepoint.move;

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
public class MoveFile extends SharePointAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(MoveFile.class);
    private static final String ACTION_NAME = "MOVE_FILE (SHAREPOINT)";

    private boolean actionPerformed;
    private String movedFileSRURL;
    private String movedFileSiteURL;

    public MoveFile() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument solrDocument) throws Exception {

        ActionOutcome actionOutcome = new ActionOutcome();
        actionOutcome.setResult(ActionResult.FAIL);
        actionOutcome.setMessage(ACTION_NAME + ": Operation started");

        initializeFields();
        docInfo = new SolrDocumentInfo(solrDocument);
        docInfo.printDocumentInfo();

        logger.debug("Received " + docInfo.getDocumentPath() + " for " + ACTION_NAME);

        boolean overwrite = Boolean.valueOf(getParameterByKey("overwrite"));

        String targetURL = getParameterByKey("target");
        targetURL = StringFunctions.removeWhiteSpacesInURL(targetURL);

        initHTTPClient(docInfo.getRepoPath());

        try {
            if (targetURL != null) {
                if (SharePointUtil.isAValidFolder(httpClient, targetURL)) {
                    String targetSite = SharePointUtil.getSiteByFolder(httpClient, targetURL);

                    if (targetSite != null) {
                        if (targetSite.equals(docInfo.getSiteURL())) {

                            this.movedFileSRURL = (new URI(targetURL)).getPath() + Punctuation.FORWARD_SLASH + docInfo.getDocumentName();

                            logger.debug(ACTION_NAME + ": Trying to move file from " + docInfo.getServerRelativeURL() + " to => " + movedFileSRURL);

                            if (SharePointUtil.moveFile(httpClient, docInfo.getSiteURL(), docInfo.getServerRelativeURL(), movedFileSRURL, overwrite)) {
                                logger.debug(ACTION_NAME + ": Moved the file from " + docInfo.getServerRelativeURL() + " to " + movedFileSRURL);

                                actionPerformed = true;
                                this.movedFileSiteURL = docInfo.getSiteURL();

                                actionOutcome.setResult(ActionResult.SUCCESS);
                                actionOutcome.setMessage("Moved file from " + docInfo.getServerRelativeURL() + " to " + movedFileSRURL);

                            } else {
                                logger.error(ACTION_NAME + ": Move Failed");
                                actionOutcome.setResult(ActionResult.FAIL);
                                actionOutcome.setMessage("Move failed.");
                                return actionOutcome;
                            }
                        } else {
                            logger.error(ACTION_NAME + ": FILES CAN ONLY BE MOVED WITHIN THE SAME SITE");
                            actionOutcome.setResult(ActionResult.FAIL);
                            actionOutcome.setMessage("Files can ONLY BE moved within the same site.");
                            return actionOutcome;
                        }
                    } else {
                        logger.error(ACTION_NAME + ": MOVE COULD NOT BE PERFORMED AS THE SITE FOR " + targetURL + " COULD NOT BE FOUND!");
                        actionOutcome.setResult(ActionResult.FAIL);
                        actionOutcome.setMessage("Move could not be performed as the site for the " + targetURL + " could not be found!");
                        return actionOutcome;
                    }
                } else {
                    logger.error(ACTION_NAME + "THE " + targetURL + " IS NOT A VALID SHAREPOINT URL.");
                    actionOutcome.setResult(ActionResult.FAIL);
                    actionOutcome.setMessage("Target destination is not a valid sharepoint folder.");
                    return actionOutcome;
                }
            } else {
                logger.error(ACTION_NAME + "TARGET DESTINATION IS NULL!");
                actionOutcome.setResult(ActionResult.FAIL);
                actionOutcome.setMessage("Target destination is undefined.");
            }
        } catch (Exception ex) {
            logger.error(ACTION_NAME + ": Error trying to move the file from " + docInfo.getServerRelativeURL() + " to " + movedFileSRURL + " : " + ex.getMessage(), ex);

            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Error trying to move the file " + ex.getMessage());
            return actionOutcome;
        }

        return actionOutcome;
    }

    public void rollback() throws RollbackException {

        if (actionPerformed) {

            logger.debug(ACTION_NAME + ": Rollback: Trying to delete the moved file at " + movedFileSRURL);

            try {
                SharePointUtil.deleteFile(httpClient, movedFileSiteURL, movedFileSRURL);
            } catch (Exception e) {
                logger.error("Rollback " + ACTION_NAME + "Could not delete the file at " + movedFileSRURL + " during rollback." + e.getMessage(), e);

                RollbackException exception = new RollbackException(e.getMessage());
                exception.setRollbackErrorType(RollbackErrorType.DELETE_FAILED);
                exception.setTargetAbsoluteFilePath(movedFileSRURL);
                throw exception;
            }
            logger.debug("Rollback " + ACTION_NAME + " : Deleted File " + movedFileSRURL + " under site " + movedFileSiteURL);
            actionPerformed = false;
            docInfo = null;
        } else {
            logger.debug("No " + ACTION_NAME + " was performed before. Nothing to rollback.");
        }
    }
}
