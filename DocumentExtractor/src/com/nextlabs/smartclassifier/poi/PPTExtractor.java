package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIDocument;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextShape;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class PPTExtractor
		extends POIDocumentExtractor {

	private POIDocument poiDocument = null;
	private POIFSFileSystem poifsFileSystem = null;

	public PPTExtractor(String absoluteFilePath) 
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileSystemInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poifsFileSystem = new POIFSFileSystem(fileSystemInputStream);
			poiDocument = new HSLFSlideShow(poifsFileSystem);
		} catch (IllegalArgumentException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath, e);
			throw e;
		}
	}

	private HSLFSlideShow getDocument() {
		return (HSLFSlideShow) poiDocument;
	}
	
	@Override
	protected String getHeader() {
		if(poiDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			SlideShow slideShow = new SlideShow(getDocument());
			Set<String> headers = new LinkedHashSet<String>();

			/**
			 * Removed reading of header and footer from notes in v1 till PPTX
			 * can also support reading from notes for consistency.
			 */

			if(slideShow.getNotesHeadersFooters() != null && slideShow.getNotesHeadersFooters().getHeaderText() != null) {
				headers.add(slideShow.getNotesHeadersFooters().getHeaderText().trim());
			}
			if(slideShow.getSlideHeadersFooters() != null && slideShow.getSlideHeadersFooters().getHeaderText() != null) {
				headers.add(slideShow.getSlideHeadersFooters().getHeaderText().trim());
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
			Slide[] slides = new SlideShow(getDocument()).getSlides();

			for(Slide slide : slides) {
				Shape[] shapes = slide.getShapes();
				for(Shape shape : shapes) {
					if(shape instanceof TextShape) {
						TextShape textShape = (TextShape) shape;
						if(textShape.getText() != null  && textShape.getText().length() > 0) {
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
		if(poiDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			SlideShow slideShow = new SlideShow(getDocument());
			Set<String> footers = new LinkedHashSet<String>();
			
			/**
			 * Removed reading of header and footer from notes in v1 till PPTX
			 * can also support reading from notes for consistency.
			 */
			
			if(slideShow.getNotesHeadersFooters() != null && slideShow.getNotesHeadersFooters().getFooterText() != null) {
				footers.add(slideShow.getNotesHeadersFooters().getFooterText().trim());
			}
			if(slideShow.getSlideHeadersFooters() != null && slideShow.getSlideHeadersFooters().getFooterText() != null) {
				footers.add(slideShow.getSlideHeadersFooters().getFooterText().trim());
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
