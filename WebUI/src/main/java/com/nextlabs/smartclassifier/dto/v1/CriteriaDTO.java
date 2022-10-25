package com.nextlabs.smartclassifier.dto.v1;

import com.nextlabs.smartclassifier.constant.DataSection;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.util.StringFunctions;
import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.Operator;
import com.nextlabs.smartclassifier.database.entity.Criteria;
import com.nextlabs.smartclassifier.dto.BaseDTO;

public class CriteriaDTO 
		extends BaseDTO {
	
	@Expose
	private Integer displayOrder;
	@Expose
	private String dataSection;
	@Expose
	private String operator;
	@Expose
	private String openBracket;
	@Expose
	private String fieldDisplayName;
	@Expose
	private String fieldName;
	@Expose
	private String field;
	@Expose
	private String matchingCondition;
	@Expose
	private String value;
	@Expose
	private String closeBracket;
	
	public CriteriaDTO() {
		super();
	}
	
	public CriteriaDTO(Criteria criteria) {
		super();
		copy(criteria);
	}
	
	public void copy(Criteria criteria) {
		if(criteria != null) {
			this.id = criteria.getId();
			this.displayOrder = criteria.getDisplayOrder();
			this.dataSection = criteria.getDataSection();
			this.operator = criteria.getOperator();
			this.openBracket = criteria.getOpenBracket();
			this.fieldName = criteria.getFieldName();
			this.fieldDisplayName = criteria.getFieldName();
			this.matchingCondition = criteria.getMatchingCondition();
			this.value = criteria.getValue();
			this.closeBracket = criteria.getCloseBracket();
		}
	}
	
	public Criteria getEntity(RepositoryType repositoryType) {
		Criteria criteria = new Criteria();
		
		criteria.setId(this.id);
		criteria.setDisplayOrder(this.displayOrder);
		criteria.setDataSection(this.dataSection);
		criteria.setOperator(StringUtils.isBlank(this.operator) ? Operator.OR.toString() : this.operator);
		criteria.setOpenBracket(this.openBracket);
		criteria.setFieldName(this.fieldName);
		criteria.setMatchingCondition(this.matchingCondition);
		criteria.setValue(this.value);
		criteria.setCloseBracket(this.closeBracket);

		DataSection section = DataSection.getSection(this.dataSection);
		if(repositoryType == RepositoryType.SHAREPOINT && section == DataSection.DIRECTORY) {
			String url = StringFunctions.removeWhiteSpacesInURL(this.value);
		    criteria.setValue(url);
		}

		return criteria;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public String getDataSection() {
		return dataSection;
	}
	
	public void setDataSection(String dataSection) {
		this.dataSection = dataSection;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public String getOpenBracket() {
		return openBracket;
	}
	
	public void setOpenBracket(String openBracket) {
		this.openBracket = openBracket;
	}
	
	public String getFieldDisplayName() {
		return fieldDisplayName;
	}
	
	public void setFieldDisplayName(String fieldDisplayName) {
		this.fieldDisplayName = fieldDisplayName;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public String getMatchingCondition() {
		return matchingCondition;
	}
	
	public void setMatchingCondition(String matchingCondition) {
		this.matchingCondition = matchingCondition;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getCloseBracket() {
		return closeBracket;
	}
	
	public void setCloseBracket(String closeBracket) {
		this.closeBracket = closeBracket;
	}
}
