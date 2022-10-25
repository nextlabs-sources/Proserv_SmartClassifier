package com.nextlabs.smartclassifier.plugin.action.sharedfolders.skydrm.encrypt;

import com.nextlabs.common.shared.JsonProject;
import com.nextlabs.nxl.RightsManager;
import com.nextlabs.common.shared.Constants.TokenGroupType;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.RollbackErrorType;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.exception.RollbackException;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.SharedFolderAction;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class SkyDRMEncryptFile extends SharedFolderAction implements ExecuteOncePerFile {
	private static final Logger logger = LogManager.getLogger(SkyDRMEncryptFile.class);
	private static final String ACTION_NAME = "SKYDRM_ENCRYPT_FILE (SHARED FOLDER)";

	private FileObject encryptedFile;

	public SkyDRMEncryptFile() {
		super(ACTION_NAME);
	}

	@Override
	public ActionOutcome execute(final SolrDocument document) throws Exception {
		ActionOutcome outcome = new ActionOutcome();
		encryptedFile = null;

		// check input
		if (document.get(SolrPredefinedField.ID) == null || document.get(SolrPredefinedField.DIRECTORY) == null) {
			logger.error("Unable to retrieve full details of input file");
			outcome.setResult(ActionResult.FAIL);
			outcome.setMessage("Unable to retrieve full details of input file");
			return outcome;
		}

		logger.info("Encrypting file with SkyDRM SDK" + document.get(SolrPredefinedField.ID));

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

		Map<String, Set<String>> tTags = getTagsByName();
		Map<String, String[]> tags = new HashMap<>();
		if (tTags != null) {
			for (Entry<String, Set<String>> entry : tTags.entrySet()) {
				List<String> values = new ArrayList<>();
				values.addAll(entry.getValue());
				String[] valuesArr = new String[values.size()];
				tags.put(entry.getKey(), values.toArray(valuesArr));
			}
		}

		RightsManager rm = setupSkyDRMSDK();

		if (rm == null) {
			logger.error("Cannot initialize SkyDRM SDK. Action cannot be performed");
			outcome.setResult(ActionResult.FAIL);
			outcome.setMessage("Cannot initialize SkyDRM Rights Manager");
			return outcome;
		}
		
		FileObject inputFile = fsMgr.resolveFile(document.get(SolrPredefinedField.ID).toString());
		
		if (!inputFile.exists()) {
			logger.debug("Encryption failed because file to be encrypted does not exist");
		}

		if (inputFile.getName().getExtension().toLowerCase().equals("nxl")) {
			outcome.setResult(ActionResult.SUCCESS);
			outcome.setMessage("File is already encrypted and will be skipped");
		}

		try {
			String outputFileName = document.get(SolrPredefinedField.DOCUMENT_NAME).toString().concat(".nxl");
			FileObject outputFile = fsMgr.resolveFile(destinationFolderString).resolveFile(outputFileName);
			
			if (outputFile.getType() == FileType.FILE) {
				outcome.setResult(ActionResult.FAIL);
				outcome.setMessage("Encrypted file already exists");
				return outcome;
			}
			logger.debug("Destination of encryption " + outputFile.getPublicURIString());
			// in this release only applies the tags map
			logger.debug("Start encrypt file " + inputFile.getPublicURIString());
			
			String projectName = getParameterByKey("project-name");
			
			String parentTenantName = null;
			
			if(!projectName.equalsIgnoreCase("system")) {
							
				JsonProject project = rm.getProjectMetadata(null, projectName, null);
				
		        if (null == project) {
		        	outcome.setResult(ActionResult.FAIL);
					outcome.setMessage("Not able to locate Project, check your configuration!");
					return outcome;
		        }
		        
		        logger.info("Project tenant name for project is : "+ project.getParentTenantName());
		        
		        parentTenantName = project.getParentTenantName();
		        
		        try(InputStream is = inputFile.getContent().getInputStream()) {
		            try(OutputStream os = outputFile.getContent().getOutputStream()) {
		            	//manager.encryptStream(is, os, fileName, length, null, rights, tagMap, parentTenantName, projectName);
		                rm.encryptStream(is, os, outputFileName, inputFile.getContent().getSize(), null, null, tags, parentTenantName, projectName);
		                logger.info("Stream Encryption Completed");
		            }
		        }
			}
			else {
				parentTenantName = getSystemConfigs().get(SystemConfigKey.SKYDRM_SYSTEM_BUCKET);
				logger.info("Using system bucket for encryption :" + parentTenantName);
				
				try(InputStream is = inputFile.getContent().getInputStream()) {
		            try(OutputStream os = outputFile.getContent().getOutputStream()) {
		            	//manager.encryptStream(is, os, fileName, length, null, rights, tagMap, parentTenantName, tgType);
		                rm.encryptStream(is, os, outputFileName, inputFile.getContent().getSize(), null, null, tags, parentTenantName, TokenGroupType.TOKENGROUP_SYSTEMBUCKET);
		                logger.info("Stream Encryption Completed");
		            }
		        }
			}
			
			
			
			logger.info("Encryption done");
		} catch (NoSuchElementException e) {
			logger.error("Error encrypting file " + document.get(SolrPredefinedField.ID) + ":" + e.getMessage(), e);
			logger.error("Could not find SkyDRM project with the name: " + getParameterByKey("project-name"));
		} catch (Exception e) {
			logger.error("Error encrypting file " + document.get(SolrPredefinedField.ID) + ":" + e.getMessage(), e);
			outcome.setResult(ActionResult.FAIL);
			outcome.setMessage("Error encrypting file " + document.get(SolrPredefinedField.ID));
			return outcome;
		}

		try {
			if (deleteOriginalFile) {
				logger.debug("Deleting original file");
				inputFile.delete();
			}
		} catch (Exception e) {
			logger.error("Cannot delete the original file: " + e.getMessage(), e);
			/* e.printStackTrace(); */
			outcome.setResult(ActionResult.SUCCESS);
			outcome.setMessage("File encrypted successfully but failed to delete the original file");
			return outcome;
		}

		outcome.setResult(ActionResult.SUCCESS);
		outcome.setMessage("File(s) encrypted successfully.");
		
		encryptedFile = inputFile;

		return outcome;
	}

	/**
	 * Undo encrypt file action by delete encrypted file if exist
	 */
	@Override
	public void rollback() throws RollbackException {
		if (encryptedFile != null) {
			try {
				if (encryptedFile.exists()) {
					logger.debug("Deleting the encrypted file " + encryptedFile);
					encryptedFile.delete();
				}
			} catch (Exception err) {
				logger.error("Unable to remove encrypted file " + encryptedFile.getPublicURIString() + " for rollback.", err);

				RollbackException exception = new RollbackException(err.getMessage());
				exception.setRollbackErrorType(RollbackErrorType.DELETE_FAILED);
				exception.setTargetAbsoluteFilePath(encryptedFile.getPublicURIString());
				throw exception;
			}
		}
	}
}
