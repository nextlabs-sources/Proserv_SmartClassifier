package com.nextlabs.smartclassifier.mail;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class MailServiceConfig {
	
	private String debug;
	private String host;
	private String port;
	private String authentication;
	private String startTLS;
	private String checkServerIdentity;
	private String username;
	private String password;
	
	private InternetAddress sender;
	
	public String getDebug() {
		return debug;
	}
	
	public void setDebug(String debug) {
		this.debug = debug;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getPort() {
		return port;
	}
	
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getAuthentication() {
		return authentication;
	}
	
	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
	
	public String getStartTLS() {
		return startTLS;
	}
	
	public void setStartTLS(String startTLS) {
		this.startTLS = startTLS;
	}
	
	public String getCheckServerIdentity() {
		return checkServerIdentity;
	}
	
	public void setCheckServerIdentity(String checkServerIdentity) {
		this.checkServerIdentity = checkServerIdentity;
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
	
	public InternetAddress getSender() {
		return this.sender;
	}
	
	public void setSender(InternetAddress sender) {
		this.sender = sender;
	}
	
	public void setSender(String sender) 
			throws AddressException {
		this.sender = new InternetAddress(sender);
	}
	
	/**
	 * Override hashCode() implementation of object class.
	 * 
	 * @return hash code of the object.
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(debug)
										  .append(host)
										  .append(port)
										  .append(authentication)
										  .append(startTLS)
										  .append(checkServerIdentity)
										  .append(username)
										  .append(password)
										  .append(sender)
										  .toHashCode();
	}
	
	/**
	 * Override equals(obj) implementation of object class.
	 * 
	 * @param obj to compare with
	 * @return true if equals
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MailServiceConfig)) {
			return false;
		}
		
		if(obj == this) {
			return true;
		}
		
		MailServiceConfig rhs = (MailServiceConfig)obj;
		return new EqualsBuilder().append(debug, rhs.debug)
								  .append(host, rhs.host)
								  .append(port, rhs.port)
								  .append(authentication, rhs.authentication)
								  .append(startTLS, rhs.startTLS)
								  .append(checkServerIdentity, rhs.checkServerIdentity)
								  .append(username, rhs.username)
								  .append(password, rhs.password)
								  .append(sender, rhs.sender)
								  .isEquals();
	}
}
