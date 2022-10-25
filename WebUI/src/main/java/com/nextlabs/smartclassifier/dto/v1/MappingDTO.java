package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;

public class MappingDTO {
	
	@Expose
	private String from;
	@Expose
	private String to;
	
	public MappingDTO() {
		super();
	}
	
	public MappingDTO(String from, String to) {
		super();
		this.from = from;
		this.to = to;
	}
	
	public String getFrom() {
		return from;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
}
