package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.DocumentSizeLimit;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class DocumentSizeLimitDTO 
		extends BaseDTO {
	
	@Expose
	private DocumentExtractorDTO documentExtractor; 
	@Expose
	private Integer maxFileSize;
	
	public DocumentSizeLimitDTO() {
		super();
	}
	
	public DocumentSizeLimitDTO(DocumentSizeLimit documentSizeLimit) {
		super();
		copy(documentSizeLimit);
	}
	
	public void copy(DocumentSizeLimit documentSizeLimit) {
		if(documentSizeLimit != null) {
			this.id = documentSizeLimit.getId();
			this.documentExtractor = new DocumentExtractorDTO(documentSizeLimit.getDocumentExtractor());
			this.maxFileSize = documentSizeLimit.getMaxFileSize();
		}
	}
	
	public DocumentSizeLimit getEntity() {
		DocumentSizeLimit documentSizeLimit = new DocumentSizeLimit();
		
		documentSizeLimit.setId(this.id);
		documentSizeLimit.setDocumentExtractor(this.documentExtractor.getEntity());
		documentSizeLimit.setMaxFileSize(this.maxFileSize);
		
		return documentSizeLimit;
	}
	
	public DocumentExtractorDTO getDocumentExtractor() {
		return documentExtractor;
	}
	
	public void setDocumentExtractor(DocumentExtractorDTO documentExtractor) {
		this.documentExtractor = documentExtractor;
	}
	
	public Integer getMaxFileSize() {
		return maxFileSize;
	}
	
	public void setMaxFileSize(Integer maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
}
