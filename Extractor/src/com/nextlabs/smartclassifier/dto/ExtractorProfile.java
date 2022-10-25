package com.nextlabs.smartclassifier.dto;

import com.nextlabs.smartclassifier.database.entity.DocumentSizeLimit;
import com.nextlabs.smartclassifier.database.entity.Extractor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExtractorProfile
        extends BaseDTO {

    private String name;
    private String hostname;
    private int documentExtractorCount;
    private int minHeapMemory;
    private Date configLoadedOn;
    private long configReloadInterval;
    private JMSConfig jmsConfig = new JMSConfig();
    private Map<String, DocumentExtractor> documentExtractorByExtn = new HashMap<>();

    public void copy(Extractor extractor) {
        if (extractor != null) {
            this.id = extractor.getId();
            this.name = extractor.getName();
            this.hostname = extractor.getHostname();
            this.documentExtractorCount = extractor.getDocumentExtractorCount();
            this.minHeapMemory = extractor.getMinHeapMemory();
            this.configLoadedOn = extractor.getConfigLoadedOn();
            this.configReloadInterval = extractor.getConfigReloadInterval();
            this.jmsConfig.copy(extractor.getJMSProfile());

            if (extractor.getDocumentSizeLimits() != null) {
                for (DocumentSizeLimit documentSizeLimit : extractor.getDocumentSizeLimits()) {
                    if (documentSizeLimit.getDocumentExtractor() != null) {
                        String extn = documentSizeLimit.getDocumentExtractor().getExtension().toLowerCase();
                        String docExtractorClassName = documentSizeLimit.getDocumentExtractor().getClassName();
                        int maxFileSize = documentSizeLimit.getMaxFileSize();
                        String params = documentSizeLimit.getDocumentExtractor().getParam();

                        documentExtractorByExtn.put(extn, new DocumentExtractor(docExtractorClassName, maxFileSize, params));
                    }
                }
            }

            this.createdTimestamp = extractor.getCreatedOn();
            this.modifiedTimestamp = extractor.getModifiedOn();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getDocumentExtractorCount() {
        return documentExtractorCount;
    }

    public void setDocumentExtractorCount(int documentExtractorCount) {
        this.documentExtractorCount = documentExtractorCount;
    }

    public int getMinHeapMemory() {
        return minHeapMemory;
    }

    public void setMinHeapMemory(int minHeapMemory) {
        this.minHeapMemory = minHeapMemory;
    }

    public Date getConfigLoadedOn() {
        return configLoadedOn;
    }

    public void setConfigLoadedOn(Date configLoadedOn) {
        this.configLoadedOn = configLoadedOn;
    }

    public long getConfigReloadInterval() {
        return configReloadInterval * 1000;
    }

    public void setConfigReloadInterval(long configReloadInterval) {
        this.configReloadInterval = configReloadInterval;
    }

    public JMSConfig getJmsConfig() {
        return jmsConfig;
    }

    public void setJmsConfig(JMSConfig jmsConfig) {
        this.jmsConfig = jmsConfig;
    }

    public Map<String, DocumentExtractor> getDocumentExtractorByExtn() {
        return documentExtractorByExtn;
    }

    public void setDocumentExtractorByExtn(Map<String, DocumentExtractor> documentExtractorByExtn) {
        this.documentExtractorByExtn = documentExtractorByExtn;
    }

}
