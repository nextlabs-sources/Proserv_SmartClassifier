package com.nextlabs.smartclassifier.exception;

public class ManagerException 
		extends Throwable {

	private static final long serialVersionUID = 2134098404777833586L;

	public ManagerException(Throwable error) {
		super(error);
	}
	
	public ManagerException(String errorMessage) {
		super(errorMessage);
	}
	
	public ManagerException(String errorMessage, Throwable error) {
		super(errorMessage, error);
	}
	
	public ManagerException(String errorMessage, Throwable error, boolean enableSuppression, boolean writableStackTrace) {
		super(errorMessage, error, enableSuppression, writableStackTrace);
	}
}
