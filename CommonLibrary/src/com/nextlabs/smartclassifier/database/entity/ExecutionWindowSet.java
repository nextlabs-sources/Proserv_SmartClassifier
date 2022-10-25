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
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "EXECUTION_WINDOW_SETS")
public class ExecutionWindowSet {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String SCHEDULE_TYPE = "scheduleType";
	public static final String EFFECTIVE_FROM = "effectiveFrom";
	public static final String EFFECTIVE_UNTIL = "effectiveUntil";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "EXECUTION_WINDOW_SETS_SEQ")})
	private Long id;
	
	@Column(name = "NAME", nullable = false, length = 100)
	private String name;
	
	@Column(name = "DESCRIPTION", nullable = true, length = 1024)
	private String description;
	
	@Column(name = "SCHEDULE_TYPE", nullable = false, length = 1)
	private String scheduleType;
	
	@Column(name = "EFFECTIVE_FROM", nullable = true)
	private Date effectiveFrom;
	
	@Column(name = "EFFECTIVE_UNTIL", nullable = true)
	private Date effectiveUntil;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "executionWindowSet", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<ExecutionWindow> executionWindows = new LinkedHashSet<ExecutionWindow>();
	
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
	
	public String getScheduleType() {
		return scheduleType;
	}
	
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}
	
	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	
	public Date getEffectiveUntil() {
		return effectiveUntil;
	}
	
	public void setEffectiveUntil(Date effectiveUntil) {
		this.effectiveUntil = effectiveUntil;
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
	
	public Set<ExecutionWindow> getExecutionWindows() {
		return executionWindows;
	}
	
	public void setExecutionWindows(Set<ExecutionWindow> executionWindows) {
		this.executionWindows = executionWindows;
	}
}
