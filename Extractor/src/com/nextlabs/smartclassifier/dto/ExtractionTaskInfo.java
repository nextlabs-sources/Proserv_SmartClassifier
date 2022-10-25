package com.nextlabs.smartclassifier.dto;

public class ExtractionTaskInfo {
    
	private String documentID;
    private String absolutePath;
    private String action;
    private String repositoryType;
    private String siteURL;
    private String repoPath;
    private int retryAttempt;
    
    public ExtractionTaskInfo() {
    	super();
    	this.retryAttempt = 0;
    }
    
	public ExtractionTaskInfo(String documentID, String absolutePath, 
			String action, String repositoryType, 
			String siteURL, String repoPath, int retryAttempt) {
		super();
		this.documentID = documentID;
		this.absolutePath = absolutePath;
		this.action = action;
		this.repositoryType = repositoryType;
		this.siteURL = siteURL;
		this.repoPath = repoPath;
		this.retryAttempt = retryAttempt;
	}
	
	public String getDocumentID() {
		return documentID;
	}
	
	public void setDocumentID(String documentID) {
		this.documentID = documentID;
	}
	
	public String getAbsolutePath() {
		return absolutePath;
	}
	
	public void setAbsolutePath(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public String getRepositoryType() {
		return repositoryType;
	}
	
	public void setRepositoryType(String repositoryType) {
		this.repositoryType = repositoryType;
	}
	
	public String getSiteURL() {
		return siteURL;
	}
	
	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}
	
	public String getRepoPath() {
		return repoPath;
	}
	
	public void setRepoPath(String repoPath) {
		this.repoPath = repoPath;
	}
	
	public int getRetryAttempt() {
		return retryAttempt;
	}
	
	public void setRetryAttempt(int retryAttempt) {
		this.retryAttempt = retryAttempt;
	}
};