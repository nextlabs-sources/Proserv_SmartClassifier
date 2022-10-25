package com.nextlabs.smartclassifier.database.entity;

import java.text.ParseException;
import java.util.Date;
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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;
import org.quartz.CronExpression;

import com.nextlabs.smartclassifier.constant.RuleExecutionOutcome;

@Entity
@Table(name = "RULES")
public class Rule {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String RULE_ENGINE = "ruleEngine";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	public static final String ENABLED = "enabled";
	public static final String REPOSITORY_TYPE = "repositoryType";
	public static final String SCHEDULE_TYPE = "scheduleType";
	public static final String EXECUTION_FREQUENCY = "executionFrequency";
	public static final String EFFECTIVE_FROM = "effectiveFrom";
	public static final String EFFECTIVE_UNTIL = "effectiveUntil";
	public static final String DELETED = "deleted";
	public static final String VERSION = "version";
	public static final String CREATED_ON = "createdOn";
	public static final String MODIFIED_ON = "modifiedOn";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "RULES_SEQ")})
	private Long id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RULE_ENGINE_ID")
	@NotFound(action = NotFoundAction.IGNORE)
	private RuleEngine ruleEngine;
	
	@Column(name = "NAME", nullable = false, length = 320)
	private String name;
	
	@Column(name = "DESCRIPTION", nullable = true, length = 1024)
	private String description;

    @Column(name = "REPOSITORY_TYPE", nullable = false, length = 30)
    private String repositoryType;

	@Column(name = "ENABLED", nullable = false)
	private Boolean enabled;
	
	@Column(name = "SCHEDULE_TYPE", nullable = false, length = 1)
	private String scheduleType;
	
	@Column(name = "EXECUTION_FREQUENCY", nullable = false)
	private String executionFrequency;
	
	@Column(name = "EFFECTIVE_FROM", nullable = true)
	private Date effectiveFrom;
	
	@Column(name = "EFFECTIVE_UNTIL", nullable = true)
	private Date effectiveUntil;
	
	@Column(name = "DELETED", nullable = false)
	private Boolean deleted;
	
	@Column(name = "VERSION", nullable = false)
	private Integer version;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rule", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<CriteriaGroup> criteriaGroups = new LinkedHashSet<CriteriaGroup>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rule", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<Action> actions = new LinkedHashSet<Action>();
	
	@Transient
	private Date lastExecutionDate;
	
	@Transient
	private RuleExecutionOutcome lastExecutionOutcome;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}
	
	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Boolean isEnabled() {
		return enabled;
	}
	
	public void isEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public String getScheduleType() {
		return scheduleType;
	}
	
	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	public String getExecutionFrequency() {
		return executionFrequency;
	}
	
	public void setExecutionFrequency(String executionFrequency) {
		this.executionFrequency = executionFrequency;
	}
	
	public Date getEffectiveFrom() {
		return effectiveFrom;
	}
	
	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}
	
	public Date getEffectiveUntil() {
		return effectiveUntil;
	}
	
	public void setEffectiveUntil(Date effectiveUntil) {
		this.effectiveUntil = effectiveUntil;
	}
	
	public Boolean isDeleted() {
		return deleted;
	}
	
	public void isDeleted(Boolean deleted) {
		this.deleted = deleted;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	public Date getModifiedOn() {
		return modifiedOn;
	}
	
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}
	
	public Set<CriteriaGroup> getCriteriaGroups() {
		return criteriaGroups;
	}
	
	public void setCriteriaGroups(Set<CriteriaGroup> criteriaGroups) {
		this.criteriaGroups = criteriaGroups;
	}
	
	public Set<Action> getActions() {
		return actions;
	}
	
	public void setActions(Set<Action> actions) {
		this.actions = actions;
	}
	
	public Date getLastExecutionDate() {
		return lastExecutionDate;
	}
	
	public void setLastExecutionDate(Date lastExecutionDate) {
		this.lastExecutionDate = lastExecutionDate;
	}
	
	public RuleExecutionOutcome getLastExecutionOutcome() {
		return lastExecutionOutcome;
	}
	
	public void setLastExecutionOutcome(RuleExecutionOutcome outcome) {
		this.lastExecutionOutcome = outcome;
	}

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }

	public Date getNextScheduleTime()
			throws ParseException {
		if(enabled && !deleted) {
			CronExpression cronExpression = new CronExpression(executionFrequency);
			System.out.println("Summary: " + cronExpression.getExpressionSummary());
			Date now = new Date();
			Date nextSchedule;
			
			// Get first date after effective from date, if effective from date is set and its future date
			if(effectiveFrom != null && effectiveFrom.after(now)) {
				nextSchedule = cronExpression.getNextValidTimeAfter(effectiveFrom);
			} else {
				nextSchedule = cronExpression.getNextValidTimeAfter(now);
			}
			
			// Check if nextSchedule has passed effective until date, if effective until date is set
			if(effectiveUntil != null && effectiveUntil.before(nextSchedule)) {
				return null;
			}
			
			return nextSchedule;
		}
		
		// Rule disabled, no next schedule date
		return null;
	}
	
	public boolean isActive() {
		if(!this.enabled) {
			return false;
		}
		
		Date now = new Date();
		
		if(effectiveFrom != null && effectiveFrom.after(now)) {
			return false;
		}
		
		if(effectiveUntil != null && effectiveUntil.before(now)) {
			return false;
		}
		
		return true;
	}
}
