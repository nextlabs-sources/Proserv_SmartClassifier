package com.nextlabs.smartclassifier.constant;

public interface SystemConfigKey {

	// true/false
	public static final String SYSTEM_CONFIGURATION_RELOAD_ENABLE = "system.config.enableReload";
	// long in second
	public static final String SYSTEM_CONFIGURATION_RELOAD_SPLIT_TIME = "system.config.reloadSplitTime";
	// String
	public static final String SYSTEM_FORMAT_TIME_FORMAT = "system.format.timeFormat";
	// int
	public static final String HEARTBEAT_CRITICAL_FAILED_COUNT = "heartbeat.criticalFailedCount";
	// long in second
	public static final String JMS_RETRIEVE_MESSAGE_TIMEOUT = "jms.timeout.retrieveMessage";

	// boolean
	public static final String EMAIL_DEBUG = "email.debug";
	// String 
	public static final String EMAIL_SERVER_URL = "email.server.url";
	// int
	public static final String EMAIL_SERVER_PORT_NUMBER = "email.server.portNumber";
	// String
	public static final String EMAIL_DEFAULT_SENDER = "email.sender.defaultSenderEmail";
	// boolean
	public static final String EMAIL_SERVER_IDENTITY_CHECK = "email.server.checkIdentity.enabled";
	// boolean
	public static final String EMAIL_AUTH_REQUIRED = "email.authentication.required";
	// String
	public static final String EMAIL_AUTH_USERNAME = "email.authentication.username";
	// String
	public static final String EMAIL_AUTH_PASSWORD = "email.authentication.password";
	// boolean
	public static final String EMAIL_STARTTLS_ENABLED = "email.starttls.enabled";

	// String
	public static final String SKYDRM_ROUTER_URL = "skydrm.config.routerURL";
	// String
	public static final String SKYDRM_APP_ID = "skydrm.config.appID";
	// String
	public static final String SKYDRM_APP_KEY = "skydrm.config.appKey";
	// String
	public static final String SKYDRM_SYSTEM_BUCKET = "skydrm.config.skydrm.systemBucket";
	
	// long in second
	public static final String WATCHER_INTERVAL_SLEEP = "w.interval.sleep";
	// long in second
	public static final String WATCHER_INTERVAL_HEARTBEAT = "w.interval.heartbeat";
	// long in second
	public static final String WATCHER_SPLIT_LAST_MODIFIED_DATE = "w.split.lastModifiedDate";

	// long in second
	public static final String EXTRACTOR_INTERVAL_SLEEP = "e.interval.sleep";
	// long in second
	public static final String EXTRACTOR_INTERVAL_HEARTBEAT = "e.interval.heartbeat";
	// int
	public static final String EXTRACTOR_EXTRACTION_FAILED_RETRY_COUNT = "e.count.failedExtractionRetry";
	// long in second
	public static final String EXTRACTOR_EXTRACTION_FAILED_RETRY_INTERVAL = "e.interval.failedExtractionRetry";
	//boolean 
	public static final String EXTRACTOR_EXTRACTION_BODY = "e.extract.extractBody";
	//boolean 
	public static final String WATCHER_CHECK_LASTMODIFIED = "w.check.lastmodified";

	// long in second
	public static final String ENGINE_INTERVAL_SLEEP = "r.interval.sleep";
	// long in second
	public static final String ENGINE_INTERVAL_HEARTBEAT = "r.interval.heartbeat";

	// String
	public static final String INDEXER_URL = "isvr.url";
	// long
	public static final String INDEXER_INTERVAL_RETRY = "isvr.interval.retry";
	// String
	public static final String INDEXER_USERNAME = "isvr.username";
	// String
	public static final String INDEXER_PASSWORD = "isvr.password";
	// int
	public static final String EXTRACTOR_MAX_TASK_SIZE = "e.size.maxTask"; 

	// date
	public static final String LICENSE_LAST_CHECK = "license.checked.date";
	// boolean
	public static final String LICENSE_VALIDITY = "license.validity";
	// date
	public static final String LICENSE_EXPIRY_DATE = "license.expiryDate";
	// int
	public static final String LICENSE_DATA_SIZE = "license.dataSize";
	
	// int
	public static final String RESTFUL_RECORD_PAGE_SIZE = "restful.record.pageSize";
	// String
	public static final String SMART_CLASSIFIER_URL = "smart.classifier.url";
	// String
	public static final String LOGIN_URL = "login.url";
	// String
	public static final String LOGOUT_URL = "logout.url";
	
	// String
	public static final String SOLR_INDEXING_QUERY_RULE_ENGINE_FIELDS = "solr.indexing.query.ruleEngineFields";
	// String
	public static final String SOLR_INDEXING_QUERY_WEB_UI_FIELDS = "solr.indexing.query.webUIFields";
	
	// boolean
	public static final String ENABLE_DOCUMENT_ADDED_LOG = "report.log.enable.documentAdded";
	// boolean
	public static final String ENABLE_DOCUMENT_UPDATED_LOG = "report.log.enable.documentUpdated";
	// boolean
	public static final String ENABLE_DOCOUMENT_REMOVED_LOG = "report.log.enable.documentRemoved";
	// boolean
	public static final String ENABLE_DOCUMENT_EXTRACTION_SUCCESS_LOG = "report.log.enable.extractionSuccess";
	// boolean
	public static final String ENABLE_DOCUMENT_EXTRACTION_FAILED_LOG = "report.log.enable.extractionFailed";
	// boolean
	public static final String ENABLE_DOCUMENT_INDEXING_SUCCESS_LOG = "report.log.enable.indexingSuccess";
	// boolean
	public static final String ENABLE_DOCUMENT_INDEXING_FAILED_LOG = "report.log.enable.indexingFailed";
	// boolean
	public static final String ENABLE_RULE_EXECUTION_START_LOG = "report.log.enable.ruleExecutionStart";
	// boolean
	public static final String ENABLE_RULE_EXECUTION_END_LOG = "report.log.enable.ruleExecutionEnd";
	// boolean
	public static final String ENABLE_RULE_EXECUTION_SUCCESS_LOG = "report.log.enable.ruleExecutionSuccess";
	// boolean
	public static final String ENABLE_RULE_EXECUTION_FAILED_LOG = "report.log.enable.ruleExecutionFailed";
	// boolean
	public static final String ENABLE_ACTION_EXECUTION_SUCCESS_LOG = "report.log.enable.actionExecutionSuccess";
	// boolean
	public static final String ENABLE_ACTION_EXECUTION_FAILED_LOG = "report.log.enable.actionExecutionFailed";
	// int
	public static final String EVENT_LOG_RETENTION_DAY = "report.log.retention.day";
}
