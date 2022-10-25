package com.nextlabs.smartclassifier.dto.v1.request;

import java.util.List;

import com.google.gson.annotations.Expose;

public class RetrievalRequest {
	
	@Expose
	private List<SearchCriteria> criteria;
	@Expose
	private List<String> fields;
	@Expose
	private List<SortField> sortFields;
	@Expose
	private int pageSize;
	@Expose
	private int pageNo;
	
	public List<SearchCriteria> getCriteria() {
		return criteria;
	}
	
	public void setCriteria(List<SearchCriteria> criteria) {
		this.criteria = criteria;
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
}
