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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "EXTRACTORS", uniqueConstraints = {@UniqueConstraint(columnNames = {"HOSTNAME"})})
public class Extractor {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String HOSTNAME = "hostname";
	public static final String JMS_PROFILE = "JMSProfile";
	public static final String JMS_PROFILE_ID = "JMSProfile.id";
	public static final String DOCUMENT_EXTRACTOR_COUNT = "documentExtractorCount";
	public static final String MIN_HEAP_MEMORY = "minHeapMemory";
	public static final String CONFIG_LOADED_ON = "configLoadedOn";
	public static final String CONFIG_RELOAD_INTERVAL = "configReloadInterval";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	public static final String DOCUMENT_SIZE_LIMIT = "documentSizeLimits";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "COMPONENTS_SEQ")})
	private Long id;
	
	@Column(name = "NAME", nullable = false, length = 100)
	private String name;
	
	@Column(name = "HOSTNAME", nullable = false, length = 50)
	private String hostname;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "JMS_PROFILE_ID")
	@NotFound(action = NotFoundAction.IGNORE)
	private JMSProfile JMSProfile;
	
	@Column(name = "DOCUMENT_EXTRACTOR_COUNT", nullable = false)
	private Integer documentExtractorCount;
	
	@Column(name = "MIN_HEAP_MEMORY", nullable = false)
	private Integer minHeapMemory;
	
	@Column(name = "CONFIG_LOADED_ON", nullable = true)
	private Date configLoadedOn;
	
	@Column(name = "CONFIG_RELOAD_INTERVAL", nullable = false)
	private Integer configReloadInterval;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "extractor", cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<DocumentSizeLimit> documentSizeLimits = new LinkedHashSet<DocumentSizeLimit>();
	
	@Transient
	private Set<ExecutionWindowSet> executionWindowSets = new LinkedHashSet<ExecutionWindowSet>();
	
	@Transient
	private Long lastHeartbeat;
	
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
	
	public String getHostname() {
		return hostname;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public JMSProfile getJMSProfile() {
		return JMSProfile;
	}
	
	public void setJMSProfile(JMSProfile JMSProfile) {
		this.JMSProfile = JMSProfile;
	}
	
	public Integer getDocumentExtractorCount() {
		return documentExtractorCount;
	}
	
	public void setDocumentExtractorCount(Integer documentExtractorCount) {
		this.documentExtractorCount = documentExtractorCount;
	}
	
	public Integer getMinHeapMemory() {
		return minHeapMemory;
	}
	
	public void setMinHeapMemory(Integer minHeapMemory) {
		this.minHeapMemory = minHeapMemory;
	}
	
	public Date getConfigLoadedOn() {
		return configLoadedOn;
	}
	
	public void setConfigLoadedOn(Date configLoadedOn) {
		this.configLoadedOn = configLoadedOn;
	}
	
	public Integer getConfigReloadInterval() {
		return configReloadInterval;
	}
	
	public void setConfigReloadInterval(Integer configReloadInterval) {
		this.configReloadInterval = configReloadInterval;
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
	
	public Set<DocumentSizeLimit> getDocumentSizeLimits() {
		return documentSizeLimits;
	}
	
	public void setDocumentSizeLimits(Set<DocumentSizeLimit> documentSizeLimits) {
		this.documentSizeLimits = documentSizeLimits;
	}
	
	public Set<ExecutionWindowSet> getExecutionWindowSets() {
		return executionWindowSets;
	}
	
	public void setExecutionWindowSets(Set<ExecutionWindowSet> executionWindowSets) {
		this.executionWindowSets = executionWindowSets;
	}
	
	public Long getLastHeartbeat() {
		return lastHeartbeat;
	}
	
	public void setLastHeartbeat(Long lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}
}
