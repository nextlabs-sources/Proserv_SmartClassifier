package com.nextlabs.smartclassifier.database.entity;

import java.text.ParseException;
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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;
import org.quartz.CronExpression;

@Entity
@Table(name = "REPORT_SCHEDULES")
public class ReportSchedule {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String REPORT_ID = "report.id";
	public static final String SCHEDULE_TYPE = "scheduleType";
	public static final String EXECUTION_FREQUENCY = "executionFrequency";
	public static final String ENABLED = "enabled";
	public static final String EFFECTIVE_FROM = "displayOrder";
	public static final String EFFECTIVE_UNTIL = "editable";
	public static final String EMAIL_SUBJECT = "emailSubject";
	public static final String EMAIL_CONTENT = "emailContent";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "REPORT_SCHEDULES_SEQ")})
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumn
	private Report report;
	
	@Column(name = "SCHEDULE_TYPE", nullable = false, length = 1)
	private String scheduleType;
	
	@Column(name = "EXECUTION_FREQUENCY", nullable = false, length = 120)
	private String executionFrequency;
	
	@Column(name = "ENABLED", nullable = true)
	private Boolean enabled;
	
	@Column(name = "EFFECTIVE_FROM", nullable = true)
	private Date effectiveFrom;
	
	@Column(name = "EFFECTIVE_UNTIL", nullable = true)
	private Date effectiveUntil;
	
	@Column(name = "EMAIL_SUBJECT", nullable = false, length = 1024)
	private String emailSubject;
	
	@Lob
	@Column(name = "EMAIL_CONTENT", nullable = false)
	private String emailContent;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reportSchedule", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<ReportRecipient> reportRecipients = new LinkedHashSet<ReportRecipient>();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Report getReport() {
		return report;
	}
	
	public void setReport(Report report) {
		this.report = report;
	}
	
	public String getScheduleType() {
		return scheduleType;
	}
	
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	public String getExecutionFrequency() {
		return executionFrequency;
	}
	
	public void setExecutionFrequency(String executionFrequency) {
		this.executionFrequency = executionFrequency;
	}
		
	public Boolean isEnabled() {
		return enabled;
	}
	
	public void isEnabled(Boolean enabled) {
		this.enabled = enabled;
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
	
	public String getEmailSubject() {
		return emailSubject;
	}
	
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	
	public String getEmailContent() {
		return emailContent;
	}
	
	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}
	
	public Set<ReportRecipient> getReportRecipients() {
		return reportRecipients;
	}
	
	public void setReportRecipients(Set<ReportRecipient> reportRecipients) {
		this.reportRecipients = reportRecipients;
	}
	
	public Date getNextScheduleTime() 
			throws ParseException {
		if(enabled) {
			CronExpression cronExpression = new CronExpression(executionFrequency);
			System.out.println("Summary: " + cronExpression.getExpressionSummary());
			Date now = new Date();
			Date nextSchedule;
			
			// Get first date after effective from date, if effective from date is set and its future date
			if(effectiveFrom != null && effectiveFrom.after(now)) {
				nextSchedule = cronExpression.getNextValidTimeAfter(effectiveFrom);
			} else {
				nextSchedule = cronExpression.getNextValidTimeAfter(now);
			}
			
			// Check if nextSchedule has passed effective until date, if effective until date is set
			if(effectiveUntil != null && effectiveUntil.before(nextSchedule)) {
				return null;
			}
			
			return nextSchedule;
		}
		
		// Rule disabled, no next schedule date
		return null;
	}
	
	public boolean isActive() {
		if(!this.enabled) {
			return false;
		}
		
		Date now = new Date();
		
		if(effectiveFrom != null && effectiveFrom.after(now)) {
			return false;
		}
		
		if(effectiveUntil != null && effectiveUntil.before(now)) {
			return false;
		}
		
		return true;
	}
}
