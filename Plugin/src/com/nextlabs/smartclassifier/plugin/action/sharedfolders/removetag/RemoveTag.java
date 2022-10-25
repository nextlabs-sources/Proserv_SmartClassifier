package com.nextlabs.smartclassifier.plugin.action.sharedfolders.removetag;

import com.nextlabs.jtagger.Tagger;
import com.nextlabs.jtagger.TaggerFactory;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.plugin.DataProviderManager;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharedFolderAction;
import com.nextlabs.smartclassifier.plugin.dataprovider.DataProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.solr.common.SolrDocument;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoveTag
        extends SharedFolderAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(RemoveTag.class);
    private static final String ACTION_NAME = "REMOVE_TAG (SHARED FOLDER)";

    public RemoveTag() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument document)
            throws Exception {
        Map<String, Set<String>> tagsByName = getTagsByName();
        ActionOutcome outcome = new ActionOutcome();

        outcome.setResult(ActionResult.SUCCESS);
        outcome.setMessage("Tags has been removed successfully.");

        String documentPath = (String) document.get("id");

        try {
            logger.info("Removing tag from " + documentPath);
            removeTags(tagsByName, documentPath);
        } catch (Exception err) {
            logger.error("An error occurred while removing tag from file: " + documentPath, err);
            outcome.setResult(ActionResult.FAIL);
            outcome.setMessage(err.getMessage());

            throw err;
        }

        return outcome;
    }

    private void removeTags(Map<String, Set<String>> map, String documentPath)
            throws Exception {
        logger.debug("Removing file tag for document " + documentPath);

        List<String> tagsToRemove = new ArrayList<>();
        for (String key : map.keySet()) {
            logger.debug("Removing tag {} = " + key);
            String keyString = key;

            if (keyString.startsWith("=")) {
                DataProvider dataProvider = DataProviderManager.getDataProvider(keyString);

                if (dataProvider != null) {
                    try {
                        keyString = dataProvider.evaluate();
                    } catch (Exception err) {
                        logger.error(err.getMessage(), err);
                        logger.debug("Skip remove tag " + keyString);
                        continue;
                    }
                } else {
                    logger.warn("Data provider not found for " + keyString);
                    continue;
                }
            }
            tagsToRemove.add(keyString);
        }

        if (new File(documentPath).exists()) {
            Tagger tagger = TaggerFactory.getTagger(documentPath);
            
            if(tagger == null) {
            	throw new Exception("File type not supported.");
            }
            
            boolean success = tagger.deleteTags(tagsToRemove, Boolean.valueOf(getParameterByKey("edit-encrypted-document")));

            if (success) {
                tagger.save(documentPath);
            } else {
                logger.warn("Unable to delete tag.");
                tagger.close();
            }
        } else {
            logger.info("File does not exist.");
        }
    }
}
