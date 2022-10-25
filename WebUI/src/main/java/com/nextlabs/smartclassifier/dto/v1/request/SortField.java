package com.nextlabs.smartclassifier.dto.v1.request;

import com.google.gson.annotations.Expose;

public class SortField {
	
	@Expose
	private String field;
	@Expose
	private String order;
	
	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
}
