package com.nextlabs.smartclassifier.dto.v1;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.DocumentExtractor;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class DocumentExtractorDTO 
		extends BaseDTO {
	
	@Expose
	private Integer defaultSizeLimit;
	@Expose
	private String extension;
	@Expose
	private String className;
	
	public DocumentExtractorDTO() {
		super();
	}
	
	public DocumentExtractorDTO(DocumentExtractor documentExtractor) {
		super();
		copy(documentExtractor);
	}
	
	public void copy(DocumentExtractor documentExtractor) {
		if(documentExtractor != null) {
			this.id = documentExtractor.getId();
			this.extension = documentExtractor.getExtension();
			this.className = documentExtractor.getClassName();
			this.defaultSizeLimit = documentExtractor.getDefaultSizeLimit();
			this.createdTimestamp = documentExtractor.getCreatedOn();
			this.createdOn = documentExtractor.getCreatedOn().getTime();
			this.modifiedTimestamp = documentExtractor.getModifiedOn();
			this.modifiedOn = documentExtractor.getModifiedOn().getTime();
		}
	}
	
	public DocumentExtractor getEntity() {
		DocumentExtractor documentExtractor = new DocumentExtractor();
		
		documentExtractor.setId(this.id);
		documentExtractor.setDefaultSizeLimit(this.defaultSizeLimit == null ? 0 : this.defaultSizeLimit);
		documentExtractor.setExtension(this.extension);
		documentExtractor.setClassName(this.className);
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			documentExtractor.setCreatedOn(new Date(this.getCreatedOn()));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			documentExtractor.setModifiedOn(new Date(this.getModifiedOn()));
		}
		
		return documentExtractor;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public Integer getDefaultSizeLimit() {
		return defaultSizeLimit;
	}
	
	public void setDefaultSizeLimit(Integer defaultSizeLimit) {
		this.defaultSizeLimit = defaultSizeLimit;
	}
}
