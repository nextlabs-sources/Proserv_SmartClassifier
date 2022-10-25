package com.nextlabs.smartclassifier.plugin.action.sharedfolders.addtag;

import com.nextlabs.jtagger.Tagger;
import com.nextlabs.jtagger.TaggerFactory;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.plugin.DataProviderManager;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharedFolderAction;
import com.nextlabs.smartclassifier.plugin.dataprovider.DataProvider;
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

public class AddTag
        extends SharedFolderAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(AddTag.class);
    private static final String ACTION_NAME = "ADD_TAG (SHARED FOLDER)";

    public AddTag() {
        super(ACTION_NAME);
    }

    /**
     * Execute the action of this solr Document
     *
     * @param solrDocument the Solr Document
     * @return <code>ActionOutcome</code> object to denote the result and message of this action
     */
    @Override
    public ActionOutcome execute(final SolrDocument solrDocument) throws Exception {
        Map<String, Set<String>> tags = getTagsByName();
        ActionOutcome outcome = new ActionOutcome();

        outcome.setResult(ActionResult.SUCCESS);
        outcome.setMessage("Tags has been added successfully.");

        String documentPath = (String) solrDocument.get(SolrPredefinedField.ID);

        try {
            logger.info("Adding tag to " + documentPath);
            addTag(tags, documentPath);
        } catch (Exception e) {
            logger.error("Error adding tag to file: " + documentPath, e);
            outcome.setResult(ActionResult.FAIL);
            outcome.setMessage(e.getMessage());
        }

        return outcome;
    }

    private void addTag(Map<String, Set<String>> tags, String documentPath)
            throws Exception {
        logger.debug("Tagging file " + documentPath);

        Map<String, Object> tagsToAdd = new HashMap<String, Object>();
        for (String key : tags.keySet()) {
            logger.debug("Adding tag " + key + " = " + tags.get(key));

            String keyString = key;
            Set<String> valueStrings = tags.get(key);

            if (keyString.startsWith("=")) {
                logger.debug("Key starts with '='. Searching for data provider.");

                DataProvider dataProvider = DataProviderManager.getDataProvider(keyString);

                if (dataProvider != null) {
                    try {
                        keyString = dataProvider.evaluate();
                    } catch (Exception err) {
                        logger.error(err.getMessage(), err);
                        logger.debug("Skip adding tag key " + keyString);
                        continue;
                    }
                } else {
                    logger.warn("Data provider not found for " + keyString);
                    continue;
                }

                logger.debug("Data provider returned: " + keyString);
            }

            List<String> values = new ArrayList<String>();
            for (String value : valueStrings) {
                if (value.startsWith("=")) {
                    logger.debug("Value starts with '='. Searching for data provider.");

                    DataProvider dataProvider = DataProviderManager.getDataProvider(value);

                    if (dataProvider != null) {
                        try {
                            values.add(dataProvider.evaluate());
                        } catch (Exception err) {
                            logger.error(err.getMessage(), err);
                        }
                    } else {
                        logger.warn("Data provider not found for " + value);
                    }

                    logger.debug("Data provider returned: " + value);
                } else {
                    values.add(value);
                }
            }

            tagsToAdd.put(keyString, StringUtils.join(values, ','));
        }

        if (new File(documentPath).exists()) {
            Tagger tagger = TaggerFactory.getTagger(documentPath);
            
            if(tagger == null) {
            	throw new Exception("File type not supported.");
            }
            
            boolean success = tagger.addTags(tagsToAdd, Boolean.valueOf(getParameterByKey("edit-encrypted-document")));

            if (success) {
                tagger.save(documentPath);
            } else {
                logger.warn("Unable to add tag to file.");
                tagger.close();
            }
        } else {
            logger.info("File does not exist.");
        }
    }
}
