package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIDocument;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.HeaderStories;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class DOCExtractor 
		extends POIDocumentExtractor {
	
	private POIDocument poiDocument = null;
	private POIFSFileSystem poifsFileSystem = null;
	
	public DOCExtractor(String absoluteFilePath) 
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileSystemInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poifsFileSystem = new POIFSFileSystem(fileSystemInputStream);
			poiDocument = new HWPFDocument(poifsFileSystem);
		} catch (IllegalArgumentException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath, e);
			throw e;
		}
	}
	
	private HWPFDocument getDocument() {
		return (HWPFDocument) poiDocument;
	}
	
	@Override
	protected String getHeader() {
		if(poiDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> headers = new LinkedHashSet<String>();
			
			try {
				HeaderStories headerStories = new HeaderStories(getDocument());

				if(getDocument().getSummaryInformation()!=null) {
					for(int i = 1; i <= getDocument().getSummaryInformation().getPageCount(); i++) {
						if(headerStories!=null && headerStories.getHeader(i)!=null) {
							headers.add(headerStories.getHeader(i).trim());
						}
					}
				}
			} catch(Exception e) {
				// Error getting header of file. Will be set as an empty list.
			}
			
			StringBuilder headerBuilder = new StringBuilder();
			for(String header : headers) {
				headerBuilder.append(header);
				headerBuilder.append(" ");
			}
			
			return headerBuilder.toString().trim();
		}
	}
	
	@Override
	protected String getContent() {
		if(poiDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			StringBuilder contentBuilder = new StringBuilder();
			for(int i = 0; i < getDocument().getRange().numParagraphs(); i++) {
				Paragraph paragraph = getDocument().getRange().getParagraph(i);
				contentBuilder.append(paragraph.text().trim());
				contentBuilder.append(" ");
			}
			return contentBuilder.toString().trim();
		}
	}
	
	@Override
	protected String getFooter() {
		if(poiDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> footers = new LinkedHashSet<String>();
			
			try {
				HeaderStories footerStories = new HeaderStories(getDocument());

				if(getDocument().getSummaryInformation()!=null) {
					for(int i = 1; i <= getDocument().getSummaryInformation().getPageCount(); i++) {
						if(footerStories!=null && footerStories.getFooter(i)!=null) {
							footers.add(footerStories.getFooter(i).trim());
						}
					}
				}
			} catch(Exception e) {
				// Error getting footer of file. Will be set as an empty list.
			}
			
			StringBuilder footerBuilder = new StringBuilder();
			for(String footer : footers) {
				footerBuilder.append(footer);
				footerBuilder.append(" ");
			}
			
			return footerBuilder.toString().trim();
		}
	}
	
	@Override
	protected Map<String, String> getMetadata() {
		return getHorribleLayoutMetadata(poifsFileSystem);
	}
}
