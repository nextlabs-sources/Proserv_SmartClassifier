package com.nextlabs.fileextractor.poi;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import com.nextlabs.smartclassifier.DocumentContent;
import com.nextlabs.smartclassifier.DocumentExtractor;
import com.nextlabs.smartclassifier.poi.MSGExtractor;


public class MSGExtractorTest {
	
	private final static Logger logger = LogManager.getLogger(MSGExtractor.class);
	
	private static DocumentContent documentContent1, documentContent2, documentContent3, documentContent4;

	private static DocumentContent extractFile(String absoluteFilePath) throws Exception {
		DocumentExtractor extractor = new MSGExtractor(absoluteFilePath);
		return extractor.extract();
	}

	@BeforeClass
	public static void init() throws Exception {
		// File : Plain text email
		// Meta-data - String 
        File file1 = new File("test/res/One.msg");
        documentContent1 = extractFile(file1.getAbsolutePath());
        
        // File : Text + Graphic email   
        // Meta-data - String
        File file2 = new File("test/res/Two.msg");
        documentContent2 = extractFile(file2.getAbsolutePath());
        
        // File : Plain text email + attachment
        // Meta-data - String
        File file3 = new File("test/res/Three.msg");
        documentContent3 = extractFile(file3.getAbsolutePath());        
        
        // File : Text + Graphic email + attachment
        // Meta-data - String
        File file4 = new File("test/res/Four.msg");
        documentContent4 = extractFile(file4.getAbsolutePath());        
	}
	
    //File 1
    @Test
    public void testDocHeader1() {
        logger.info("Checking MSG One header.");
        assertEquals("", documentContent1.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testDocContent1() {
        logger.info("Checking MSG One content.");
        assertEquals("This is a plain text email.", documentContent1.getDocumentContent().trim());
        logger.info("Done");
    }

    @Test
    public void testDocFooter1() {
        logger.info("Checking MSG One footer.");
        assertEquals("", documentContent1.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testDocMetadata1() {    	   	    
        logger.info("Checking MSG One metadata.");
        assertEquals("application/ms-tnef; name=\"winmail.dat\"", documentContent1.getDocumentMetadataKeyToValueMap().get("Content-Type"));
        assertEquals("Email Test 1", documentContent1.getDocumentMetadataKeyToValueMap().get("Subject"));
        assertEquals("Shah Neng Chok <ShahNeng.Chok@nextlabs.com>", documentContent1.getDocumentMetadataKeyToValueMap().get("From"));
        assertEquals("Shah Neng Chok <ShahNeng.Chok@nextlabs.com>", documentContent1.getDocumentMetadataKeyToValueMap().get("To"));
        logger.info("Done");
    }

    //File 2
    @Test
    public void testDocHeader2() {
        logger.info("Checking MSG Two header.");
        assertEquals("", documentContent2.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testDocContent2() {
        logger.info("Checking MSG Two content.");
        System.out.println(documentContent2.getDocumentContent().trim());
        assertEquals("This is an email with graphic.\n\n \n\nhttp://dreamatico.com/data_images/cake/cake-8.jpg", documentContent2.getDocumentContent().trim());
        logger.info("Done");
    }

    @Test
    public void testDocFooter2() {
        logger.info("Checking MSG Two footer.");
        assertEquals("", documentContent2.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testDocMetadata2() {    	   	    
        logger.info("Checking MSG Two metadata.");
        assertEquals("application/ms-tnef; name=\"winmail.dat\"", documentContent2.getDocumentMetadataKeyToValueMap().get("Content-Type"));
        assertEquals("Email Test 2", documentContent2.getDocumentMetadataKeyToValueMap().get("Subject"));
        assertEquals("Shah Neng Chok <ShahNeng.Chok@nextlabs.com>", documentContent2.getDocumentMetadataKeyToValueMap().get("From"));
        assertEquals("Shah Neng Chok <ShahNeng.Chok@nextlabs.com>", documentContent2.getDocumentMetadataKeyToValueMap().get("To"));
        logger.info("Done");
    }
	
    //File 3
    @Test
    public void testDocHeader3() {
        logger.info("Checking MSG Three header.");
        assertEquals("", documentContent3.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testDocContent3() {
        logger.info("Checking MSG Three content.");
        assertEquals("This is a plain text email with document attached.", documentContent3.getDocumentContent().trim());
        logger.info("Done");
    }

    @Test
    public void testDocFooter3() {
        logger.info("Checking MSG Three footer.");
        assertEquals("", documentContent3.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testDocMetadata3() {    	   	    
        logger.info("Checking MSG Three metadata.");
        assertEquals("application/ms-tnef; name=\"winmail.dat\"", documentContent3.getDocumentMetadataKeyToValueMap().get("Content-Type"));
        assertEquals("Email Test 3", documentContent3.getDocumentMetadataKeyToValueMap().get("Subject"));
        assertEquals("Shah Neng Chok <ShahNeng.Chok@nextlabs.com>", documentContent3.getDocumentMetadataKeyToValueMap().get("From"));
        assertEquals("Shah Neng Chok <ShahNeng.Chok@nextlabs.com>", documentContent3.getDocumentMetadataKeyToValueMap().get("To"));
        logger.info("Done");
    }

    //File 4
    @Test
    public void testDocHeader4() {
        logger.info("Checking MSG Four header.");
        assertEquals("", documentContent4.getDocumentHeader());
        logger.info("Done");
    }

    @Test
    public void testDocContent4() {
        logger.info("Checking MSG Four content.");
        assertEquals("This is an email with graphic and attachment.\nhttp://eyl.co.in/images/gifts/cake-blackforest.jpg", documentContent4.getDocumentContent().trim());
        logger.info("Done");
    }

    @Test
    public void testDocFooter4() {
        logger.info("Checking MSG Four footer.");
        assertEquals("", documentContent4.getDocumentFooter());
        logger.info("Done");
    }
    
    @Test
    public void testDocMetadata4() {    	   	    
        logger.info("Checking MSG Four metadata.");
        assertEquals("application/ms-tnef; name=\"winmail.dat\"", documentContent4.getDocumentMetadataKeyToValueMap().get("Content-Type"));
        assertEquals("Email Test 4", documentContent4.getDocumentMetadataKeyToValueMap().get("Subject"));
        assertEquals("Shah Neng Chok <ShahNeng.Chok@nextlabs.com>", documentContent4.getDocumentMetadataKeyToValueMap().get("From"));
        assertEquals("Shah Neng Chok <ShahNeng.Chok@nextlabs.com>", documentContent4.getDocumentMetadataKeyToValueMap().get("To"));
        logger.info("Done");
    }
}
