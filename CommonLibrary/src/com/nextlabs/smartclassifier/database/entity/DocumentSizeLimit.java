package com.nextlabs.smartclassifier.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "DOCUMENT_SIZE_LIMITS", uniqueConstraints = {@UniqueConstraint(columnNames = {"EXTRACTOR_ID", "DOCUMENT_EXTRACTOR_ID"})})
public class DocumentSizeLimit {
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "DOCUMENT_SIZE_LIMITS_SEQ")})
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXTRACTOR_ID")
	private Extractor extractor;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "DOCUMENT_EXTRACTOR_ID")
	private DocumentExtractor documentExtractor;
	
	@Column(name = "MAX_FILE_SIZE", nullable = false)
	private Integer maxFileSize;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Extractor getExtractor() {
		return extractor;
	}
	
	public void setExtractor(Extractor extractor) {
		this.extractor = extractor;
	}
	
	public DocumentExtractor getDocumentExtractor() {
		return documentExtractor;
	}
	
	public void setDocumentExtractor(DocumentExtractor documentExtractor) {
		this.documentExtractor = documentExtractor;
	}
	
	public Integer getMaxFileSize() {
		return maxFileSize;
	}
	
	public void setMaxFileSize(Integer maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
}
