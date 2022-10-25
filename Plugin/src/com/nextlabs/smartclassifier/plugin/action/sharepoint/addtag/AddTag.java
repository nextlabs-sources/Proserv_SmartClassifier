package com.nextlabs.smartclassifier.plugin.action.sharepoint.addtag;

import com.nextlabs.jtagger.Tagger;
import com.nextlabs.jtagger.TaggerFactory;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharePointAction;
import com.nextlabs.smartclassifier.solr.SolrDocumentInfo;
import org.apache.commons.lang.StringUtils;
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
public class AddTag extends SharePointAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(AddTag.class);
    private static final String ACTION_NAME = "ADD_TAG (SHAREPOINT)";

    public AddTag() {
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

        logger.debug(ACTION_NAME + ": Received " + docInfo.getDocumentPath() + " for " + ACTION_NAME);

        initHTTPClient(docInfo.getRepoPath());

        Map<String, Set<String>> tags = getTagsByName();
        logger.debug("Number of tags = " + tags.size());

        logger.info("Trying to checkout the file at " + docInfo.getServerRelativeURL());

        try {
            if (!checkOutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL())) {
                logger.error(ACTION_NAME + ": File check out failed");

                actionOutcome.setMessage("File check out failed");
                actionOutcome.setResult(ActionResult.FAIL);
                return actionOutcome;
            }
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Failed to checkout the file " + e.getMessage(), e);

            actionOutcome.setMessage("Failed to checkout the file" + e.getMessage());
            actionOutcome.setResult(ActionResult.FAIL);
            return actionOutcome;
        }
        logger.info(ACTION_NAME + ": File Checked Out successfully");


        logger.info(ACTION_NAME + ": Trying to add tags to " + docInfo.getServerRelativeURL());

        try {
            addTag(tags, docInfo.getDocumentPath());
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Error adding tag to file: " + docInfo.getDocumentPath() + e.getMessage(), e);

            undoCheckoutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL());
            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage(e.getMessage());
            return actionOutcome;
        }

        logger.info(ACTION_NAME + ": Tags added successfully to " + docInfo.getDocumentPath());

        logger.info(ACTION_NAME + ": Trying to check-in the file at " + docInfo.getServerRelativeURL());

        try {
            if (!checkInFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL(), "TAGS_ADDED_BY_SMART_CLASSIFIER")) {
                logger.error(ACTION_NAME + ": Failed to check-in the file");

                actionOutcome.setMessage("Failed to check-in the file");
                actionOutcome.setResult(ActionResult.FAIL);
                return actionOutcome;
            }
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Error failing to checkin the file: " + docInfo.getDocumentPath() + " " + e.getMessage(), e);

            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Failed to check-in the file " + e.getMessage());
            return actionOutcome;
        }

        logger.info(ACTION_NAME + ": Successfully checked-in the file at " + docInfo.getServerRelativeURL());

        actionPerformed = true;
        actionOutcome.setResult(ActionResult.SUCCESS);
        actionOutcome.setMessage("Tags have been added to " + docInfo.getDocumentPath() + " successfully.");
        return actionOutcome;
    }

    private void addTag(Map<String, Set<String>> tags, String documentPath)
            throws Exception {

        logger.debug(ACTION_NAME + ": Tagging file " + documentPath);

        Map<String, Object> tagsToAdd = new HashMap<>();

        for (String key : tags.keySet()) {

            logger.debug(ACTION_NAME + ": Adding tag,  " + key + " = " + tags.get(key));

            Set<String> valueStrings = tags.get(key);
            List<String> values = new ArrayList<>(valueStrings);
            tagsToAdd.put(key, StringUtils.join(values, ','));
        }
        
        File targetFile = new File(documentPath);
        
        if (targetFile.exists()) {
            logger.debug(ACTION_NAME + "File found..");
            Tagger tagger = TaggerFactory.getTagger(documentPath);
            
            if(tagger == null) {
            	throw new Exception("File type not supported.");
            }
            
            boolean success = tagger.addTags(tagsToAdd, Boolean.valueOf(getParameterByKey("edit-encrypted-document")));

            if (success) {
                tagger.save(documentPath);
                targetFile.setLastModified(System.currentTimeMillis());
                logger.debug(ACTION_NAME + ": Tags successfully added to the file");
            } else {
                logger.warn(ACTION_NAME + "UNABLE TO ADD TAG TO FILE!");
                tagger.close();
            }
        } else {
            logger.warn(ACTION_NAME + "FILE DOES NOT EXIST!");
        }
    }
}
