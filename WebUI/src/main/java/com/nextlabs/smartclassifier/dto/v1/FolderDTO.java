package com.nextlabs.smartclassifier.dto.v1;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class FolderDTO {
	
	@Expose
	private String path;
	
	@Expose
	private String folderName;
	
	@Expose
	private Set<FolderDTO> subFolders;
	
	public FolderDTO() {
		super();
	}
	
	public FolderDTO(String path, String folderName) {
		super();
		this.path = path;
		this.folderName = folderName;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getFolderName() {
		return folderName;
	}
	
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	public Set<FolderDTO> getSubFolders() {
		return subFolders;
	}
	
	public void setSubFolders(Set<FolderDTO> subFolders) {
		this.subFolders = subFolders;
	}
	
	public void addSubFolder(String path, String subFolderName) {
		if(this.subFolders == null) {
			this.subFolders = new LinkedHashSet<FolderDTO>();
		}
		
		this.subFolders.add(new FolderDTO(path, subFolderName));
	}
}
