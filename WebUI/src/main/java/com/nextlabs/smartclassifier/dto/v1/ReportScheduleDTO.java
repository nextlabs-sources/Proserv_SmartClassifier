package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ReportRecipient;
import com.nextlabs.smartclassifier.database.entity.ReportSchedule;
import com.nextlabs.smartclassifier.dto.BaseDTO;
import com.nextlabs.smartclassifier.util.DateUtil;

public class ReportScheduleDTO 
		extends BaseDTO {
	
	@Expose
	private String scheduleType;
	@Expose
	private ExecutionFrequencyDTO executionFrequency;
	@Expose
	private Boolean enabled;
	@Expose
	private Long effectiveFrom;
	@Expose
	private Long effectiveUntil;
	@Expose
	private String emailSubject;
	@Expose
	private String emailContent;
	@Expose
	private List<ReportRecipientDTO> recipients;
	
	public ReportScheduleDTO() {
		super();
	}
	
	public ReportScheduleDTO(ReportSchedule reportSchedule) {
		super();
		copy(reportSchedule);
	}
	
	public void copy(ReportSchedule reportSchedule) {
		if(reportSchedule != null) {
			this.id = reportSchedule.getId();
			this.scheduleType = reportSchedule.getScheduleType();
			this.executionFrequency = new ExecutionFrequencyDTO(reportSchedule.getScheduleType(), reportSchedule.getExecutionFrequency());
			this.enabled = reportSchedule.isEnabled();
			
			if(reportSchedule.getEffectiveFrom() != null) {
				this.effectiveFrom = reportSchedule.getEffectiveFrom().getTime();
			}
			
			if(reportSchedule.getEffectiveUntil() != null) {
				this.effectiveUntil = reportSchedule.getEffectiveUntil().getTime();
			}
			
			this.emailSubject = reportSchedule.getEmailSubject();
			this.emailContent = reportSchedule.getEmailContent();
			
			if(reportSchedule.getReportRecipients() != null
					&& reportSchedule.getReportRecipients().size() > 0) {
				this.recipients = new ArrayList<ReportRecipientDTO>(reportSchedule.getReportRecipients().size());
				
				for(ReportRecipient recipient : reportSchedule.getReportRecipients()) {
					this.recipients.add(new ReportRecipientDTO(recipient));
				}
			}
		}
	}
	
	public ReportSchedule getEntity() {
		ReportSchedule reportSchedule = new ReportSchedule();
		
		reportSchedule.setId(this.id);
		reportSchedule.setScheduleType(this.scheduleType);
		reportSchedule.setExecutionFrequency(this.executionFrequency.getCronExpression(this.scheduleType));
		reportSchedule.isEnabled(this.enabled == null ? false : this.enabled);
		
		if(this.effectiveFrom > 0) {
			reportSchedule.setEffectiveFrom(DateUtil.toStartOfTheDay(this.effectiveFrom));
		}
		
		if(this.effectiveUntil > 0) {
			reportSchedule.setEffectiveUntil(DateUtil.toEndOfTheDay(this.effectiveUntil));
		}
		
		reportSchedule.setEmailSubject(this.emailSubject);
		reportSchedule.setEmailContent(this.emailContent);
		
		if(this.recipients != null
				&& this.recipients.size() > 0) {
			for(ReportRecipientDTO recipientDTO : this.recipients) {
				reportSchedule.getReportRecipients().add(recipientDTO.getEntity());
			}
		}
		
		return reportSchedule;
	}
	
	public String getScheduleType() {
		return scheduleType;
	}
	
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	public ExecutionFrequencyDTO getExecutionFrequency() {
		return executionFrequency;
	}
	
	public void setExecutionFrequency(ExecutionFrequencyDTO executionFrequency) {
		this.executionFrequency = executionFrequency;
	}
	
	public Boolean isEnabled() {
		return enabled;
	}
	
	public void isEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public Long getEffectiveFrom() {
		return effectiveFrom;
	}
	
	public void setEffectiveFrom(Long effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	
	public Long getEffectiveUntil() {
		return effectiveUntil;
	}
	
	public void setEffectiveUntil(Long effectiveUntil) {
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
	
	public List<ReportRecipientDTO> getRecipients() {
		return recipients;
	}
	
	public void setRecipients(List<ReportRecipientDTO> recipients) {
		this.recipients = recipients;
	}
}
