package com.nextlabs.smartclassifier.manager;

import com.nextlabs.smartclassifier.DocumentContent;
import com.nextlabs.smartclassifier.constant.SCConstant;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.util.Converter;
import com.nextlabs.smartclassifier.util.SolrMetadataFieldUtil;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;	
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BaseHttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.UpdateRequest;	
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;	
import java.util.Map.Entry;

import static com.nextlabs.smartclassifier.constant.Punctuation.FORWARD_SLASH;

public class IndexManager
        implements SolrPredefinedField {

    private static final Logger logger = LogManager.getLogger(IndexManager.class);
    private static final SimpleDateFormat solrDateFormat = new SimpleDateFormat(SCConstant.SOLR_DATETIME_FORMAT);
    
    private HttpSolrClient server;
    private long retryInterval;
    private boolean shutdown;
    private Map<String, MetadataField> metadataFieldNameToMetadataFieldMap;
    private String userName;
    private String passWord;

    public IndexManager(String indexServerURL, long retryInterval, 
    		Map<String, MetadataField> metadataFieldNameToMetadataFieldMap, String userName, String passWord) 
    				throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        super();
        
        solrDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        if (indexServerURL == null || indexServerURL.length() == 0) {
            throw new IllegalArgumentException("Index server URL cannot be null or empty.");
        }
        
        this.userName = userName;
        this.passWord = passWord;
        
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        @SuppressWarnings("deprecation")
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder. 
                         <ConnectionSocketFactory> create()
                        .register("http", new PlainConnectionSocketFactory())
                        .register("https", sslsf)
                        .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(2000);
                
        
        CloseableHttpClient httpClient = HttpClients.custom()
                        .setSSLSocketFactory(sslsf)
                        .setConnectionManager(cm)
                        .build();
                
        this.server = new HttpSolrClient.Builder(indexServerURL).withHttpClient(httpClient).build();

        this.retryInterval = retryInterval;
        this.metadataFieldNameToMetadataFieldMap = metadataFieldNameToMetadataFieldMap;
        this.shutdown = false;
    }

    public void addDocument(DocumentContent documentContent)
            throws Exception {
        SolrInputDocument doc = new SolrInputDocument();

        try {
            int indexLocation = documentContent.getAbsoluteFilePath().lastIndexOf(FORWARD_SLASH) + 1;
            String directory = documentContent.getAbsoluteFilePath().substring(0, indexLocation);
            String documentName = documentContent.getAbsoluteFilePath().substring(indexLocation);

            doc.addField(ID, documentContent.getAbsoluteFilePath());
            doc.addField(FILE_ID, documentContent.getDocumentID());
            doc.addField(DIRECTORY, directory);
            doc.addField(DIRECTORY_LCASE, directory.toLowerCase());
            doc.addField(DOCUMENT_NAME, documentName);
            doc.addField(DOCUMENT_NAME_LCASE, documentName.toLowerCase());
            doc.addField(FILE_TYPE, documentContent.getFileType());
            doc.addField(REPOSITORY_TYPE, documentContent.getRepositoryType());
            doc.addField(REPO_PATH, documentContent.getRepoPath());
            doc.addField(HEADER, documentContent.getDocumentHeader());
            doc.addField(BODY, documentContent.getDocumentContent());
            doc.addField(FOOTER, documentContent.getDocumentFooter());
            if(documentContent.getLastModified() > 0) {
            	doc.addField(LAST_MODIFIED_DATE, solrDateFormat.format(new Date(documentContent.getLastModified())));
            	doc.addField(LAST_MODIFIED_DATE_MILLISECOND, documentContent.getLastModified());
            }
            // adding metadata here
            for (Entry<String, String> pair : documentContent.getDocumentMetadataKeyToValueMap().entrySet()) {
                Object value = Converter.convertToNative(pair.getValue());
                doc.addField(SolrMetadataFieldUtil.resolveFieldName(metadataFieldNameToMetadataFieldMap, pair.getKey(), value), value);
            }

            while (!shutdown) {
                try {
                    logger.info("Sending " + documentContent.getAbsoluteFilePath() + " to the indexer.");
                    UpdateRequest up = new UpdateRequest();
                    up.setBasicAuthCredentials(userName, passWord);
                    up.add(doc);
                    up.process(server);
                    break;
                } catch (SolrServerException e) {
                    logger.error(e.getMessage(), e);
                    logger.warn("Error adding document: " + documentContent.getAbsoluteFilePath() + " to index. Will try again in " + retryInterval / 1000.0 + " seconds.", e);
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException e1) {
                        logger.error("Interrupted while waiting for indexer.", e1);
                    }
                }
                catch (RemoteSolrException e) {
                    logger.error(e.getMessage(), e);
                    logger.warn("Error adding document: " + documentContent.getAbsoluteFilePath() + " to index. Will try again in " + retryInterval / 1000.0 + " seconds.", e);
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException e1) {
                        logger.error("Interrupted while waiting for indexer.", e1);
                    }
                }
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw err;
        }
    }

    public void deleteDocument(String absoluteDocumentPath)
            throws Exception {
        while (!shutdown) {
            try {
            	
            	UpdateRequest up = new UpdateRequest();
                up.setBasicAuthCredentials(userName, passWord);
                up.deleteById(absoluteDocumentPath);
                up.process(server);
                break;
            } catch (SolrServerException e) {
                logger.warn("Error delete document: " + absoluteDocumentPath + " from index. Will try again in " + retryInterval / 1000.0 + " seconds.", e);
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e1) {
                    logger.error("Interrupted while waiting for indexer.", e1);
                }
            } catch (RemoteSolrException e) {
                logger.warn("Error delete document: " + absoluteDocumentPath + " from index. Will try again in " + retryInterval / 1000.0 + " seconds.", e);
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e1) {
                    logger.error("Interrupted while waiting for indexer.", e1);
                }
            }
        }
    }

    public void commit()
            throws SolrServerException, IOException {
        server.commit();
    }

    public void shutdown() {
        this.shutdown = true;
    }
}
