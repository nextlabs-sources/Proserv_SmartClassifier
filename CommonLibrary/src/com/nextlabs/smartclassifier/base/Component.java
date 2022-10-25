package com.nextlabs.smartclassifier.base;

import java.util.Map;

import org.hibernate.SessionFactory;

import com.nextlabs.smartclassifier.constant.ComponentType;
import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.database.entity.RollbackError;
import com.nextlabs.smartclassifier.dto.Event;

public interface Component {
	
	public Long getId();
	public String getName();
	public ComponentType getType();
	public SessionFactory getSessionFactory();
	public String getHostname();
	public Map<String, String> getSystemConfigs();
	public String getSystemConfig(String key);
	public Map<String, MetadataField> getMetadataFieldByName();
	public boolean isShuttingDown();
	public void beat();
	public void log(Event event);
	public void log(RollbackError rollbackError);
	public boolean isWithinExecutionWindow();
	public void reQueue(Runnable task);
}
