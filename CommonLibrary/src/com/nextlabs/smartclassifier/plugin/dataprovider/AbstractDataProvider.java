package com.nextlabs.smartclassifier.plugin.dataprovider;

public abstract class AbstractDataProvider 
		implements DataProvider {
	
	protected String name;
	protected String dataSourceName;
	protected String description;
	protected String[] arguments;
	
	protected AbstractDataProvider(String name, String dataSourceName) {
		super();
		this.name = name;
		this.dataSourceName = dataSourceName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDataSourceName() {
		return dataSourceName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setArguments(String... args) {
		if(args != null && args.length > 0) {
			String[] trimmedArgs = new String[args.length];
			
			for(int i = 0; i < args.length; i++) {
				trimmedArgs[i] = args[i].trim();
			}
			
			this.arguments = trimmedArgs;
		}
	}
	
	public String[] getArguments() {
		return this.arguments;
	}
}
