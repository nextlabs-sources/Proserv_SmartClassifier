package com.nextlabs.smartclassifier.database.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "RULE_HISTORIES", uniqueConstraints = {@UniqueConstraint(columnNames = {"RULE_ID", "RULE_VERSION"})})
public class RuleHistory {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String RULE_ID = "ruleId";
	public static final String RULE_VERSION = "ruleVersion";
	public static final String SUMMARY = "summary";
	public static final String CREATED_BY = "createdBy";
	public static final String CREATED_ON = "createdOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "RULE_HISTORIES_SEQ")})
	private Long id;
	
	@Column(name = "RULE_ID", nullable = false)
	private Long ruleId;
	
	@Column(name = "RULE_VERSION", nullable = false)
	private Integer ruleVersion;
	
	@Column(name = "SUMMARY", nullable = false)
	private String summary;
	
	@Column(name = "CREATED_BY", nullable = true, length = 100)
	private String createdBy;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}
	
	public Integer getRuleVersion() {
		return ruleVersion;
	}
	
	public void setRuleVersion(Integer ruleVersion) {
		this.ruleVersion = ruleVersion;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
