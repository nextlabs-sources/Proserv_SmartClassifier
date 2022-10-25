package com.nextlabs.smartclassifier.constant;

public enum AuthenticationHandlerType {
	
	LOCAL_DB("LOCAL_DB", "Smart Classifier database"),
	FOREIGN_DB("FOREIGN_DB", "Non-Smart Classifier database"),
	AD("AD", "Active Directory (AD)"),
	LDAP("LDAP", "Lightweight Directory Access Protocol (LDAP)");
	
	private String code;
	
	private String name;
	
	private AuthenticationHandlerType(final String code, final String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public static AuthenticationHandlerType getType(final String handlerCode) {
		if(handlerCode != null) {
			for(AuthenticationHandlerType handler : AuthenticationHandlerType.values()) {
				if(handler.getCode().equals(handlerCode)) {
					return handler;
				}
			}
		}
		
		return null;
	}
}
