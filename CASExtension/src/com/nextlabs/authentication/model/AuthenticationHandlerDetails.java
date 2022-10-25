package com.nextlabs.authentication.model;

import com.google.gson.annotations.Expose;

public class AuthenticationHandlerDetails {
	
	private transient String type;
	@Expose
	private boolean secureChannel;
	@Expose
	private String url;
	@Expose
	private String domain;
	@Expose
	private String rootDN;
	@Expose
	private String username;
	@Expose
	private String password;
	@Expose
	private String userFilter;
	@Expose
	private String userSearchBase;
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isSecureChannel() {
		return secureChannel;
	}
	
	public void setSecureChannel(boolean secureChannel) {
		this.secureChannel = secureChannel;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getRootDN() {
		return rootDN;
	}
	
	public void setRootDN(String rootDN) {
		this.rootDN = rootDN;
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
	
	public String getUserFilter() {
		return userFilter;
	}
	
	public void setUserFilter(String userFilter) {
		this.userFilter = userFilter;
	}
	
	public String getUserSearchBase() {
		return userSearchBase;
	}
	
	public void setUserSearchBase(String userSearchBase) {
		this.userSearchBase = userSearchBase;
	}
}
