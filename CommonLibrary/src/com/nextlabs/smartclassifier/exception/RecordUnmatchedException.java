package com.nextlabs.smartclassifier.exception;

public class RecordUnmatchedException 
		extends Throwable {
	
	private static final long serialVersionUID = -5495994705702525557L;

	public RecordUnmatchedException(Throwable error) {
		super(error);
	}
	
	public RecordUnmatchedException(String errorMessage) {
		super(errorMessage);
	}
	
	public RecordUnmatchedException(String errorMessage, Throwable error) {
		super(errorMessage, error);
	}
	
	public RecordUnmatchedException(String errorMessage, Throwable error, boolean enableSuppression, boolean writableStackTrace) {
		super(errorMessage, error, enableSuppression, writableStackTrace);
	}
}
