package com.nextlabs.smartclassifier.database.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "USERS", uniqueConstraints = {@UniqueConstraint(columnNames = {"USERNAME"})})
public class User {
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String USER_TYPE = "userType";
	public static final String USERNAME = "username";
	public static final String DISPLAY_NAME = "displayName";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String EMAIL = "email";
	public static final String PASSWORD = "password";
	public static final String ADMIN = "admin";
	public static final String VISIBLE = "visible";
	public static final String ENABLED = "enabled";
	public static final String STATUS = "status";
	public static final String AUTHENTICATION_HANDLER_ID = "authenticationHandler.id";
	public static final String PASSWORD_CHANGED_ON = "passwordChangedOn";
	public static final String FAILED_ATTEMPT = "failedAttempt";
	public static final String LOGIN_ON = "loginOn";
	public static final String LAST_LOGIN_ON = "lastLoginOn";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "USERS_SEQ")})
	private Long id;
	
	@Column(name = "USER_TYPE", nullable = false, length = 1)
	private String userType;
	
	@Column(name = "USERNAME", nullable = false, length = 100)
	private String username;
	
	@Column(name = "DISPLAY_NAME", nullable = true, length = 255)
	private String displayName;
	
	@Column(name = "FIRST_NAME", nullable = true, length = 70)
	private String firstName;
	
	@Column(name = "LAST_NAME", nullable = true, length = 35)
	private String lastName;
	
	@Column(name = "EMAIL", nullable = true, length = 320)
	private String email;
	
	@Column(name = "PASSWORD", nullable = true, length = 256)
	private String password;
	
	@Transient
	private String newPassword;
	
	@Column(name = "ADMIN", nullable = false)
	private Boolean admin;
	
	@Column(name = "VISIBLE", nullable = false)
	private Boolean visible;
	
	@Column(name = "ENABLED", nullable = false)
	private Boolean enabled;
	
	@Column(name = "STATUS", nullable = false, length = 10)
	private String status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AUTH_HANDLER_ID")
	private AuthenticationHandler authenticationHandler;
	
	@Column(name = "PASSWORD_CHANGED_ON", nullable = true)
	private Long passwordChangedOn;
	
	@Column(name = "FAILED_ATTEMPT", nullable = true)
	private Integer failedAttempt;
	
	@Column(name = "LOGIN_ON", nullable = true)
	private Long loginOn;
	
	@Column(name = "LAST_LOGIN_ON", nullable = true)
	private Long lastLoginOn;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getUserType() {
		return userType;
	}
	
	public void setUserType(String userType) {
		this.userType = userType;
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
	
	public AuthenticationHandler getAuthenticationHandler() {
		return authenticationHandler;
	}
	
	public void setAuthenticationHandler(AuthenticationHandler authenticationHandler) {
		this.authenticationHandler = authenticationHandler;
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
	
	public Date getCreatedOn() {
		return createdOn;
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	public Date getModifiedOn() {
		return modifiedOn;
	}
	
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
}
