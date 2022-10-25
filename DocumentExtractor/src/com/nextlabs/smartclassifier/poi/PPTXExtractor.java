package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;

public class PPTXExtractor 
		extends POIDocumentExtractor {
	
	private POIXMLDocument poixmlDocument = null;
		
	public PPTXExtractor(String absoluteFilePath) 
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poixmlDocument = new XMLSlideShow(fileInputStream);	
		} catch (POIXMLException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath , e);
			throw e;
		}
	}
	
	private XMLSlideShow getDocument() {
		return (XMLSlideShow) poixmlDocument;
	}

	@Override
	protected String getHeader() {
		if(poixmlDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> headers = new LinkedHashSet<String>();
			// Left empty for v1.
			
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
			XSLFSlide[] slides = getDocument().getSlides();
			
			for(XSLFSlide slide : slides) {
				XSLFShape[] shapes = slide.getShapes();
				for(XSLFShape shape : shapes) {
					if(shape instanceof XSLFTextShape) {
						XSLFTextShape textShape = (XSLFTextShape) shape;
						if(textShape.getText() != null && textShape.getText().length() > 0) {
							contentBuilder.append(textShape.getText()).append(" ");
						}
					}
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
			// Left empty for v1.
			
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
