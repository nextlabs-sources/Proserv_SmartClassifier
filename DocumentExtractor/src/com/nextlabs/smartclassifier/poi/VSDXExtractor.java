package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLException;
import org.apache.poi.xdgf.usermodel.XDGFPage;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;

import com.nextlabs.smartclassifier.DocumentContent;

public class VSDXExtractor 
		extends POIDocumentExtractor {
	
	private POIXMLDocument poixmlDocument = null;
	
	public VSDXExtractor(String absoluteFilePath)
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poixmlDocument = new XmlVisioDocument(fileInputStream);	
		} catch (POIXMLException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath , e);
			throw e;
		}
	}
	
	private XmlVisioDocument getDocument() {
		return (XmlVisioDocument) poixmlDocument;
	}
	
	@Override
	protected String getHeader() {
		if(poixmlDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> headers = new LinkedHashSet<String>();
			
//			for(XDGFPage page : getDocument().getPages()) {
//				
//				
//				
//				
//			}
			
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
			
			for(XDGFPage page : getDocument().getPages()) {
				for(XDGFShape shape : page.getContent().getShapes()) {
					contentBuilder.append(shape.getTextAsString()).append(" ");
				}
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
	
	
	
	public static void main(String[] args) {
		try {
			VSDXExtractor extractor = new VSDXExtractor("C:/Users/tduong/workspace-js/Smart Classifier/FileExtractor/test/res/test.vsdx");
			
			DocumentContent documentContent = extractor.extract();
			
			System.out.println("Header: " + documentContent.getDocumentHeader());
			System.out.println("Body: " + documentContent.getDocumentContent());
			System.out.println("Footer: " + documentContent.getDocumentFooter());
			System.out.println("Metadata: ");
			
			for(String key : documentContent.getDocumentMetadataKeyToValueMap().keySet()) {
				System.out.println(key + ":" + documentContent.getDocumentMetadataKeyToValueMap().get(key));
			}
			
		} catch(Exception err) {
			err.printStackTrace();
		}
	}
}
