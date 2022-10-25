package com.nextlabs.smartclassifier.dto.v1.response;

import com.google.gson.annotations.Expose;

public class ExecutionResponse 
		extends ServiceResponse {
	
	@Expose
	private Object data;
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
}
