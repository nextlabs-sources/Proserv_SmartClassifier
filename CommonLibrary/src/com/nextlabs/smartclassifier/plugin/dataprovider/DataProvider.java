package com.nextlabs.smartclassifier.plugin.dataprovider;

public interface DataProvider {
	
	public String getName();
	
	public String getDataSourceName();
	
	public void setArguments(String... args);
	
	public String[] getArguments();
	
	public String evaluate() throws Exception;
	
}
