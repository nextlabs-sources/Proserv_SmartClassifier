package com.nextlabs.smartclassifier.plugin.action;

import com.nextlabs.smartclassifier.constant.ActionResult;

public class ActionOutcome {
	
	private ActionResult result;
	
	private String message;
	
	public ActionOutcome() {
		super();
		this.result = ActionResult.SUCCESS;
		this.message = "Action completed successfully.";
	}
	
	public ActionOutcome(ActionResult result, String message) {
		super();
		this.result = result;
		this.message = message;
	}
	
	public ActionResult getResult() {
		return result;
	}
	
	public void setResult(ActionResult result) {
		this.result = result;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
