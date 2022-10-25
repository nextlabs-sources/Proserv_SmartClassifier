package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;

public class CodeValueDTO {
	
	@Expose
	private String code;
	@Expose
	private String value;
	
	public CodeValueDTO() {
		super();
	}
	
	public CodeValueDTO(String code, String value) {
		super();
		this.code = code;
		this.value = value;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
