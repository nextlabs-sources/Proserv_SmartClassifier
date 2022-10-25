package com.nextlabs.smartclassifier.constant;

public enum OperatingSystem {
	
	WINDOWS("WINDOWS", "Microsoft Windows"),
	UNIX("UNIX", "Unix"),
	OSX("OSX", "OS X");
	
	private String code;
	
	private String name;
	
	private OperatingSystem(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public OperatingSystem getOperatingSystem(String code) {
		if(code != null && code.length() > 0) {
			for(OperatingSystem system : OperatingSystem.values()) {
				if(system.getCode().equals(code)) {
					return system;
				}
			}
		}
		
		return null;
	}
}
