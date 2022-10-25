package com.nextlabs.smartclassifier.constant;

public enum DataSection {
	
	ALL("A", "All content"),
	DIRECTORY("D", "Directory"),
	FILE_NAME("N", "File name"),
	HEADER("H", "Header section"),
	CONTENT("C", "File content"),
	FOOTER("F", "Footer section"),
	METADATA("M", "File metadata");
	
	private String code;
	
	private String name;
	
	private DataSection(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static DataSection getSection(String code) {
		if(code != null) {
			for(DataSection section : DataSection.values()) {
				if(section.getCode().equals(code)) {
					return section;
				}
			}
		}
		
		return null;
	}
}
