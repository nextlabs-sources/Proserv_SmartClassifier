package com.nextlabs.smartclassifier.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "METADATA_FIELDS")
public class MetadataField {
	
	public static final String FIELD_NAME = "fieldName";
	public static final String DISPLAY_ORDER = "displayOrder";
	public static final String DISPLAY_NAME = "displayName";
	public static final String UI_DATA_TYPE = "uiDataType";
	public static final String STORAGE_DATA_TYPE = "storageDataType";
	public static final String VISIBLE = "visible";
	
	@Id
	@Column(name = "FIELD_NAME", unique = true, nullable = false, length = 100)
	private String fieldName;
	
	@Column(name = "DISPLAY_ORDER", nullable = false)
	private Integer displayOrder;
	
	@Column(name = "DISPLAY_NAME", nullable = false, length = 100)
	private String displayName;
	
	@Column(name = "UI_DATA_TYPE", nullable = false, length = 10)
	private String uiDataType;
	
	@Column(name = "STORAGE_DATA_TYPE", nullable = false, length = 10)
	private String storageDataType;
	
	@Column(name = "VISIBLE", nullable = false)
	private Boolean visible;
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	
	public Boolean isVisible() {
		return visible;
	}
	
	public void isVisible(Boolean visible) {
		this.visible = visible;
	}
}
