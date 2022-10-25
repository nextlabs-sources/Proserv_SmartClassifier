package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ReportFilter;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ReportFilterDTO 
		extends BaseDTO {
	
	@Expose
	private Integer displayOrder;
	@Expose
	private String fieldName;
	@Expose
	private String operator;
	@Expose
	private String value;
	
	public ReportFilterDTO() {
		super();
	}
	
	public ReportFilterDTO(ReportFilter reportFilter) {
		super();
		copy(reportFilter);
	}
	
	public void copy(ReportFilter reportFilter) {
		if(reportFilter != null) {
			this.id = reportFilter.getId();
			this.displayOrder = reportFilter.getDisplayOrder();
			this.fieldName = reportFilter.getFieldName();
			this.operator = reportFilter.getOperator();
			this.value = reportFilter.getValue();
		}
	}
	
	public ReportFilter getEntity() {
		ReportFilter reportFilter = new ReportFilter();
		
		reportFilter.setId(this.id);
		reportFilter.setDisplayOrder(this.displayOrder);
		reportFilter.setFieldName(this.fieldName);
		reportFilter.setOperator(this.operator);
		reportFilter.setValue(this.value);
		
		return reportFilter;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}
