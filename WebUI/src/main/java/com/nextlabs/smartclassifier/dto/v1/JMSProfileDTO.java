package com.nextlabs.smartclassifier.dto.v1;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.JMSProfile;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class JMSProfileDTO 
		extends BaseDTO {
	
	@Expose
	private String displayName;
	@Expose
	private String description;
	@Expose
	private String type;
	@Expose
	private String providerURL;
	@Expose
	private String initialContextFactory;
	@Expose
	private String username;
	@Expose
	private String password;
	@Expose
	private String serviceName;
	@Expose
	private Long connectionRetryInterval;
	
	public JMSProfileDTO() {
		super();
	}
	
	public JMSProfileDTO(JMSProfile jmsProfile) {
		super();
		copy(jmsProfile);
	}
	
	public void copy(JMSProfile jmsProfile) {
		if(jmsProfile != null) {
			this.id = jmsProfile.getId();
			this.displayName = jmsProfile.getDisplayName();
			this.description = jmsProfile.getDescription();
			this.type = jmsProfile.getType();
			this.providerURL = jmsProfile.getProviderURL();
			this.initialContextFactory = jmsProfile.getInitialContextFactory();
			this.username = jmsProfile.getUsername();
			this.password = jmsProfile.getPassword();
			this.serviceName = jmsProfile.getServiceName();
			this.connectionRetryInterval = jmsProfile.getConnectionRetryInterval();
			this.createdTimestamp = jmsProfile.getCreatedOn();
			this.createdOn = jmsProfile.getCreatedOn().getTime();
			this.modifiedTimestamp = jmsProfile.getModifiedOn();
			this.modifiedOn = jmsProfile.getModifiedOn().getTime();
		}
	}
	
	public JMSProfile getEntity() {
		JMSProfile  jmsProfile = new JMSProfile();
		
		jmsProfile.setId(this.id);
		jmsProfile.setDisplayName(this.displayName);
		jmsProfile.setDescription(this.description);
		jmsProfile.setType(this.type);
		jmsProfile.setProviderURL(this.providerURL);
		jmsProfile.setInitialContextFactory(this.initialContextFactory);
		jmsProfile.setUsername(this.username);
		jmsProfile.setPassword(this.password);
		jmsProfile.setServiceName(this.serviceName);
		jmsProfile.setConnectionRetryInterval(this.connectionRetryInterval);
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			jmsProfile.setCreatedOn(new Date(this.createdOn));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			jmsProfile.setModifiedOn(new Date(this.modifiedOn));		
		}
		
		return jmsProfile;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getProviderURL() {
		return providerURL;
	}
	
	public void setProviderURL(String providerURL) {
		this.providerURL = providerURL;
	}
	
	public String getInitialContextFactory() {
		return initialContextFactory;
	}
	
	public void setInitialContextFactory(String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public Long getConnectionRetryInterval() {
		return connectionRetryInterval;
	}
	
	public void setConnectionRetryInterval(Long connectionRetryInterval) {
		this.connectionRetryInterval = connectionRetryInterval;
	}
}
