package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ExecutionTimeSlot;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ExecutionTimeSlotDTO 
		extends BaseDTO {
	
	@Expose
	private Integer displayOrder;
	@Expose
	private String startTime;
	@Expose
	private String endTime;
	
	public ExecutionTimeSlotDTO() {
		super();
	}
	
	public ExecutionTimeSlotDTO(ExecutionTimeSlot executionTimeSlot) {
		super();
		copy(executionTimeSlot);
	}
	
	public void copy(ExecutionTimeSlot executionTimeSlot) {
		if(executionTimeSlot != null) {
			this.id = executionTimeSlot.getId();
			this.displayOrder = executionTimeSlot.getDisplayOrder();
			this.startTime = executionTimeSlot.getStartTime();
			this.endTime = executionTimeSlot.getEndTime();
		}
	}
	
	public ExecutionTimeSlot getEntity() {
		ExecutionTimeSlot executionTimeSlot = new ExecutionTimeSlot();
		
		executionTimeSlot.setId(this.id);
		executionTimeSlot.setDisplayOrder(this.displayOrder);
		executionTimeSlot.setStartTime(this.startTime);
		executionTimeSlot.setEndTime(this.endTime);
		
		return executionTimeSlot;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
