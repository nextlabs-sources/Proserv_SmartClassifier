package com.nextlabs.smartclassifier.plugin;

import com.nextlabs.smartclassifier.plugin.action.Action;
import com.nextlabs.smartclassifier.plugin.dataprovider.DataProvider;
import com.nextlabs.smartclassifier.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class PluginManager {
	
	private static final Logger logger = LogManager.getLogger(PluginManager.class);
	
	private static URLClassLoader urlClassLoader;
	private static Map<String, Action> actionNameToActionMap;
	private static Map<String, DataProvider> dataProviderNameToDataProviderMap;
	
	private volatile static PluginManager instance;
	
	private PluginManager() 
			throws Throwable {
		logger.info("Initializing plug-in.");
		
		try {
			File pluginFolder = new File(FileUtil.getPluginFolder());
			
			File[] plugins = pluginFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.getPath().toLowerCase().endsWith(".jar");
				}
			});
			
			if(plugins != null) {
				logger.debug("Number of plugin files found: " + plugins.length);
				
				URL[] urls = new URL[plugins.length];
				
				for (int i = 0; i < plugins.length; i++) {
					urls[i] = plugins[i].toURI().toURL();
				}
				
				urlClassLoader = new URLClassLoader(urls);
			}
			
			actionNameToActionMap = new HashMap<>();
			dataProviderNameToDataProviderMap = new HashMap<>();
			
			logger.info("Loading [ACTION] plugins.");
			for (Action action : loadActions()) {
				actionNameToActionMap.put(action.getName(), action);
				logger.info("Loaded " + action.getName());
			}
			logger.info(actionNameToActionMap.size() + " [ACTION] plugins loaded.");
			
			logger.info("Loading [DATA PROVIDER] plugins.");
			for (DataProvider provider : loadDataProviders()) {
				dataProviderNameToDataProviderMap.put(provider.getName(), provider);
				logger.info("Loaded " + provider.getName());
			}
			logger.info(dataProviderNameToDataProviderMap.size() + " [DATA PROVIDER] plugins loaded.");
			
			logger.info("Plug-in initialized.");
		} catch(Throwable throwable) {
			logger.error("Failed to initialize plugin.", throwable);
			throw throwable;
		}
	}
	
	public static PluginManager getInstance() {
		if(instance == null) {
			synchronized (PluginManager.class) {
				if(instance == null) {
					try {
						instance = new PluginManager();
					} catch(Throwable throwable) {
						logger.error(throwable.getMessage(), throwable);
						throw new ExceptionInInitializerError(throwable.getMessage());
					}
				}
			}
		}
		
		return instance;
	}
	
	public Map<String, Action> getActionNameToActionMap() {
		return Collections.unmodifiableMap(actionNameToActionMap);
	}
	
	public Action getAction(String actionName) {
		logger.debug("Retrieving action: " + actionName);
		
		if(actionNameToActionMap != null) {
			return actionNameToActionMap.get(actionName);
		}
		
		return null;
	}
	
	public Map<String, DataProvider> getDataProviders() {
		return Collections.unmodifiableMap(dataProviderNameToDataProviderMap);
	}
	
	public DataProvider getDataProvider(String dataProviderName) {
		logger.debug("Retrieving data provider: " + dataProviderName);
		
		if(dataProviderNameToDataProviderMap != null) {
			return dataProviderNameToDataProviderMap.get(dataProviderName);
		}
		
		return null;
	}

	private ServiceLoader<Action> loadActions() {
		return ServiceLoader.load(Action.class, urlClassLoader);
	}

	private ServiceLoader<DataProvider> loadDataProviders() {
		return ServiceLoader.load(DataProvider.class, urlClassLoader);
	}
}
