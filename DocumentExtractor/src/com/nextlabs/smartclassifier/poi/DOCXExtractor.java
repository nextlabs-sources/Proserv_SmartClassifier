package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class DOCXExtractor 
		extends POIDocumentExtractor {
	
	private POIXMLDocument poixmlDocument = null;
		
	public DOCXExtractor(String absoluteFilePath) 
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poixmlDocument = new XWPFDocument(fileInputStream);
		} catch (POIXMLException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath, e);
			throw e;
		}
	}
	
	private XWPFDocument getDocument() {
		return (XWPFDocument) poixmlDocument;
	}
	
	@Override
	protected String getHeader() {
		if(poixmlDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> headers = new LinkedHashSet<String>();
			XWPFDocument document = getDocument();
			
			for(XWPFHeader header : document.getHeaderList()) {
				headers.add(header.getText().trim());
			}
			
			if(document.getHeaderFooterPolicy() != null) {
				if(document.getHeaderFooterPolicy().getDefaultHeader() != null) {
					headers.add(document.getHeaderFooterPolicy().getDefaultHeader().getText().trim());
				}
				if(document.getHeaderFooterPolicy().getOddPageHeader() != null) {
					headers.add(document.getHeaderFooterPolicy().getOddPageHeader().getText().trim());
				}
				if(document.getHeaderFooterPolicy().getEvenPageHeader() != null) {
					headers.add(document.getHeaderFooterPolicy().getEvenPageHeader().getText().trim());
				}
			}
			
			StringBuilder headerBuilder = new StringBuilder();
			for(String header : headers) {
				headerBuilder.append(header).append(" ");
			}
			
			return headerBuilder.toString().trim();
		}
	}
	
	@Override
	protected String getContent() {
		if(poixmlDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			StringBuilder contentBuilder = new StringBuilder();
			XWPFDocument document = getDocument();
			
			for(XWPFParagraph paragraph : document.getParagraphs()) {
				contentBuilder.append(paragraph.getText()).append(" ");
			}
			return contentBuilder.toString().trim();
		}
	}
	
	@Override
	protected String getFooter() {
		if(poixmlDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> footers = new LinkedHashSet<String>();
			XWPFDocument document = getDocument();
			
			for(XWPFFooter footer : document.getFooterList()) {
				footers.add(footer.getText().trim());
			}
			
			if(document.getHeaderFooterPolicy() != null) {
				if(document.getHeaderFooterPolicy().getDefaultFooter() != null) {
					footers.add(document.getHeaderFooterPolicy().getDefaultFooter().getText().trim());
				}
				if(document.getHeaderFooterPolicy().getOddPageFooter() != null) {
					footers.add(document.getHeaderFooterPolicy().getOddPageFooter().getText().trim());
				}
				if(document.getHeaderFooterPolicy().getEvenPageFooter() != null) {
					footers.add(document.getHeaderFooterPolicy().getEvenPageFooter().getText().trim());
				}
			}
			
			StringBuilder footerBuilder = new StringBuilder();
			for(String footer : footers) {
				footerBuilder.append(footer).append(" ");
			}
			
			return footerBuilder.toString().trim();
		}
	}
	
	@Override
	protected Map<String, String> getMetadata() {
		return getXMLLayoutMetadata(poixmlDocument);
	}
}
