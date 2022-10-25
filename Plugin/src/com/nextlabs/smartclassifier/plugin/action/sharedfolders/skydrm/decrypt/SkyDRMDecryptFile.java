package com.nextlabs.smartclassifier.plugin.action.sharedfolders.skydrm.decrypt;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import com.nextlabs.nxl.NxlFile;
import com.nextlabs.nxl.RightsManager;
import com.nextlabs.smartclassifier.constant.ActionResult;	
import com.nextlabs.smartclassifier.constant.RollbackErrorType;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.exception.RollbackException;	
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharedFolderAction;

public class SkyDRMDecryptFile extends SharedFolderAction implements ExecuteOncePerFile {
	private static final Logger logger = LogManager.getLogger(SkyDRMDecryptFile.class);
	private static final String ACTION_NAME = "SKYDRM_DECRYPT_FILE (SHARED FOLDER)";

	private FileObject decryptedFile;

	public SkyDRMDecryptFile() {
		super(ACTION_NAME);
	}

	@Override
	public ActionOutcome execute(final SolrDocument document) throws Exception {
		ActionOutcome outcome = new ActionOutcome();
		boolean overwrite = Boolean.valueOf(getParameterByKey("overwrite"));

		// check input
		if (document.get(SolrPredefinedField.ID) == null || document.get(SolrPredefinedField.DIRECTORY) == null) {
			logger.error("Unable to retrieve full details of input file");
			outcome.setResult(ActionResult.FAIL);
			outcome.setMessage("Unable to retrieve full details of input file");
			return outcome;
		}

		String inputFileDir = document.get(SolrPredefinedField.ID).toString();

		logger.info("Decrypting file " + inputFileDir);

		// setting variables
		boolean deleteOriginalFile = false;
		try {
			deleteOriginalFile = Boolean.parseBoolean(getParameterByKey("delete-original-file"));
		} catch (Exception e) {
			logger.error("Cannot parse parameter deleteOriginalFile. Default value as False will be set instead", e);
		}

		// check destination folder
		// the plugin supports specifying specific destination folder, however
		// if no destination folder is provided, the folder of the original file
		// will be used
		String destinationFolderString = getParameterByKey("destination-folder");
		if (destinationFolderString == null || destinationFolderString.length() == 0) {
			destinationFolderString = document.get(SolrPredefinedField.DIRECTORY).toString();
		}

		RightsManager rm = setupSkyDRMSDK();
		if (rm == null) {
			logger.error("Cannot initialize Right Management SDK. Action cannot be performed");
			outcome.setResult(ActionResult.FAIL);
			outcome.setMessage("Cannot initialize Rights Manager");
			return outcome;
		}
		
		FileObject inputFile = fsMgr.resolveFile(inputFileDir);
		
		if (!inputFile.getName().getExtension().toLowerCase().equals("nxl")) {
			outcome.setResult(ActionResult.SUCCESS);
			outcome.setMessage("File is already decrypted and will be skipped");
		}

		try {
			FileObject outputFile = fsMgr.resolveFile(destinationFolderString).resolveFile(
					FilenameUtils.removeExtension(document.get(SolrPredefinedField.DOCUMENT_NAME).toString()));
			if (!overwrite && outputFile.getType() == FileType.FILE) {
				outcome.setResult(ActionResult.FAIL);
				outcome.setMessage("Unencrypted file of the same name already exists");
			} else {
				outputFile.createFile();
			}
			logger.debug("Destination of encryption " + outputFile.getPublicURIString());
			logger.info("Start decrypt file " + inputFileDir);


			try (InputStream is = inputFile.getContent().getInputStream()) {
				NxlFile nxlFile = NxlFile.parse(is);
					
				try (OutputStream os = outputFile.getContent().getOutputStream()) {
					rm.decryptPartial(is, os, nxlFile, 0L, nxlFile.getContentLength());
	
					logger.info("Decryption done");
				}
			} catch (NoSuchElementException e) {
				logger.error("Error encrypting file " + document.get(SolrPredefinedField.ID) + ":" + e.getMessage(), e);
			} catch (Exception e) {
				logger.error("Error decrypting file " + document.get(SolrPredefinedField.ID) + ":" + e.getMessage(), e);
				outcome.setResult(ActionResult.FAIL);
				outcome.setMessage("Error encrypting file " + document.get(SolrPredefinedField.ID));
				return outcome;
			}

			if (deleteOriginalFile) {
				logger.debug("Deleting original file");
				inputFile.delete();
			}
		} catch (Exception e) {
			logger.error("Cannot delete the original file: " + e.getMessage(), e);
			/* e.printStackTrace(); */
			outcome.setResult(ActionResult.SUCCESS);
			outcome.setMessage("File decrypts successfully but failed to delete the original file");
			return outcome;
		}

		outcome.setResult(ActionResult.SUCCESS);
		outcome.setMessage("File(s) decrypted successfully.");

		return outcome;
	}

	/**
	 * Undo decrypt file action by delete decrypted file if exist
	 */
	@Override
	public void rollback() throws RollbackException {
		if (decryptedFile != null) {
			try {
				if (decryptedFile.exists()) {
					logger.debug("Deleting the decryptedFile file " + decryptedFile);
					decryptedFile.delete();
				}
			} catch (Exception err) {
				logger.error("Unable to remove decrypted file " + decryptedFile.getPublicURIString() + " for rollback.", err);

				RollbackException exception = new RollbackException(err.getMessage());
				exception.setRollbackErrorType(RollbackErrorType.DELETE_FAILED);
				exception.setTargetAbsoluteFilePath(decryptedFile.getPublicURIString());
				throw exception;
			}
		}
	}

	public static void main(String[] args) {
	}
}
