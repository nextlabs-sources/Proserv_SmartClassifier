package com.nextlabs.smartclassifier.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "CRITERIAS")
public class Criteria {
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "CRITERIAS_SEQ")})
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRITERIA_GROUP_ID")
	private CriteriaGroup criteriaGroup;
	
	@Column(name = "DISPLAY_ORDER", nullable = false)
	private Integer displayOrder;
	
	@Column(name = "DATA_SECTION", nullable = false, length = 1)
	private String dataSection;
	
	@Column(name = "OPERATOR", nullable = true, length = 3)
	private String operator;
	
	@Column(name = "OPEN_BRACKET", nullable = true, length = 5)
	private String openBracket;
	
	@Column(name = "FIELD_NAME", nullable = true, length = 100)
	private String fieldName;
	
	@Column(name = "MATCHING_CONDITION", nullable = false, length = 25)
	private String matchingCondition;
	
	@Column(name = "VALUE", nullable = false, length = 1024)
	private String value;
	
	@Column(name = "CLOSE_BRACKET", nullable = true, length = 5)
	private String closeBracket;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public CriteriaGroup getCriteriaGroup() {
		return criteriaGroup;
	}
	
	public void setCriteriaGroup(CriteriaGroup criteriaGroup) {
		this.criteriaGroup = criteriaGroup;
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

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\nOrder: ").append(displayOrder).append("\n");
		builder.append("Data section: ").append(dataSection).append("\n");
		builder.append("Criteria:-\n");
		builder.append("Operator: ").append(operator).append("\n");
		builder.append("Open bracket: ").append(openBracket).append("\n");
		builder.append("Field name: ").append(fieldName).append("\n");
		builder.append("Matching condition: ").append(matchingCondition).append("\n");
		builder.append("Value: ").append(value).append("\n");
		builder.append("Close bracket: ").append(closeBracket).append("\n");
		
		return builder.toString();
	}
}
