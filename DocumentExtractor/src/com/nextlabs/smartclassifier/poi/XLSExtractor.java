package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIDocument;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class XLSExtractor 
		extends POIDocumentExtractor {

	private POIDocument poiDocument = null;
	private POIFSFileSystem poifsFileSystem = null;

	public XLSExtractor(String absoluteFilePath) 
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileSystemInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poifsFileSystem = new POIFSFileSystem(fileSystemInputStream);
			poiDocument = new HSSFWorkbook(poifsFileSystem);
		} catch (IllegalArgumentException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath, e);
			throw e;
		}
	}

	private HSSFWorkbook getDocument() {
		return (HSSFWorkbook) poiDocument;
	}

	@Override
	protected String getHeader() {
		if(poiDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> headers = new LinkedHashSet<String>();
			for(int i = 0; i < getDocument().getNumberOfSheets(); i++) {
				HSSFSheet sheet = getDocument().getSheetAt(i);
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
			for(int i = 0; i < getDocument().getNumberOfSheets(); i++) {
				HSSFSheet sheet = getDocument().getSheetAt(i);

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
		if(poiDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> footers = new LinkedHashSet<String>();
			for(int i = 0; i < getDocument().getNumberOfSheets(); i++) {
				HSSFSheet sheet = getDocument().getSheetAt(i);
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
