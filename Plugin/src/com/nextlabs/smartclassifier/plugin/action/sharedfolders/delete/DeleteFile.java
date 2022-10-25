package com.nextlabs.smartclassifier.plugin.action.sharedfolders.delete;

import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharedFolderAction;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.solr.common.SolrDocument;

import java.io.File;

public class DeleteFile
        extends SharedFolderAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(DeleteFile.class);
    private static final String ACTION_NAME = "DELETE_FILE (SHARED FOLDER)";

    public DeleteFile() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument document)
            throws Exception {
        ActionOutcome outcome = new ActionOutcome();
        outcome.setResult(ActionResult.SUCCESS);
        outcome.setMessage("File(s) deleted successfully.");

        try {
            logger.info("Deleting " + document.get("id"));
            FileUtils.deleteQuietly(new File((String) document.get("id")));
        } catch (Exception err) {
            logger.error("Error deleting file: " + document.get("id"));

            outcome.setResult(ActionResult.FAIL);
            outcome.setMessage("Error deleting file: " + document.get("id"));
        }

        return outcome;
    }
}
