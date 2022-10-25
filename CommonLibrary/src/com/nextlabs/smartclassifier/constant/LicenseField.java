package com.nextlabs.smartclassifier.constant;

public enum LicenseField {

	//DEPLOYMENT_TYPE("deployment_type"), 
	//EXTRACTOR_COUNT("content_extractor_count"),
	//CPU_CORE_COUNT("number_of_cores"),
	LICENSE_EXPIRY_DATE("expiration"),
	ENFORCE_EXPIRY("enforce_expiry"),
	DATA_SIZE_IN_GB("data_size");

	private String value;

	LicenseField(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
