package com.nextlabs.smartclassifier.dto.v1.request;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.dto.v1.RangeDTO;
import com.nextlabs.smartclassifier.dto.v1.ReportFilterGroupDTO;

public class ReportQueryRequest {
	
	@Expose
	private RangeDTO range;
	@Expose
	private List<ReportFilterGroupDTO> filterGroups;
	@Expose
	private List<ReportFilterGroupDTO> refineGroups;
	@Expose
	private String eventStatus;
	@Expose
	private String filePath;
	@Expose
	private String fileName;
	@Expose
	private List<String> fields;
	@Expose
	private List<SortField> sortFields;
	@Expose
	private int pageSize;
	@Expose
	private int pageNo;
	@Expose
	private String layout;
	@Expose
	private String format;
	
	public RangeDTO getRange() {
		return range;
	}
	
	public void setRange(RangeDTO range) {
		this.range = range;
	}
	
	public List<ReportFilterGroupDTO> getFilterGroups() {
		return filterGroups;
	}
	
	public void setFilterGroups(List<ReportFilterGroupDTO> filterGroups) {
		this.filterGroups = filterGroups;
	}
	
	public List<ReportFilterGroupDTO> getRefineGroups() {
		return refineGroups;
	}
	
	public void setRefineGroups(List<ReportFilterGroupDTO> refineGroups) {
		this.refineGroups = refineGroups;
	}
	
	public String getEventStatus() {
		return eventStatus;
	}
	
	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public List<String> getFields() {
		return fields;
	}
	
	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	public List<SortField> getSortFields() {
		return sortFields;
	}
	
	public void setSortFields(List<SortField> sortFields) {
		this.sortFields = sortFields;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getPageNo() {
		return pageNo;
	}
	
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	public String getLayout() {
		return layout;
	}
	
	public void setLayout(String layout) {
		this.layout = layout;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
}
