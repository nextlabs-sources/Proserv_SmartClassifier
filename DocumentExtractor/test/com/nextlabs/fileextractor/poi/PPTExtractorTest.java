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
import com.nextlabs.smartclassifier.poi.PPTExtractor;

public class PPTExtractorTest {
	
	private final Logger logger = LoggerFactory.getLogger(PPTExtractorTest.class);
	
	private static DocumentContent documentContent1, documentContent2, documentContent3, documentContent4;

	private static DocumentContent extractFile(String absoluteFilePath) throws Exception {
		DocumentExtractor extractor = new PPTExtractor(absoluteFilePath);
		return extractor.extract();
	}
	
	@BeforeClass
	public static void init() throws Exception {
		// File : Header - Single Word, Footer - Single Word, Content - One Paragraph, 
		// Meta-data - String 
        File file1 = new File("test/res/One.ppt");
        documentContent1 = extractFile(file1.getAbsolutePath());
        
        // File : Header - Hyphen Separated Word, Footer - Hyphen Separated Word,   
        // Content - One Paragraph, Meta-data - Number
        File file2 = new File("test/res/Two.ppt");
        documentContent2 = extractFile(file2.getAbsolutePath());
        
        // File : Header - Space Separated Word, Footer - Space Separated Word,   
        // Content - One Paragraph, Meta-data - Date
        File file3 = new File("test/res/Three.ppt");
        documentContent3 = extractFile(file3.getAbsolutePath());        

        // File : Header - With Special Characters (, : / -), Footer - With Special Characters (, : / -), 
        // Content - Multiple Paragraphs, Meta-data - BAE Classification Type Tags
        File file4 = new File("test/res/Four.ppt");
        documentContent4 = extractFile(file4.getAbsolutePath());        
	}
	
	//File 1
//    @Test
//    public void testPptHeader1() {
//        logger.info("Checking PPT One header.");
//        assertEquals("OFFICIAL", documentContent1.getDocumentHeader());
//        logger.info("Done");
//    }

    @Test
    public void testPptContent1() {
        logger.info("Checking PPT One content.");
        assertEquals("This is a test presentation Sample file OFFICIAL", documentContent1.getDocumentContent());
        logger.info("Done");
    }

//    @Test
//    public void testPptFooter1() {
//        logger.info("Checking PPT One footer.");
//        assertEquals("OFFICIAL", documentContent1.getDocumentFooter());
//        logger.info("Done");
//    }
    
    @Test
    public void testPptMetadata1() {    	   	    
        logger.info("Checking PPT One metadata.");
        assertEquals("Private", documentContent1.getDocumentMetadataKeyToValueMap().get("Classification"));
        assertEquals("ITAR", documentContent1.getDocumentMetadataKeyToValueMap().get("Export Control"));
        assertEquals("TAA 2400/40E", documentContent1.getDocumentMetadataKeyToValueMap().get("License"));
        logger.info("Done");
    }
    
        
    //File 2    
//    @Test
//    public void testPptHeader2() {
//        logger.info("Checking PPT Two header.");
//        assertEquals("OFFICIAL-SENSITIVE-NNPPI", documentContent2.getDocumentHeader());
//        logger.info("Done");
//    }

    @Test
    public void testPptContent2() {
        logger.info("Checking PPT Two content.");
        assertEquals("This is a test presentation Sample file OFFICIAL", documentContent1.getDocumentContent());
        logger.info("Done");
    }

//    @Test
//    public void testPptFooter2() {
//        logger.info("Checking PPT Two footer.");
//        assertEquals("OFFICIAL-SENSITIVE-NNPPI", documentContent2.getDocumentFooter());
//        logger.info("Done");
//    }
    
    @Test
    public void testPptMetadata2() {    	   	    
        logger.info("Checking PPT Two metadata.");
        assertEquals("4", documentContent2.getDocumentMetadataKeyToValueMap().get("Confidentiality Level"));
        logger.info("Done");
    }
    
    //File 3
//    @Test
//    public void testPptHeader3() {
//        logger.info("Checking PPT Three header.");
//        assertEquals("OFFICIAL SENSITIVE", documentContent3.getDocumentHeader());
//        logger.info("Done");
//    }

    @Test
    public void testPptContent3() {
        logger.info("Checking PPT Three content.");
        assertEquals("This is a test presentation Sample file OFFICIAL", documentContent1.getDocumentContent());
        logger.info("Done");
    }

//    @Test
//    public void testPptFooter3() {
//        logger.info("Checking PPT Three footer.");
//        assertEquals("OFFICIAL SENSITIVE", documentContent3.getDocumentFooter());
//        logger.info("Done");
//    }   
    
    @Test
    public void testPptMetadata3() throws ParseException {    	   	    
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    	SimpleDateFormat sdf1 = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
    	sdf1.setTimeZone(TimeZone.getTimeZone("SGT"));
    	String str = "2014-07-20T00:00:00Z";
    	Date date = sdf.parse(str);
        logger.info("Checking DOC Three metadata.");
        assertEquals(date, sdf1.parse(documentContent3.getDocumentMetadataKeyToValueMap().get("License Start Date")));
        logger.info("Done");
    }
       
    //File 4
//    @Test
//    public void testPptHeader4() {
//        logger.info("Checking PPT Four header.");
//        assertEquals("Subject to UK Export Control, License ID: ATT/1", documentContent4.getDocumentHeader());
//        logger.info("Done");
//    }

    @Test
    public void testPptContent4() {
        logger.info("Checking PPT Four content.");
        assertEquals("This is a test presentation Subject to UK Export Control, License ID: ATT/1", documentContent4.getDocumentContent());
        logger.info("Done");
    }

//    @Test
//    public void testPptFooter4() {
//        logger.info("Checking PPT Four footer.");
//        assertEquals("Subject to UK Export Control, License ID: ATT/1", documentContent4.getDocumentFooter());
//        logger.info("Done");
//    }
    
    @Test
    public void testPptMetadata4() {
    	logger.info("Checking PPT Four metadata.");  	    	    
        assertEquals("AB XYZ 2011 M-N-O", documentContent4.getDocumentMetadataKeyToValueMap().get("Tag"));
        assertEquals("http://www.google.com/", documentContent4.getDocumentMetadataKeyToValueMap().get("Link"));
        logger.info("Done");
    }

}
