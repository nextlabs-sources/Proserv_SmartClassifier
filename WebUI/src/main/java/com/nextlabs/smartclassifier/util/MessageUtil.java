package com.nextlabs.smartclassifier.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtil {
	
	private static ResourceBundle messageResourceBundle;
	
	static {
		messageResourceBundle = ResourceBundle.getBundle("RestfulService", Locale.getDefault());
	}
	
	public static String getMessage(String propertyName, Object... args) {
		return MessageFormat.format(messageResourceBundle.getString(propertyName), args);
	}
}
