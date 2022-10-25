package com.nextlabs.smartclassifier.nxl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nextlabs.nxl.NxlException;
import com.nextlabs.nxl.RightsManager;
import com.nextlabs.smartclassifier.DocumentContent;
import com.nextlabs.smartclassifier.DocumentExtractor;
import com.nextlabs.smartclassifier.constant.SolrPredefinedField;
import com.nextlabs.smartclassifier.constant.SystemConfigKey;	
	
public class NXLExtractor
		extends DocumentExtractor
		implements SolrPredefinedField, SystemConfigKey {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());	
	protected String absoluteFilePath = null;
	protected Map<String, String> parameters;
	protected File file;
	private Map<String,String> hRMSConfig;
	private RightsManager rm;
	
	public NXLExtractor(String absoluteFilePath, Map<String, String> hRMSConfig) throws Exception {
		
		this.absoluteFilePath = absoluteFilePath;
		this.hRMSConfig = hRMSConfig;
		
		file = new File(absoluteFilePath);
		
		if(!file.exists()){
			throw new FileNotFoundException();
		}
		
		rm = setupRMSDK();
		
		if(rm==null){
			throw new Exception("RMSDK Setup failed");
		}
		
	}
	
	protected String getHeader() {
				
			return "";
	}
	
	protected String getContent() {
		return "";
	}
	
	protected String getFooter() {
		return "";
	}
	

	protected Map<String, String> getMetadata() {
		
		LinkedHashMap<String, String> metadataTags = new LinkedHashMap<String, String>();
		
		Map<String, String[]> hTags = null;
		
		try {		
			hTags =  rm.readTags(absoluteFilePath);
		} catch (Exception e) {
			logger.error("NXLExtractor :: getMetadata() Error reading metadata from nxl file " + e.getMessage(), e);
		}
		
		if (hTags!=null){
			
			logger.debug("NXL file contained metadata, proceed to extract!");
			
			Iterator<Entry<String, String[]>> it = hTags.entrySet().iterator();
		    while (it.hasNext()) {
		    	
		        Map.Entry<String, String[]> pair = (Map.Entry<String, String[]>)it.next();
		        String[] lstValues = pair.getValue();
		        metadataTags.put(pair.getKey(), getMultiValueTag(lstValues));
		        // avoids a ConcurrentModificationException
		        it.remove(); 
		    }
			
		}
		
		return metadataTags;
	}
	
	private String getMultiValueTag(String[] lstValues){
		
		if(lstValues.length<2){
			return lstValues[0];
		}
		
		String sValues = "";
		
        for (String st: lstValues){
        	sValues+=st+"|";
        }
        
        sValues = sValues.substring(0,sValues.length()-1);
		
		return sValues;
	}
	
	
	@Override
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public DocumentContent extract() throws Exception {
		DocumentContent documentContent = new DocumentContent();
		documentContent.setAbsoluteFilePath(absoluteFilePath);
		documentContent.setHeader(getHeader());
		documentContent.setContent(getContent());
		documentContent.setFooter(getFooter());
		documentContent.setMetadata(getMetadata());
		return documentContent;
	}
	
	private RightsManager setupRMSDK() {
		try {
			RightsManager rm = new RightsManager(hRMSConfig.get(SystemConfigKey.SKYDRM_ROUTER_URL), Integer.parseInt(hRMSConfig.get(SystemConfigKey.SKYDRM_APP_ID)), hRMSConfig.get(SystemConfigKey.SKYDRM_APP_KEY));
			logger.debug("SkyDRM Manager init success!!");
			return rm;
		} catch (NumberFormatException e) {
			logger.error("Error parsing Application Key for SkyDRM. Check that the application key is set as an Integer", e);
			return null;
		} catch (NxlException e) {
			logger.error("Error setting up SkyDRMSDK. SkyDRMSDK Exception is: " + e.getMessage(), e);
			return null;
		}
	}

	@Override
	public DocumentContent extract(boolean bExtractBody) throws Exception {
		return extract();
	}
}
