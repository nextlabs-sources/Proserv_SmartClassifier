package com.nextlabs.smartclassifier.plugin.action.sharepoint.removetag;

import com.nextlabs.jtagger.Tagger;
import com.nextlabs.jtagger.TaggerFactory;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharePointAction;
import com.nextlabs.smartclassifier.solr.SolrDocumentInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pkalra on 11/21/2016.
 */
public class RemoveTag extends SharePointAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(RemoveTag.class);
    private static final String ACTION_NAME = "REMOVE_TAG (SHAREPOINT)";

    public RemoveTag() {
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

        logger.info(ACTION_NAME + ": Received file " + docInfo.getDocumentPath() + " for " + ACTION_NAME);

        initHTTPClient(docInfo.getRepoPath());

        Map<String, Set<String>> tagsByName = getTagsByName();
        logger.debug("Number of tags = " + tagsByName.size());

        logger.info("Trying to checkout the file at " + docInfo.getServerRelativeURL());
        try {
            if (!checkOutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL())) {
                logger.error(ACTION_NAME + ": Failed to checkout the file " + docInfo.getServerRelativeURL());

                actionOutcome.setMessage(ACTION_NAME + ": File check out failed");
                actionOutcome.setResult(ActionResult.FAIL);
                return actionOutcome;
            }
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Failed to checkout the file at " + docInfo.getServerRelativeURL() + " " + e.getMessage(), e);

            actionOutcome.setMessage("Failed to checkout the file " + e.getMessage());
            actionOutcome.setResult(ActionResult.FAIL);
            return actionOutcome;
        }

        logger.info(ACTION_NAME + ": File " + docInfo.getServerRelativeURL() + "Checked Out successfully");

        logger.info(ACTION_NAME + ": Trying to remove tags from " + docInfo.getDocumentPath());
        try {
            removeTags(tagsByName, docInfo.getDocumentPath());
        } catch (Exception err) {
            logger.error(ACTION_NAME + ": An error occurred while removing tag from file: " + docInfo.getDocumentPath() + " " + err.getMessage(), err);

            undoCheckoutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL());
            actionOutcome.setMessage(err.getMessage());
            actionOutcome.setResult(ActionResult.FAIL);
            return actionOutcome;
        }

        logger.info(ACTION_NAME + ": Successfully removed tags from " + docInfo.getDocumentPath());

        logger.info(ACTION_NAME + ": Trying to check-in the file at " + docInfo.getDocumentPath());
        try {
            if (!checkInFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL(), "TAGS_REMOVED_BY_SMART_CLASSIFIER")) {
                logger.error(ACTION_NAME + ": Failed to check-in the file " + docInfo.getServerRelativeURL());

                actionOutcome.setMessage("Failed to check-in the file");
                actionOutcome.setResult(ActionResult.FAIL);
                return actionOutcome;
            }
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Error failing to check-in the file: " + docInfo.getServerRelativeURL() + " " + e.getMessage(), e);

            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Failed to check in the file " + e.getMessage());
            return actionOutcome;
        }

        logger.info(ACTION_NAME + ": Successfully checked in the file " + docInfo.getServerRelativeURL());

        actionPerformed = true;
        actionOutcome.setResult(ActionResult.SUCCESS);
        actionOutcome.setMessage("Tags has been removed successfully from " + docInfo.getServerRelativeURL());
        return actionOutcome;
    }

    private void removeTags(Map<String, Set<String>> map, String documentPath)
            throws Exception {

        logger.debug(ACTION_NAME + ": Removing file tag for document " + documentPath);
        List<String> tagsToRemove = new ArrayList<>(map.keySet());
        
        File targetFile = new File(documentPath);
        
        if (targetFile.exists()) {
            logger.debug(ACTION_NAME + "File found..");
            Tagger tagger = TaggerFactory.getTagger(documentPath);
            
            if(tagger == null) {
            	throw new Exception("File type not supported.");
            }
            
            boolean success = tagger.deleteTags(tagsToRemove, Boolean.valueOf(getParameterByKey("edit-encrypted-document")));

            if (success) {
                tagger.save(documentPath);
                targetFile.setLastModified(System.currentTimeMillis());
            } else {
                logger.warn("UNABLE TO DELETE TAG FROM " + documentPath);
                tagger.close();
            }
        } else {
            logger.warn("FILE " + documentPath + " DOES NOT EXIST!");
        }
    }

}
