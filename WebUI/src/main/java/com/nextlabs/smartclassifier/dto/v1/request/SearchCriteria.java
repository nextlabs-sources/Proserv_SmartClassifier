package com.nextlabs.smartclassifier.dto.v1.request;

import java.util.List;

import com.google.gson.annotations.Expose;

public class SearchCriteria {
	
	@Expose
	private List<SearchField> fields;
	
	public List<SearchField> getFields() {
		return fields;
	}
	
	public void setFields(List<SearchField> fields) {
		this.fields = fields;
	}
}