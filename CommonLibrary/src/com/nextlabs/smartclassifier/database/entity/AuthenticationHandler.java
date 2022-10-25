package com.nextlabs.smartclassifier.database.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "AUTH_HANDLERS")
public class AuthenticationHandler {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String TYPE = "type";
	public static final String CONFIGURATION_DATA = "configurationData";
	public static final String USER_ATTRIBUTE_MAPPING = "userAttributeMapping";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "AUTH_HANDLERS_SEQ")})
	private Long id;
	
	@Column(name = "NAME", nullable = false, length = 100)
	private String name;
	
	@Column(name = "TYPE", nullable = false, length = 25)
	private String type;
	
	@Column(name = "CONFIGURATION_DATA", nullable = true, length = 4000)
	private String configurationData;
	
	@Column(name = "USER_ATTRIBUTE_MAPPING", nullable = true, length = 4000)
	private String userAttributeMapping;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getConfigurationData() {
		return configurationData;
	}
	
	public void setConfigurationData(String configurationData) {
		this.configurationData = configurationData;
	}
	
	public String getUserAttributeMapping() {
		return userAttributeMapping;
	}
	
	public void setUserAttributeMapping(String userAttributeMapping) {
		this.userAttributeMapping = userAttributeMapping;
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
}
