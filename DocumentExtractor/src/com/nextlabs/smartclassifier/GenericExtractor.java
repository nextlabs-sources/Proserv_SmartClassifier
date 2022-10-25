package com.nextlabs.smartclassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.smartclassifier.constant.SolrPredefinedField;

public class GenericExtractor 
		extends DocumentExtractor
		implements SolrPredefinedField {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());	
	protected String absoluteFilePath = null;
	protected File file;
	protected Map<String, String> parameters;
	
	public GenericExtractor(String absoluteFilePath) throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		file = new File(absoluteFilePath);
		
		if(!file.exists()){
			throw new FileNotFoundException();
		}
	}
	
	@Override
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	protected String getHeader() {
				
			return "";
	}
	
	protected String getContent() {
		return "";
	}
	
	protected String getFooter() {
		return "";
	}
	
	protected Map<String, String> getMetadata() {
		LinkedHashMap<String, String> metadataTags = new LinkedHashMap<String, String>();
		
		// No additional meta data to add.
		
		return metadataTags;
	}
	
	public DocumentContent extract() throws Exception {
		DocumentContent documentContent = new DocumentContent();
		documentContent.setAbsoluteFilePath(absoluteFilePath);
		documentContent.setHeader(getHeader());
		documentContent.setContent(getContent());
		documentContent.setFooter(getFooter());
		documentContent.setMetadata(getMetadata());
		return documentContent;
	}

	@Override
	public DocumentContent extract(boolean bExtractBody) throws Exception {
		return extract();
	}
}
