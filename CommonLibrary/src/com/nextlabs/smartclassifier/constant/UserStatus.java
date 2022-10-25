package com.nextlabs.smartclassifier.constant;

public enum UserStatus {
	
	ACTIVE("ACTIVE", "Active"),
	SUSPENDED("SUSPENDED", "Suspended"),
	LOCKED("LOCKED", "Locked");
	
	private String code;
	
	private String name;
	
	private UserStatus(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static UserStatus getStatus(String code) {
		if(code != null) {
			for(UserStatus status : UserStatus.values()) {
				if(status.code.equals(code)) {
					return status;
				}
			}
		}
		
		return null;
	}
}
