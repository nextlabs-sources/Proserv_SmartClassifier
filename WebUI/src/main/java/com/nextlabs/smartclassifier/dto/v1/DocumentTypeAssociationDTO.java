package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.DocumentTypeAssociation;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class DocumentTypeAssociationDTO 
		extends BaseDTO {
	
	@Expose
	private DocumentExtractorDTO documentExtractor;
	@Expose
	private Boolean include;
	
	public DocumentTypeAssociationDTO() {
		super();
	}
	
	public DocumentTypeAssociationDTO(DocumentTypeAssociation documentTypeAssociation) {
		super();
		copy(documentTypeAssociation);
	}
	
	public void copy(DocumentTypeAssociation documentTypeAssociation) {
		if(documentTypeAssociation != null) {
			this.id = documentTypeAssociation.getId();
			this.documentExtractor = new DocumentExtractorDTO(documentTypeAssociation.getDocumentExtractor());
			this.include = documentTypeAssociation.isInclude();
		}
	}
	
	public DocumentTypeAssociation getEntity() {
		DocumentTypeAssociation documentTypeAssociation = new DocumentTypeAssociation();
		
		documentTypeAssociation.setId(this.id);
		documentTypeAssociation.setDocumentExtractor(this.documentExtractor.getEntity());
		documentTypeAssociation.isInclude(this.include == null ? false : this.include);
		
		return documentTypeAssociation;
	}
	
	public DocumentExtractorDTO getDocumentExtractor() {
		return documentExtractor;
	}
	
	public void setDocumentExtractor(DocumentExtractorDTO documentExtractor) {
		this.documentExtractor = documentExtractor;
	}
	
	public Boolean getInclude() {
		return include;
	}
	
	public void setInclude(Boolean include) {
		this.include = include;
	}
}
