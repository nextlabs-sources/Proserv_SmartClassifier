package com.nextlabs.smartclassifier.dto;

import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public abstract class BaseDTO {
	
	protected static transient Gson gson = new Gson();
	@Expose
	protected Long id;
	protected Date createdTimestamp;
	@Expose
	protected Long createdOn;
	protected Date modifiedTimestamp;
	@Expose
	protected Long modifiedOn;
	
	protected BaseDTO() {
		super();
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
		this.createdOn = createdTimestamp.getTime();
	}
	
	public Long getCreatedOn() {
		return createdOn;
	}
	
	public Date getModifiedTimestamp() {
		return modifiedTimestamp;
	}
	
	public void setModifiedTimestamp(Date modifiedTimestamp) {
		this.modifiedTimestamp = modifiedTimestamp;
		this.modifiedOn = modifiedTimestamp.getTime();
	}
	
	public Long getModifiedOn() {
		return modifiedOn;
	}
	
	public void setModifiedOn(Long modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
}
