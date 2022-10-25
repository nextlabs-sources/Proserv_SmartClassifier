package com.nextlabs.smartclassifier.dto.v1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;

public class SolrDocumentDTO {

    @Expose
    private String id;
    @Expose
    private String directory;
    @Expose
    private String documentName;
    @Expose
    private String author;
    @Expose
    private String folderURL;
    @Expose
    private Long createdOn;
    @Expose
    private Long modifiedOn;

    protected final Logger logger = LogManager.getLogger(getClass());

    public SolrDocumentDTO() {
        super();
    }

    public SolrDocumentDTO(SolrDocument document) {
        super();
        copy(document);
    }

    public void copy(SolrDocument document) {
        if (document != null) {
            this.id = (String) document.get(SolrPredefinedField.ID);
            this.directory = (String) document.get(SolrPredefinedField.DIRECTORY);
            this.documentName = (String) document.get(SolrPredefinedField.DOCUMENT_NAME);
            this.author = (String) document.get(SolrPredefinedField.AUTHOR);

            if (document.get(SolrPredefinedField.FOLDER_URL) != null) {
                this.folderURL = document.get(SolrPredefinedField.FOLDER_URL).toString();
            } else {
                this.folderURL = "";
            }

            if (document.get(SolrPredefinedField.CREATION_DATE_MILLISECOND) != null) {
                try {
                    this.createdOn = ((Long) document.get(SolrPredefinedField.CREATION_DATE_MILLISECOND));
                } catch (Exception err) {
                    // Ignore
                }
            }

            if (document.get(SolrPredefinedField.LAST_MODIFIED_DATE_MILLISECOND) != null) {
                try {
                    this.modifiedOn = ((Long) document.get(SolrPredefinedField.LAST_MODIFIED_DATE_MILLISECOND));
                } catch (Exception err) {
                    // Ignore
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getFolderURL() {
        return folderURL;
    }

    public void setFolderURL(String folderURL) {
        this.folderURL = folderURL;
    }
}
