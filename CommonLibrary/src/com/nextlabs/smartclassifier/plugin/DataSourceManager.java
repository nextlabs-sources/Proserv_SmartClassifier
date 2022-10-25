package com.nextlabs.smartclassifier.plugin;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.nextlabs.smartclassifier.constant.NextLabsConstant;
import com.nextlabs.smartclassifier.plugin.datasource.CSVConfig;
import com.nextlabs.smartclassifier.plugin.datasource.DatabaseConfig;
import com.nextlabs.smartclassifier.util.FileUtil;
import com.nextlabs.smartclassifier.util.NxlCryptoUtil;

public class DataSourceManager {
	
	private static final Logger logger = LogManager.getLogger(DataSourceManager.class);
	
	// Hold all defined database configurations
	private static Map<String, DatabaseConfig> dbdsConfigs;
	// Hold all created database connection pools
	private static Map<String, ComboPooledDataSource> dbdsPools;
	// Hold all defined CSV configurations
	private static Map<String, CSVConfig> csvdsConfigs;
	
	private volatile static DataSourceManager instance;
	
	private DataSourceManager() {
		dbdsConfigs = new HashMap<String, DatabaseConfig>();
		dbdsPools = new ConcurrentHashMap<String, ComboPooledDataSource>();
		csvdsConfigs = new HashMap<String, CSVConfig>();
		
		// Load configuration from datasource.xml file
		try {
			XMLConfiguration xmlConfiguration = new XMLConfiguration(FileUtil.getPluginFolder() + NextLabsConstant.DATA_SOURCE_CONFIG_FILE);
			
			List<HierarchicalConfiguration> dbDataSources = xmlConfiguration.configurationsAt("database.data-sources.data-source");
			for(HierarchicalConfiguration dbDataSource : dbDataSources) {
				DatabaseConfig databaseConfig = new DatabaseConfig();
				databaseConfig.setDriverClass(dbDataSource.getString("driver-class"));
				databaseConfig.setJdbcUrl(dbDataSource.getString("jdbc-url"));
				databaseConfig.setUser(dbDataSource.getString("username"));
				databaseConfig.setPassword(dbDataSource.getString("password"));
				databaseConfig.setMinPoolSize(dbDataSource.getInt("min-pool-size", 1));
				databaseConfig.setAcquireIncrement(dbDataSource.getInt("acquire-increment", 1));
				databaseConfig.setMaxPoolSize(dbDataSource.getInt("max-pool-size", 5));
				databaseConfig.setMaxStatements(dbDataSource.getInt("max-statements", 180));
				databaseConfig.setIdleConnectionTestPeriod(dbDataSource.getInt("idle-connection-test-period", 60000));
				
				dbdsConfigs.put(dbDataSource.getString("name"), databaseConfig);
				logger.info("Loaded database data source " + dbDataSource.getString("name"));
			}
			
			List<HierarchicalConfiguration> csvDataSources = xmlConfiguration.configurationsAt("csv.data-sources.data-source");
			for(HierarchicalConfiguration csvDataSource : csvDataSources) {
				CSVConfig csvConfig = new CSVConfig();
				csvConfig.setFilePath(csvDataSource.getString("file-path"));
				
				List<HierarchicalConfiguration> properties = csvDataSource.configurationsAt("properties");
				for(HierarchicalConfiguration property : properties) {
					csvConfig.getProperties().put(property.getString("name"), property.getString("value"));
				}
				
				csvdsConfigs.put(csvDataSource.getString("name"), csvConfig);
				logger.info("Loaded CSV data source " + csvDataSource.getString("name"));
			}
		} catch(Exception err) {
			logger.error(err.getMessage(), err);
		}
	}
	
	/**
	 * Double locking singleton implementation.
	 * 
	 * @return Singleton instance of database data source object.
	 */
	public static DataSourceManager getInstance() {
		if(instance == null) {
			synchronized(DataSourceManager.class) {
				if(instance == null) {
					instance = new DataSourceManager();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Only create database data source when it is required.
	 * 
	 * @param dataSourceName Data source unique identifier.
	 * @return Database connection pool which matched the unique identifier.
	 * @throws IllegalArgumentException if given data source name is not exist in conf/datasource.xml
	 */
	public ComboPooledDataSource getDBDS(String dataSourceName) 
			throws IllegalArgumentException, PropertyVetoException {
		if(dbdsPools.containsKey(dataSourceName)) {
			return dbdsPools.get(dataSourceName);
		}
		
		if(dbdsConfigs.containsKey(dataSourceName)) {
			// Make sure only one thread can enter this code block for connection pool creation
			synchronized(dbdsPools) {
				// Double check if pool has been created
				if(!dbdsPools.containsKey(dataSourceName)) {
					DatabaseConfig databaseConfig = dbdsConfigs.get(dataSourceName);
					// Create data source and put into data source pool
					ComboPooledDataSource dataSource = new ComboPooledDataSource();
					dataSource.setDriverClass(databaseConfig.getDriverClass());
					dataSource.setJdbcUrl(databaseConfig.getJdbcUrl());
					dataSource.setUser(databaseConfig.getUser());
					dataSource.setPassword(NxlCryptoUtil.decrypt(databaseConfig.getPassword()));
					
					dataSource.setMinPoolSize(databaseConfig.getMinPoolSize());
					dataSource.setAcquireIncrement(databaseConfig.getAcquireIncrement());
					dataSource.setMaxPoolSize(databaseConfig.getMaxPoolSize());
					dataSource.setMaxStatements(databaseConfig.getMaxStatements());
					dataSource.setIdleConnectionTestPeriod(databaseConfig.getIdleConnectionTestPeriod());
					
					// Add to data source pool for future use
					dbdsPools.put(dataSourceName, dataSource);
					
					return dataSource;
				} else {
					return dbdsPools.get(dataSourceName);
				}
			}
		}
		
		throw new IllegalArgumentException("Invalid data source name [" + dataSourceName + "]. This data source is not configure in configuration file.");
	}
	
	/**
	 * Get CSV data source configuration information.
	 * 
	 * @param dataSourceName Name of CSV data source to retrieve.
	 * @return CSVConfig object of given data source name. Null if not found.
	 */
	public CSVConfig getCSVConfig(String dataSourceName) {
		return csvdsConfigs.get(dataSourceName);
	}
}