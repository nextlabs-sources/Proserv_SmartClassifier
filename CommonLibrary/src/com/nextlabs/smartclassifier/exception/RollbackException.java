package com.nextlabs.smartclassifier.exception;

import com.nextlabs.smartclassifier.constant.RollbackErrorType;

public class RollbackException
		extends Exception {
	
	private static final long serialVersionUID = 408745604540330427L;
	
	private RollbackErrorType rollbackErrorType;
	private String backupAbsoluteFilePath;
	private String targetAbsoluteFilePath;
	
	public RollbackException(Throwable error) {
		super(error);
	}
	
	public RollbackException(String errorMessage) {
		super(errorMessage);
	}
	
	public RollbackException(String errorMessage, Throwable error) {
		super(errorMessage, error);
	}
	
	public RollbackException(String errorMessage, Throwable error, boolean enableSuppression, boolean writableStackTrace) {
		super(errorMessage, error, enableSuppression, writableStackTrace);
	}
	
	public RollbackErrorType getRollbackErrorType() {
		return rollbackErrorType;
	}
	
	public void setRollbackErrorType(RollbackErrorType rollbackErrorType) {
		this.rollbackErrorType = rollbackErrorType;
	}
	
	public String getBackupAbsoluteFilePath() {
		return backupAbsoluteFilePath;
	}
	
	public void setBackupAbsoluteFilePath(String backupAbsoluteFilePath) {
		this.backupAbsoluteFilePath = backupAbsoluteFilePath;
	}
	
	public String getTargetAbsoluteFilePath() {
		return targetAbsoluteFilePath;
	}
	
	public void setTargetAbsoluteFilePath(String targetAbsoluteFilePath) {
		this.targetAbsoluteFilePath = targetAbsoluteFilePath;
	}
}
