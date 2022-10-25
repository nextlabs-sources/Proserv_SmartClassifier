package com.nextlabs.smartclassifier.database.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "DOCUMENT_EXTRACTORS", uniqueConstraints = {@UniqueConstraint(columnNames = {"EXTENSION"})})
public class DocumentExtractor {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String EXTENSION = "extension";
	public static final String CLASS_NAME = "className";
	public static final String DEFAULT_SIZE_LIMIT = "defaultSizeLimit";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "DOCUMENT_EXTRACTORS_SEQ")})
	private Long id;
	
	@Column(name = "EXTENSION", nullable = false, length = 10)
	private String extension;
	
	@Column(name = "CLASS_NAME", nullable = false, length = 255)
	private String className;
	
	@Column(name = "DEFAULT_SIZE_LIMIT", nullable = false)
	private Integer defaultSizeLimit;
	
	@Column(name = "PARAM", nullable = true, length = 1024)
	private String param;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "documentExtractor", cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<DocumentTypeAssociation> documentTypeAssociations = new HashSet<DocumentTypeAssociation>();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	
	public String getParam() {
		return param;
	}
	
	public void setParam(String param) {
		this.param = param;
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	public int getDefaultSizeLimit() {
		return defaultSizeLimit;
	}
	
	public void setDefaultSizeLimit(int defaultSizeLimit) {
		this.defaultSizeLimit = defaultSizeLimit;
	}
	
	
	public Date getModifiedOn() {
		return modifiedOn;
	}
	
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	public Set<DocumentTypeAssociation> getDocumentTypeAssociations() {
		return documentTypeAssociations;
	}
	
	public void setDocumentTypeAssociations(
			Set<DocumentTypeAssociation> documentTypeAssociations) {
		this.documentTypeAssociations = documentTypeAssociations;
	}
}