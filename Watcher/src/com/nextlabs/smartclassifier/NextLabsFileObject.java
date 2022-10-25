package com.nextlabs.smartclassifier;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

import com.nextlabs.smartclassifier.constant.RepositoryType;

public class NextLabsFileObject {

    private boolean exists;
    private boolean recoveredObject;
    private long timestamp;
    private FileType fileType;
    private Set<String> children;
    private String sFileName;
    private RepositoryType repositoryType;
    private String repositoryURL;

    public NextLabsFileObject() {

    }

    public NextLabsFileObject(FileObject file, RepositoryType repositoryType, String repositoryURL) {

        try {
            file.refresh();
            this.exists = file.exists();
        } catch (FileSystemException fse) {
            this.exists = false;
            this.timestamp = -1;
        }
        
        this.recoveredObject = false;
        if (this.exists) {
            try {
            	this.fileType = file.getType();
                this.sFileName = file.getName().getURI();
                this.timestamp = file.getContent().getLastModifiedTime();
                this.repositoryType = repositoryType;

                if (this.repositoryType == RepositoryType.SHAREPOINT) {
                    this.repositoryURL = repositoryURL;
                } else {
                    this.repositoryURL = null;
                }
            } catch (FileSystemException fse) {
                this.timestamp = -1;
            }
        }
    }

    /**
     * This constructor is for use of recovering NextLabsFileObject from dump file
     * @param file
     * @param repositoryType
     * @param repositoryURL
     * @param lastModifiedTimestamp
     */
    public NextLabsFileObject(FileObject file, RepositoryType repositoryType, String repositoryURL, FileType fileType, long lastModifiedTimestamp) {
        try {
            this.fileType = fileType;
        	this.sFileName = file.getName().getURI();
            this.timestamp = lastModifiedTimestamp;
            this.repositoryType = repositoryType;
            this.recoveredObject = true;
            this.exists = true;

            if (this.repositoryType == RepositoryType.SHAREPOINT) {
                this.repositoryURL = repositoryURL;
            } else {
                this.repositoryURL = null;
            }
        } catch (Exception fse) {
            this.timestamp = -1;
        }
    }
    
    public FileType getFileType() {
		return fileType;
	}

	public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean exists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getsFileName() {
        return sFileName;
    }

    public void setsFileName(String sFileName) {
        this.sFileName = sFileName;
    }

    public RepositoryType getRepositoryType() {
        return repositoryType;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }

	public boolean isRecoveredObject() {
		return recoveredObject;
	}

	public void setRecoveredObject(boolean recoveredObject) {
		this.recoveredObject = recoveredObject;
	}

	public Set<String> getChildren() {
		return children;
	}

	public void setChildren(Set<String> children) {
		this.children = children;
	}

	public boolean addChild(String child) {
		if(child == null) {
			return false;
		}
		
		if(this.children == null) {
			this.children = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); 
		}
		
		return this.children.add(child);
	}
	
	public boolean addChildren(Set<String> children) {
		if(children == null) {
			return false;
		}
		
		if(this.children == null) {
			this.children = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); 
		}
		
		return this.children.addAll(children);
	}
}
