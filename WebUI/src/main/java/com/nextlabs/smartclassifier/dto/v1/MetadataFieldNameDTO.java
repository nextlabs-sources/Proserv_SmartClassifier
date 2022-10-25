package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;

public class MetadataFieldNameDTO {
	
	@Expose
	private String code;
	@Expose
	private String value;
	@Expose
	private String uiDataType;
	@Expose
	private String storageDataType;
	
	public MetadataFieldNameDTO() {
		super();
	}
	
	public MetadataFieldNameDTO(String code, String value, String uiDataType, String storageDataType) {
		super();
		this.code = code;
		this.value = value;
		this.uiDataType = uiDataType;
		this.storageDataType = storageDataType;
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
	
	public String getUIDataType() {
		return uiDataType;
	}
	
	public void setUIDataType(String uiDataType) {
		this.uiDataType = uiDataType;
	}
	
	public String getStorageDataType() {
		return storageDataType;
	}
	
	public void setStorageDataType(String storageDataType) {
		this.storageDataType = storageDataType;
	}
}
