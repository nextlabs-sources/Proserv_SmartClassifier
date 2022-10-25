package com.nextlabs.smartclassifier.database.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.util.Date;

@Entity
@Table(name = "SYSTEM_CONFIGS", uniqueConstraints = {@UniqueConstraint(columnNames = {"IDENTIFIER"})})
public class SystemConfig {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String DISPLAY_ORDER = "displayOrder";
	public static final String LABEL = "label";
	public static final String IDENTIFIER = "identifier";
	public static final String VALUE = "value";
	public static final String DESCRIPTION = "description";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "SYSTEM_CONFIGS_SEQ")})
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SYSTEM_CONFIG_GROUP_ID")
	@NotFound(action = NotFoundAction.IGNORE)
	private SystemConfigGroup systemConfigGroup;

	@Column(name = "DISPLAY_ORDER", nullable = false)
	private Integer displayOrder;
	
	@Column(name = "LABEL", nullable = false, length = 100)
	private String label;
	
	@Column(name = "IDENTIFIER", nullable = false, length = 100)
	private String identifier;
	
	@Column(name = "VALUE", nullable = false, length = 320)
	private String value;
	
	@Column(name = "DESCRIPTION", nullable = true, length = 1024)
	private String description;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	@Transient
	private Boolean encrypted;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public SystemConfigGroup getSystemConfigGroup() {
		return systemConfigGroup;
	}
	
	public void setSystemConfigGroup(SystemConfigGroup systemConfigGroup) {
		this.systemConfigGroup = systemConfigGroup;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	public Date getModifiedOn() {
		return modifiedOn;
	}
	
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	public Boolean isEncrypted() {
		return encrypted;
	}
	
	public void isEncrypted(Boolean encrypted) {
		this.encrypted = encrypted;
	}
}
