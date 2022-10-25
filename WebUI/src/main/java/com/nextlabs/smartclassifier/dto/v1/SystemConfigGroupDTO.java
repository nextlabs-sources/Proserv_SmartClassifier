package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.SystemConfig;
import com.nextlabs.smartclassifier.database.entity.SystemConfigGroup;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class SystemConfigGroupDTO 
		extends BaseDTO {
	
	@Expose
	private String name;
	@Expose
	private String description;
	@Expose
	private Integer displayOrder;
	@Expose
	private List<SystemConfigDTO> systemConfigs;
	
	public SystemConfigGroupDTO() {
		super();
	}
	
	public SystemConfigGroupDTO(SystemConfigGroup systemConfigGroup) {
		super();
		copy(systemConfigGroup);
	}
	
	public void copy(SystemConfigGroup systemConfigGroup) {
		if(systemConfigGroup != null) {
			this.id = systemConfigGroup.getId();
			this.name = systemConfigGroup.getName();
			this.description = systemConfigGroup.getDescription();
			this.displayOrder = systemConfigGroup.getDisplayOrder();
			
			if(systemConfigGroup.getSystemConfigs() != null) {
				this.systemConfigs = new ArrayList<SystemConfigDTO>();
				for(SystemConfig systemConfig : systemConfigGroup.getSystemConfigs()) {
					this.systemConfigs.add(new SystemConfigDTO(systemConfig));
				}
			}
			
			this.createdTimestamp = systemConfigGroup.getCreatedOn();
			this.createdOn = systemConfigGroup.getCreatedOn().getTime();
			this.modifiedTimestamp = systemConfigGroup.getModifiedOn();
			this.modifiedOn = systemConfigGroup.getModifiedOn().getTime();
		}
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
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public List<SystemConfigDTO> getSystemConfigs() {
		return systemConfigs;
	}
	
	public void setSystemConfigs(List<SystemConfigDTO> systemConfigs) {
		this.systemConfigs = systemConfigs;
	}
}
