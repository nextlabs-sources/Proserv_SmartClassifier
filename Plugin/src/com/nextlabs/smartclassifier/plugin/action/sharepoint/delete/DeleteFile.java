package com.nextlabs.smartclassifier.plugin.action.sharepoint.delete;

import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.Punctuation;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharePointAction;
import com.nextlabs.smartclassifier.solr.SolrDocumentInfo;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import com.nextlabs.smartclassifier.util.StringFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.solr.common.SolrDocument;

/**
 * Created by pkalra on 11/21/2016.
 */
public class DeleteFile extends SharePointAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(DeleteFile.class);
    private static final String ACTION_NAME = "DELETE_FILE (SHAREPOINT)";

    public DeleteFile() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument solrDocument) throws Exception {

        ActionOutcome actionOutcome = new ActionOutcome();
        actionOutcome.setResult(ActionResult.FAIL);
        actionOutcome.setMessage(ACTION_NAME + ": SharePoint delete operation started.");

        initializeFields();
        docInfo = new SolrDocumentInfo(solrDocument);
        docInfo.printDocumentInfo();

        logger.debug("Received " + docInfo.getDocumentPath() + " for " + ACTION_NAME);

        initHTTPClient(docInfo.getRepoPath());

        try {
            if (SharePointUtil.deleteFile(httpClient, docInfo.getSiteURL(), docInfo.getServerRelativeURL())) {

                actionPerformed = true;

                String filePath = docInfo.getFolderURL() + Punctuation.FORWARD_SLASH + docInfo.getDocumentName();
                filePath = StringFunctions.removeWhiteSpacesInURL(filePath);

                logger.info(ACTION_NAME + ": Deleted File " + filePath);

                actionOutcome.setResult(ActionResult.SUCCESS);
                actionOutcome.setMessage(filePath + " deleted successfully.");
            } else {
                logger.error(ACTION_NAME + ": THE SHAREPOINT DELETE OPERATION THREW AN ERROR!");
                actionOutcome.setResult(ActionResult.FAIL);
                actionOutcome.setMessage("SharePoint delete file operation was unsuccessful.");
            }

            return actionOutcome;
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Error trying to delete the file " + docInfo.getServerRelativeURL() + " : " + e.getMessage(), e);

            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Error deleting the file : " + e.getMessage());
            return actionOutcome;
        }

    }

}
