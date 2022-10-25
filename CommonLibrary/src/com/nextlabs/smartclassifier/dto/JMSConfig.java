package com.nextlabs.smartclassifier.dto;

import com.nextlabs.smartclassifier.constant.JMSType;
import com.nextlabs.smartclassifier.database.entity.JMSProfile;

public class JMSConfig 
		extends BaseDTO {
	
	private String displayName;
	private String description;
	private JMSType type;
	private String providerURL;
	private String initialContextFactory;
	private String serviceName;
	private Long connectionRetryInterval;
	private String username;
	private String password;
	
	public void copy(JMSProfile profile) {
		if(profile != null) {
			this.id = profile.getId();
			this.displayName = profile.getDisplayName();
			this.description = profile.getDescription();
			this.type = JMSType.getType(profile.getType());
			this.providerURL = profile.getProviderURL();
			this.initialContextFactory = profile.getInitialContextFactory();
			this.serviceName = profile.getServiceName();
			this.connectionRetryInterval = profile.getConnectionRetryInterval();
			this.username = profile.getUsername();
			this.password = profile.getPassword();
			this.createdTimestamp = profile.getCreatedOn();
			this.modifiedTimestamp = profile.getModifiedOn();
		}
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
	
	public JMSType getType() {
		return type;
	}
	
	public void setType(JMSType type) {
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
}
