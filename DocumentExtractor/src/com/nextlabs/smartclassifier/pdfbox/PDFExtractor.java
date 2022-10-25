package com.nextlabs.smartclassifier.pdfbox;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.smartclassifier.DocumentContent;
import com.nextlabs.smartclassifier.DocumentExtractor;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;

public class PDFExtractor 
		extends DocumentExtractor
		implements SolrPredefinedField {
	
	private static final Logger logger = LoggerFactory.getLogger(PDFExtractor.class);
	private static final String HEADER_REGION = "header";
	private static final String BODY_REGION = "body";
	private static final String FOOTER_REGION = "footer";
	
	private static final String HEADER_OFFSET = "HeaderOffset";
	private static final String FOOTER_OFFSET = "FooterOffset";
	
	protected String absoluteFilePath = null;
	protected Map<String, String> parameters;
	protected Set<String> headers;
	protected StringBuilder content;
	protected Set<String> footers;
	protected Map<String, String> metadatas;
	
	public PDFExtractor(String absoluteFilePath)
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		File pdfFile = new File(this.absoluteFilePath);
		
		if(!pdfFile.exists()){
			throw new FileNotFoundException();
		}
		
		this.headers = new LinkedHashSet<String>();
		this.content = new StringBuilder();
		this.footers = new LinkedHashSet<String>();
		this.metadatas = new HashMap<String, String>();
	}
	
	@Override
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public DocumentContent extract() 
			throws Exception {
		// TODO Auto-generated method stub
		DocumentContent documentContent = new DocumentContent();
		
		try(PDDocument pdDocument = PDDocument.load(new File(this.absoluteFilePath))) {
			int headerOffset = getHeaderOffset();
			int footerOffset = getFooterOffset();
			
			if(headerOffset < 0) {
				headerOffset = 0;
			}
			
			if(footerOffset < 0) {
				footerOffset = 0;
			}
			
			// Each page size might be different
			for(int i=0; i < pdDocument.getNumberOfPages(); i++) {
				double documentWidth = pdDocument.getPage(i).getMediaBox().getWidth();
				double documentHeight = pdDocument.getPage(i).getMediaBox().getHeight();
				Rectangle2D headerRegion = new Rectangle2D.Double(0, 0, documentWidth, headerOffset);
				Rectangle2D bodyRegion = new Rectangle2D.Double(0, headerOffset, documentWidth, documentHeight - footerOffset);
				Rectangle2D footerRegion = new Rectangle2D.Double(0, documentHeight - footerOffset, documentWidth, documentHeight);
				
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.addRegion(HEADER_REGION, headerRegion);
				stripper.addRegion(BODY_REGION, bodyRegion);
				stripper.addRegion(FOOTER_REGION, footerRegion);
				
				stripper.extractRegions(pdDocument.getPage(i));
				
				String trimmedHeader = stripper.getTextForRegion(HEADER_REGION).trim();
				if(trimmedHeader != null && trimmedHeader.length() > 0) {
					headers.add(trimmedHeader);
				}
				
				String trimmedBody = stripper.getTextForRegion(BODY_REGION).trim();
				if(trimmedBody != null && trimmedBody.length() > 0) {
					content.append(trimmedBody + " ");
				}
				
				String trimmedFooter = stripper.getTextForRegion(FOOTER_REGION).trim();
				if(trimmedFooter != null && trimmedFooter.length() > 0) {
					footers.add(trimmedFooter);
				}
			}
			
			PDDocumentInformation documentInfo = pdDocument.getDocumentInformation();
			metadatas.put(PAGE_COUNT, Integer.toString(pdDocument.getNumberOfPages()));
			metadatas.put(AUTHOR, documentInfo.getAuthor());
			metadatas.put(CREATOR, documentInfo.getCreator());
			metadatas.put(PRODUCER, documentInfo.getProducer());
			metadatas.put(SUBJECT, documentInfo.getSubject());
			metadatas.put(TITLE, documentInfo.getTitle());
			metadatas.put(KEYWORDS, documentInfo.getKeywords());
			// There are cases where creation date and modification date can be null
			if(documentInfo.getCreationDate() != null) {
				metadatas.put(CREATION_DATE, extractorDateFormat.format(documentInfo.getCreationDate().getTime()));
				metadatas.put(CREATION_DATE_MILLISECOND, Long.toString(documentInfo.getCreationDate().getTimeInMillis()));
			}
			
	        Set<String> customMetadataKeys = documentInfo.getMetadataKeys();
	        Iterator<String> itr = customMetadataKeys.iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				String lcaseKey = key.toLowerCase();
				if(!metadatas.containsKey(lcaseKey)) {
					String value = documentInfo.getCustomMetadataValue(key);
					if(!"null".equalsIgnoreCase(value)) {
						metadatas.put(lcaseKey, value);
					}
				}
			}
		} catch(Exception err) {
			throw err;
		}
		
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
		// TODO Auto-generated method stub
		DocumentContent documentContent = new DocumentContent();
		
		try(PDDocument pdDocument = PDDocument.load(new File(this.absoluteFilePath))) {
			int headerOffset = getHeaderOffset();
			int footerOffset = getFooterOffset();
			
			if(headerOffset < 0) {
				headerOffset = 0;
			}
			
			if(footerOffset < 0) {
				footerOffset = 0;
			}
			
			// Each page size might be different
			for(int i=0; i < pdDocument.getNumberOfPages(); i++) {
				double documentWidth = pdDocument.getPage(i).getMediaBox().getWidth();
				double documentHeight = pdDocument.getPage(i).getMediaBox().getHeight();
				Rectangle2D headerRegion = new Rectangle2D.Double(0, 0, documentWidth, headerOffset);
				Rectangle2D bodyRegion = new Rectangle2D.Double(0, headerOffset, documentWidth, documentHeight - footerOffset);
				Rectangle2D footerRegion = new Rectangle2D.Double(0, documentHeight - footerOffset, documentWidth, documentHeight);
				
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.addRegion(HEADER_REGION, headerRegion);
				if (bExtractBody)
					stripper.addRegion(BODY_REGION, bodyRegion);
				
				stripper.addRegion(FOOTER_REGION, footerRegion);
				
				stripper.extractRegions(pdDocument.getPage(i));
				
				String trimmedHeader = stripper.getTextForRegion(HEADER_REGION).trim();
				if(trimmedHeader != null && trimmedHeader.length() > 0) {
					headers.add(trimmedHeader);
				}
				
				if (bExtractBody) {
					String trimmedBody = stripper.getTextForRegion(BODY_REGION).trim();
					if(trimmedBody != null && trimmedBody.length() > 0) {
						content.append(trimmedBody + " ");
					}
				}
				
				String trimmedFooter = stripper.getTextForRegion(FOOTER_REGION).trim();
				if(trimmedFooter != null && trimmedFooter.length() > 0) {
					footers.add(trimmedFooter);
				}
			}
			
			PDDocumentInformation documentInfo = pdDocument.getDocumentInformation();
			metadatas.put(PAGE_COUNT, Integer.toString(pdDocument.getNumberOfPages()));
			metadatas.put(AUTHOR, documentInfo.getAuthor());
			metadatas.put(CREATOR, documentInfo.getCreator());
			metadatas.put(PRODUCER, documentInfo.getProducer());
			metadatas.put(SUBJECT, documentInfo.getSubject());
			metadatas.put(TITLE, documentInfo.getTitle());
			metadatas.put(KEYWORDS, documentInfo.getKeywords());
			// There are cases where creation date and modification date can be null
			if(documentInfo.getCreationDate() != null) {
				metadatas.put(CREATION_DATE, extractorDateFormat.format(documentInfo.getCreationDate().getTime()));
				metadatas.put(CREATION_DATE_MILLISECOND, Long.toString(documentInfo.getCreationDate().getTimeInMillis()));
			}
			
	        Set<String> customMetadataKeys = documentInfo.getMetadataKeys();
	        Iterator<String> itr = customMetadataKeys.iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				String lcaseKey = key.toLowerCase();
				if(!metadatas.containsKey(lcaseKey)) {
					String value = documentInfo.getCustomMetadataValue(key);
					if(!"null".equalsIgnoreCase(value)) {
						metadatas.put(lcaseKey, value);
					}
				}
			}
		} catch(Exception err) {
			throw err;
		}
		
		documentContent.setAbsoluteFilePath(absoluteFilePath);
		documentContent.setHeader(getHeader());
		documentContent.setContent(getContent());
		documentContent.setFooter(getFooter());
		documentContent.setMetadata(getMetadata());
		return documentContent;
	}
	
	protected String getHeader() {
		StringBuilder headerBuilder = new StringBuilder();
		
		if(headers != null) {
			for(String header : headers) {
				headerBuilder.append(header).append(" ");
			}
		}
		
		return headerBuilder.toString().trim();
	}
	
	protected String getContent() {
		return content.toString();
		
	}
	
	protected String getFooter() {
		StringBuilder footerBuilder = new StringBuilder();
		
		if(footers != null) {
			for(String footer : footers) {
				footerBuilder.append(footer).append(" ");
			}
		}
		
		return footerBuilder.toString().trim();
	}
	
	protected Map<String, String> getMetadata() {
		return metadatas;
	}
	
	private int getHeaderOffset() {
		if(parameters != null
				&& parameters.containsKey(HEADER_OFFSET)) {
			try {
				return Integer.parseInt(parameters.get(HEADER_OFFSET));
			} catch(NumberFormatException err) {
				// Ignore
			}
		}
		
		logger.debug("Default header offset.");
		return 72;
	}
	
	private int getFooterOffset() {
		if(parameters != null
				&& parameters.containsKey(FOOTER_OFFSET)) {
			try {
				return Integer.parseInt(parameters.get(FOOTER_OFFSET));
			} catch(NumberFormatException err) {
				// Ignore
			}
		}
		
		logger.debug("Default footer offset.");
		return 72;
	}
}
