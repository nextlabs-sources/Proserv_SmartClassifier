package com.nextlabs.smartclassifier.plugin.datasource;

import java.util.HashMap;
import java.util.Map;

public class CSVConfig {
	
	private String filePath;
	private Map<String, String> properties = new HashMap<String, String>();
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
