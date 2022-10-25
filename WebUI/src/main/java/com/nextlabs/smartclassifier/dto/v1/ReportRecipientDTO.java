package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.database.entity.ReportRecipient;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class ReportRecipientDTO 
		extends BaseDTO {
	
	@Expose
	private Integer displayOrder;
	@Expose
	private String field;
	@Expose
	private String email;
	
	public ReportRecipientDTO() {
		super();
	}
	
	public ReportRecipientDTO(ReportRecipient reportRecipient) {
		super();
		copy(reportRecipient);
	}
	
	public void copy(ReportRecipient reportRecipient) {
		if(reportRecipient != null) {
			this.id = reportRecipient.getId();
			this.displayOrder = reportRecipient.getDisplayOrder();
			this.field = reportRecipient.getField();
			this.email = reportRecipient.getEmail();
		}
	}
	
	public ReportRecipient getEntity() {
		ReportRecipient reportRecipient = new ReportRecipient();
		
		reportRecipient.setId(this.id);
		reportRecipient.setDisplayOrder(this.displayOrder);
		reportRecipient.setField(this.field);
		reportRecipient.setEmail(this.email);
		
		return reportRecipient;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
