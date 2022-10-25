package com.nextlabs.smartclassifier.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "DOCUMENT_RECORDS")
public class DocumentRecord {
	
	// Field name for search criteria construction
	public static final String FILE_ID = "fileId";
	public static final String ABSOLUTE_FILE_PATH = "absoluteFilePath";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String EXTRACTED = "extracted";
	public static final String EXTRACTION_TIME = "extractionTime";
	public static final String FAIL_COUNT = "failCount";
	public static final String FILE_SIZE = "fileSize";
	public static final String FILE_TYPE = "fileType";
	public static final String REPOSITORY_TYPE = "repositoryType";
	public static final String INDEXED = "indexed";
	public static final String INDEXING_TIME = "indexingTime";
	public static final String LAST_UPDATED = "lastUpdated";
	public static final String LAST_MODIFIED = "lastModified";
	
	public DocumentRecord() {
		this.extracted = false;
		this.indexed = false;
	}
	
	@Column(name = "FILE_ID", unique = true, nullable = false)
	private String fileId;
	
	@Id
	@Column(name = "ABSOLUTE_FILE_PATH", unique = true, nullable = false, length = 450)
	private String absoluteFilePath;
	
	@Column(name = "ERROR_MESSAGE", nullable = true, length = 1024)
	private String errorMessage;
	
	@Column(name = "EXTRACTED", nullable = true)
	private Boolean extracted;
	
	@Column(name = "EXTRACTION_TIME", nullable = true)
	private Double extractionTime;
	
	@Column(name = "FAIL_COUNT", nullable = true)
	private Integer failCount;
	
	@Column(name = "FILE_SIZE", nullable = true)
	private Long fileSize;
	
	@Column(name = "FILE_TYPE", nullable = true, length = 255)
	private String fileType;

	@Column(name = "REPOSITORY_TYPE", nullable = true, length = 30)
	private String repositoryType;
	
	@Column(name = "INDEXED", nullable = true)
	private Boolean indexed;
	
	@Column(name = "INDEXING_TIME", nullable = true)
	private Double indexingTime;

	@Column(name = "LAST_UPDATED", nullable = true)
	private Date lastUpdated;
	
	@Column(name = "LAST_MODIFIED", nullable = true)
	private Long lastModified;
	
	public String getFileId() {
		return fileId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public String getAbsoluteFilePath() {
		return absoluteFilePath;
	}
	
	public void setAbsoluteFilePath(String absoluteFilePath) {
		this.absoluteFilePath = absoluteFilePath;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public Boolean isExtracted() {
		return extracted;
	}
	
	public void isExtracted(Boolean extracted) {
		this.extracted = extracted;
	}
	
	public Double getExtractionTime() {
		return extractionTime;
	}
	
	public void setExtractionTime(Double extractionTime) {
		this.extractionTime = extractionTime;
	}
	
	public Integer getFailCount() {
		return failCount;
	}
	
	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
	
	public Long getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	
	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public Boolean isIndexed() {
		return indexed;
	}
	
	public void isIndexed(Boolean indexed) {
		this.indexed = indexed;
	}
	
	public Double getIndexingTime() {
		return indexingTime;
	}
	
	public void setIndexingTime(Double indexingTime) {
		this.indexingTime = indexingTime;
	}
	
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public Long getLastModified() {
		return lastModified;
	}
	
	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	public String getRepositoryType() {
		return repositoryType;
	}

	public void setRepositoryType(String repositoryType) {
		this.repositoryType = repositoryType;
	}
}
