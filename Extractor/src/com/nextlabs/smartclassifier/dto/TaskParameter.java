package com.nextlabs.smartclassifier.dto;

import com.nextlabs.smartclassifier.manager.IndexManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TaskParameter {

    private static final int DEFAULT_FILE_SIZE_LIMIT = 0x10;

    private int minimumHeapMemorySize;
    private IndexManager indexManager;

    private Map<String, DocumentExtractor> documentExtractorByExtn;
    private Map<String, String> rmsProperties;
    private List<String> excludedMetadata;


    public TaskParameter() {
        super();
    }

    public DocumentExtractor getDocumentExtractor(String extension) {
        if (documentExtractorByExtn != null) {
            return documentExtractorByExtn.get(extension);
        }

        return null;
    }

    public int getFileSizeLimit(String extension) {
        if (documentExtractorByExtn != null) {
            DocumentExtractor documentExtractor = documentExtractorByExtn.get(extension);

            if (documentExtractor != null) {
                return documentExtractor.getMaximumFileSize();
            }
        }

        // No configuration found, return default file size.
        return DEFAULT_FILE_SIZE_LIMIT;
    }

    public String getExtractorClassName(String extension) {
        if (documentExtractorByExtn != null) {
            DocumentExtractor documentExtractor = documentExtractorByExtn.get(extension);

            if (documentExtractor != null) {
                return documentExtractor.getClassName();
            }
        }

        return null;
    }

    public int getMinimumHeapMemorySize() {
        return minimumHeapMemorySize;
    }

    public void setMinimumHeapMemorySize(int minimumHeapMemorySize) {
        this.minimumHeapMemorySize = minimumHeapMemorySize;
    }

    public IndexManager getIndexManager() {
        return indexManager;
    }

    public void setIndexManager(IndexManager manager) {
        this.indexManager = manager;
    }

    public Map<String, DocumentExtractor> getDocumentExtractorByExtn() {
        return documentExtractorByExtn;
    }

    public void setDocumentExtractorByExtn(
            Map<String, DocumentExtractor> documentExtractorByExtn) {
        this.documentExtractorByExtn = documentExtractorByExtn;
    }

    public void setRmsProperties(Map<String, String> rmsProperties) {
        this.rmsProperties = rmsProperties;
    }

    public Map<String, String> getRmsProperties() {
        return rmsProperties;
    }

    public List<String> getExcludedMetadata() {
        return new ArrayList<>(excludedMetadata);
    }

    public void setExcludedMetadata(List<String> excludedMetadata) {
        this.excludedMetadata = excludedMetadata;
    }
}
