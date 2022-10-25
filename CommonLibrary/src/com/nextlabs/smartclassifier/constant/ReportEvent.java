package com.nextlabs.smartclassifier.constant;

public enum ReportEvent {
	
	COMPONENT_START_SUCCESS("componentstart.success"),
	COMPONENT_START_FAIL("componentstart.fail"),
	COMPONENT_STOP_SUCCESS("componentstop.success"),
	COMPONENT_STOP_FAIL("componentstop.fail"),
	DOCUMENT_ADD("documentadd.detected"),
	DOCUMENT_UPDATE("documentupdate.detected"),
	DOCUMENT_REMOVE("documentremove.detected"),
	DOCUMENT_EXTRACT_SUCCESS("extraction.success"),
	DOCUMENT_EXTRACT_FAIL("extraction.fail"),
	DOCUMENT_INDEXING_ADD_SUCCESS("indexingadd.success"),
	DOCUMENT_INDEXING_ADD_FAIL("indexingadd.fail"),
	DOCUMENT_INDEXING_UPDATE_SUCCESS("indexingupdate.success"),
	DOCUMENT_INDEXING_UPDATE_FAIL("indexingupdate.fail"),
	DOCUMENT_INDEXING_REMOVE_SUCCESS("indexingremove.success"),
	DOCUMENT_INDEXING_REMOVE_FAIL("indexingremove.fail"),
	RULE_EXECUTE_START("ruleexecution.start"),
	RULE_EXECUTE_END("ruleexecution.end"),
	RULE_EXECUTE_NO_MATCH("ruleexecution.nomatch"),
	RULE_EXECUTE_SUCCESS("ruleexecution.success"),
	RULE_EXECUTE_FAIL("ruleexecution.fail"),
	ACTION_APPLY_SUCCESS("actionapply.success"),
	ACTION_APPLY_ROLLBACK("actionapply.rollback"),
	ACTION_APPLY_FAIL("actionapply.fail");
	
	private String messageCode;
	
	private ReportEvent(final String messageCode) {
		this.messageCode = messageCode;
	}
	
	public String getMessageCode() {
		return messageCode;
	}
	
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
}
