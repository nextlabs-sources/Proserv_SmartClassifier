package com.nextlabs.smartclassifier.constant;

public enum RollbackErrorType {
	
	DELETE_FAILED("D", "Delete file failed"),
	REPLACE_FAILED("R", "Replace file failed");
	
	private String code;
	
	private String name;
	
	private RollbackErrorType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static RollbackErrorType getType(String code) {
		if(code != null) {
			for(RollbackErrorType type : RollbackErrorType.values()) {
				if(type.code.equals(code)) {
					return type;
				}
			}
		}
		
		return null;
	}
}
