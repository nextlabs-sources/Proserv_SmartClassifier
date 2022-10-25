package com.nextlabs.smartclassifier.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nextlabs.smartclassifier.constant.SCConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Converter {

	private static final Logger logger = LogManager.getLogger(Converter.class);
	
	private static SimpleDateFormat solrDateFormat = new SimpleDateFormat(SCConstant.SOLR_DATETIME_FORMAT);
	
	public static Object convertToNative(String s) {
		Object value = s;
		// try casting into Integer or Date

		try {
    		value = Integer.parseInt(s);
    		logger.debug(s + " is an integer.");
    		return value;
    	} catch(NumberFormatException e) {
    		logger.debug(s + " is not an integer.");
    	}
		
		//Comment out by Kent, not supported by Rule Engine
//		try {
//    		value = Long.parseLong(s);
//    		logger.debug(s + " is a long.");
//    		return value;
//    	} catch(NumberFormatException e) {
//    		logger.debug(s + " is not a long.");
//    	}
//		
//		try {
//    		value = Float.parseFloat(s);
//    		logger.debug(s + " is a float.");
//    		return value;
//    	} catch(NumberFormatException e) {
//    		logger.debug(s + " is not a float.");
//    	} catch(NullPointerException e) {
//    		logger.debug(s + " is not a float.");
//    	}
//		
//		try {
//    		value = Double.parseDouble(s);
//    		logger.debug(s + " is a double.");
//    		return value;
//    	} catch(NumberFormatException e) {
//    		logger.debug(s + " is not a double.");
//    	} catch(NullPointerException e) {
//    		logger.debug(s + " is not a double.");
//    	}
		
		for(String dateFormat : TimestampUtil.getTimeFormats()) {
    		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    		simpleDateFormat.setLenient(false);
    		logger.debug("Trying for date format: " + dateFormat);
    		try {
    			value = simpleDateFormat.parse(s);
				logger.debug(s + " is a recognized date. with coverted value " + value.toString());
				value =  solrDateFormat.format(value);
    			return value;
			} catch (ParseException | NullPointerException e) {
				logger.debug(s + " is not a recognized date.");
			}
		}
		
		return value;
	}
	
}
