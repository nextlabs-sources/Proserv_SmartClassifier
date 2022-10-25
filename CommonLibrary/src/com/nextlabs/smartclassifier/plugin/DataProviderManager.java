package com.nextlabs.smartclassifier.plugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.nextlabs.smartclassifier.constant.SCConstant;
import com.nextlabs.smartclassifier.plugin.dataprovider.DataProvider;

public class DataProviderManager {
	
	private static final Logger logger = LogManager.getLogger(DataProviderManager.class);
	
	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";
	
	/**
	 * Retrieve data provider from loaded plug-ins for string evaluation. 
	 * 
	 * @param expression Complete data provider expression string which stored in database.
	 * @return Data provider object with trimmed parameter.
	 */
	public static DataProvider getDataProvider(String expression) {
		logger.debug("Received data provider expression: " + expression);
		
		if(expression != null && expression.startsWith(SCConstant.DATA_PROVIDER_EXPRESSION_PREFIX)) {
			String dataProviderName = expression.substring(SCConstant.DATA_PROVIDER_EXPRESSION_PREFIX.length(), expression.indexOf(OPEN_BRACKET));
			String argument = expression.substring(expression.indexOf(OPEN_BRACKET) + 1, expression.indexOf(CLOSE_BRACKET));
			
			logger.debug("Argument string before parse: " + argument);
			
			DataProvider dataProvider = PluginManager.getInstance().getDataProvider(dataProviderName);
			
			if(dataProvider == null) {
				throw new IllegalArgumentException("Data provider class is not provided for " + dataProviderName);
			}
			
			if(argument.length() > 0) {
				dataProvider.setArguments(argument.split(","));
			}
			
			return dataProvider;
		}
		
		logger.error("Data provider expression doesn't match expected format.");
		return null;
	}
}
