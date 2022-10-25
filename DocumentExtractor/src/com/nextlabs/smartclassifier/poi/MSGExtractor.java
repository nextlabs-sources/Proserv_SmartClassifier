package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.CustomProperty;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class MSGExtractor 
		extends POIDocumentExtractor {
	private static final String DELIMITER = ": ";
	
	private MAPIMessage message = null;
	private POIFSFileSystem poifsFileSystem = null;
	
	public MSGExtractor(String absoluteFilePath) 
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileSystemInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poifsFileSystem = new POIFSFileSystem(fileSystemInputStream);
		} catch (IllegalArgumentException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath, e);
			throw e;
		}
		
		message = new MAPIMessage(absoluteFilePath);
	}
	
	@Override
	protected String getHeader() {
		return "";
	}

	@Override
	protected String getContent() {
		if(message == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			try {
				return message.getTextBody();
			} catch(Exception err) {
				logger.error(err.getMessage(), err);
				return null;
			}
		}
	}

	@Override
	protected String getFooter() {
		return "";
	}

	@Override
	protected HashMap<String, String> getMetadata() {
		if(message == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			LinkedHashMap<String, String> metadataTags = new LinkedHashMap<String, String>();
			
			// Perform data massaging for header content
			try {
				StringBuilder completeString = null;
				
				for(String header : message.getHeaders()) {
					if(header.startsWith(" ")) {
						if(completeString == null) {
							completeString = new StringBuilder();
						}
						
						completeString.append(header);
					} else if(header.startsWith("\t")) {
						if(completeString == null) {
							completeString = new StringBuilder();
						}
						
						completeString.append(header.replaceFirst("\t", " "));
					} else {
						if(completeString != null) {
							String[] keyValuePair = splitKeyValue(completeString.toString());
							
							if(metadataTags.containsKey(keyValuePair[0].toLowerCase())) {
								metadataTags.put(keyValuePair[0].toLowerCase(), metadataTags.get(keyValuePair[0].toLowerCase()) + "|" + keyValuePair[1]);
							} else {
								metadataTags.put(keyValuePair[0].toLowerCase(), keyValuePair[1]);
							}
						}
						
						completeString = new StringBuilder(header);
					}
				}
			} catch(Exception err) {
				logger.error(err.getMessage(), err);
			}
			
			// Read custom properties
			DirectoryEntry directoryEntry = poifsFileSystem.getRoot();
			DocumentSummaryInformation documentSummaryInformation = null;
			DocumentInputStream documentInputStream = null;
			DocumentEntry documentEntry;
			
			try {
				documentEntry = (DocumentEntry) directoryEntry.getEntry(DocumentSummaryInformation.DEFAULT_STREAM_NAME);
				documentInputStream = new DocumentInputStream(documentEntry);
				PropertySet propertySet = new PropertySet(documentInputStream);
				documentSummaryInformation = new DocumentSummaryInformation(propertySet);
			} catch (FileNotFoundException e) {
				documentSummaryInformation = PropertySetFactory.newDocumentSummaryInformation();
			} catch (Exception e) {
				logger.error("Error parsing metadata.", e);
			} finally {
				if(documentInputStream != null) {
					documentInputStream.close();
				}
			}
			
			CustomProperties customProperties = documentSummaryInformation.getCustomProperties();
			if(customProperties == null) {
				customProperties = new CustomProperties();
			}
			
			for(Entry<Object, CustomProperty> entry : customProperties.entrySet()) {
				if(metadataTags.containsKey(entry.getValue().getName().toLowerCase())) {
					metadataTags.put(entry.getValue().getName().toLowerCase(), metadataTags.get(entry.getValue().getName().toLowerCase()) 
							+ "|" + entry.getValue().getValue().toString());
				} else {
					metadataTags.put(entry.getValue().getName().toLowerCase(), entry.getValue().getValue().toString());
				}
			}
			
			return metadataTags;
		}
	}
	
	private String[] splitKeyValue(String header) {
		if(header.contains(DELIMITER)) {
			String[] keyValue = new String[2];
			
			keyValue[0] = header.substring(0, header.indexOf(DELIMITER));
			keyValue[1] = header.substring(header.indexOf(DELIMITER) + DELIMITER.length());
			
			return keyValue;
		} else {
			return new String[]{header, ""};
		}
	}
}
