package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.Report;
import com.nextlabs.smartclassifier.database.entity.ReportFilterGroup;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ReportDTO 
		extends BaseDTO {
	
	@Expose
	private String name;
	@Expose
	private String description;
	@Expose
	private String type;
	@Expose
	private RangeDTO range;
	@Expose
	private String createdBy;
	@Expose
	private List<ReportFilterGroupDTO> filterGroups;
	@Expose
	private String eventStatus;
	@Expose
	private ReportScheduleDTO schedule;
	
	public ReportDTO() {
		super();
	}
	
	public ReportDTO(Report report) {
		super();
		copy(report);
	}
	
	public void copy(Report report) {
		if(report != null) {
			this.id = report.getId();
			this.name = report.getName();
			this.description = report.getDescription();
			this.type = report.getType();
			this.range = gson.fromJson(report.getRange(), RangeDTO.class);
			this.createdBy = report.getCreatedBy();
			
			if(report.getReportFilterGroups() != null ) {
				this.filterGroups = new ArrayList<ReportFilterGroupDTO>();
				
				for(ReportFilterGroup reportFilterGroup : report.getReportFilterGroups()) {
					this.filterGroups.add(new ReportFilterGroupDTO(reportFilterGroup));
				}
			}
			this.eventStatus = report.getEventStatus();
			
			if(report.getReportSchedule() != null) {
				this.schedule = new ReportScheduleDTO(report.getReportSchedule());
			}
			
			if(report.getCreatedOn() != null) {
				this.createdTimestamp = report.getCreatedOn();
				this.createdOn = report.getCreatedOn().getTime();
			}
			
			if(report.getModifiedOn() != null) {
				this.modifiedTimestamp = report.getModifiedOn();
				this.modifiedOn = report.getModifiedOn().getTime();
			}
		}
	}
	
	public Report getEntity() {
		Report report = new Report();
		
		report.setId(this.id);
		report.setName(this.name);
		report.setDescription(this.description);
		report.setType(this.type);
		report.setRange(gson.toJson(this.range));
		report.setCreatedBy(this.createdBy);
		
		if(this.filterGroups != null
				&& this.filterGroups.size() > 0) {
			for(ReportFilterGroupDTO reportFilterGroup : this.filterGroups) {
				report.getReportFilterGroups().add(reportFilterGroup.getEntity());
			}
		}
		report.setEventStatus(this.eventStatus);
		
		if(this.schedule != null) {
			report.setReportSchedule(this.schedule.getEntity());
		}
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			report.setCreatedOn(new Date(this.createdOn));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			report.setModifiedOn(new Date(this.modifiedOn));
		}
		
		return report;
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
	
	public RangeDTO getRange() {
		return range;
	}
	
	public void setRange(RangeDTO range) {
		this.range = range;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public List<ReportFilterGroupDTO> getFilterGroups() {
		return filterGroups;
	}
	
	public void setFilterGroups(List<ReportFilterGroupDTO> reportFilterGroups) {
		this.filterGroups = reportFilterGroups;
	}
	
	public String getEventStatus() {
		return eventStatus;
	}
	
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	
	public ReportScheduleDTO getSchedule() {
		return schedule;
	}
	
	public void setSchedule(ReportScheduleDTO schedule) {
		this.schedule = schedule;
	}
}
