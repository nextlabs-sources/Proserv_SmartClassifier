package com.nextlabs.smartclassifier.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.POIDocument;
import org.apache.poi.hdgf.HDGFDiagram;
import org.apache.poi.hdgf.chunks.Chunk;
import org.apache.poi.hdgf.chunks.Chunk.Command;
import org.apache.poi.hdgf.streams.ChunkStream;
import org.apache.poi.hdgf.streams.PointerContainingStream;
import org.apache.poi.hdgf.streams.Stream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class VSDExtractor 
		extends POIDocumentExtractor {
	
	private POIDocument poiDocument = null;
	private POIFSFileSystem poifsFileSystem = null;
	
	public VSDExtractor(String absoluteFilePath)
			throws Exception {
		this.absoluteFilePath = absoluteFilePath;
		
		try (FileInputStream fileSystemInputStream = new FileInputStream(new File(absoluteFilePath))) {
			poifsFileSystem = new POIFSFileSystem(fileSystemInputStream);
			poiDocument = new HDGFDiagram(poifsFileSystem);
		} catch (IllegalArgumentException | IOException e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath + ". Reason: " + e);
			throw e;
		} catch (Exception e) {
			logger.error("(Constructor) Error processing file: " + absoluteFilePath, e);
			throw e;
		}
	}
	
	private HDGFDiagram getDocument() {
		return (HDGFDiagram) poiDocument;
	}
	
	@Override
	protected String getHeader() {
		if(poiDocument == null) {
			logger.error("Document not initialized. Unable to extract document data.");
			return null;
		} else {
			Set<String> headers = new LinkedHashSet<String>();
			
			// Leave empty for now
			
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
			List<String> textList = new ArrayList<String>();
			
			// Find text on each stream
			for(Stream stream : getDocument().getTopLevelStreams()) {
				findText(stream, textList);
			}
			
			for(String text : textList) {
				contentBuilder.append(text);
				
				if(!(text.endsWith("\r") && text.endsWith("\n"))) {
					contentBuilder.append("\n");
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
			
			// Leave empty for now
			
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
	
	private void findText(Stream stream, List<String> textList) {
		if(stream instanceof PointerContainingStream) {
			PointerContainingStream ps = (PointerContainingStream)stream;
			for(int i=0; i<ps.getPointedToStreams().length; i++) {
				findText(ps.getPointedToStreams()[i], textList);
			}
		}
		
		if(stream instanceof ChunkStream) {
			ChunkStream cs = (ChunkStream)stream;
			for(int i=0; i<cs.getChunks().length; i++) {
				Chunk chunk = cs.getChunks()[i];
				if(chunk != null &&
						chunk.getName() != null &&
						chunk.getName().equals("Text") &&
						chunk.getCommands().length > 0) {
				   
					// First command
					Command cmd = chunk.getCommands()[0];
					if(cmd != null && cmd.getValue() != null) {
					   // Capture the text, as long as it isn't
					   //  simply an empty string
					   String str = cmd.getValue().toString();
					   if(str.equals("") || str.equals("\n")) {
						   // Ignore empty strings
					   } else {
						   textList.add(str);
					   }
					}
				}
			}
		}
	}
}
