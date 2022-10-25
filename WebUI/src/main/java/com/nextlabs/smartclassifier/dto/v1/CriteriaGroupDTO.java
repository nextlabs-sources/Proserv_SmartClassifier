package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.Operator;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.entity.Criteria;
import com.nextlabs.smartclassifier.database.entity.CriteriaGroup;
import com.nextlabs.smartclassifier.dto.BaseDTO;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CriteriaGroupDTO 
		extends BaseDTO {
	
	@Expose
	private Integer displayOrder;
	@Expose
	private String operator;
	@Expose
	private List<CriteriaDTO> criterias;
	
	public CriteriaGroupDTO() {
		super();
	}
	
	public CriteriaGroupDTO(CriteriaGroup criteriaGroup) {
		super();
		copy(criteriaGroup);
	}
	
	public void copy(CriteriaGroup criteriaGroup) {
		if(criteriaGroup != null) {
			this.id = criteriaGroup.getId();
			this.displayOrder = criteriaGroup.getDisplayOrder();
			this.operator = criteriaGroup.getOperator();
			
			if(criteriaGroup.getCriterias() != null && criteriaGroup.getCriterias().size() > 0) {
				this.criterias = new ArrayList<CriteriaDTO>();
				
				for(Criteria criteria : criteriaGroup.getCriterias()) {
					this.criterias.add(new CriteriaDTO(criteria));
				}
			}
		}
	}
	
	public CriteriaGroup getEntity(RepositoryType repositoryType) {
		CriteriaGroup criteriaGroup = new CriteriaGroup();
		
		criteriaGroup.setId(this.id);
		criteriaGroup.setDisplayOrder(this.displayOrder);
		criteriaGroup.setOperator(StringUtils.isBlank(this.operator) ? Operator.AND.toString() : this.operator);
		
		if(this.criterias != null && this.criterias.size() > 0) {
			for(CriteriaDTO criteriaDTO : this.criterias) {
				criteriaGroup.getCriterias().add(criteriaDTO.getEntity(repositoryType));
			}
		}
		
		return criteriaGroup;
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
	
	public List<CriteriaDTO> getCriterias() {
		return criterias;
	}
	
	public void setCriterias(List<CriteriaDTO> criterias) {
		this.criterias = criterias;
	}
}
