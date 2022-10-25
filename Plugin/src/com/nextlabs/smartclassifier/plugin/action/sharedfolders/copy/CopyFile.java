package com.nextlabs.smartclassifier.plugin.action.sharedfolders.copy;

import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.RollbackErrorType;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.exception.RollbackException;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharedFolderAction;
import com.nextlabs.smartclassifier.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import java.io.File;

public class CopyFile
        extends SharedFolderAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(CopyFile.class);
    private static final String ACTION_NAME = "COPY_FILE (SHARED FOLDER)";

    private File copiedFile;

    public CopyFile() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument solrDocument)
            throws Exception {
        ActionOutcome outcome = new ActionOutcome();
        copiedFile = null;
        String target = getParameterByKey("target");
        boolean overwrite = Boolean.valueOf(getParameterByKey("overwrite"));

        outcome.setResult(ActionResult.SUCCESS);
        outcome.setMessage("Copied " + solrDocument.get(SolrPredefinedField.ID) + " files into " + target + ".");

        if (target != null) {
            logger.info("Copy file " + solrDocument.get(SolrPredefinedField.ID) + " to " + target);

            try {
                File sourceFile = new File((String) solrDocument.get(SolrPredefinedField.ID));

                if (overwrite) {
                    copiedFile = new File(target, FilenameUtils.getName(sourceFile.getAbsolutePath()));

                    if (!copiedFile.exists() || !FileUtils.contentEquals(copiedFile, sourceFile)) {
                        FileUtils.copyFile(sourceFile, copiedFile);
                    } else {
                        logger.info("File already up-to-date.");
                    }
                } else {
                    copiedFile = new File(target, FileUtil.getNonDuplicateFilename(target, FilenameUtils.getName(sourceFile.getAbsolutePath())));

                    FileUtils.copyFile(sourceFile, copiedFile);
                    logger.info("File copied without overwrite.");
                }

            } catch (Exception e) {
                logger.error("Unable to copy file: " + solrDocument.get(SolrPredefinedField.ID), e);

                outcome.setResult(ActionResult.FAIL);
                outcome.setMessage("Unable to copy file to target destination.");
            }
        } else {
            logger.error("Target destination is null.");
            outcome.setResult(ActionResult.FAIL);
            outcome.setMessage("Copy file: Target destination is undefined.");
        }

        return outcome;
    }

    /**
     * Undo copy file action by delete copied file if exist
     */
    @Override
    public void rollback() 
    		throws RollbackException {
        if (copiedFile != null) {
        	try {
                if (copiedFile.exists()) {
                    logger.debug("Deleting the copied file " + copiedFile);
                    copiedFile.delete();
                }
            } catch (Exception err) {
                logger.error("Unable to remove copied file " + copiedFile.getAbsolutePath() + " for rollback.", err);
        		
                RollbackException exception = new RollbackException(err.getMessage());
        		exception.setRollbackErrorType(RollbackErrorType.DELETE_FAILED);
        		exception.setTargetAbsoluteFilePath(copiedFile.getAbsolutePath());
        		throw exception;
            }
        }
    }
}
