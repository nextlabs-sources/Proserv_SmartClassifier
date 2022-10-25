package com.nextlabs.smartclassifier.plain;

import com.nextlabs.smartclassifier.DocumentContent;
import com.nextlabs.smartclassifier.DocumentExtractor;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.nextlabs.smartclassifier.constant.Punctuation.EMPTY_STRING;

public class PlainTextExtractor 
		extends DocumentExtractor
		implements SolrPredefinedField {
	
	private static final Logger logger = LoggerFactory.getLogger(PlainTextExtractor.class);
	
	protected String absoluteFilePath = null;
	protected Map<String, String> parameters;
	protected File file;
	
	public PlainTextExtractor(String absoluteFilePath) 
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		this.file = new File(absoluteFilePath); 
		
		if(!file.exists()){
			throw new FileNotFoundException();
		}
	}
	
	@Override
	public DocumentContent extract()
			throws Exception {
		DocumentContent documentContent = new DocumentContent();
		
		documentContent.setAbsoluteFilePath(this.absoluteFilePath);
		documentContent.setHeader(getHeader());
		documentContent.setContent(getContent());
		documentContent.setFooter(getFooter());
		documentContent.setMetadata(getMetadata());
		
		return documentContent;
	}
	
	@Override
	public DocumentContent extract(boolean bExtractBody)
			throws Exception {
		DocumentContent documentContent = new DocumentContent();
		
		documentContent.setAbsoluteFilePath(this.absoluteFilePath);
		documentContent.setHeader(getHeader());
		if (bExtractBody) {
			documentContent.setContent(getContent());
		}
		else {
			documentContent.setContent("");
		}
		documentContent.setFooter(getFooter());
		documentContent.setMetadata(getMetadata());
		
		return documentContent;
	}
	
	@Override
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	protected String getHeader() {
		return EMPTY_STRING;
	}
	
	protected String getContent() {
		StringBuilder contentBuilder = new StringBuilder();
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), "UTF-8"))) {
			String line;
			
			while((line = reader.readLine()) != null) {
				contentBuilder.append(line).append(" ");
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
		
		return contentBuilder.toString();
	}
	
	protected String getFooter() {
		return EMPTY_STRING;
	}
	
	protected Map<String, String> getMetadata() {
		Map<String, String> metadataTags = new LinkedHashMap<String, String>();
		
		try {
			BasicFileAttributes basicAttribute = Files.readAttributes(this.file.toPath(), BasicFileAttributes.class);
			
			if(basicAttribute.creationTime() != null) {
				metadataTags.put(CREATION_DATE, extractorDateFormat.format(new Date(basicAttribute.creationTime().toMillis())));
				metadataTags.put(CREATION_DATE_MILLISECOND, Long.toString(basicAttribute.creationTime().toMillis()));
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
		
		return metadataTags;
	}
}
