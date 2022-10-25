package com.nextlabs.smartclassifier.plugin.dataprovider.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.nextlabs.smartclassifier.plugin.DataSourceManager;
import com.nextlabs.smartclassifier.plugin.dataprovider.AbstractDataProvider;

public class DBDataProvider 
		extends AbstractDataProvider {

	private static final Logger logger = LogManager.getLogger(DBDataProvider.class);
	private static final String DATA_PROVIDER_NAME = "DB_DATA_PROVIDER";
	private static final String DATA_SOURCE_NAME = "DB-CLASSIFIED_DOCUMENT";
	
	public DBDataProvider() {
		super(DATA_PROVIDER_NAME, DATA_SOURCE_NAME);
	}
	
	/*
	 * (non-Javadoc) 
	 * @see com.nextlabs.smartclassifier.ruleengine.dataprovider.DataProvider#evaluate()
	 * 
	 * Arguments:
	 * ----------
	 * 
	 * =DB_DATA_PROVIDER(CLASS%)
	 * 
	 * args0: name of document type
	 *  
	 * Returns null if there is no match.
	 * 
	 */
	@Override
	public String evaluate() 
		throws Exception {
		
		if(arguments.length < 1) {
			logger.error("Invalid number of arguments (" + arguments.length + ") passed to DB data provider.");
			return null;
		}
		
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		
		try {
			String queryString = "SELECT DOCUMENT_TYPE FROM DOCUMENT_TYPES WHERE NAME LIKE '%s'";
			queryString = String.format(queryString, arguments[0]);
			connection = DataSourceManager.getInstance().getDBDS(DATA_SOURCE_NAME).getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(queryString);
			
			if(resultSet.next()) {
				return resultSet.getString(1);
			}
		} catch(SQLException err) {
			logger.error(err.getMessage(), err);
		} finally {
			try {
				if (null != resultSet) {
					resultSet.close();
				}
				
				if (null != statement) {
					statement.close();
				}
				
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				// Ignore
			}
		}
		
		return null;
	}
}
