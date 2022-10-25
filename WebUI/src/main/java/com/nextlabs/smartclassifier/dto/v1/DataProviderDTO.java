package com.nextlabs.smartclassifier.dto.v1;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.DataProvider;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class DataProviderDTO 
		extends BaseDTO {
	
	@Expose
	private String className;
	@Expose
	private String name;
	@Expose
	private String description;
	@Expose
	private String suggestion;
	
	public DataProviderDTO() {
		super();
	}
	
	public DataProviderDTO(DataProvider dataProvider) {
		super();
		copy(dataProvider);
	}
	
	public void copy(DataProvider dataProvider) {
		if(dataProvider != null) {
			this.id = dataProvider.getId();
			this.className = dataProvider.getClassName();
			this.name = dataProvider.getName();
			this.description = dataProvider.getDescription();
			this.suggestion = dataProvider.getSuggestion();
			this.createdTimestamp = dataProvider.getCreatedOn();
			this.createdOn = dataProvider.getCreatedOn().getTime();
			this.modifiedTimestamp = dataProvider.getModifiedOn();
			this.modifiedOn = dataProvider.getModifiedOn().getTime();
		}
	}
	
	public DataProvider getEntity() {
		DataProvider dataProvider = new DataProvider();
		
		dataProvider.setId(this.id);
		dataProvider.setClassName(this.className);
		dataProvider.setName(this.name);
		dataProvider.setDescription(this.description);
		dataProvider.setSuggestion(this.suggestion);
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			dataProvider.setCreatedOn(new Date(this.createdOn));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			dataProvider.setModifiedOn(new Date(this.modifiedOn));
		}
		
		return dataProvider;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSuggestion() {
		return suggestion;
	}
	
	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}
}
