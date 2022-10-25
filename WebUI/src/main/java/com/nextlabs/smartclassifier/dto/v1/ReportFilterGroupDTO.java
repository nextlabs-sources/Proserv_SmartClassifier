package com.nextlabs.smartclassifier.dto.v1;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.Operator;
import com.nextlabs.smartclassifier.database.entity.ReportFilter;
import com.nextlabs.smartclassifier.database.entity.ReportFilterGroup;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ReportFilterGroupDTO 
		extends BaseDTO {
	
	@Expose
	private Integer displayOrder;
	@Expose
	private String operator;
	@Expose
	private List<ReportFilterDTO> filters;
	
	public ReportFilterGroupDTO() {
		super();
	}
	
	public ReportFilterGroupDTO(ReportFilterGroup filterGroup) {
		super();
		copy(filterGroup);
	}
	
	public void copy(ReportFilterGroup reportFilterGroup) {
		if(reportFilterGroup != null) {
			this.id = reportFilterGroup.getId();
			this.displayOrder = reportFilterGroup.getDisplayOrder();
			this.operator = reportFilterGroup.getOperator();

			if(reportFilterGroup.getReportFilters() != null && reportFilterGroup.getReportFilters().size() > 0) {
				this.filters = new ArrayList<ReportFilterDTO>();
				
				for(ReportFilter filter : reportFilterGroup.getReportFilters()) {
					this.filters.add(new ReportFilterDTO(filter));
				}
			}
		}
	}
	
	public ReportFilterGroup getEntity() {
		ReportFilterGroup reportFilterGroup = new ReportFilterGroup();
		
		reportFilterGroup.setId(this.id);
		reportFilterGroup.setDisplayOrder(this.displayOrder);
		reportFilterGroup.setOperator(StringUtils.isBlank(this.operator) ? Operator.AND.toString() : this.operator);
		
		if(this.filters != null && this.filters.size() > 0) {
			for(ReportFilterDTO reportFilterDTO : this.filters) {
				reportFilterGroup.getReportFilters().add(reportFilterDTO.getEntity());
			}
		}
		
		return reportFilterGroup;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public List<ReportFilterDTO> getFilters() {
		return filters;
	}
	
	public void setFilters(List<ReportFilterDTO> reportFilters) {
		this.filters = reportFilters;
	}
}
