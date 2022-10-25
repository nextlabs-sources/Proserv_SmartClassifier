package com.nextlabs.smartclassifier.dto.v1.request;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class CreationRequest {
	
	@Expose
	private BaseDTO data;
	
	public BaseDTO getData() {
		return data;
	}
	
	public void setData(BaseDTO data) {
		this.data = data;
	}
}
