package com.nextlabs.fileextractor.poi;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.smartclassifier.DocumentContent;
import com.nextlabs.smartclassifier.DocumentExtractor;
import com.nextlabs.smartclassifier.poi.DOCXExtractor;

public class DOCXExtractorTest {
	
	private final Logger logger = LoggerFactory.getLogger(DOCXExtractorTest.class);
	
	private static DocumentContent documentContent1, documentContent2, documentContent3, documentContent4;

	private static DocumentContent extractFile(String absoluteFilePath) throws Exception {
		DocumentExtractor extractor = new DOCXExtractor(absoluteFilePath);
		return extractor.extract();
	}
	
	@BeforeClass
	public static void init() throws Exception {
		// File : Header - Single Word, Footer - Single Word, Content - One Paragraph, 
		// Meta-data - String 
        File file1 = new File("test/res/One.docx");
        documentContent1 = extractFile(file1.getAbsolutePath());
        
        // File : Header - Hyphen Separated Word, Footer - Hyphen Separated Word,   
        // Content - One Paragraph, Meta-data - Number
        File file2 = new File("test/res/Two.docx");
        documentContent2 = extractFile(file2.getAbsolutePath());
        
        // File : Header - Space Separated Word, Footer - Space Separated Word,   
        // Content - One Paragraph, Meta-data - Date
        File file3 = new File("test/res/Three.docx");
        documentContent3 = extractFile(file3.getAbsolutePath());        

        // File : Header - With Special Characters (, : / -), Footer - With Special Characters (, : / -), 
        // Content - Multiple Paragraphs, Meta-data - BAE Classification Type Tags
        File file4 = new File("test/res/Four.docx");
        documentContent4 = extractFile(file4.getAbsolutePath());        
	}
	
	//File 1
    @Test
    public void testDocxHeader1() {
        logger.info("Checking DOCX One header.");
        assertEquals("OFFICIAL", documentContent1.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testDocxContent1() {
        logger.info("Checking DOCX One content.");
        assertEquals("This document describes the solution design for the Management of Controlled Data InfoSec Enhancements project at BAE Systems powered by NextLabs Information Risk Management technologies.", documentContent1.getDocumentContent());
        logger.info("Done");
    }

    @Test
    public void testDocxFooter1() {
        logger.info("Checking DOCX One footer.");
        assertEquals("OFFICIAL", documentContent1.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testDocxMetadata1() {    	   	    
        logger.info("Checking DOCX One metadata.");
        assertEquals("Private", documentContent1.getDocumentMetadataKeyToValueMap().get("Classification"));
        assertEquals("ITAR", documentContent1.getDocumentMetadataKeyToValueMap().get("Export Control"));
        assertEquals("TAA 2400/40E", documentContent1.getDocumentMetadataKeyToValueMap().get("License"));
        logger.info("Done");
    }
    
        
    //File 2    
    @Test
    public void testDocxHeader2() {
        logger.info("Checking DOCX Two header.");
        assertEquals("OFFICIAL-SENSITIVE-NNPPI", documentContent2.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testDocxContent2() {
        logger.info("Checking DOCX Two content.");
        assertEquals("This document describes the solution design for the Management of Controlled Data InfoSec Enhancements project at BAE Systems powered by NextLabs Information Risk Management technologies.", documentContent2.getDocumentContent());
        logger.info("Done");
    }

    @Test
    public void testDocxFooter2() {
        logger.info("Checking DOCX Two footer.");
        assertEquals("OFFICIAL-SENSITIVE-NNPPI", documentContent2.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testDocxMetadata2() {    	   	    
        logger.info("Checking DOCX Two metadata.");
        assertEquals("4", documentContent2.getDocumentMetadataKeyToValueMap().get("Confidentiality Level"));
        logger.info("Done");
    }
    
    //File 3
    @Test
    public void testDocxHeader3() {
        logger.info("Checking DOCX Three header.");
        assertEquals("OFFICIAL SENSITIVE", documentContent3.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testDocxContent3() {
        logger.info("Checking DOCX Three content.");
        assertEquals("This document describes the solution design for the Management of Controlled Data InfoSec Enhancements project at BAE Systems powered by NextLabs Information Risk Management technologies.", documentContent3.getDocumentContent());
        logger.info("Done");
    }

    @Test
    public void testDocxFooter3() {
        logger.info("Checking DOCX Three footer.");
        assertEquals("OFFICIAL SENSITIVE", documentContent3.getDocumentFooter());
        logger.info("Done");
    }   
    
    @Test
    public void testDocxMetadata3() throws ParseException {    	   	    
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    	sdf1.setTimeZone(TimeZone.getTimeZone("SGT"));
    	String str = "2014-07-20T00:00:00";
    	Date date = sdf.parse(str);
        logger.info("Checking DOCX Three metadata.");
        assertEquals(date, sdf1.parse(documentContent3.getDocumentMetadataKeyToValueMap().get("License Start Date")));
        logger.info("Done");
    }
       
    //File 4
    @Test
    public void testDocxHeader4() {
        logger.info("Checking DOCX Four header.");
        assertEquals("Subject to UK Export Control, License ID: ATT/1", documentContent4.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testDocxContent4() {
        logger.info("Checking DOCX Four content.");
        assertEquals("This is a test document.", documentContent4.getDocumentContent());
        logger.info("Done");
    }

    @Test
    public void testDocxFooter4() {
        logger.info("Checking DOCX Four footer.");
        assertEquals("Subject to UK Export Control, License ID: ATT/1", documentContent4.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testDocxMetadata4() {    	  	    	
    	logger.info("Checking DOCX Four metadata.");
        assertEquals("AB XYZ 2011 M-N-O", documentContent4.getDocumentMetadataKeyToValueMap().get("Tag"));
        assertEquals("http://www.google.com/", documentContent4.getDocumentMetadataKeyToValueMap().get("Link"));
        logger.info("Done");
    }

}
