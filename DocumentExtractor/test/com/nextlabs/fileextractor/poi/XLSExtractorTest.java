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
import com.nextlabs.smartclassifier.poi.XLSExtractor;

public class XLSExtractorTest {
	
	private final Logger logger = LoggerFactory.getLogger(XLSExtractorTest.class);
	
	private static DocumentContent documentContent1, documentContent2, documentContent3, documentContent4;

	private static DocumentContent extractFile(String absoluteFilePath) throws Exception {
		DocumentExtractor extractor = new XLSExtractor(absoluteFilePath);
		return extractor.extract();
	}
	
	@BeforeClass
	public static void init() throws Exception {
		// File : Header - Single Word, Footer - Single Word, Content - One Paragraph, 
		// Meta-data - String 
        File file1 = new File("test/res/One.xls");
        documentContent1 = extractFile(file1.getAbsolutePath());
        
        // File : Header - Hyphen Separated Word, Footer - Hyphen Separated Word,   
        // Content - One Paragraph, Meta-data - Number
        File file2 = new File("test/res/Two.xls");
        documentContent2 = extractFile(file2.getAbsolutePath());
        
        // File : Header - Space Separated Word, Footer - Space Separated Word,   
        // Content - One Paragraph, Meta-data - Date
        File file3 = new File("test/res/Three.xls");
        documentContent3 = extractFile(file3.getAbsolutePath());        

        // File : Header - With Special Characters (, : / -), Footer - With Special Characters (, : / -), 
        // Content - Multiple Paragraphs, Meta-data - BAE Classification Type Tags
        File file4 = new File("test/res/Four.xls");
        documentContent4 = extractFile(file4.getAbsolutePath());        
	}
	
	//File 1
    @Test
    public void testXlsHeader1() {
        logger.info("Checking XLS One header.");
        assertEquals("OFFICIAL", documentContent1.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testXlsContent1() {
        logger.info("Checking XLS One content.");
        assertEquals("1 2 3 4 5", documentContent1.getDocumentContent());
        logger.info("Done");
    }

    @Test
    public void testXlsFooter1() {
        logger.info("Checking XLS One footer.");
        assertEquals("OFFICIAL", documentContent1.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testXlsMetadata1() {    	   	    
        logger.info("Checking XLS One metadata.");
        assertEquals("Private", documentContent1.getDocumentMetadataKeyToValueMap().get("Classification"));
        assertEquals("ITAR", documentContent1.getDocumentMetadataKeyToValueMap().get("Export Control"));
        assertEquals("TAA 2400/40E", documentContent1.getDocumentMetadataKeyToValueMap().get("License"));
        logger.info("Done");
    }
    
        
    //File 2    
    @Test
    public void testXlsHeader2() {
        logger.info("Checking XLS Two header.");
        assertEquals("OFFICIAL-SENSITIVE-NNPPI", documentContent2.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testXlsContent2() {
        logger.info("Checking XLS Two content.");
        assertEquals("1 2 3 4 5", documentContent2.getDocumentContent());
        logger.info("Done");
    }

    @Test
    public void testXlsFooter2() {
        logger.info("Checking XLS Two footer.");
        assertEquals("OFFICIAL-SENSITIVE-NNPPI", documentContent2.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testXlsMetadata2() {    	   	    
        logger.info("Checking XLS Two metadata.");
        assertEquals("4", documentContent2.getDocumentMetadataKeyToValueMap().get("Confidentiality Level"));
        logger.info("Done");
    }
    
    //File 3
    @Test
    public void testXlsHeader3() {
        logger.info("Checking XLS Three header.");
        assertEquals("OFFICIAL SENSITIVE", documentContent3.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testXlsContent3() {
        logger.info("Checking XLS Three content.");
        assertEquals("1 2 3 4 5", documentContent3.getDocumentContent());
        logger.info("Done");
    }

    @Test
    public void testXlsFooter3() {
        logger.info("Checking XLS Three footer.");
        assertEquals("OFFICIAL SENSITIVE", documentContent3.getDocumentFooter());
        logger.info("Done");
    }   
    
    @Test
    public void testXlsMetadata3() throws ParseException {    	   	    
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
    @Test
    public void testXlsHeader4() {
        logger.info("Checking XLS Four header.");
        assertEquals("Subject to UK Export Control, License ID: ATT/1", documentContent4.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testXlsContent4() {
        logger.info("Checking XLS Four content.");
        assertEquals("1 2 3 4 5", documentContent4.getDocumentContent());
        logger.info("Done");
    }

    @Test
    public void testXlsFooter4() {
        logger.info("Checking XLS Four footer.");
        assertEquals("Subject to UK Export Control, License ID: ATT/1", documentContent4.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testXlsMetadata4() {
    	  	    	
        logger.info("Checking XLS Four metadata."); 
        assertEquals("AB XYZ 2011 M-N-O", documentContent4.getDocumentMetadataKeyToValueMap().get("Tag"));
        assertEquals("http://www.google.com/", documentContent4.getDocumentMetadataKeyToValueMap().get("Link"));
        logger.info("Done");
    }

}
