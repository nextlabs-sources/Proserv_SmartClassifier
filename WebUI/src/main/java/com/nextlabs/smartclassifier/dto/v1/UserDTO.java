package com.nextlabs.smartclassifier.dto.v1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.AuthenticationHandler;
import com.nextlabs.smartclassifier.database.entity.User;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class UserDTO
		extends BaseDTO {
	
	@Expose
	private String type;
	@Expose
	private String username;
	@Expose
	private String displayName;
	@Expose
	private String firstName;
	@Expose
	private String lastName;
	@Expose
	private String email;
	@Expose
	private String password;
	@Expose
	private String newPassword;
	@Expose
	private Boolean admin;
	@Expose
	private Boolean visible;
	@Expose
	private Boolean enabled;
	@Expose
	private String status;
	@Expose
	private Long authenticationHandlerId;
	@Expose
	private Long passwordChangedOn;
	@Expose
	private Integer failedAttempt;
	@Expose
	private Long loginOn;
	@Expose
	private Long lastLoginOn;
	
	public UserDTO() {
		super();
	}
	
	public UserDTO(User user) {
		super();
		copy(user);
	}
	
	public void copy(User user) {
		if(user != null) {
			this.id = user.getId();
			this.type = user.getUserType();
			this.username = user.getUsername();
			this.displayName = user.getDisplayName();
			this.firstName = user.getFirstName();
			this.lastName = user.getLastName();
			this.email = user.getEmail();
			this.admin	= user.isAdmin();
			this.visible = user.isVisible();
			this.enabled = user.isEnabled();
			this.status = user.getStatus();
			if(user.getAuthenticationHandler() != null) {
				this.authenticationHandlerId = user.getAuthenticationHandler().getId();
			}
			this.passwordChangedOn = user.getPasswordChangedOn();
			this.failedAttempt = user.getFailedAttempt();
			this.loginOn = user.getLoginOn();
			this.lastLoginOn = user.getLastLoginOn();
			this.createdTimestamp = user.getCreatedOn();
			this.createdOn = user.getCreatedOn().getTime();
			this.modifiedTimestamp = user.getModifiedOn();
			this.modifiedOn = user.getModifiedOn().getTime();
		}
	}
	
	public User getEntity() {
		User user = new User();
		
		user.setId(this.id);
		user.setUserType(this.type);
		user.setUsername(this.username);
		if(StringUtils.isNotBlank(this.displayName)) {
			user.setDisplayName(this.displayName);
		} else {
			user.setDisplayName(this.firstName + " " + (StringUtils.isBlank(this.lastName) ? "" : this.lastName));
		}
		user.setFirstName(this.firstName);
		user.setLastName(this.lastName);
		user.setEmail(this.email);
		user.setPassword(hashPassword(this.password));
		user.setNewPassword(hashPassword(this.newPassword));
		user.isAdmin(this.admin == null ? false : this.admin);
		user.isVisible(this.visible == null ? false : this.visible);
		user.isEnabled(this.enabled == null ? false : this.enabled);
		user.setStatus(this.status);
		if(this.authenticationHandlerId != null 
				&& this.authenticationHandlerId > 0) {
			AuthenticationHandler authenticationHandler = new AuthenticationHandler();
			authenticationHandler.setId(this.authenticationHandlerId);
			
			user.setAuthenticationHandler(authenticationHandler);
		}
		
		if(this.createdOn != null
				&& this.createdOn > 0) {
			user.setCreatedOn(new Date(this.createdOn));
		}
		
		if(this.modifiedOn != null
				&& this.modifiedOn > 0) {
			user.setModifiedOn(new Date(this.modifiedOn));
		}
		
		return user;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public Boolean isAdmin() {
		return admin;
	}
	
	public void isAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	public Boolean isVisible() {
		return visible;
	}
	
	public void isVisible(Boolean visible) {
		this.visible = visible;
	}
	
	public Boolean isEnabled() {
		return enabled;
	}
	
	public void isEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Long getAuthenticationHandlerId() {
		return authenticationHandlerId;
	}
	
	public void setAuthenticationHandlerId(Long authenticationHandlerId) {
		this.authenticationHandlerId = authenticationHandlerId;
	}
	
	public Long getPasswordChangedOn() {
		return passwordChangedOn;
	}
	
	public void setPasswordChangedOn(Long passwordChangedOn) {
		this.passwordChangedOn = passwordChangedOn;
	}
	
	public Integer getFailedAttempt() {
		return failedAttempt;
	}
	
	public void setFailedAttempt(Integer failedAttempt) {
		this.failedAttempt = failedAttempt;
	}
	
	public Long getLoginOn() {
		return loginOn;
	}
	
	public void setLoginOn(Long loginOn) {
		this.loginOn = loginOn;
	}
	
	public Long getLastLoginOn() {
		return lastLoginOn;
	}
	
	public void setLastLoginOn(Long lastLoginOn) {
		this.lastLoginOn = lastLoginOn;
	}
	
	private String hashPassword(String password) {
		if(StringUtils.isNotBlank(password)) {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.update(password.getBytes());
				
				StringBuilder stringBuilder = new StringBuilder();
				
				for(byte byteData : messageDigest.digest()) {
					stringBuilder.append(Integer.toString((byteData & 0xff) + 0x100, 16).substring(1));
				}
				
				return stringBuilder.toString();
			} catch(NoSuchAlgorithmException err) {
				// Nothing we can do . . . 
			}
		}
		
		return null;
	}
}
