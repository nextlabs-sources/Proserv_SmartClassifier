package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;

public class FacetDTO {
	
	public static final String VALUE = "value";
	public static final String DISPLAY_NAME = "displayName";
	public static final String COUNT = "count";
	
	@Expose
	private String value;
	@Expose
	private String displayName;
	@Expose
	private Long count;
	
	public FacetDTO() {
		super();
	}
	
	public FacetDTO(String value, String displayName, Long count) {
		super();
		this.value = value;
		this.displayName = displayName;
		this.count = count;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Long getCount() {
		return count;
	}
	
	public void setCount(Long count) {
		this.count = count;
	}
}
