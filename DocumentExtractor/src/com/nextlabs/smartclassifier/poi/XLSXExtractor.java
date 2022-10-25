package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXExtractor 
		extends POIDocumentExtractor {
	
	private POIXMLDocument poixmlDocument = null;
		
	public XLSXExtractor(String absoluteFilePath) 
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poixmlDocument = new XSSFWorkbook(fileInputStream);
		} catch (POIXMLException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath, e);
			throw e;
		}
	}
	
	private XSSFWorkbook getDocument() {
		return (XSSFWorkbook) poixmlDocument;
	}

	@Override
	protected String getHeader() {
		if(poixmlDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> headers = new LinkedHashSet<String>();
			XSSFWorkbook document = getDocument();
			
			for(int i = 0; i < document.getNumberOfSheets(); i++) {
				XSSFSheet sheet = document.getSheetAt(i);
				if(sheet.getHeader()!=null && sheet.getHeader().getLeft()!=null) {
					headers.add(sheet.getHeader().getLeft().trim());
				}				
				if(sheet.getHeader()!=null && sheet.getHeader().getCenter()!=null) {
					headers.add(sheet.getHeader().getCenter().trim());
				}
				if(sheet.getHeader()!=null && sheet.getHeader().getRight()!=null) {
					headers.add(sheet.getHeader().getRight().trim());
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
			XSSFWorkbook document = getDocument();
			
			for(int i = 0; i < document.getNumberOfSheets(); i++) {
				XSSFSheet sheet = document.getSheetAt(i);
				Iterator<Row> rowIterator = sheet.iterator();
				
				while(rowIterator.hasNext()) {
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					
					while(cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						 
						switch(cell.getCellType()) {
							case Cell.CELL_TYPE_BOOLEAN:
								contentBuilder.append(cell.getBooleanCellValue()).append(" ");
								break;
							case Cell.CELL_TYPE_NUMERIC:
								// To avoid '.0' for integer numbers.
								double cellContent = cell.getNumericCellValue();
								if((cellContent % 1) == 0) {
									contentBuilder.append(new Double(cellContent).intValue()).append(" ");									
								} else {
									contentBuilder.append(cellContent).append(" ");		
								}
								break;
							case Cell.CELL_TYPE_STRING:
								contentBuilder.append(cell.getStringCellValue()).append(" ");
								break;
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
			XSSFWorkbook document = getDocument();
			
			for(int i = 0; i < document.getNumberOfSheets(); i++) {
				XSSFSheet sheet = document.getSheetAt(i);
				if(sheet.getFooter()!=null && sheet.getFooter().getLeft()!=null) {
					footers.add(sheet.getFooter().getLeft().trim());
				}
				if(sheet.getFooter()!=null && sheet.getFooter().getLeft()!=null) {
					footers.add(sheet.getFooter().getCenter().trim());
				}
				if(sheet.getFooter()!=null && sheet.getFooter().getLeft()!=null) {
					footers.add(sheet.getFooter().getRight().trim());
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
