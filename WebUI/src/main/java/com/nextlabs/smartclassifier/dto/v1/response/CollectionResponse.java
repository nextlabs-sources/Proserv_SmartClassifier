package com.nextlabs.smartclassifier.dto.v1.response;

import com.google.gson.annotations.Expose;

public class CollectionResponse 
		extends ServiceResponse {
	
	@Expose
	private Object data;
	@Expose
	private long totalNoOfRecords;
	
	public CollectionResponse() {
		super();
		this.data = null;
		this.totalNoOfRecords = 0;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public long getTotalNoOfRecords() {
		return totalNoOfRecords;
	}
	
	public void setTotalNoOfRecords(long totalNumberOfRecords) {
		this.totalNoOfRecords = totalNumberOfRecords;
	}
}
