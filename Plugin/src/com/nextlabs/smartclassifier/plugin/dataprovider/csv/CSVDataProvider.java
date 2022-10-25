package com.nextlabs.smartclassifier.plugin.dataprovider.csv;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.nextlabs.smartclassifier.plugin.DataSourceManager;
import com.nextlabs.smartclassifier.plugin.dataprovider.AbstractDataProvider;

public class CSVDataProvider 
		extends AbstractDataProvider {

	private static final Logger logger = LogManager.getLogger(CSVDataProvider.class);
	private static final String DATA_PROVIDER_NAME = "CSV_DATA_PROVIDER";
	private static final String DATA_SOURCE_NAME = "CSV-USER_ACCOUNT";
	
	public CSVDataProvider() {
		super(DATA_PROVIDER_NAME, DATA_SOURCE_NAME);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.nextlabs.smartclassifier.ruleengine.dataprovider.DataProvider#evaluate()
	 * 
	 * Arguments:
	 * ----------
	 * 
	 * =CSV_DATA_PROVIDER(kent,0,2,false)
	 * 
	 * args0: Value to search (key).
	 * args1: Column for the key, starts from 0.
	 * args2: Column number for the value to be picked up from.
	 * args3: Case sensitiveness - "true" for case sensitive. Can be ignored for "false". 
	 *  
	 * Returns null if there is no match.
	 * 
	 */
	
	@Override
	public String evaluate() 
			throws Exception {
		if(arguments.length != 3 || arguments.length != 4) {
			logger.error("Invalid number of arguments (" + arguments.length + ") passed to CSV data provider.");
			return null;
		}
		
		String absoluteFilePath = DataSourceManager.getInstance().getCSVConfig(DATA_SOURCE_NAME).getFilePath();
		String key = arguments[0];
		int keyColumn = Integer.parseInt(arguments[1]);
		int valueColumn = Integer.parseInt(arguments[2]);
		Boolean caseSensitiveness = Boolean.parseBoolean("false");
		
		if(arguments.length==4) {
			caseSensitiveness = Boolean.parseBoolean(arguments[3]);
		}
		
		logger.info("Searching for " + key + " in column no. " + keyColumn + " of file " + absoluteFilePath + " with case sensitiveness set to " + caseSensitiveness + ".");
		
		String returnValue = null;
		String line = "";
		String separator = ",";
		BufferedReader bufferedReader = null;
		
		try {
			bufferedReader = new BufferedReader(new FileReader(absoluteFilePath));
			
			while((line = bufferedReader.readLine()) != null) {
				String[] entries = line.split(separator);
				if(caseSensitiveness) {
					if(entries[keyColumn].equals(key)) {
						returnValue = entries[valueColumn];
						break;
					}
				} else {
					if(entries[keyColumn].equalsIgnoreCase(key)) {
						returnValue = entries[valueColumn];
						break;
					}
				}
			}
		} catch(IOException err) {
			logger.error(err.getMessage(), err);
		} finally {
			bufferedReader.close();
		}
		
		logger.info("Returning: " + returnValue);
		return returnValue;
	}
}
