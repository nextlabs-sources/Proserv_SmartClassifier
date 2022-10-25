package com.nextlabs.smartclassifier.database.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "REPORT_FILTER_GROUPS")
public class ReportFilterGroup {
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "REPORT_FILTER_GROUPS_SEQ")})
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REPORT_ID")
	private Report report;
	
	@Column(name = "DISPLAY_ORDER", nullable = false)
	private Integer displayOrder;
	
	@Column(name = "OPERATOR", nullable = false, length = 3)
	private String operator;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "reportFilterGroup", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<ReportFilter> reportFilters = new LinkedHashSet<ReportFilter>();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Report getReport() {
		return report;
	}
	
	public void setReport(Report report) {
		this.report = report;
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
	
	public Set<ReportFilter> getReportFilters() {
		return reportFilters;
	}
	
	public void setReportFilters(Set<ReportFilter> reportFilters) {
		this.reportFilters = reportFilters;
	}
}
