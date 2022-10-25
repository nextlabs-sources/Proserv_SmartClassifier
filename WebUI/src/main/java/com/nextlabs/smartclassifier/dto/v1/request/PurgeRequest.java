package com.nextlabs.smartclassifier.dto.v1.request;

import com.google.gson.annotations.Expose;

public class PurgeRequest {
	
	@Expose
	private Long dateInMillis;
	
	public Long getDateInMillis() {
		return dateInMillis;
	}
	
	public void setDateInMillis(Long dateInMillis) {
		this.dateInMillis = dateInMillis;
	}
}
