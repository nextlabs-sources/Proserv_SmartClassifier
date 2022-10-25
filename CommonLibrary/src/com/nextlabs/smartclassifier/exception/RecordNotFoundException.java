package com.nextlabs.smartclassifier.exception;

public class RecordNotFoundException 
		extends Throwable {
	
	private static final long serialVersionUID = 8402857641560723032L;

	public RecordNotFoundException(Throwable error) {
		super(error);
	}
	
	public RecordNotFoundException(String errorMessage) {
		super(errorMessage);
	}
	
	public RecordNotFoundException(String errorMessage, Throwable error) {
		super(errorMessage, error);
	}
	
	public RecordNotFoundException(String errorMessage, Throwable error, boolean enableSuppression, boolean writableStackTrace) {
		super(errorMessage, error, enableSuppression, writableStackTrace);
	}
}
