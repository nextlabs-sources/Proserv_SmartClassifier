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
@Table(name = "DOCUMENT_TYPE_ASSOCIATIONS", uniqueConstraints = {@UniqueConstraint(columnNames = {"WATCHER_ID", "DOCUMENT_EXTRACTOR_ID"})})
public class DocumentTypeAssociation {
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "DOCUMENT_TYPE_ASSOCIATIONS_SEQ")})
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WATCHER_ID")
	private Watcher watcher;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DOCUMENT_EXTRACTOR_ID")
	private DocumentExtractor documentExtractor;
	
	@Column(name = "INCLUDE", nullable = false)
	private Boolean include;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Watcher getWatcher() {
		return watcher;
	}
	
	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}
	
	public DocumentExtractor getDocumentExtractor() {
		return documentExtractor;
	}
	
	public void setDocumentExtractor(DocumentExtractor documentExtractor) {
		this.documentExtractor = documentExtractor;
	}
	
	public boolean isInclude() {
		return include;
	}
	
	public void isInclude(boolean include) {
		this.include = include;
	}
}
