package com.nextlabs.smartclassifier.plugin.datasource;

public class DatabaseConfig {
	
	private String driverClass;
	private String JdbcUrl;
	private String user;
	private String password;
	
	private int minPoolSize;
	private int acquireIncrement;
	private int maxPoolSize;
	private int maxStatements;
	private int idleConnectionTestPeriod;
	
	public String getDriverClass() {
		return driverClass;
	}
	
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	
	public String getJdbcUrl() {
		return JdbcUrl;
	}
	
	public void setJdbcUrl(String jdbcUrl) {
		JdbcUrl = jdbcUrl;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getMinPoolSize() {
		return minPoolSize;
	}
	
	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}
	
	public int getAcquireIncrement() {
		return acquireIncrement;
	}
	
	public void setAcquireIncrement(int acquireIncrement) {
		this.acquireIncrement = acquireIncrement;
	}
	
	public int getMaxPoolSize() {
		return maxPoolSize;
	}
	
	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
	
	public int getMaxStatements() {
		return maxStatements;
	}
	
	public void setMaxStatements(int maxStatements) {
		this.maxStatements = maxStatements;
	}
	
	public int getIdleConnectionTestPeriod() {
		return idleConnectionTestPeriod;
	}
	
	public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
		this.idleConnectionTestPeriod = idleConnectionTestPeriod;
	}
}
