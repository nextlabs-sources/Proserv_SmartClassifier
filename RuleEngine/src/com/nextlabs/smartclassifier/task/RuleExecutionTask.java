package com.nextlabs.smartclassifier.task;

import com.nextlabs.smartclassifier.base.Component;
import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.EventCategory;
import com.nextlabs.smartclassifier.constant.EventStage;
import com.nextlabs.smartclassifier.constant.EventStatus;
import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.constant.ReportEvent;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.constant.RollbackErrorType;
import com.nextlabs.smartclassifier.constant.RuleExecutionOutcome;
import com.nextlabs.smartclassifier.constant.RuleExecutionStatus;
import com.nextlabs.smartclassifier.constant.SolrDynamicField;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.database.entity.RollbackError;
import com.nextlabs.smartclassifier.database.entity.RuleExecution;
import com.nextlabs.smartclassifier.database.manager.RuleExecutionManager;
import com.nextlabs.smartclassifier.dto.Event;
import com.nextlabs.smartclassifier.dto.SourceAuthenticationDTO;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RollbackException;
import com.nextlabs.smartclassifier.plugin.ActionManager;
import com.nextlabs.smartclassifier.plugin.action.Action;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerFile;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerRule;
import com.nextlabs.smartclassifier.solr.QueryEngine;
import com.nextlabs.smartclassifier.solr.SolrDocumentInfo;
import com.nextlabs.smartclassifier.util.FileUtil;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;
import com.nextlabs.smartclassifier.util.RepositoryUtil;
import com.nextlabs.smartclassifier.util.SharePointUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CursorMarkParams;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.LockAcquisitionException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RuleExecutionTask
        implements Runnable {

    private static final Logger logger = LogManager.getLogger(RuleExecutionTask.class);
    private static final int QUERY_BLOCK_SIZE = 100;

    private Component component;
    private RuleExecution ruleExecution;
    private ExecutionType executionType;
    private boolean success;
    private boolean fail;
    private boolean documentNotFound;
    private String errMessage;

    public RuleExecutionTask(Component component, RuleExecution ruleExecution) {
        super();
        this.component = component;
        this.ruleExecution = ruleExecution;
        this.executionType = ExecutionType.getType(ruleExecution.getType());
    }

    @Override
    public void run() {
        if (ruleExecution != null) {
            try {
                // Wait for execution window
                while (ruleExecution.getType().equalsIgnoreCase(ExecutionType.SCHEDULED.getCode())
                        && !component.isWithinExecutionWindow()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException err) {
                        logger.error(err.getMessage(), err);
                    }
                }

                // inside the execution windows now
                updateRuleExecutionStatus(RuleExecutionStatus.EXECUTING, null);
                updateRuleExecutionStatus(RuleExecutionStatus.COMPLETED, execute(ruleExecution));

                if (ExecutionType.SCHEDULED.getCode().equals(ruleExecution.getType())) {
                    logger.info("Scheduled task " + ruleExecution.getId() + " completed.");
                } else {
                    logger.info("On-demand task " + ruleExecution.getId() + " completed.");
                }
            } catch (Exception err) {
                logger.error(err.getMessage(), err);
                updateRuleExecutionStatus(RuleExecutionStatus.COMPLETED, RuleExecutionOutcome.FAILED);
            }
        }
    }

    private synchronized void updateRuleExecutionStatus(RuleExecutionStatus status, RuleExecutionOutcome outcome) {
        boolean success = false;
        int retry = 0;
        while (!success && retry < 10) {
            Session session = null;
            Transaction transaction = null;

            try {
                session = component.getSessionFactory().openSession();
                transaction = session.beginTransaction();

                (new RuleExecutionManager(component.getSessionFactory(), session)).updateRuleExecutionStatus(ruleExecution, status, outcome);

                transaction.commit();
                success = true;
            } catch (LockAcquisitionException err) {
                if (transaction != null) {
                    try {
                        transaction.rollback();
                    } catch (Exception rollbackErr) {
                        logger.error(rollbackErr.getMessage(), rollbackErr);
                    }
                }
                try {
                    int sleepTime = ((int) (Math.random() * 10) + 1) * 100;
                    Thread.sleep(sleepTime);
                    retry++;
                } catch (InterruptedException err1) {
                    // Ignore
                }
            } catch (ManagerException | Exception err) {
                if (transaction != null) {
                    try {
                        transaction.rollback();
                    } catch (Exception rollbackErr) {
                        logger.error(rollbackErr.getMessage(), rollbackErr);
                    }
                }
                success = true;
                logger.error(err.getMessage(), err);
            } finally {
                if (session != null) {
                    try {
                        session.close();
                    } catch (HibernateException err) {
                        logger.error(err.getMessage(), err);
                    }
                }
            }
        }
    }

    private RuleExecutionOutcome execute(RuleExecution ruleExecution) {

        boolean isOnlyEmailAction = false;
        File backupFile = null;
        CloseableHttpClient httpClient = null;

        try {
            String workstation = InetAddress.getLocalHost().getHostName();

            logRuleExecutionEvent(true);

            RepositoryType repositoryType = RepositoryType.getRepositoryType(ruleExecution.getRule().getRepositoryType());

            List<Action> ruleActions = ActionManager.getActions(ruleExecution.getRule(), component.getSystemConfigs(), component.getMetadataFieldByName());
            logger.debug(ruleActions);

            if (ruleActions != null) {
            	HttpSolrClient solrServer = new HttpSolrClient.Builder(component.getSystemConfig(SystemConfigKey.INDEXER_URL)).build();
            	String userName = component.getSystemConfig(SystemConfigKey.INDEXER_USERNAME);
            	String passWord = component.getSystemConfig(SystemConfigKey.INDEXER_PASSWORD);
                SolrQuery solrQuery = createSolrQuery(ruleExecution);
                Map<Long, ActionOutcome> fireOnceActions = new HashMap<>();
                String cursorMark = CursorMarkParams.CURSOR_MARK_START;
                boolean done = false;

                while (!done) { // done changes to true when cursor mark reaches the end

                    solrQuery.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
                    
                    QueryRequest req = new QueryRequest(solrQuery);
				    req.setBasicAuthCredentials(userName, passWord);
				    QueryResponse response = req.process(solrServer);
                    SolrDocumentList solrDocumentList = response.getResults();

                    String nextCursorMark = response.getNextCursorMark();

                    // Set action outcome as success when no matching document is found.
                    // first time check
                    if (CursorMarkParams.CURSOR_MARK_START.equals(cursorMark) && solrDocumentList.size() == 0) {
                        logger.debug("No file match for this rule.");
                        documentNotFound = true;
                        success = true;
                    }

                    if (ruleActions.size() == 1) {
                        Action action = ruleActions.get(0);
                        if (action.getDisplayName().equalsIgnoreCase("Email Notification")) {
                            isOnlyEmailAction = true;
                        }
                    }

                    // based on whether it is sharepoint or normal document, change your actions
                    if (repositoryType == RepositoryType.SHARED_FOLDER) {
                        for (SolrDocument solrDocument : solrDocumentList) {
                            try {
                                String absolutePath = (String) solrDocument.get(SolrPredefinedField.ID);

                                if (!isDocumentUpToDate(solrDocument)) {
                                    logger.warn("Skip outdated file " + absolutePath + " for rule [" + ruleExecution.getRule().getName() + "] execution.");
                                    continue;
                                }

                                boolean isFileRestored = true;
                                boolean rollBackOccur = false;

                                if (!isOnlyEmailAction) {
                                    backupFile = createBackupFile(absolutePath);
                                }

                                if (isOnlyEmailAction || (backupFile != null && backupFile.exists())) {

                                    List<Event> actionEvents = new LinkedList<>();
                                    Deque<Action> performedActions = new ArrayDeque<>();

                                    for (Action ruleAction : ruleActions) {

                                        // Skip fire once actions
                                        if (ruleAction.isFireOncePerRule()
                                                && fireOnceActions.containsKey(ruleAction.getId())) {
                                            Event actionEvent = getActionExecutionEvent(ruleAction,
                                                    (String) solrDocument.get(SolrPredefinedField.FILE_ID),
                                                    ruleExecution.getRule().getRepositoryType(),
                                                    absolutePath, fireOnceActions.get(ruleAction.getId()));

                                            if (actionEvent != null) {
                                                actionEvents.add(actionEvent);
                                            }

                                            continue;
                                        }

                                        ActionOutcome actionOutcome = null;

                                        try {
                                            if (ruleAction instanceof ExecuteOncePerFile) {
                                                actionOutcome = ((ExecuteOncePerFile) ruleAction).execute(solrDocument);
                                            } else if (ruleAction instanceof ExecuteOncePerRule) {
                                                actionOutcome = ((ExecuteOncePerRule) ruleAction).execute(
                                                        QueryEngine.getQueryString(ruleAction.getMetadataFieldByName(),
                                                                ruleExecution.getRule().getCriteriaGroups(),
                                                                ruleExecution.getRule().getRepositoryType()));
                                            }
                                            performedActions.add(ruleAction); // stores successful actions

                                            if (actionOutcome != null) {
                                                if (ActionResult.SUCCESS.equals(actionOutcome.getResult())) {
                                                    success = true;
                                                } else {
                                                    fail = true;
                                                }
                                            } else {
                                                throw new Exception("ActionOutcome is null from action [" + ruleAction.getDisplayName() + "].");
                                            }

                                            // Add into fire once set after action triggered
                                            if (ruleAction.isFireOncePerRule()) {
                                                fireOnceActions.put(ruleAction.getId(), actionOutcome);
                                            }
                                        } catch (Exception err) {
                                            logger.error(err.getMessage(), err);

                                            if (actionOutcome == null) {
                                                actionOutcome = new ActionOutcome();
                                            }
                                            actionOutcome.setResult(ActionResult.FAIL);
                                            actionOutcome.setMessage(err.getMessage());
                                            fail = true;
                                        }

                                        Event actionEvent = getActionExecutionEvent(ruleAction,
                                                (String) solrDocument.get(SolrPredefinedField.FILE_ID),
                                                ruleExecution.getRule().getRepositoryType(),
                                                absolutePath, actionOutcome);

                                        if (actionEvent != null) {
                                            actionEvents.add(actionEvent);
                                        }

                                        // Rollback performed ruleActions if current action has failed
                                        if (!ActionResult.SUCCESS.equals(actionOutcome.getResult()) && !isOnlyEmailAction) {
                                        /* Incase of error
                                        * 1. rollback actions
                                        * 2. recover file
                                        * */
                                            rollBackOccur = true;

                                            logger.error("Rolling back file " + absolutePath);

                                            Iterator<Action> iterator = performedActions.descendingIterator();

                                            while (iterator.hasNext()) {
                                                Action action = iterator.next();

                                                try {
                                                    action.rollback();
                                                } catch (RollbackException err) {
                                                    logRollbackError(err.getBackupAbsoluteFilePath(), err.getTargetAbsoluteFilePath(),
                                                            err.getRollbackErrorType().getCode(), err.getMessage(), action);
                                                }
                                            }

                                            isFileRestored = restoreOriginalFile(backupFile, absolutePath);

                                            break; // do not perform subsequent actions in case of failure
                                        }
                                    }

                                    logActionExecutionEvent(actionEvents, rollBackOccur);

                                    if (!isOnlyEmailAction) {
                                        if (isFileRestored) { // can remove the backup file

                                            logger.debug("Trying to delete the backup file at " + backupFile.getAbsolutePath());

                                            try {
                                                backupFile.delete();
                                            } catch (Exception err) {
                                                logger.error(err.getMessage(), err);
                                                logRollbackError(null, backupFile.getAbsolutePath(),
                                                        RollbackErrorType.DELETE_FAILED.getCode(), err.getMessage(), null);

                                            }
                                        } else {
                                            logger.warn("Cannot delete the backup file " + backupFile.getAbsolutePath() + " since the original file " + absolutePath + " was not restored back!");
                                        }
                                    }
                                } else {
                                    logger.warn("Could not create the backup file for " + absolutePath);
                                }
                            } catch (Exception err) {
                                logger.error(err.getMessage(), err);
                            }
                        }
                    } else if (repositoryType == RepositoryType.SHAREPOINT) {

                        for (SolrDocument solrDocument : solrDocumentList) {

                            try {
                                String absolutePath = (String) solrDocument.get(SolrPredefinedField.ID);

                                if (!isDocumentUpToDate(solrDocument)) {
                                    logger.warn("Skip outdated file " + absolutePath + " for rule [" + ruleExecution.getRule().getName() + "] execution.");
                                    continue;
                                }

                                String srURL = (String) solrDocument.get(SolrDynamicField.SERVER_RELATIVE_URL);
                                String siteURL = (String) solrDocument.get(SolrPredefinedField.SITE_URL);
                                String repoPath = (String) solrDocument.get(SolrPredefinedField.REPO_PATH);
                                if (StringUtils.isBlank(srURL) || StringUtils.isBlank(siteURL) || StringUtils.isBlank(repoPath)) {
                                    throw new Exception("The serverRelativeURL = " + srURL + ", siteURL = " + siteURL + ", repoPath = " + repoPath + " for " + absolutePath);
                                }
                                SourceAuthenticationDTO credentials = RepositoryUtil.getSourceAuthentication(repoPath);

                                if (credentials != null) {

                                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                                    credentialsProvider.setCredentials(
                                            new AuthScope(AuthScope.ANY),
                                            new NTCredentials(credentials.getUserName(), NxlCryptoUtil.decrypt(credentials.getPassword()), workstation, credentials.getDomain()));

                                    httpClient = HttpClients.custom()
                                            .setDefaultCredentialsProvider(credentialsProvider)
                                            .build();

                                    logger.debug("Testing the connection for " + srURL);

                                    if (!SharePointUtil.testConnection(httpClient, siteURL)) {
                                        logger.warn("Connection failed for " + srURL);

                                        httpClient.close();

                                        RepositoryUtil.reloadRepositories();
                                        credentials = RepositoryUtil.getSourceAuthentication(repoPath);

                                        credentialsProvider = new BasicCredentialsProvider();
                                        credentialsProvider.setCredentials(
                                                new AuthScope(AuthScope.ANY),
                                                new NTCredentials(credentials.getUserName(), NxlCryptoUtil.decrypt(credentials.getPassword()), workstation, credentials.getDomain()));
                                        httpClient = HttpClients.custom()
                                                .setDefaultCredentialsProvider(credentialsProvider)
                                                .build();
                                        //httpClient = HTTPClientUtil.getHTTPClient(sa.getUserName(), NxlCryptoUtil.decrypt(sa.getPassword()), sa.getDomain());
                                    }

                                    boolean isFileRestored = true;
                                    boolean rollBackOccur = false;

                                    SolrDocumentInfo backupFileInfo = new SolrDocumentInfo();

                                    if (isOnlyEmailAction || createBackupFile(httpClient, solrDocument, backupFileInfo)) {

                                        List<Event> actionEvents = new LinkedList<>();
                                        Deque<Action> performedActions = new ArrayDeque<>();

                                        for (Action ruleAction : ruleActions) {

                                            // Skip fire once actions
                                            if (ruleAction.isFireOncePerRule()
                                                    && fireOnceActions.containsKey(ruleAction.getId())) {
                                                Event actionEvent = getActionExecutionEvent(ruleAction,
                                                        (String) solrDocument.get(SolrPredefinedField.FILE_ID),
                                                        ruleExecution.getRule().getRepositoryType(),
                                                        absolutePath, fireOnceActions.get(ruleAction.getId()));

                                                if (actionEvent != null) {
                                                    actionEvents.add(actionEvent);
                                                }

                                                continue;
                                            }

                                            ActionOutcome actionOutcome = null;

                                            try {
                                                if (ruleAction instanceof ExecuteOncePerFile) {
                                                    actionOutcome = ((ExecuteOncePerFile) ruleAction).execute(solrDocument);
                                                } else if (ruleAction instanceof ExecuteOncePerRule) {
                                                    actionOutcome = ((ExecuteOncePerRule) ruleAction).execute(
                                                            QueryEngine.getQueryString(ruleAction.getMetadataFieldByName(),
                                                                    ruleExecution.getRule().getCriteriaGroups(),
                                                                    ruleExecution.getRule().getRepositoryType()));
                                                }
                                                performedActions.add(ruleAction); // stores successful actions

                                                if (actionOutcome != null) {
                                                    if (ActionResult.SUCCESS.equals(actionOutcome.getResult())) {
                                                        success = true;
                                                    } else {
                                                        fail = true;
                                                    }
                                                } else {
                                                    throw new Exception("ActionOutcome is null from action [" + ruleAction.getDisplayName() + "].");
                                                }

                                                // Add into fire once set after action triggered
                                                if (ruleAction.isFireOncePerRule()) {
                                                    fireOnceActions.put(ruleAction.getId(), actionOutcome);
                                                }
                                            } catch (Exception err) {
                                                logger.error(err.getMessage(), err);

                                                if (actionOutcome == null) {
                                                    actionOutcome = new ActionOutcome();
                                                }
                                                actionOutcome.setResult(ActionResult.FAIL);
                                                actionOutcome.setMessage(err.getMessage());
                                                fail = true;
                                            }

                                            Event actionEvent = getActionExecutionEvent(ruleAction,
                                                    (String) solrDocument.get(SolrPredefinedField.FILE_ID),
                                                    ruleExecution.getRule().getRepositoryType(),
                                                    absolutePath, actionOutcome);

                                            if (actionEvent != null) {
                                                actionEvents.add(actionEvent);
                                            }

                                            // Rollback performed ruleActions if current action has failed
                                            if (!ActionResult.SUCCESS.equals(actionOutcome.getResult()) && !isOnlyEmailAction) {
                                        /* Incase of error
                                        * 1. rollback actions
                                        * 2. recover file
                                        * */
                                                rollBackOccur = true;

                                                logger.error("Rolling back actions on the file at " + backupFileInfo.getServerRelativeURL());

                                                Iterator<Action> iterator = performedActions.descendingIterator();

                                                while (iterator.hasNext()) {
                                                    Action action = iterator.next();

                                                    try {
                                                        action.rollback();
                                                    } catch (RollbackException err) {
                                                        logRollbackError(err.getBackupAbsoluteFilePath(), err.getTargetAbsoluteFilePath(),
                                                                err.getRollbackErrorType().getCode(), err.getMessage(), action);
                                                    }
                                                }

                                                isFileRestored = restoreOriginalFile(httpClient, backupFileInfo, solrDocument);

                                                break; // do not perform subsequent actions in case of failure
                                            }
                                        }

                                        logActionExecutionEvent(actionEvents, rollBackOccur);

                                        if (!isOnlyEmailAction) {
                                            if (isFileRestored) { // can remove the backup file
                                                try {
                                                    deleteBackUpFile(httpClient, backupFileInfo);
                                                } catch (Exception err) {
                                                    logger.error(err.getMessage(), err);
                                                    logRollbackError(null, backupFileInfo.getServerRelativeURL(),
                                                            RollbackErrorType.DELETE_FAILED.getCode(), err.getMessage(), null);
                                                }
                                            } else {
                                                logger.warn("Cannot delete the backup file " + backupFileInfo.getServerRelativeURL() + " since the original file + " + solrDocument.get(SolrDynamicField.SERVER_RELATIVE_URL) + " was not restored back!");
                                            }
                                        }

                                    } else {
                                        logger.warn("Backup file creation failed! Skipping actions on  " + srURL);
                                    }
                                } else {
                                    throw new Exception("Source authentication for " + srURL + " could not be found");
                                }

                            } catch (Exception err) {
                                logger.error(err.getMessage(), err);
                            } finally {
                                if (httpClient != null) {
                                    httpClient.close();
                                }
                            }
                        }
                    }

                    done = cursorMark.equals(nextCursorMark);
                    cursorMark = nextCursorMark;
                }
            } else {
                logger.error(executionType.getName() + " unable to resolve action for [" + ruleExecution.getRule().getName() + "].");
                errMessage = "Unable to resolve action for " + ruleExecution.getRule().getName() + ".";
            }
        } catch (Throwable err) {
            logger.error(err.getMessage(), err);
            errMessage = err.getCause().getMessage();
        }

        logRuleExecutionOutcomeEvent();
        logRuleExecutionEvent(false);

        if (success && !fail) {
            return RuleExecutionOutcome.SUCCESS;
        } else if (success && fail) {
            return RuleExecutionOutcome.PARTIAL_FAILED;
        }

        return RuleExecutionOutcome.FAILED;
    }

    private void deleteBackUpFile(CloseableHttpClient httpClient, SolrDocumentInfo backupFileInfo) throws Exception {

        String siteURL = backupFileInfo.getSiteURL();
        String srURL = backupFileInfo.getServerRelativeURL();

        logger.debug("Trying to delete the backup file at " + srURL);

        if (SharePointUtil.deleteFile(httpClient, siteURL, srURL)) {
            logger.debug("Successfully deleted the backup file at " + srURL);
        } else {
            logger.error("Could not delete the backup file at " + srURL);
        }
    }


    private SolrQuery createSolrQuery(RuleExecution ruleExecution) {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery("id:*");
        solrQuery.setFields(component.getSystemConfig(SystemConfigKey.SOLR_INDEXING_QUERY_RULE_ENGINE_FIELDS));
        solrQuery.setRows(QUERY_BLOCK_SIZE);
        solrQuery.setSort(SortClause.asc(SolrPredefinedField.ID));
        solrQuery.setFilterQueries(QueryEngine.getQueryString(component.getMetadataFieldByName(), ruleExecution.getRule().getCriteriaGroups(), ruleExecution.getRule().getRepositoryType()));

        return solrQuery;
    }

    /**
     * Check whether the solrDocument is updated to the latest version on the disc.
     *
     * @param solrDocument the solr Document
     * @return true if the solrDocument is updated to the latest version on the disc, false if it is out dated.
     */
    private boolean isDocumentUpToDate(SolrDocument solrDocument) {
        String absolutePath = (String) solrDocument.get(SolrPredefinedField.ID);
        File file = new File(absolutePath);

        if (file.exists()) {
            if (solrDocument.get(SolrPredefinedField.LAST_MODIFIED_DATE_MILLISECOND) != null) {
                if (file.lastModified() == ((Long) solrDocument.get(SolrPredefinedField.LAST_MODIFIED_DATE_MILLISECOND))) {
                    return true;
                } else {
                    logger.warn("Last modified time for " + absolutePath + " does not match! file.lastModified() = " + file.lastModified() + ", Indexer last modified = " + solrDocument.get(SolrPredefinedField.LAST_MODIFIED_DATE_MILLISECOND));
                    return false;
                }
            } else {
                logger.warn("Unable to retrieve last modified date (millisecond) for " + solrDocument.get(SolrPredefinedField.ID) + " from indexer.");
            }
        }

        return false;
    }


    /**
     * Creates a backup file for shared_folder repository type
     *
     * @param absolutePath the absolute path to the file
     * @return
     */
    private File createBackupFile(String absolutePath) {

        File backupFile = null;

        try {
            backupFile = new File(FileUtil.getBackupAbsolutePath(absolutePath));
            FileUtils.copyFile(new File(absolutePath), backupFile);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }

        return backupFile;
    }

    /**
     * Creates a backup file for SharePoint repository type
     *
     * @param solrDocument   the solrDocument containing the details of the document
     * @param backupFileInfo the object to store information about the backed up document
     * @return true if backup is successful, false otherwise
     */
    private boolean createBackupFile(CloseableHttpClient httpClient, SolrDocument solrDocument, SolrDocumentInfo backupFileInfo) {
        try {
            String fileName = (String) solrDocument.get(SolrPredefinedField.DOCUMENT_NAME);
            String siteURL = (String) solrDocument.get(SolrPredefinedField.SITE_URL);
            String srURL = (String) solrDocument.get(SolrDynamicField.SERVER_RELATIVE_URL);

            String backupFileName = fileName + FileUtil.BACKUP_FILE_EXTENSION;
            String backupSRURL = srURL.substring(0, srURL.lastIndexOf("/") + 1) + backupFileName;

            logger.debug("Trying to backup the file at " + srURL + " to => " + backupSRURL);

            if (SharePointUtil.copyFile(httpClient, siteURL, srURL, backupSRURL, true)) {

                logger.debug("Successfully backed up the file from  " + srURL + " to => " + backupSRURL);

                backupFileInfo.setDocumentName(backupFileName);
                backupFileInfo.setSiteURL(siteURL);
                backupFileInfo.setServerRelativeURL(backupSRURL);
                return true;
            } else {
                logger.error("Failed to backup the file " + srURL + " to => " + backupSRURL);
            }

        } catch (Exception e) {
            logger.error("Encountered an Error creating the backup file " + e.getMessage(), e);
        }
        return false;
    }

    /**
     * Recover original file from backup file
     *
     * @param backupFile
     * @param originalAbsolutePath
     */
    private boolean restoreOriginalFile(File backupFile, String originalAbsolutePath) {
        if (backupFile != null && backupFile.exists()) {
            try {
                File originalFile = new File(originalAbsolutePath);

                if (originalFile.exists()) {
                    FileUtils.deleteQuietly(originalFile);
                }

                FileUtils.copyFile(backupFile, originalFile);
                return true;
            } catch (IOException err) {
                logger.error(err.getMessage(), err);
                logRollbackError(backupFile.getAbsolutePath(), originalAbsolutePath,
                        RollbackErrorType.REPLACE_FAILED.getCode(), err.getMessage(), null);
            }
        } else {
            logger.error("File restoration failed. Backup file does not exist anymore.");
        }

        return false;
    }

    /**
     * @param backupFileInfo
     * @param solrDocument
     * @return
     */
    private boolean restoreOriginalFile(CloseableHttpClient httpClient, SolrDocumentInfo backupFileInfo, SolrDocument solrDocument) {

        // delete the original and copy the backup file
        String siteURL = (String) solrDocument.get(SolrPredefinedField.SITE_URL);
        String serverRelativeURL = (String) solrDocument.get(SolrDynamicField.SERVER_RELATIVE_URL);

        String backupFileSRURL = backupFileInfo.getServerRelativeURL();

        try {
            // copy file
            if (SharePointUtil.copyFile(httpClient, siteURL, backupFileSRURL, serverRelativeURL, true)) {
                logger.debug("Restored the original file from " + backupFileSRURL + " to => " + serverRelativeURL);
                return true;
            } else {
                logger.error("Could not restore the file from " + backupFileSRURL + " to => " + serverRelativeURL);
                throw new Exception("Could not restore the file from " + backupFileSRURL + " to => " + serverRelativeURL);
            }

        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            logRollbackError(backupFileSRURL, serverRelativeURL,
                    RollbackErrorType.REPLACE_FAILED.getCode(), err.getMessage(), null);

        }
        return false;
    }

    private void logRuleExecutionEvent(boolean isStart) {
        if (isStart) {
            if (Boolean.valueOf(component.getSystemConfig(SystemConfigKey.ENABLE_RULE_EXECUTION_START_LOG))) {
                try {
                    Event executionEvent = new Event();
                    executionEvent.setStage(EventStage.RULE_EXECUTION);
                    executionEvent.setRuleId(ruleExecution.getRule().getId());
                    executionEvent.setRuleName(ruleExecution.getRule().getName());
                    executionEvent.setRuleExecutionId(ruleExecution.getId());
                    executionEvent.setRuleExecutionType(ruleExecution.getType());
                    executionEvent.setCategory(EventCategory.OPERATION);
                    executionEvent.setStatus(EventStatus.SUCCESS);
                    executionEvent.setMessageCode(ReportEvent.RULE_EXECUTE_START.getMessageCode());
                    executionEvent.setTimestamp(System.currentTimeMillis());

                    component.log(executionEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        } else {
            if (Boolean.valueOf(component.getSystemConfig(SystemConfigKey.ENABLE_RULE_EXECUTION_END_LOG))) {
                try {
                    Event executionEvent = new Event();
                    executionEvent.setStage(EventStage.RULE_EXECUTION);
                    executionEvent.setRuleId(ruleExecution.getRule().getId());
                    executionEvent.setRuleName(ruleExecution.getRule().getName());
                    executionEvent.setRuleExecutionId(ruleExecution.getId());
                    executionEvent.setRuleExecutionType(ruleExecution.getType());
                    executionEvent.setCategory(EventCategory.OPERATION);
                    executionEvent.setStatus(EventStatus.SUCCESS);
                    executionEvent.setMessageCode(ReportEvent.RULE_EXECUTE_END.getMessageCode());
                    executionEvent.setTimestamp(System.currentTimeMillis());

                    component.log(executionEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        }
    }

    private void logRuleExecutionOutcomeEvent() {
        // Rule execution failed
        if (!success && fail) {
            if (Boolean.valueOf(component.getSystemConfig(SystemConfigKey.ENABLE_RULE_EXECUTION_FAILED_LOG))) {
                try {
                    Event outcomeEvent = new Event();
                    outcomeEvent.setStage(EventStage.RULE_EXECUTION);
                    outcomeEvent.setRuleId(ruleExecution.getRule().getId());
                    outcomeEvent.setRuleName(ruleExecution.getRule().getName());
                    outcomeEvent.setRuleExecutionId(ruleExecution.getId());
                    outcomeEvent.setRuleExecutionType(ruleExecution.getType());
                    outcomeEvent.setCategory(EventCategory.OPERATION);
                    outcomeEvent.setStatus(EventStatus.FAIL);
                    outcomeEvent.setMessageCode(ReportEvent.RULE_EXECUTE_FAIL.getMessageCode());
                    if (errMessage != null) {
                        outcomeEvent.addMessageParam(errMessage);
                    } else {
                        outcomeEvent.addMessageParam("Unknown.");
                    }
                    outcomeEvent.setTimestamp(System.currentTimeMillis());

                    component.log(outcomeEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        } else {
            if (Boolean.valueOf(component.getSystemConfig(SystemConfigKey.ENABLE_RULE_EXECUTION_SUCCESS_LOG))) {
                try {
                    Event outcomeEvent = new Event();
                    outcomeEvent.setStage(EventStage.RULE_EXECUTION);
                    outcomeEvent.setRuleId(ruleExecution.getRule().getId());
                    outcomeEvent.setRuleName(ruleExecution.getRule().getName());
                    outcomeEvent.setRuleExecutionId(ruleExecution.getId());
                    outcomeEvent.setRuleExecutionType(ruleExecution.getType());
                    outcomeEvent.setCategory(EventCategory.OPERATION);
                    outcomeEvent.setStatus(EventStatus.SUCCESS);
                    if (documentNotFound) {
                        outcomeEvent.setMessageCode(ReportEvent.RULE_EXECUTE_NO_MATCH.getMessageCode());
                    } else {
                        outcomeEvent.setMessageCode(ReportEvent.RULE_EXECUTE_SUCCESS.getMessageCode());
                    }
                    outcomeEvent.setTimestamp(System.currentTimeMillis());

                    component.log(outcomeEvent);
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        }
    }

    private Event getActionExecutionEvent(Action action, String fileId, String repositoryType, String absolutePath, ActionOutcome outcome) {
        Event actionEvent = null;

        if (ActionResult.SUCCESS.equals(outcome.getResult())) {
            if (Boolean.valueOf(component.getSystemConfig(SystemConfigKey.ENABLE_ACTION_EXECUTION_SUCCESS_LOG))) {
                try {
                    actionEvent = new Event();
                    actionEvent.setStage(EventStage.ACTION_EXECUTION);
                    actionEvent.setFileId(fileId);
                    actionEvent.setRepositoryType(repositoryType);
                    actionEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    actionEvent.setRuleId(ruleExecution.getRule().getId());
                    actionEvent.setRuleName(ruleExecution.getRule().getName());
                    actionEvent.setRuleExecutionId(ruleExecution.getId());
                    actionEvent.setRuleExecutionType(ruleExecution.getType());
                    actionEvent.setActionPluginId(action.getPluginId());
                    actionEvent.setActionName(action.getDisplayName());
                    actionEvent.setActionId(action.getId());
                    actionEvent.setCategory(EventCategory.OPERATION);
                    actionEvent.setStatus(EventStatus.SUCCESS);
                    actionEvent.setMessageCode(ReportEvent.ACTION_APPLY_SUCCESS.getMessageCode());
                    actionEvent.setTimestamp(System.currentTimeMillis());
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        } else {
            if (Boolean.valueOf(component.getSystemConfig(SystemConfigKey.ENABLE_ACTION_EXECUTION_FAILED_LOG))) {
                try {
                    actionEvent = new Event();
                    actionEvent.setStage(EventStage.ACTION_EXECUTION);
                    actionEvent.setFileId(fileId);
                    actionEvent.setRepositoryType(repositoryType);
                    actionEvent.setAbsolutePath(absolutePath, RepositoryType.getRepositoryType(repositoryType));
                    actionEvent.setRuleId(ruleExecution.getRule().getId());
                    actionEvent.setRuleName(ruleExecution.getRule().getName());
                    actionEvent.setRuleExecutionId(ruleExecution.getId());
                    actionEvent.setRuleExecutionType(ruleExecution.getType());
                    actionEvent.setActionPluginId(action.getPluginId());
                    actionEvent.setActionName(action.getDisplayName());
                    actionEvent.setActionId(action.getId());
                    actionEvent.setCategory(EventCategory.OPERATION);
                    actionEvent.setStatus(EventStatus.FAIL);
                    actionEvent.setMessageCode(ReportEvent.ACTION_APPLY_FAIL.getMessageCode());
                    actionEvent.addMessageParam(outcome.getMessage());
                    actionEvent.setTimestamp(System.currentTimeMillis());
                } catch (Exception err) {
                    logger.error(err.getMessage(), err);
                }
            }
        }

        return actionEvent;
    }

    private void logActionExecutionEvent(Collection<Event> events, boolean rollbackOccur) {
        if (events != null) {
            for (Event event : events) {
                if (rollbackOccur
                        && EventStatus.SUCCESS.equals(event.getStatus())) {
                    if (StringUtils.isNotBlank(event.getActionName())
                            && event.getActionName().toLowerCase().contains("email")) {
                        continue;
                    } else {
                        event.setStatus(EventStatus.ROLLBACK);
                        event.setMessageCode(ReportEvent.ACTION_APPLY_ROLLBACK.getMessageCode());
                    }
                }

                component.log(event);
            }
        }
    }

    private void logRollbackError(String backupFilePath, String targetFilePath, String errorType, String message, Action action) {
        try {
            RollbackError rollbackError = new RollbackError();

            rollbackError.setRepositoryType(ruleExecution.getRule().getRepositoryType());
            rollbackError.setBackupAbsoluteFilePath(backupFilePath == null ? "" : backupFilePath);
            rollbackError.setTargetAbsoluteFilePath(targetFilePath);
            rollbackError.setRuleId(ruleExecution.getRule().getId());
            rollbackError.setRuleName(ruleExecution.getRule().getName());
            rollbackError.setRuleExecutionId(ruleExecution.getId());
            rollbackError.setRuleExecutionType(ruleExecution.getType());
            rollbackError.setErrorType(errorType);
            rollbackError.setMessage(message);

            if (action != null) {
                rollbackError.setActionPluginId(action.getPluginId());
                rollbackError.setActionName(action.getName());
                rollbackError.setActionId(action.getId());
            }

            rollbackError.setTimestamp(System.currentTimeMillis());
            component.log(rollbackError);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }
    }
}