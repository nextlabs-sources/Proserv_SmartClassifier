package com.nextlabs.smartclassifier.validator.v1;

public class Validation {
	
	private boolean valid;
	
	private String errorCode;
	
	private String errorMessage;
	
	public Validation() {
		super();
		this.valid = true;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void isValid(boolean valid) {
		this.valid = valid;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
