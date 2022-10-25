package com.nextlabs.smartclassifier.dto.v1.request;

import java.util.List;

import com.google.gson.annotations.Expose;

public class SearchField {
		
	@Expose
	private String field;
	@Expose
	private String type;
	@Expose
	private List<SearchValue> values;
	
	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<SearchValue> getValues() {
		return values;
	}
	
	public void setValues(List<SearchValue> values) {
		this.values = values;
	}
}