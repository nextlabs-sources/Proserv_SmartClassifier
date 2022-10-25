package com.nextlabs.smartclassifier.plugin.action.sharepoint.setcolumnvalue;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by pkalra on 11/21/2016.
 */
public class SetColumnValue extends SharePointAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(SetColumnValue.class);
    private static final String ACTION_NAME = "SET_COLUMN_VALUE (SHAREPOINT)";
    private Map<String, Object> oldFieldValues = new HashMap<>();
    private Map<String, String> newFieldValues = new HashMap<>();
    private StringBuilder objType;
    private Map<String, Integer> fieldType = new HashMap<>();


    public SetColumnValue() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument solrDocument) throws Exception {

        ActionOutcome actionOutcome = new ActionOutcome();
        actionOutcome.setResult(ActionResult.FAIL);
        actionOutcome.setMessage(ACTION_NAME + ": Set Field Value action started.");

        initializeFields();
        docInfo = new SolrDocumentInfo(solrDocument);
        docInfo.printDocumentInfo();

        logger.info("Received file " + docInfo.getDocumentPath() + " for " + ACTION_NAME);

        initHTTPClient(docInfo.getRepoPath());

        oldFieldValues.clear();
        fieldType.clear();
        objType = new StringBuilder("");

        Map<String, Set<String>> tagsByName = getTagsByName();

        for (String fieldName : tagsByName.keySet()) {
            List<String> values = new ArrayList<>(tagsByName.get(fieldName));
            newFieldValues.put(fieldName, StringUtils.join(values, ','));
        }

        Set<String> fieldsToSet = newFieldValues.keySet();
        try {
            if (!getFieldValues(
                    docInfo.getSiteURL(),
                    docInfo.getServerRelativeURL(),
                    docInfo.getFolderURL(),
                    oldFieldValues,
                    fieldsToSet,
                    fieldType,
                    objType)) {

                logger.error(ACTION_NAME + ": Could not obtain the old field values");
                actionOutcome.setMessage("Could not obtain the old field values");
                actionOutcome.setResult(ActionResult.FAIL);
                return actionOutcome;
            }
        } catch (Exception e) {
            logger.error(ACTION_NAME + ": Could not obtain the old field values " + e.getMessage(), e);

            actionOutcome.setMessage("Could not obtain the old field values " + e.getMessage());
            actionOutcome.setResult(ActionResult.FAIL);
            return actionOutcome;
        }


        if (oldFieldValues.size() == 0 && fieldType.size() == 0) {

            logger.error(ACTION_NAME + ": No field to update");
            actionOutcome.setMessage("No field to update");
            actionOutcome.setResult(ActionResult.SUCCESS);
            return actionOutcome;
        }

        // checkout the file
        try {
            if (!checkOutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL())) {

                logger.error(ACTION_NAME + ": File check out failed");
                actionOutcome.setMessage("File check out failed");
                actionOutcome.setResult(ActionResult.FAIL);
                return actionOutcome;
            }
        } catch(Exception e) {
            logger.error(ACTION_NAME + ": File check out failed " + e.getMessage(), e);

            actionOutcome.setMessage("File check out failed " + e.getMessage());
            actionOutcome.setResult(ActionResult.FAIL);
            return actionOutcome;
        }


        logger.debug(ACTION_NAME + ": File Checked Out successfully");

        for (String fieldName : newFieldValues.keySet()) {

            if (!oldFieldValues.containsKey(fieldName)) {
                logger.warn(fieldName + " is not present in the " + docInfo.getFolderURL());
            } else {
                logger.debug(ACTION_NAME + ": Updating the field " + fieldName + " from " + oldFieldValues.get(fieldName)
                                + " to "
                                + newFieldValues.get(fieldName));

                String fieldValueString = newFieldValues.get(fieldName);
                int fieldTypeKind = fieldType.get(fieldName);
                Object fieldValue;
                String type = "";

                try {

                    switch (fieldTypeKind) {
                        case 1:
                            type = "Integer";
                            logger.debug("Casting " + fieldValueString + " as " + type);
                            fieldValue = Integer.parseInt(fieldValueString);
                            break;

                        case 2:
                        case 3:
                        case 6:
                        case 11:
                        case 14:
                            type = "String";
                            logger.debug("Casting " + fieldValueString + " as " + type);
                            fieldValue = fieldValueString;
                            break;

                        case 8:
                            type = "Boolean";
                            logger.debug("Casting " + fieldValueString + " as " + type);
                            fieldValue = Boolean.parseBoolean(fieldValueString);
                            break;

                        case 9:
                            type = "Float";
                            logger.debug("Casting " + fieldValueString + " as " + type);
                            fieldValue = Float.parseFloat(fieldValueString);
                            break;

                        case 10:
                            type = "BigDecimal";
                            logger.debug("Casting " + fieldValueString + " as " + type);
                            fieldValue = new BigDecimal(fieldValueString);
                            break;

                        default:
                            type = "UNKNOWN -> STRING";
                            logger.warn("UNKNOWN field type. Trying to set the value as a string..");
                            fieldValue = fieldValueString;
                            break;
                    }
                    logger.debug("fieldValue = " + fieldValue);
                } catch (Exception e) {
                    logger.error(
                            "Unable to cast " + fieldValueString + " as a " + type + " " + e.getMessage());
                    undoCheckoutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL());
                    actionOutcome.setMessage("Error casting " + fieldValueString + " to " + type);
                    actionOutcome.setResult(ActionResult.FAIL);
                    return actionOutcome;
                }

                try {
                    if (!setFieldValue(
                            docInfo.getSiteURL(),
                            docInfo.getServerRelativeURL(),
                            fieldName,
                            fieldValue,
                            objType.toString())) {

                        logger.error(
                                ACTION_NAME + ": Error updating the field " + fieldName + " to " + fieldValue);

                        undoCheckoutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL());
                        actionOutcome.setMessage(
                                "Error updating " + fieldName + " for " + docInfo.getServerRelativeURL());
                        actionOutcome.setResult(ActionResult.FAIL);
                        return actionOutcome;
                    }
                } catch (Exception e) {
                    logger.error(
                            ACTION_NAME + ": Error updating the field " + fieldName + " to " + fieldValue + " " + e.getMessage(), e);

                    undoCheckoutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL());
                    actionOutcome.setMessage(
                            "Error updating the " + fieldName + " to " + fieldValue + " " + e.getMessage());
                    actionOutcome.setResult(ActionResult.FAIL);
                    return actionOutcome;
                }

            }
        }
        
        File targetFile = new File(docInfo.getDocumentPath());
        
        if(targetFile.exists()) {
        	targetFile.setLastModified((System.currentTimeMillis()/1000)*1000);
        }
        
        try {
            if (!checkInFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL(), "FIELD_VALUE_UPDATED")) {

                logger.error(ACTION_NAME + " Unable to check-in the file");
                undoCheckoutFile(docInfo.getSiteURL(), docInfo.getServerRelativeURL());
                actionOutcome.setMessage("Cannot check in the file");
                actionOutcome.setResult(ActionResult.FAIL);
                return actionOutcome;
            }
        } catch(Exception e) {
            logger.error(ACTION_NAME + " Unable to check-in the file " + e.getMessage(), e);

            actionOutcome.setMessage("Cannot check in the file " + e.getMessage());
            actionOutcome.setResult(ActionResult.FAIL);
            return actionOutcome;
        }

        actionPerformed = true;
        logger.debug(ACTION_NAME + ": Field values successfully updated!");

        actionOutcome.setResult(ActionResult.SUCCESS);
        actionOutcome.setMessage("Field values have been set successfully.");
        return actionOutcome;
    }

/*    public void rollback() {
        if (actionPerformed) {

            if (!checkOutFile(siteURL, serverRelativeURL)) {
                logger.error(ACTION_NAME + "Could not checkout the file at " + serverRelativeURL);
                return;
            }

            for (String fieldName : oldFieldValues.keySet()) {
                Object fieldValue = oldFieldValues.get(fieldName);

                logger.debug(ACTION_NAME + ": Rollback: Resetting the " + fieldName + " to " + fieldValue);
                if (!setFieldValue(
                        siteURL,
                        serverRelativeURL,
                        fieldName,
                        fieldValue,
                        objType.toString())) {

                    logger.error(
                            ACTION_NAME
                                    + ": Rollback: Error updating the field "
                                    + fieldName
                                    + " to "
                                    + fieldValue);

                    undoCheckoutFile(siteURL, serverRelativeURL);
                    return;
                }
            }

            if (!checkInFile(siteURL, serverRelativeURL, "FIELD_VALUE_UPDATED")) {
                logger.error(ACTION_NAME + ": Rollback: Unable to check-in the file");
                return;
            }
        } else {
            logger.debug("Rollback: No " + ACTION_NAME + " was performed before. Nothing to rollback.");
        }
    }*/
}
