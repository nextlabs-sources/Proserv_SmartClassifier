package com.nextlabs.smartclassifier.database.entity;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "SYSTEM_CONFIG_GROUPS", uniqueConstraints = {@UniqueConstraint(columnNames = {"NAME"})})
public class SystemConfigGroup {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String DISPLAY_ORDER = "displayOrder";
	public static final String EDITABLE = "editable";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "SYSTEM_CONFIG_GROUPS_SEQ")})
	private Long id;

	@Column(name = "NAME", nullable = false, length = 100)
	private String name;
	
	@Column(name = "DESCRIPTION", nullable = true, length = 1024)
	private String description;
	
	@Column(name = "DISPLAY_ORDER", nullable = false)
	private Integer displayOrder;
	
	@Column(name = "EDITABLE", nullable = false)
	private Boolean editable;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "systemConfigGroup", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<SystemConfig> systemConfigs = new LinkedHashSet<SystemConfig>();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public Boolean getEditable() {
		return editable;
	}
	
	public void setEditable(Boolean editable) {
		this.editable = editable;
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
	
	public Set<SystemConfig> getSystemConfigs() {
		return systemConfigs;
	}
	
	public void setSystemConfigs(Set<SystemConfig> systemConfigs) {
		this.systemConfigs = systemConfigs;
	}
}
