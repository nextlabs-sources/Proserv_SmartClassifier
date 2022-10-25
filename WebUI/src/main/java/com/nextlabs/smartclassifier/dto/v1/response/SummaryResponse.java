package com.nextlabs.smartclassifier.dto.v1.response;

import com.google.gson.annotations.Expose;

public class SummaryResponse 
		extends ServiceResponse {
	
	@Expose
	private Object data;
	@Expose
	private int criticalHeartbeat;	
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
	public int getCriticalHeartbeat() {
		return criticalHeartbeat;
	}
	
	public void setCriticalHeartbeat(int criticalHeartbeat) {
		this.criticalHeartbeat = criticalHeartbeat;
	}
}
