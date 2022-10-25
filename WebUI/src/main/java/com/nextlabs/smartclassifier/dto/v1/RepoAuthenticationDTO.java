package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class RepoAuthenticationDTO 
		extends BaseDTO {
	
	@Expose
	private String username;
	
	@Expose
	private String password;
	
	public RepoAuthenticationDTO() {
		super();
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
