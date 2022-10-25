package com.nextlabs.smartclassifier.task;

import com.nextlabs.smartclassifier.DocumentContent;
import com.nextlabs.smartclassifier.DocumentExtractor;
import com.nextlabs.smartclassifier.GenericExtractor;
import com.nextlabs.smartclassifier.base.Component;
import com.nextlabs.smartclassifier.constant.EventCategory;
import com.nextlabs.smartclassifier.constant.EventStage;
import com.nextlabs.smartclassifier.constant.EventStatus;
import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import com.nextlabs.smartclassifier.constant.ReportEvent;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.entity.DocumentRecord;
import com.nextlabs.smartclassifier.database.manager.DocumentRecordManager;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.dto.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.dto.TaskParameter;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.nxl.NXLExtractor;
import com.nextlabs.smartclassifier.util.CollectionUtil;
import com.nextlabs.smartclassifier.util.FileUtil;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;
import com.nextlabs.smartclassifier.util.RepositoryUtil;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.nextlabs.smartclassifier.constant.Punctuation.EMPTY_STRING;

public class ExtractDocumentTask implements Runnable {

    private static final Logger logger = LogManager.getLogger(ExtractDocumentTask.class);
    private static final double MB = 1024 * 1024.0;
    private static final int MAX_EXTRACTION_ATTEMPT = 3;

    private Component component;
    private TaskParameter taskParameter;
    private String absolutePath;
    private String action;
    private String extension;
    private int extractionAttemptCount;
    private final String repositoryType;
    private final String siteURL;
    private final String documentID;
    private final String repoPath;

    public ExtractDocumentTask(
            Component component,
            TaskParameter taskParameter,
            String documentID,
            String absolutePath,
            String action,
            String repositoryType,
            String siteURL,
            String repoPath) {
        this.component = component;
        this.taskParameter = taskParameter;
        this.documentID = documentID;
        this.absolutePath = absolutePath;
        this.action = action;
        this.extension = FileUtil.getFileExtension(absolutePath).toLowerCase();
        this.repositoryType = repositoryType;
        this.siteURL = siteURL;
        this.repoPath = repoPath;
        this.extractionAttemptCount = 0;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getAction() {
        return action;
    }

    public String getRepositoryType() {
        return repositoryType;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public String getDocumentID() {
        return documentID;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public int getExtractionAttemptCount() {
        return extractionAttemptCount;
    }

    public void setExtractionAttemptCount(int extractionAttemptCount) {
        this.extractionAttemptCount = extractionAttemptCount;
    }

    @Override
    public void run() {
        try {
            // Wait for execution window
            while (!component.isWithinExecutionWindow()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException err) {
                    logger.error(err.getMessage(), err);
                }
            }

            logger.info("Trying to process file " + absolutePath + " for " + action);

            if ((new File(FileUtil.getBackupAbsolutePath(absolutePath))).exists()) {
                if (getExtractionAttemptCount() < MAX_EXTRACTION_ATTEMPT) {
                    logger.info("Discovered recovery file for " + absolutePath + ". Re-queue for another extraction attempt.");
                    component.reQueue(this);
                } else {
                    logger.warn("Discovered recovery file for " + absolutePath + ". This file may inconsistent and require manual recovery or there is rule executing action on it. Skipping extraction process.");
                    logExtractionEvent(EventStatus.FAIL, documentID, "Discovered recovery file, skip extraction process.");
                }

                return;
            }

            double sFreeMemory = Runtime.getRuntime().freeMemory() / (MB);

            if (sFreeMemory < taskParameter.getMinimumHeapMemorySize()) {
                logger.info(
                        "Free memory is --> " + String.format("%.01f", sFreeMemory) + "MB. hold for process");
            }

            while (sFreeMemory < taskParameter.getMinimumHeapMemorySize()
                    && !component.isShuttingDown()) {
                pause(500);
                sFreeMemory = Runtime.getRuntime().freeMemory() / (MB);
            }

            // Extractor program is shutting down, quit task processing
            if (component.isShuttingDown()) {
                return;
            }

            logger.info(
                    "Proceeding to extract the file "
                            + absolutePath
                            + " with free memory "
                            + String.format("%.01f", sFreeMemory)
                            + "MBs.");

            switch (action) {
                case NextLabsConstant.ACTION_ADD:
                    processAdd();
                    break;

                case NextLabsConstant.ACTION_UPDATE:
                    processUpdate();
                    break;

                case NextLabsConstant.ACTION_DELETE:
                    processDelete();
                    break;

                default:
                    logger.error("UNSUPPORTED ACTION " + action + " for file: " + absolutePath);
                    break;
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }
    }

    private void processAdd() {
        logger.debug("Adding the file: " + absolutePath);

        Session session = null;
        Transaction transaction = null;
        CloseableHttpClient httpClient = null;
        boolean exceededSizeLimit = false;
        boolean extractionFailed = false;

        try {
            Path path = Paths.get(absolutePath);
            File file = path.toFile();

            if (file.exists()) {
                DocumentRecord documentRecord = new DocumentRecord();

                try {
                    documentRecord.setAbsoluteFilePath(absolutePath);
                    documentRecord.setFileId(documentID);
                    documentRecord.setFileType(extension.toUpperCase());
                    documentRecord.setFileSize(file.length());
                    documentRecord.setLastModified(file.lastModified());
                    documentRecord.setRepositoryType(repositoryType);

                    double fileSize = file.length() / MB;

                    logger.debug("The file size for " + path + " is = " + fileSize + " MBs.");

                    if (taskParameter.getFileSizeLimit(extension) > 0
                            && fileSize > taskParameter.getFileSizeLimit(extension)) {
                        logger.info(
                                "File size is larger than the allowed max file size limit, skipping extraction...");
                        exceededSizeLimit = true;
                        documentRecord.isExtracted(false);
                        documentRecord.setExtractionTime(0.0);
                        documentRecord.isIndexed(false);
                        documentRecord.setIndexingTime(0.0);
                        documentRecord.setErrorMessage("File size is larger than the allowed maximum.");
                    } else {

                        DocumentContent documentContent = extractDocument(documentRecord);

                        if (documentContent != null) {
                            if (RepositoryType.getRepositoryType(repositoryType) == RepositoryType.SHAREPOINT) {

                                logger.debug("Trying to find source authentication details for the repo path = " + repoPath);

                                SourceAuthenticationDTO authDetails = RepositoryUtil.getSourceAuthentication(repoPath);

                                if (authDetails != null) {

                                    logger.debug("The source authentication details for repo path = " + authDetails);
                                    //                                    CloseableHttpClient httpClient =
//                                            HTTPClientUtil.getHTTPClient(
//                                                    authDetails.getUserName(),
//                                                    NxlCryptoUtil.decrypt(authDetails.getPassword()),
//                                                    authDetails.getDomain());
                                    String workstation = InetAddress.getLocalHost().getHostName();

                                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                                    credentialsProvider.setCredentials(
                                            new AuthScope(AuthScope.ANY),
                                            new NTCredentials(authDetails.getUserName(), NxlCryptoUtil.decrypt(authDetails.getPassword()), workstation, authDetails.getDomain()));
                                    httpClient = HttpClients.custom()
                                            .setDefaultCredentialsProvider(credentialsProvider)
                                            .build();

                                    if (!SharePointUtil.testConnection(httpClient, siteURL)) {
                                        logger.error("Connection test failed for " + absolutePath + ". Will try to reload the repositories.");
                                        RepositoryUtil.reloadRepositories();
                                        authDetails = RepositoryUtil.getSourceAuthentication(repoPath);
                                        httpClient.close();
//                                        httpClient = HTTPClientUtil.getHTTPClient(authDetails.getUserName(), NxlCryptoUtil.decrypt(authDetails.getPassword()), authDetails.getDomain());
                                        credentialsProvider = new BasicCredentialsProvider();
                                        credentialsProvider.setCredentials(
                                                new AuthScope(AuthScope.ANY),
                                                new NTCredentials(authDetails.getUserName(), NxlCryptoUtil.decrypt(authDetails.getPassword()), workstation, authDetails.getDomain()));
                                        httpClient = HttpClients.custom()
                                                .setDefaultCredentialsProvider(credentialsProvider)
                                                .build();
                                    } else {
                                        logger.debug("Connection test for " + absolutePath + " successful");
                                    }

                                    logger.debug("Trying to get the sharepoint metadata for " + absolutePath);

                                    Map<String, String> sharePointMetadata =
                                            SharePointUtil.getSharePointMetadata(httpClient, siteURL, absolutePath);
                                    logger.debug("After getSharePointMetadata() " + absolutePath);
                                    List<String> excludedMetadataFields = taskParameter.getExcludedMetadata();
                                    CollectionUtil.toLowerCase(excludedMetadataFields);
                                    sharePointMetadata.keySet().removeAll(excludedMetadataFields);

                                    logger.debug("The metadata found is = " + sharePointMetadata.size());
                                    documentContent.addToMetadata(sharePointMetadata);
                                    documentContent.setDocumentID(documentID);
                                    documentContent.setRepositoryType(documentRecord.getRepositoryType());
                                    documentContent.setFileType(extension.toUpperCase());
                                    documentContent.setRepoPath(repoPath);
                                    documentContent.setLastModified(file.lastModified());
                                    addToIndex(documentContent, documentRecord);

                                } else {
                                    throw new Exception("Could not get the source authentication details for " + absolutePath);
                                }
                            } else if (RepositoryType.getRepositoryType(repositoryType) == RepositoryType.SHARED_FOLDER) {
                                logger.info("File: " + absolutePath + " extracted successfully.");

                                documentContent.setDocumentID(documentID);
                                documentContent.setRepositoryType(documentRecord.getRepositoryType());
                                documentContent.setFileType(extension.toUpperCase());
                                documentContent.setRepoPath(repoPath);
                                documentContent.setLastModified(file.lastModified());
                                addToIndex(documentContent, documentRecord);
                            }

                        } else {
                            extractionFailed = true;
                        }
                    }

                    session = component.getSessionFactory().openSession();
                    transaction = session.beginTransaction();
                    (new DocumentRecordManager(component.getSessionFactory(), session))
                            .addDocumentRecord(documentRecord);
                    transaction.commit();

                    if (exceededSizeLimit || extractionFailed) {
                        logExtractionEvent(
                                EventStatus.FAIL, documentRecord.getFileId(), documentRecord.getErrorMessage());
                    } else {
                        logExtractionEvent(EventStatus.SUCCESS, documentRecord.getFileId());

                        if (documentRecord.isIndexed()) {
                            logIndexingEvent(
                                    NextLabsConstant.ACTION_ADD, EventStatus.SUCCESS, documentRecord.getFileId());
                        } else {
                            logIndexingEvent(
                                    NextLabsConstant.ACTION_ADD,
                                    EventStatus.FAIL,
                                    documentRecord.getFileId(),
                                    documentRecord.getErrorMessage());
                        }
                    }
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);

                    if (transaction != null) {
                        try {
                            transaction.rollback();
                        } catch (Exception rollbackErr) {
                            logger.error(rollbackErr.getMessage(), rollbackErr);
                        }
                    }
                } finally {
                    if (httpClient != null) {
                        httpClient.close();
                    }
                }
            } else {
                logger.warn("File " + absolutePath + " no longer exist when performing add operation");
            }
        } catch (ManagerException | Exception err) {
            logger.error(err.getMessage(), err);

            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore
                }
            }
        }
    }

    private void processUpdate() {
        logger.debug("Updating the file: " + absolutePath);

        Session session = null;
        Transaction transaction = null;
        CloseableHttpClient httpClient = null;
        boolean exceededSizeLimit = false;
        boolean extractionFailed = false;

        try {
            Path path = Paths.get(absolutePath);
            File file = path.toFile();

            if (file.exists()) {
                DocumentRecord documentRecord = new DocumentRecord();

                try {
                    documentRecord.setAbsoluteFilePath(absolutePath);
                    documentRecord.setFileId(documentID);
                    documentRecord.setFileType(extension.toUpperCase());
                    documentRecord.setFileSize(file.length());
                    documentRecord.setLastModified(file.lastModified());
                    documentRecord.setRepositoryType(repositoryType);

                    //Check maximum file before proceed to extract
                    double fileSize = file.length() / MB;

                    logger.debug("The file size for " + path + " is = " + fileSize + " MBs.");

                    if (taskParameter.getFileSizeLimit(extension) > 0
                            && fileSize > taskParameter.getFileSizeLimit(extension)) {
                        logger.info(
                                "File size is larger than the allowed max file size limit, skipping extraction...");
                        exceededSizeLimit = true;
                        documentRecord.isExtracted(false);
                        documentRecord.setExtractionTime(0.0);
                        documentRecord.isIndexed(false);
                        documentRecord.setIndexingTime(0.0);
                        documentRecord.setErrorMessage("File size is larger than the allowed maximum.");

                        // File may indexed previously when file size < file size limit
                        deleteFromIndex();
                    } else {
                        DocumentContent documentContent = extractDocument(documentRecord);

                        if (documentContent != null) {
                            if (RepositoryType.getRepositoryType(repositoryType) == RepositoryType.SHAREPOINT) {

                                logger.debug("Trying to get the source authentication details for " + repoPath);
                                SourceAuthenticationDTO authDetails = RepositoryUtil.getSourceAuthentication(repoPath);

                                if (authDetails != null) {

                                    logger.debug("The source authentication details for repo path = " + authDetails);

                                    /*CloseableHttpClient httpClient =
                                            HTTPClientUtil.getHTTPClient(
                                                    authDetails.getUserName(),
                                                    NxlCryptoUtil.decrypt(authDetails.getPassword()),
                                                    authDetails.getDomain());
*/
                                    String workstation = InetAddress.getLocalHost().getHostName();

                                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                                    credentialsProvider.setCredentials(
                                            new AuthScope(AuthScope.ANY),
                                            new NTCredentials(authDetails.getUserName(), NxlCryptoUtil.decrypt(authDetails.getPassword()), workstation, authDetails.getDomain()));
                                    httpClient = HttpClients.custom()
                                            .setDefaultCredentialsProvider(credentialsProvider)
                                            .build();

                                    logger.debug("Trying to test the connection details");

                                    if (!SharePointUtil.testConnection(httpClient, siteURL)) {
                                        logger.error("Connection test failed for " + absolutePath + ". Will try to reload the repositories.");
                                        RepositoryUtil.reloadRepositories();
                                        authDetails = RepositoryUtil.getSourceAuthentication(repoPath);
                                        httpClient.close();
                                        //httpClient = HTTPClientUtil.getHTTPClient(authDetails.getUserName(), NxlCryptoUtil.decrypt(authDetails.getPassword()), authDetails.getDomain());
                                        credentialsProvider = new BasicCredentialsProvider();
                                        credentialsProvider.setCredentials(
                                                new AuthScope(AuthScope.ANY),
                                                new NTCredentials(authDetails.getUserName(), NxlCryptoUtil.decrypt(authDetails.getPassword()), workstation, authDetails.getDomain()));
                                        httpClient = HttpClients.custom()
                                                .setDefaultCredentialsProvider(credentialsProvider)
                                                .build();
                                    } else {
                                        logger.debug("Connection test for " + absolutePath + " was successful !");
                                    }

                                    Map<String, String> sharePointMetadata =
                                            SharePointUtil.getSharePointMetadata(httpClient, siteURL, absolutePath);

                                    List<String> excludedMetadataFields = taskParameter.getExcludedMetadata();
                                    CollectionUtil.toLowerCase(excludedMetadataFields);
                                    sharePointMetadata.keySet().removeAll(excludedMetadataFields);

                                    logger.debug("The metadata found is = " + sharePointMetadata.size());
                                    documentContent.addToMetadata(sharePointMetadata);
                                    documentContent.setDocumentID(documentID);
                                    documentContent.setRepositoryType(repositoryType);
                                    documentContent.setFileType(extension.toUpperCase());
                                    documentContent.setRepoPath(repoPath);
                                    documentContent.setLastModified(file.lastModified());
                                    addToIndex(documentContent, documentRecord);
                                } else {
                                    throw new Exception("Could not get the source authentication details " + absolutePath);
                                }
                            } else if (RepositoryType.getRepositoryType(repositoryType) == RepositoryType.SHARED_FOLDER) {
                                logger.info("File: " + absolutePath + " extracted successfully.");
                                documentContent.setDocumentID(documentID);
                                documentContent.setRepositoryType(repositoryType);
                                documentContent.setFileType(extension.toUpperCase());
                                documentContent.setRepoPath(repoPath);
                                documentContent.setLastModified(file.lastModified());
                                addToIndex(documentContent, documentRecord);
                            }

                        } else {
                            extractionFailed = true;
                        }
                    }

                    session = component.getSessionFactory().openSession();
                    transaction = session.beginTransaction();
                    (new DocumentRecordManager(component.getSessionFactory(), session))
                            .updateDocumentRecord(documentRecord);
                    transaction.commit();

                    if (exceededSizeLimit || extractionFailed) {
                        logExtractionEvent(
                                EventStatus.FAIL, documentRecord.getFileId(), documentRecord.getErrorMessage());
                    } else {
                        logExtractionEvent(EventStatus.SUCCESS, documentRecord.getFileId());

                        if (documentRecord.isIndexed()) {
                            logIndexingEvent(
                                    NextLabsConstant.ACTION_UPDATE, EventStatus.SUCCESS, documentRecord.getFileId());
                        } else {
                            logIndexingEvent(
                                    NextLabsConstant.ACTION_UPDATE,
                                    EventStatus.FAIL,
                                    documentRecord.getFileId(),
                                    documentRecord.getErrorMessage());
                        }
                    }
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);

                    if (transaction != null) {
                        try {
                            transaction.rollback();
                        } catch (Exception rollbackErr) {
                            logger.error(rollbackErr.getMessage(), rollbackErr);
                        }
                    }
                } finally {
                    if (httpClient != null) {
                        httpClient.close();
                    }
                }
            } else {
                logger.warn(
                        "File " + absolutePath + " no longer exist when performing update operation");
            }
        } catch (ManagerException | Exception err) {
            logger.error(err.getMessage(), err);

            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore
                }
            }
        }
    }

    private void processDelete() {
        logger.debug("Deleting file: " + absolutePath);
        Session session = null;
        Transaction transaction = null;
        String fileId = EMPTY_STRING;

        try {
            deleteFromIndex();

            session = component.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            fileId =
                    (new DocumentRecordManager(component.getSessionFactory(), session))
                            .deleteDocumentRecord(absolutePath);
            transaction.commit();

            logIndexingEvent(NextLabsConstant.ACTION_DELETE, EventStatus.SUCCESS, fileId);
        } catch (ManagerException err) {
            logger.error(err.getMessage(), err);

            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
        } catch (IOException err) {
            logger.error(
                    "Error deleting from from indexing: "
                            + absolutePath
                            + ". Reason: "
                            + err.getMessage());
            logIndexingEvent(NextLabsConstant.ACTION_DELETE, EventStatus.FAIL, fileId);
        } catch (Exception e) {
            logger.error("Error removing record from DB for file: " + absolutePath, e);

            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackErr) {
                    logger.error(rollbackErr.getMessage(), rollbackErr);
                }
            }
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException err) {
                    // Ignore
                }
            }
        }
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    private void addToIndex(DocumentContent documentContent, DocumentRecord documentRecord) {
        try {
            long indexingStartTime = System.nanoTime();

            taskParameter.getIndexManager().addDocument(documentContent);

            documentRecord.isIndexed(true);
            documentRecord.setIndexingTime((System.nanoTime() - indexingStartTime) / 1000000.0);
        } catch (Exception e) {
            logger.error("Error indexing the file: " + absolutePath + "\t" + e.getMessage(), e);

            documentRecord.isIndexed(false);
            documentRecord.setIndexingTime(0.0);
            documentRecord.setErrorMessage(e.getMessage());
        }
    }

    private void deleteFromIndex() throws Exception {
        taskParameter.getIndexManager().deleteDocument(absolutePath);
    }

    private DocumentContent extractDocument(DocumentRecord documentRecord) {
        logger.debug("Extracting the file " + absolutePath);

        DocumentContent documentContent = null;
        DocumentExtractor extractor;

        try {
            extractor = getExtractor(documentRecord);

            if (extractor != null) {
                int counter = 0;

                while (counter
                        < Integer.parseInt(
                        component.getSystemConfig(
                                SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_COUNT))) {
                    try {
                        long extractionStartTime = System.nanoTime();

                        documentContent = extractor.extract(Boolean.parseBoolean(component.getSystemConfig(SystemConfigKey.EXTRACTOR_EXTRACTION_BODY)));

                        documentRecord.setExtractionTime((System.nanoTime() - extractionStartTime) / 1000000.0);
                        documentRecord.isExtracted(true);
                        documentRecord.setErrorMessage(null);
                        break;
                    } catch (InvocationTargetException | FileNotFoundException fileException) {
                        counter++;
                        if (counter
                                >= Integer.parseInt(
                                component.getSystemConfig(
                                        SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_COUNT))) {
                            logger.error(
                                    "Unable to extract document after "
                                            + Integer.parseInt(
                                            component.getSystemConfig(
                                                    SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_COUNT))
                                            + " times of tries. Skip document.");
                            documentRecord.isExtracted(false);
                            documentRecord.setExtractionTime(0.0);
                            if (fileException.getCause() != null
                                    && StringUtils.isNotBlank(fileException.getCause().getMessage())) {
                                documentRecord.setErrorMessage(fileException.getCause().getMessage());
                            } else {
                                documentRecord.setErrorMessage(fileException.getMessage());
                            }
                        } else {
                            logger.info(
                                    "Failed to extract document due to file issue. "
                                            + "Retry after "
                                            + Integer.parseInt(
                                            component.getSystemConfig(
                                                    SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_INTERVAL))
                                            + " seconds. Try counter ["
                                            + counter
                                            + "].");
                            try {
                                Thread.sleep(
                                        Integer.parseInt(
                                                component.getSystemConfig(
                                                        SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_INTERVAL))
                                                * 1000);
                            } catch (InterruptedException interruptedException) {
                                logger.error(interruptedException.getMessage(), interruptedException);
                            }
                        }
                    }
                }
            }
        } catch (Exception | OutOfMemoryError err) {
            logger.error(err.getMessage(), err);
            documentRecord.isExtracted(false);
            documentRecord.setExtractionTime(0.0);
            if (err.getCause() != null && StringUtils.isNotBlank(err.getCause().getMessage())) {
                documentRecord.setErrorMessage(err.getCause().getMessage());
            } else {
                documentRecord.setErrorMessage(err.getMessage());
            }
        }

        return documentContent;
    }

    private DocumentExtractor getExtractor(DocumentRecord documentRecord) throws Exception {
        if (StringUtils.isNotBlank(extension)) {
            try {
                int counter = 0;
                while (counter
                        < Integer.parseInt(
                        component.getSystemConfig(
                                SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_COUNT))) {
                    try {
                        if ("nxl".equals(extension)) {
                            return new NXLExtractor(absolutePath, taskParameter.getRmsProperties());
                        } else {
                            com.nextlabs.smartclassifier.dto.DocumentExtractor documentExtractorDTO =
                                    taskParameter.getDocumentExtractor(extension);

                            if (documentExtractorDTO != null) {
                                Class<?> clz = Class.forName(documentExtractorDTO.getClassName());
                                DocumentExtractor documentExtractor =
                                        (DocumentExtractor)
                                                clz.getConstructor(String.class).newInstance(absolutePath);
                                documentExtractor.setParameters(documentExtractorDTO.getParameters());

                                return documentExtractor;
                            } else {
                                return new GenericExtractor(absolutePath);
                            }
                        }
                    } catch (InvocationTargetException | FileNotFoundException fileException) {
                        counter++;
                        if (counter
                                >= Integer.parseInt(
                                component.getSystemConfig(
                                        SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_COUNT))) {
                            logger.error(
                                    "Unable to create document extractor after "
                                            + Integer.parseInt(
                                            component.getSystemConfig(
                                                    SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_COUNT))
                                            + " times of tries. Skip document.");
                            documentRecord.isExtracted(false);
                            documentRecord.setExtractionTime(0.0);
                            if (fileException.getCause() != null
                                    && StringUtils.isNotBlank(fileException.getCause().getMessage())) {
                                documentRecord.setErrorMessage(fileException.getCause().getMessage());
                            } else {
                                documentRecord.setErrorMessage(fileException.getMessage());
                            }
                        } else {
                            logger.info(
                                    "Failed to create document extractor due to file issue. "
                                            + "Retry after "
                                            + Integer.parseInt(
                                            component.getSystemConfig(
                                                    SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_INTERVAL))
                                            + " seconds. Try counter ["
                                            + counter
                                            + "].");
                            try {
                                Thread.sleep(
                                        Integer.parseInt(
                                                component.getSystemConfig(
                                                        SystemConfigKey.EXTRACTOR_EXTRACTION_FAILED_RETRY_INTERVAL))
                                                * 1000);
                            } catch (InterruptedException interruptedException) {
                                logger.error(interruptedException.getMessage(), interruptedException);
                            }
                        }
                    }
                }
            } catch (Exception err) {
                logger.error(err.getMessage(), err);
                if (err.getCause() != null && StringUtils.isNotBlank(err.getCause().getMessage())) {
                    documentRecord.setErrorMessage(err.getCause().getMessage());
                } else {
                    documentRecord.setErrorMessage(err.getMessage());
                }
            }
        }

        return null;
    }

    private void logExtractionEvent(EventStatus status, String fileId, String... errorMessage) {
        if (EventStatus.SUCCESS.equals(status)) {
            if (Boolean.valueOf(
                    component.getSystemConfig(SystemConfigKey.ENABLE_DOCUMENT_EXTRACTION_SUCCESS_LOG))) {
                try {
                    Event extractionEvent = new Event();
                    extractionEvent.setStage(EventStage.FILE_EXTRACTION);
                    extractionEvent.setFileId(fileId);
                    extractionEvent.setRepositoryType(repositoryType);
                    extractionEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    extractionEvent.setCategory(EventCategory.OPERATION);
                    extractionEvent.setStatus(status);
                    extractionEvent.setMessageCode(ReportEvent.DOCUMENT_EXTRACT_SUCCESS.getMessageCode());
                    extractionEvent.setTimestamp(System.currentTimeMillis());

                    component.log(extractionEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        } else {
            if (Boolean.valueOf(
                    component.getSystemConfig(SystemConfigKey.ENABLE_DOCUMENT_EXTRACTION_FAILED_LOG))) {
                try {
                    Event extractionEvent = new Event();
                    extractionEvent.setStage(EventStage.FILE_EXTRACTION);
                    extractionEvent.setFileId(fileId);
                    extractionEvent.setRepositoryType(repositoryType);
                    extractionEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    extractionEvent.setCategory(EventCategory.OPERATION);
                    extractionEvent.setStatus(status);
                    extractionEvent.setMessageCode(ReportEvent.DOCUMENT_EXTRACT_FAIL.getMessageCode());
                    if (errorMessage != null && errorMessage.length > 0) {
                        extractionEvent.addMessageParam(errorMessage[0]);
                    }
                    extractionEvent.setTimestamp(System.currentTimeMillis());

                    component.log(extractionEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        }
    }

    private void logIndexingEvent(
            String action, EventStatus status, String fileId, String... errorMessage) {
        if (EventStatus.SUCCESS.equals(status)) {
            if (Boolean.valueOf(
                    component.getSystemConfig(SystemConfigKey.ENABLE_DOCUMENT_INDEXING_SUCCESS_LOG))) {
                try {
                    Event indexingEvent = new Event();
                    indexingEvent.setStage(EventStage.FILE_INDEXING);
                    indexingEvent.setFileId(fileId);
                    indexingEvent.setRepositoryType(repositoryType);
                    indexingEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    indexingEvent.setCategory(EventCategory.OPERATION);
                    indexingEvent.setStatus(status);
                    if (NextLabsConstant.ACTION_ADD.equals(action)) {
                        indexingEvent.setMessageCode(
                                ReportEvent.DOCUMENT_INDEXING_ADD_SUCCESS.getMessageCode());
                    } else if (NextLabsConstant.ACTION_UPDATE.equals(action)) {
                        indexingEvent.setMessageCode(
                                ReportEvent.DOCUMENT_INDEXING_UPDATE_SUCCESS.getMessageCode());
                    } else {
                        indexingEvent.setMessageCode(
                                ReportEvent.DOCUMENT_INDEXING_REMOVE_SUCCESS.getMessageCode());
                    }
                    indexingEvent.setTimestamp(System.currentTimeMillis());

                    component.log(indexingEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        } else {
            if (Boolean.valueOf(
                    component.getSystemConfig(SystemConfigKey.ENABLE_DOCUMENT_INDEXING_FAILED_LOG))) {
                try {
                    Event indexingEvent = new Event();
                    indexingEvent.setStage(EventStage.FILE_INDEXING);
                    indexingEvent.setFileId(fileId);
                    indexingEvent.setRepositoryType(repositoryType);
                    indexingEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    indexingEvent.setCategory(EventCategory.OPERATION);
                    indexingEvent.setStatus(status);
                    if (NextLabsConstant.ACTION_ADD.equals(action)) {
                        indexingEvent.setMessageCode(ReportEvent.DOCUMENT_INDEXING_ADD_FAIL.getMessageCode());
                    } else if (NextLabsConstant.ACTION_UPDATE.equals(action)) {
                        indexingEvent.setMessageCode(
                                ReportEvent.DOCUMENT_INDEXING_UPDATE_FAIL.getMessageCode());
                    } else {
                        indexingEvent.setMessageCode(
                                ReportEvent.DOCUMENT_INDEXING_REMOVE_FAIL.getMessageCode());
                    }
                    if (errorMessage != null && errorMessage.length > 0) {
                        indexingEvent.addMessageParam(errorMessage[0]);
                    }
                    indexingEvent.setTimestamp(System.currentTimeMillis());

                    component.log(indexingEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        }
    }
}
