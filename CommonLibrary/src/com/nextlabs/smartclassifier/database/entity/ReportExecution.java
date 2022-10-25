package com.nextlabs.smartclassifier.database.entity;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "REPORT_EXECUTIONS")
public class ReportExecution {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String REPORT_ID = "report.id";
	public static final String REPORT_SCHEDULE_ID = "reportSchedule.id";
	public static final String REPORT_DOCUMENT = "reportDocument";
	public static final String STATUS = "status";
	public static final String PLAN_TIME = "planTime";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "REPORT_EXECUTIONS_SEQ")})
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPORT_SCHEDULE_ID")
	private ReportSchedule reportSchedule;
	
	@Column(name = "REPORT_DOCUMENT", nullable = true, length = 1024)
	private String reportDocument;
	
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status;
	
	@Column(name = "PLAN_TIMESTAMP", nullable = false)
	private Date planTime;
	
	@Column(name = "START_TIMESTAMP", nullable = true)
	private Date startTime;
	
	@Column(name = "END_TIMESTAMP", nullable = true)
	private Date endTime;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public ReportSchedule getReportSchedule() {
		return reportSchedule;
	}
	
	public void setReportSchedule(ReportSchedule reportSchedule) {
		this.reportSchedule = reportSchedule;
	}
	
	public String getReportDocument() {
		return reportDocument;
	}
	
	public void setReportDocument(String reportDocument) {
		this.reportDocument = reportDocument;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getPlanTime() {
		return planTime;
	}
	
	public void setPlanTime(Date planTime) {
		this.planTime = planTime;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
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
