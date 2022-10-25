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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "REPORTS")
public class Report {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String TYPE = "type";
	public static final String RANGE = "range";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String DELETED = "deleted";
	public static final String CREATED_BY = "createdBy";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_BY = "modifiedBy";
	public static final String MODIFIED_ON = "modifiedOn";
	public static final String LAST_ACCESSED_ON = "lastAccessedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "REPORTS_SEQ")})
	private Long id;
	
	@Column(name = "NAME", nullable = false, length = 100)
	private String name;
	
	@Column(name = "DESCRIPTION", nullable = true, length = 1024)
	private String description;
	
	@Column(name = "TYPE", nullable = false, length = 1)
	private String type;
	
	@Column(name = "RANGE", nullable = false, length = 1024)
	private String range;
	
	@Column(name = "EVENT_STATUS", nullable = false, length = 1)
	private String eventStatus;
	
	@Column(name = "DELETED", nullable = false)
	private Boolean deleted;
	
	@Column(name = "CREATED_BY", nullable = false, length = 100)
	private String createdBy;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Column(name = "MODIFIED_BY", nullable = true, length = 100)
	private String modifiedBy;
	
	@Column(name = "MODIFIED_ON", nullable = false)
	private Date modifiedOn;
	
	@Column(name = "LAST_ACCESSED_ON", nullable = true)
	private Date lastAccessedOn;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "report", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<ReportFilterGroup> reportFilterGroups = new LinkedHashSet<ReportFilterGroup>();
	
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "report", cascade = CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	private ReportSchedule reportSchedule;
	
	@Transient
	private Date lastExecutionDate;
	
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getRange() {
		return range;
	}
	
	public void setRange(String range) {
		this.range = range;
	}
	
	public String getEventStatus() {
		return eventStatus;
	}
	
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	
	public Boolean isDeleted() {
		return deleted;
	}
	
	public void isDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	public String getModifiedBy() {
		return modifiedBy;
	}
	
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	public Date getModifiedOn() {
		return modifiedOn;
	}
	
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	public Date getLastAccessedOn() {
		return lastAccessedOn;
	}
	
	public void setLastAccessedOn(Date lastAccessedOn) {
		this.lastAccessedOn = lastAccessedOn;
	}
	
	public Set<ReportFilterGroup> getReportFilterGroups() {
		return reportFilterGroups;
	}
	
	public void setReportFilterGroups(Set<ReportFilterGroup> reportFilterGroups) {
		this.reportFilterGroups = reportFilterGroups;
	}
	
	public ReportSchedule getReportSchedule() {
		return reportSchedule;
	}
	
	public void setReportSchedule(ReportSchedule reportSchedule) {
		this.reportSchedule = reportSchedule;
	}
	
	public Date getLastExecutionDate() {
		return lastExecutionDate;
	}
	
	public void setLastExecutionDate(Date lastExecutionDate) {
		this.lastExecutionDate = lastExecutionDate;
	}
}
