package com.nextlabs.smartclassifier.constant;

public enum DocumentMatchingCondition {
	
	IN("CONTAINS", "In", ""),
	NOT_IN("NOT_CONTAINS", "Not In", "-");
	
	private String code;
	
	private String name;
	
	private String sign;
	
	private DocumentMatchingCondition(String code, String name, String sign) {
		this.code = code;
		this.name = name;
		this.sign = sign;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSign() {
		return sign;
	}
	
	public static DocumentMatchingCondition getCondition(String code) {
		if(code != null && code.length() > 0) {
			for(DocumentMatchingCondition condition : DocumentMatchingCondition.values()) {
				if(condition.getCode().equals(code)) {
					return condition;
				}
			}
		}
		
		return null;
	}
}
