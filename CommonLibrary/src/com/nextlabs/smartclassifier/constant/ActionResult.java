package com.nextlabs.smartclassifier.constant;

public enum ActionResult {
	SUCCESS("S", "Success"), 
	FAIL("F", "Fail");
	
	private String code;
	
	private String name;
	
	private ActionResult(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
	
	public static ActionResult getResult(String code) {
		if(code != null) {
			for(ActionResult result : ActionResult.values()) {
				if(result.code.equals(code)) {
					return result;
				}
			}
		}
		
		return null;
	}
}
