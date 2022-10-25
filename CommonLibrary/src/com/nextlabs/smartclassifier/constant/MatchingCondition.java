package com.nextlabs.smartclassifier.constant;

public enum MatchingCondition {
	
	CONTAINS("CONTAINS", "Contains", ""),
	NOT_CONTAINS("NOT_CONTAINS", "Not Contains", "-"),
	MUST_CONTAINS("MUST_CONTAINS", "Must Contains", "+");
	
	private String code;
	
	private String name;
	
	private String sign;
	
	private MatchingCondition(String code, String name, String sign) {
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
	
	public static MatchingCondition getCondition(String code) {
		if(code != null && code.length() > 0) {
			for(MatchingCondition condition : MatchingCondition.values()) {
				if(condition.getCode().equals(code)) {
					return condition;
				}
			}
		}
		
		return null;
	}
}
