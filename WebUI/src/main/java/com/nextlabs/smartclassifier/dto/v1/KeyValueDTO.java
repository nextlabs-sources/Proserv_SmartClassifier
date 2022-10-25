package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;

public class KeyValueDTO {
	
	@Expose
	private String key;
	@Expose
	private String value;
	
	public KeyValueDTO() {
		super();
	}
	
	public KeyValueDTO(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
