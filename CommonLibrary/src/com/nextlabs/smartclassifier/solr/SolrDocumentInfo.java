package com.nextlabs.smartclassifier.solr;

import com.nextlabs.smartclassifier.constant.SolrDynamicField;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

/**
 * Created by pkalra on 3/1/2017.
 */
public class SolrDocumentInfo {

    private String documentPath;
    private String documentName;
    private String siteURL;
    private String serverRelativeURL;
    private String directory;
    private String repoPath;
    private String folderURL;
    private String file_id;

    private static final Logger LOGGER = LogManager.getLogger(SolrDocumentInfo.class);

    public SolrDocumentInfo() {

    }

    public SolrDocumentInfo(SolrDocument solrDocument) {
        this.documentPath = (String) solrDocument.get(SolrPredefinedField.ID);
        this.documentName = (String) solrDocument.get(SolrPredefinedField.DOCUMENT_NAME);
        this.siteURL = (String) solrDocument.get(SolrPredefinedField.SITE_URL);
        this.serverRelativeURL = (String) solrDocument.get(SolrDynamicField.SERVER_RELATIVE_URL);
        this.repoPath = (String) solrDocument.get(SolrPredefinedField.REPO_PATH);
        this.directory = (String) solrDocument.get(SolrPredefinedField.DIRECTORY);
        this.folderURL = (String) solrDocument.get(SolrPredefinedField.FOLDER_URL);
        this.file_id = (String) solrDocument.get(SolrPredefinedField.FILE_ID);
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public String getServerRelativeURL() {
        return serverRelativeURL;
    }

    public void setServerRelativeURL(String serverRelativeURL) {
        this.serverRelativeURL = serverRelativeURL;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

    public String getFolderURL() {
        return folderURL;
    }

    public void setFolderURL(String folderURL) {
        this.folderURL = folderURL;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public void printDocumentInfo() {
        LOGGER.debug("Document Path = " + this.documentPath);
        LOGGER.debug("Document Name = " + this.documentName);
        LOGGER.debug("Site URL = " + this.siteURL);
        LOGGER.debug("ServerRelativeURL = " + this.serverRelativeURL);
        LOGGER.debug("Repo_Path = " + this.repoPath);
        LOGGER.debug("Folder_URL = " + this.folderURL);
    }
}
