package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;

public class RangeDTO {
	
	@Expose
	private String option;
	@Expose
	private String unit;
	@Expose
	private Integer value;
	@Expose
	private Long from;
	@Expose
	private Long to;
	@Expose
	private String display;
	
	public RangeDTO() {
		super();
	}
	
	public String getOption() {
		return option;
	}
	
	public void setOption(String option) {
		this.option = option;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public Integer getValue() {
		return value;
	}
	
	public void setValue(Integer value) {
		this.value = value;
	}
	
	public Long getFrom() {
		return from;
	}
	
	public void setFrom(Long from) {
		this.from = from;
	}
	
	public Long getTo() {
		return to;
	}
	
	public void setTo(Long to) {
		this.to = to;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public void setDisplay(String display) {
		this.display = display;
	}
}
