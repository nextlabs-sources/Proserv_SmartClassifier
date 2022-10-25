package com.nextlabs.smartclassifier;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DocumentContent {

    private String documentID;
    private String absoluteFilePath;
    private String fileType;
    private String repositoryType;
    private String header;
    private String content;
    private String footer;
    private String repoPath;
    private long lastModified;
    private Map<String, String> metadata;

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDocumentHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDocumentContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDocumentFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public Map<String, String> getDocumentMetadataKeyToValueMap() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * Add SharePoint metadata to the metadata map
     *
     * @param spMetadata the SharePoint Metadata
     */
    public void addToMetadata(Map<String, String> spMetadata) {

        Set<String> metadataKeys = new HashSet<>(metadata.keySet());
        Set<String> spMetadataKeys = new HashSet<>(spMetadata.keySet());

        // Keys common to both maps - replace
        Set<String> commonKeys = new HashSet<>(metadataKeys);
        commonKeys.retainAll(spMetadataKeys);

        for (String commonKey : commonKeys) {
            metadata.put(commonKey, spMetadata.get(commonKey));
        }

        // Keys in spMetadata and not in metadata
        Set<String> keysExclusiveInSP = new HashSet<>(spMetadataKeys);
        keysExclusiveInSP.removeAll(metadataKeys);

        for (String spKey : keysExclusiveInSP) {
            metadata.put(spKey, spMetadata.get(spKey));
        }
    }

    public String getRepoPath() {
        return repoPath;
    }

    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }
}
