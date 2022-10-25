package com.nextlabs.smartclassifier.constant;

public enum Placeholder {
	RULE_ID("%rule_id%");

	private final String value;

	private Placeholder(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
