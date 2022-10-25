package com.nextlabs.smartclassifier.database.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "HEARTBEATS", uniqueConstraints = {@UniqueConstraint(columnNames = {"COMPONENT_ID", "COMPONENT_TYPE"})})
public class Heartbeat {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String COMPONENT_ID = "componentId";
	public static final String COMPONENT_TYPE = "componentType";
	public static final String LAST_HEARTBEAT = "lastHeartbeat";
	public static final String CREATED_ON = "createdOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "COMPONENT_ID", nullable = false)
	private Long componentId;
	
	@Column(name = "COMPONENT_TYPE", nullable = false, length = 1)
	private String componentType;
	
	@Column(name = "LAST_HEARTBEAT", nullable = false)
	private Long lastHeartbeat;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getComponentId() {
		return componentId;
	}
	
	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}
	
	public String getComponentType() {
		return componentType;
	}
	
	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}
	
	public Long getLastHeartbeat() {
		return lastHeartbeat;
	}
	
	public void setLastHeartbeat(Long lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
