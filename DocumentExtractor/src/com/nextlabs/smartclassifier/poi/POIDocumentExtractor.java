package com.nextlabs.smartclassifier.poi;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLProperties;
import org.apache.poi.POIXMLProperties.CoreProperties;
import org.apache.poi.POIXMLProperties.CustomProperties;
import org.apache.poi.hpsf.CustomProperty;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.smartclassifier.DocumentContent;
import com.nextlabs.smartclassifier.DocumentExtractor;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;

public abstract class POIDocumentExtractor 
	extends DocumentExtractor
	implements SolrPredefinedField {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected String absoluteFilePath = null;
	protected Map<String, String> parameters;
	
	protected abstract String getHeader();
	protected abstract String getContent();
	protected abstract String getFooter();
	protected abstract Map<String, String> getMetadata();
	
	@Override
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public DocumentContent extract() 
			throws Exception {
		DocumentContent documentContent = new DocumentContent();
		documentContent.setAbsoluteFilePath(absoluteFilePath);
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
		documentContent.setAbsoluteFilePath(absoluteFilePath);
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
	
	protected Map<String, String> getHorribleLayoutMetadata(POIFSFileSystem poifsFileSystem) {
		if(poifsFileSystem == null) {
			logger.error("POIFSFileSystem is null.");
			return null;
		} else {
			DirectoryEntry directoryEntry = poifsFileSystem.getRoot();
			SummaryInformation summaryInformation = null;
			DocumentSummaryInformation documentSummaryInformation = null;
			DocumentInputStream documentInputStream = null;
			
			DocumentEntry documentEntry;
			try {
				documentEntry = (DocumentEntry) directoryEntry.getEntry(DocumentSummaryInformation.DEFAULT_STREAM_NAME);
				documentInputStream = new DocumentInputStream(documentEntry);
				PropertySet propertySet = new PropertySet(documentInputStream);
				summaryInformation = (SummaryInformation)PropertySetFactory.create(directoryEntry, SummaryInformation.DEFAULT_STREAM_NAME);
				documentSummaryInformation = new DocumentSummaryInformation(propertySet);
			} catch (FileNotFoundException e) {
				summaryInformation = PropertySetFactory.newSummaryInformation();
				documentSummaryInformation = PropertySetFactory.newDocumentSummaryInformation();
			} catch (Exception e) {
				logger.error("Error parsing metadata.", e);
			} finally {
				if(documentInputStream != null) {
					documentInputStream.close();
				}
			}
			
			LinkedHashMap<String, String> metadataTags = new LinkedHashMap<String, String>();
			if(summaryInformation!=null) {
				metadataTags.put(AUTHOR, summaryInformation.getAuthor());
				metadataTags.put(COMMENTS, summaryInformation.getComments());
				metadataTags.put(KEYWORDS, summaryInformation.getKeywords());
				metadataTags.put(LAST_AUTHOR, summaryInformation.getLastAuthor());
				metadataTags.put(REVISION_NUMBER, summaryInformation.getRevNumber());
				metadataTags.put(SUBJECT, summaryInformation.getSubject());
				metadataTags.put(TEMPLATE, summaryInformation.getTemplate());
				metadataTags.put(TITLE, summaryInformation.getTitle());
				if(summaryInformation.getCreateDateTime()!=null) {
					metadataTags.put(CREATION_DATE, extractorDateFormat.format(summaryInformation.getCreateDateTime()));
					metadataTags.put(CREATION_DATE_MILLISECOND, Long.toString(summaryInformation.getCreateDateTime().getTime()));
				}
				metadataTags.put(APPLICATION_NAME, summaryInformation.getApplicationName());
				metadataTags.put(WORD_COUNT, String.valueOf(summaryInformation.getWordCount()));
			}
			
			if(documentSummaryInformation!=null) {
				org.apache.poi.hpsf.CustomProperties customProperties = documentSummaryInformation.getCustomProperties();
				if(customProperties == null) {
					customProperties = new org.apache.poi.hpsf.CustomProperties();
				}

				for(Entry<Object, CustomProperty> entry : customProperties.entrySet()) {
					metadataTags.put(entry.getValue().getName().toLowerCase(), entry.getValue().getValue().toString());
				}
			}
			
			return metadataTags;
		}
	}
	
	protected Map<String, String> getXMLLayoutMetadata(POIXMLDocument poixmlDocument) {
		if(poixmlDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			LinkedHashMap<String, String> metadataTags = new LinkedHashMap<String, String>();
			
			POIXMLProperties props = poixmlDocument.getProperties();
			CoreProperties coreProperties = props.getCoreProperties();
			
			if(coreProperties != null) {
				metadataTags.put(AUTHOR, coreProperties.getCreator());
				metadataTags.put(KEYWORDS, coreProperties.getKeywords());
				if(coreProperties.getUnderlyingProperties().getLastModifiedByProperty() != null) {
					metadataTags.put(LAST_AUTHOR, coreProperties.getUnderlyingProperties().getLastModifiedByProperty().getValue());
				}
				metadataTags.put(REVISION_NUMBER, coreProperties.getRevision());
				metadataTags.put(SUBJECT, coreProperties.getSubject());
				metadataTags.put(TITLE, coreProperties.getTitle());
				if(coreProperties.getCreated() != null) {
					metadataTags.put(CREATION_DATE, extractorDateFormat.format(coreProperties.getCreated()));
					metadataTags.put(CREATION_DATE_MILLISECOND, Long.toString(coreProperties.getCreated().getTime()));
				}
				
				metadataTags.put(CATEGORY, coreProperties.getCategory());
				metadataTags.put(COMMENTS, coreProperties.getDescription());
			}
			
			CustomProperties customProperties = props.getCustomProperties();
			if (customProperties != null) {
				List<CTProperty> ctProperties = customProperties.getUnderlyingProperties().getPropertyList();
				for (CTProperty property : ctProperties) {
					String value = "";
					
					if (property.isSetLpwstr()) {
						value = property.getLpwstr();
					} else if (property.isSetLpstr()) {
						value = property.getLpstr();
					} else if (property.isSetDate()) {
						value = property.getDate().toString();
					} else if (property.isSetFiletime()) {
						value = property.getFiletime().toString();
					} else if (property.isSetBool()) {
						value = Boolean.toString(property.getBool());
					}
					
					// Integers
					else if (property.isSetI1()) {
						value = Integer.toString(property.getI1());
					} else if (property.isSetI2()) {
						value = Integer.toString(property.getI2());
					} else if (property.isSetI4()) {
						value = Integer.toString(property.getI4());
					} else if (property.isSetI8()) {
						value = Long.toString(property.getI8());
					} else if (property.isSetInt()) {
						value = Integer.toString(property.getInt());
					}
					
					// Unsigned Integers
					else if (property.isSetUi1()) {
						value = Integer.toString(property.getUi1());
					} else if (property.isSetUi2()) {
						value = Integer.toString(property.getUi2());
					} else if (property.isSetUi4()) {
						value = Long.toString(property.getUi4());
					} else if (property.isSetUi8()) {
						value = property.getUi8().toString();
					} else if (property.isSetUint()) {
						value = Long.toString(property.getUint());
					}
					
					// Reals
					else if (property.isSetR4()) {
						value = Float.toString(property.getR4());
					} else if (property.isSetR8()) {
						value = Double.toString(property.getR8());
					} else if (property.isSetDecimal()) {
						BigDecimal d = property.getDecimal();
						if (d == null) {
							value = null;
						} else {
							value = d.toPlainString();
						}
					}
					
					else if (property.isSetArray()) {
						// TODO Fetch the array values and output
					} else if (property.isSetVector()) {
						// TODO Fetch the vector values and output
					}
					
					else if (property.isSetBlob() || property.isSetOblob()) {
						// TODO Decode, if possible
					} else if (property.isSetStream()
							|| property.isSetOstream()
							|| property.isSetVstream()) {
						// TODO Decode, if possible
					} else if (property.isSetStorage()
							|| property.isSetOstorage()) {
						// TODO Decode, if possible
					}
					
					metadataTags.put(property.getName().toLowerCase(), value);
				}
			}
			
			return metadataTags;
		}
	}
}
