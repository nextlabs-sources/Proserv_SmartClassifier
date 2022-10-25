package com.nextlabs.smartclassifier.constant;

public enum DeploymentType {

	POC("POC"), PRODUCTION("PRODUCTION");

	private final String value;

	DeploymentType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value; 
	}
}
