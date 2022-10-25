package com.nextlabs.smartclassifier.constant;

public enum MetadataMatchingCondition {
	
	NOT("NOT", "Not", "!"),
	CONTAINS("CONTAINS", "Contains", ""),
	NOT_CONTAINS("NOT_CONTAINS", "Not Contains", "-"),
	MUST_CONTAINS("MUST_CONTAINS", "Must Contains", "+"),
//	BETWEEN("BETWEEN", "Between", "{%s TO %s}"),
	NUM_LESSER("NUM_LESSER", "Number Lesser", "{* TO %s}"),
	NUM_LESSER_OR_EQUAL("NUM_LESSER_OR_EQUAL", "Number Lesser or Equal", "[* TO %s]"),
	NUM_EQUALS("NUM_EQUALS", "Number Equals", "[%s TO %s]"),
	NUM_GREATER_OR_EQUAL("NUM_GREATER_OR_EQUAL", "Number Greater or Equal", "[%s TO *]"),
	NUM_GREATER("NUM_GREATER", "Number Greater", "{%s TO *}"),
	DATE_BEFORE("DATE_BEFORE", "Date Before", "{* TO %s}"),
	DATE_BEFORE_OR_EQUAL("DATE_BEFORE_OR_EQUAL", "Date Before or Equal", "[* TO %s]"),
	DATE_EQUALS("DATE_EQUALS", "Date Equals", "[%s TO %s]"), 
	DATE_AFTER_OR_EQUAL("DATE_AFTER_OR_EQUAL", "Date After or Equal", "[%s TO *]"),
	DATE_AFTER("DATE_AFTER", "Date After", "{%s TO *}");
	
	private String code;
	
	private String name;
	
	private String sign;
	
	private MetadataMatchingCondition(String code, String name, String sign) {
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
	
	public static MetadataMatchingCondition getCondition(String code) {
		if(code != null && code.length() > 0) {
			for(MetadataMatchingCondition condition : MetadataMatchingCondition.values()) {
				if(condition.getCode().equals(code)) {
					return condition;
				}
			}
		}
		
		return null;
	}
}
