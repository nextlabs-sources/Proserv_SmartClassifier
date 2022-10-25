package com.nextlabs.smartclassifier.dto.v1.request;

import java.util.List;

import com.google.gson.annotations.Expose;

public class SearchValue {
	
	@Expose
	private String type;
	@Expose
	private List<String> value;
	@Expose
	private long fromDate;
	@Expose
	private long toDate;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<String> getValue() {
		return value;
	}
	
	public void setValue(List<String> value) {
		this.value = value;
	}
	
	public long getFromDate() {
		return fromDate;
	}
	
	public void setFromDate(long fromDate) {
		this.fromDate = fromDate;
	}
	
	public long getToDate() {
		return toDate;
	}
	
	public void setToDate(long toDate) {
		this.toDate = toDate;
	}
}
