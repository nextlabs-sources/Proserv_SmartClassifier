package com.nextlabs.smartclassifier.plugin.action.sharedfolders.move;

import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.RollbackErrorType;
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

public class MoveFile
        extends SharedFolderAction implements ExecuteOncePerFile {

    private static final Logger logger = LogManager.getLogger(MoveFile.class);
    private static final String ACTION_NAME = "MOVE_FILE (SHARED FOLDER)";

    private File movedFile;

    public MoveFile() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final SolrDocument document)
            throws Exception {
        ActionOutcome outcome = new ActionOutcome();
        movedFile = null;
        String target = getParameterByKey("target");
        boolean overwrite = Boolean.valueOf(getParameterByKey("overwrite"));

        outcome.setResult(ActionResult.SUCCESS);
        outcome.setMessage("File(s) moved to " + target + ".");

        if (target != null) {
            try {
                File sourceFile = new File((String) document.get("id"));

                // Only perform action if the source and destination doesn't refer to same file
                if (overwrite) {
                    movedFile = new File(target, FilenameUtils.getName(sourceFile.getAbsolutePath()));

                    if (!sourceFile.getAbsolutePath().equalsIgnoreCase(movedFile.getAbsolutePath())) {
                        if (movedFile.exists()) {
                            FileUtils.deleteQuietly(movedFile);
                        }

                        FileUtils.moveFile(sourceFile, movedFile);
                    }
                } else {
                    movedFile = new File(target, FileUtil.getNonDuplicateFilename(target, FilenameUtils.getName(sourceFile.getAbsolutePath())));
                    FileUtils.moveFile(sourceFile, movedFile);
                }

                logger.info("Moving file " + (String) document.get("id") + " to " + target);
            } catch (Exception e) {
                logger.error("Unable to move file: " + (String) document.get("id"), e);

                outcome.setResult(ActionResult.FAIL);
                outcome.setMessage("Unable to copy file: " + (String) document.get("id"));
            }
        } else {
            outcome.setResult(ActionResult.FAIL);
            outcome.setMessage("Move file destination is undefined.");
        }

        return outcome;
    }

    /**
     * Undo move file action by delete moved file if exist
     */
    @Override
    public void rollback() 
    		throws RollbackException {
        if (movedFile != null) {
        	try {
                if (movedFile.exists()) {
                    logger.debug("Deleting the moved file " + movedFile);
                    movedFile.delete();
                }
            } catch (Exception err) {
                logger.error("Unable to remove moved file " + movedFile.getAbsolutePath() + " for rollback.", err);
                
        		RollbackException exception = new RollbackException(err.getMessage());
        		exception.setRollbackErrorType(RollbackErrorType.DELETE_FAILED);
        		exception.setTargetAbsoluteFilePath(movedFile.getAbsolutePath());
        		throw exception;
            }
        }
    }
}
