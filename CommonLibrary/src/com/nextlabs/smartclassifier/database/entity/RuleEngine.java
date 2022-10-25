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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "RULE_ENGINES", uniqueConstraints = {@UniqueConstraint(columnNames = {"HOSTNAME"})})
public class RuleEngine {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String HOSTNAME = "hostname";
	public static final String ON_DEMAND_INTERVAL = "onDemandInterval";
	public static final String SCHEDULED_INTERVAL = "scheduledInterval";
	public static final String CONFIG_LOADED_ON = "configLoadedOn";
	public static final String CONFIG_RELOAD_INTERVAL = "configReloadInterval";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
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
	
	@Column(name = "ON_DEMAND_INTERVAL", nullable = false)
	private Long onDemandInterval;
	
	@Column(name = "SCHEDULED_INTERVAL", nullable = false)
	private Long scheduledInterval;
	
	@Column(name = "ON_DEMAND_POOL_SIZE", nullable = false)
	private Integer onDemandPoolSize;
	
	@Column(name = "SCHEDULED_POOL_SIZE", nullable = false)
	private Integer scheduledPoolSize;
	
	@Column(name = "CONFIG_LOADED_ON", nullable = true)
	private Date configLoadedOn;
	
	@Column(name = "CONFIG_RELOAD_INTERVAL", nullable = false)
	private Integer configReloadInterval;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ruleEngine", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<Rule> rules = new LinkedHashSet<Rule>();
	
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
	
	public Long getOnDemandInterval() {
		return onDemandInterval;
	}
	
	public void setOnDemandInterval(Long onDemandInterval) {
		this.onDemandInterval = onDemandInterval;
	}
	
	public Long getScheduledInterval() {
		return scheduledInterval;
	}
	
	public void setScheduledInterval(Long scheduledInterval) {
		this.scheduledInterval = scheduledInterval;
	}
	
	public Integer getOnDemandPoolSize() {
		return onDemandPoolSize;
	}
	
	public void setOnDemandPoolSize(Integer onDemandPoolSize) {
		this.onDemandPoolSize = onDemandPoolSize;
	}
	
	public Integer getScheduledPoolSize() {
		return scheduledPoolSize;
	}
	
	public void setScheduledPoolSize(Integer scheduledPoolSize) {
		this.scheduledPoolSize = scheduledPoolSize;
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
	
	public Set<Rule> getRules() {
		return rules;
	}
	
	public void setRules(Set<Rule> rules) {
		this.rules = rules;
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
