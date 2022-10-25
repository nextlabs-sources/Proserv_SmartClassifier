package com.nextlabs.smartclassifier.exception;

public class RecordInUseException 
		extends Throwable {
	
	private static final long serialVersionUID = 4348137708971812468L;

	public RecordInUseException(Throwable error) {
		super(error);
	}
	
	public RecordInUseException(String errorMessage) {
		super(errorMessage);
	}
	
	public RecordInUseException(String errorMessage, Throwable error) {
		super(errorMessage, error);
	}
	
	public RecordInUseException(String errorMessage, Throwable error, boolean enableSuppression, boolean writableStackTrace) {
		super(errorMessage, error, enableSuppression, writableStackTrace);
	}
}
