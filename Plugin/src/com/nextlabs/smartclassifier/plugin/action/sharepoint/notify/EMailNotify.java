package com.nextlabs.smartclassifier.plugin.action.sharepoint.notify;

import com.nextlabs.smartclassifier.constant.ActionResult;
import com.nextlabs.smartclassifier.constant.Punctuation;
import com.nextlabs.smartclassifier.constant.SolrDynamicField;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;
import com.nextlabs.smartclassifier.mail.Mail;
import com.nextlabs.smartclassifier.mail.MailRecipient;
import com.nextlabs.smartclassifier.mail.MailService;
import com.nextlabs.smartclassifier.plugin.action.ActionOutcome;
import com.nextlabs.smartclassifier.plugin.action.ExecuteOncePerRule;
import com.nextlabs.smartclassifier.plugin.action.SharePointAction;
import com.nextlabs.smartclassifier.solr.QueryEngine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CursorMarkParams;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pkalra on 11/21/2016.
 */
public class EMailNotify extends SharePointAction implements ExecuteOncePerRule {

    private static final Logger logger = LogManager.getLogger(EMailNotify.class);
    private static final String ACTION_NAME = "EMAIL_NOTIFICATION (SHAREPOINT)";
    private Path snapShotsFile;
    private String snapshotFileName;
    private HttpSolrClient client = null;

    public EMailNotify() {
        super(ACTION_NAME);
    }

    @Override
    public ActionOutcome execute(final String solrQuery) throws Exception {

        ActionOutcome actionOutcome = new ActionOutcome();
        actionOutcome.setResult(ActionResult.FAIL);
        actionOutcome.setMessage("Email plugin initialized.");

        String targetFolder = getParameterByKey("snapshots-folder");

        logger.debug(ACTION_NAME + ": The snapshots folder = " + targetFolder);

        try {

            logger.debug(ACTION_NAME + ": Trying to get the list of matching documents to be added to snapshot file..");

            String indexerURL = getSystemConfigs().get(SystemConfigKey.INDEXER_URL);
            String userName = getSystemConfigs().get(SystemConfigKey.INDEXER_USERNAME);
            String passWord = getSystemConfigs().get(SystemConfigKey.INDEXER_PASSWORD);

            if (StringUtils.isNotBlank(indexerURL)) {

            	logger.debug("The indexer URL found is = " + indexerURL);

    			QueryEngine queryEngine = new QueryEngine(indexerURL, userName, passWord);
    			
    			client = queryEngine.getHttpSolrClient();

    			long totalDocs = queryEngine.getDocumentCount(solrQuery);
    			
    			logger.info ("Query is : " + solrQuery);

    			logger.debug("The number of documents found = "+ totalDocs);
    			
    			if (totalDocs > 0) {
                    SolrDocumentList documentList;

                    createSnapshotFile(getRuleId());
                    
                    SolrQuery query = new SolrQuery();
					query.setFields("file_id folder_url document_name site_url serverrelativeurl_t file_type author last_author creation_date last_modified_date");
					query.setQuery(solrQuery);
					query.setRows(1000);
					query.setSort(SolrPredefinedField.FILE_ID, ORDER.asc);
					
					String cursorMark = CursorMarkParams.CURSOR_MARK_START;
					boolean done = false;
					long numberOfDocumentsFound = 0;
					
					while (! done) {
						
						  query.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
						  
						  QueryRequest req = new QueryRequest(query);
					      req.setBasicAuthCredentials(userName, passWord);
					      QueryResponse response = req.process(client);
						  
						  String nextCursorMark = response.getNextCursorMark();
						  					  
						  documentList = response.getResults();
						  	
						  numberOfDocumentsFound = documentList.size();
						  
						  logger.info("Number of document found is " + numberOfDocumentsFound);
							
						  appendToSnapshotFile(documentList);

						  if (cursorMark.equals(nextCursorMark)) {
						    done = true;
						  }
						  
						  cursorMark = nextCursorMark;
					}

                    File targetFile = new File(targetFolder, snapshotFileName);

                    FileUtils.moveFile(snapShotsFile.toFile(), targetFile);

                    logger.debug("File moved from  " + snapShotsFile.toFile() + " to => " + targetFile);

                    if(sendEmail()) {
                        actionOutcome.setResult(ActionResult.SUCCESS);
                        actionOutcome.setMessage("Email successfully sent");
                    } else {
                        actionOutcome.setResult(ActionResult.FAIL);
                        actionOutcome.setMessage("Email sending failed");
                    }

                } else {
                    logger.info(ACTION_NAME + ": Not sending the email since no matching documents have been found!");
                    actionOutcome.setResult(ActionResult.FAIL);
                    actionOutcome.setMessage("No documents found");
                }
            } else {
                logger.error(ACTION_NAME + ": The indexer URL found in the database = " + indexerURL);
                actionOutcome.setResult(ActionResult.FAIL);
                actionOutcome.setMessage("The indexer URL = " + indexerURL);
            }

        } catch (Exception err) {
            logger.error(ACTION_NAME + ": " + err.getMessage(), err);

            actionOutcome.setResult(ActionResult.FAIL);
            actionOutcome.setMessage("Email send failed. " + err.getMessage());
        }
        finally {
        	if (client!=null)
        		client.close();
        }

        return actionOutcome;

    }

    private void appendToSnapshotFile(SolrDocumentList documentList) throws IOException {

        List<String> lines = new ArrayList<>();

        if (documentList != null && documentList.size() > 0) {

            logger.debug(ACTION_NAME + ": Appending " + documentList.size() + " documents to the snapshot file!");

            StringBuilder stringBuilder;
            for (SolrDocument solrDocument : documentList) {
                stringBuilder = new StringBuilder("");
                //file_id folder_url document_name site_url serverrelativeurl_t file_type author last_author creation_date last_modified_date
                stringBuilder.append(solrDocument.get(SolrPredefinedField.FILE_ID));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrPredefinedField.FOLDER_URL));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrPredefinedField.DOCUMENT_NAME));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrPredefinedField.SITE_URL));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrDynamicField.SERVER_RELATIVE_URL));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrPredefinedField.FILE_TYPE));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrPredefinedField.AUTHOR));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrPredefinedField.LAST_AUTHOR));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrPredefinedField.CREATION_DATE));
                stringBuilder.append(Punctuation.COMMA);
                stringBuilder.append(solrDocument.get(SolrPredefinedField.LAST_MODIFIED_DATE));
                lines.add(stringBuilder.toString());
            }

            Files.write(snapShotsFile, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } else {
            logger.debug(ACTION_NAME + ": No more documents found to append to snapshot file!");
        }

    }

    private void createSnapshotFile(Long ruleID) throws IOException {

        Path snapShotsFolder = Paths.get("")
                .toAbsolutePath()
                .getParent()
                .resolve("snapshots");

        logger.info(ACTION_NAME + ": The snapshots folder path = " + snapShotsFolder);

        snapshotFileName = "R" + ruleID + "_T" + System.currentTimeMillis() + ".csv";

        logger.info(ACTION_NAME + ": The snapshot file-name = " + snapshotFileName);

        snapShotsFile = snapShotsFolder.resolve(snapshotFileName);

        logger.info(ACTION_NAME + ": The snapshot file-path = " + snapShotsFile);
        //id directory document_name file_type author last_author creation_date last_modified_date

        Files.createDirectories(snapShotsFile.getParent());

        ArrayList<String> lines = new ArrayList<>();
        //file_id folder_url document_name site_url serverrelativeurl_t file_type author last_author creation_date last_modified_date
        lines.add("file_id,folder_url,document_name,site_url,serverrelativeurl,file_type,author,last_author,creation_date,last_modified_date");
        lines.add("");
        Files.write(snapShotsFile, lines, Charset.forName("UTF-8"));

        logger.debug(ACTION_NAME + ": Created the snapshot file " + snapShotsFile);
    }

    private boolean sendEmail() throws Exception {

        Mail email = new Mail();

        String subject = getParameterByKey("email-subject");
        String body = getParameterByKey("email-content");
        Set<String> emailRecipientSet = getParametersByKey("email-recipient");

        String url = getSystemConfigs().get(SystemConfigKey.SMART_CLASSIFIER_URL) + "download?id=";

        url += snapshotFileName;

        logger.debug(ACTION_NAME + ": The download URL for the snapshot = " + url);

        email.setSubject(subject);

        String emailBody = body + "<hr style=\"border-top: dotted 1px;\" />" +
                "<p>Please click this <a href=\"" + url + "\">link</a> to download the file, containing the documents found.</p>";

        email.setContent(emailBody);
        email.setContentType("text/html");

        List<MailRecipient> emailAddresses = new ArrayList<>();
        for (String recipient : emailRecipientSet) {
            emailAddresses.add(new MailRecipient(Message.RecipientType.TO, new InternetAddress(recipient)));
        }

        logger.debug("Recipient List = " + emailRecipientSet);

        email.setRecipients(emailAddresses);

        if (MailService.sendMail(email)) {
            logger.info("Email sent successfully");
            return true;
        } else {
            logger.error("Email sending failed");
        }
        return false;
    }
}
