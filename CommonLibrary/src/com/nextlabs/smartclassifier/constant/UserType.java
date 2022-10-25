package com.nextlabs.smartclassifier.constant;

public enum UserType {
	
	INTERNAL("I", "Internal"),
	LDAP("L", "LDAP");
	
	private String code;
	
	private String name;
	
	private UserType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static UserType getType(String code) {
		if(code != null) {
			for(UserType type : UserType.values()) {
				if(type.code.equals(code)) {
					return type;
				}
			}
		}
		
		return null;
	}
}
