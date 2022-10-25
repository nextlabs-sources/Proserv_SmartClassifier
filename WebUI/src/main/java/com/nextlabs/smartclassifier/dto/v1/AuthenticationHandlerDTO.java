package com.nextlabs.smartclassifier.dto.v1;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class AuthenticationHandlerDTO
		extends BaseDTO {
	
	@Expose
	private String name;
	@Expose
	private String type;
	@Expose
	private LDAPConfigurationDataDTO configurationData;
	@Expose
	private Set<MappingDTO> userAttributeMapping;
	@Expose
	private String filter;
	
	public AuthenticationHandlerDTO() {
		super();
	}
	
	public AuthenticationHandlerDTO(AuthenticationHandler authenticationHandler) {
		super();
		copy(authenticationHandler);
	}
	
	public void copy(AuthenticationHandler authenticationHandler) {
		if(authenticationHandler != null) {
			this.id = authenticationHandler.getId();
			this.name = authenticationHandler.getName();
			this.type = authenticationHandler.getType();
			if(StringUtils.isNotBlank(authenticationHandler.getConfigurationData())) {
				this.configurationData = gson.fromJson(authenticationHandler.getConfigurationData(), LDAPConfigurationDataDTO.class);
			}
			if(StringUtils.isNotBlank(authenticationHandler.getUserAttributeMapping())) {
				this.userAttributeMapping = gson.fromJson(authenticationHandler.getUserAttributeMapping(), new TypeToken<LinkedHashSet<MappingDTO>>(){}.getType());
			}
			this.createdTimestamp = authenticationHandler.getCreatedOn();
			this.createdOn = authenticationHandler.getCreatedOn().getTime();
			this.modifiedTimestamp = authenticationHandler.getModifiedOn();
			this.modifiedOn = authenticationHandler.getModifiedOn().getTime();
		}
	}
	
	public AuthenticationHandler getEntity() {
		AuthenticationHandler authenticationHandler = new AuthenticationHandler();
		
		authenticationHandler.setId(this.id);
		authenticationHandler.setName(this.name);
		authenticationHandler.setType(this.type);
		authenticationHandler.setConfigurationData(gson.toJson(this.configurationData));
		authenticationHandler.setUserAttributeMapping(gson.toJson(this.userAttributeMapping));
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			authenticationHandler.setCreatedOn(new Date(this.createdOn));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			authenticationHandler.setModifiedOn(new Date(this.modifiedOn));
		}
		
		return authenticationHandler;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public LDAPConfigurationDataDTO getConfigurationData() {
		return configurationData;
	}
	
	public void setConfigurationData(LDAPConfigurationDataDTO configurationData) {
		this.configurationData = configurationData;
	}
	
	public Set<MappingDTO> getUserAttributeMapping() {
		return userAttributeMapping;
	}
	
	public void setUserAttributeMapping(Set<MappingDTO> userAttributeMapping) {
		this.userAttributeMapping = userAttributeMapping;
	}
	
	public String getFilter() {
		return filter;
	}
	
	public void setFilter(String filter) {
		this.filter = filter;
	}
}
