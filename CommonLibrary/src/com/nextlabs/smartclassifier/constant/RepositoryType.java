package com.nextlabs.smartclassifier.constant;

import org.apache.commons.lang.StringUtils;

/**
 * Created by pkalra on 10/18/2016.
 */
public enum RepositoryType {
    SHARED_FOLDER("SHARED FOLDER", "Shared Folder"),
    SHAREPOINT("SHAREPOINT", "SharePoint");

    private final String name;
    
    private final String displayValue;
    
    RepositoryType(String name, String displayValue) {
        this.name = name;
        this.displayValue = displayValue;
    }

    public String getName() {
        return name;
    }
    
    public String getDisplayValue() {
    	return displayValue;
    }

    public static RepositoryType getRepositoryType(String repositoryType) {
        if (StringUtils.isNotBlank(repositoryType)) {
            for (RepositoryType repoType : RepositoryType.values()) {
                if (repoType.getName().equals(repositoryType)) {
                    return repoType;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        for (RepositoryType repositoryType : RepositoryType.values()) {
            System.out.println(repositoryType.getName());
        }
    }
}
