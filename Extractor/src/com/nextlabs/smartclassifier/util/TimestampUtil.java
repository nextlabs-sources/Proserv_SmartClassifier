package com.nextlabs.smartclassifier.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static com.nextlabs.smartclassifier.constant.Punctuation.GENERAL_DELIMITER;

public class TimestampUtil {
	
	private static final Logger logger = LogManager.getLogger(TimestampUtil.class);
	
	private static Set<String> timeFormats;
	
	public static void setTimeFormats(String formats) {
		logger.debug(formats);
		if(formats != null) {
			String[] arrays = formats.split(Pattern.quote(GENERAL_DELIMITER));
			
			logger.debug("Number of time format: " + arrays.length);
			for(String value : arrays) {
				logger.debug("Time format:" + value);
			}
			
			timeFormats = new HashSet<String>(Arrays.asList(arrays));
		}
	}
	
	public static void setTimeFormats(HashSet<String> formats) {
		timeFormats = formats;
	}
	
	public static Set<String> getTimeFormats() {
		return timeFormats;
	}
}
