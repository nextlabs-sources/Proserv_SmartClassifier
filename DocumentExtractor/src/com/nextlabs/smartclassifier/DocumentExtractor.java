package com.nextlabs.smartclassifier;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

import com.nextlabs.smartclassifier.constant.SCConstant;


public abstract class DocumentExtractor {
	
	protected static final SimpleDateFormat extractorDateFormat = new SimpleDateFormat(SCConstant.SOLR_DATETIME_FORMAT);
	
	public abstract DocumentContent extract() throws Exception;
	
	public abstract DocumentContent extract(boolean bExtractBody) throws Exception;
	
	public abstract void setParameters(Map<String, String> parameters);
	
	public DocumentExtractor() {
		super();
		extractorDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
}
