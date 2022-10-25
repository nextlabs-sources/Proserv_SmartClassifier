package com.nextlabs.smartclassifier.dto.v1;

import com.google.gson.annotations.Expose;
import com.nextlabs.smartclassifier.constant.DataSection;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.database.entity.Action;
import com.nextlabs.smartclassifier.database.entity.CriteriaGroup;
import com.nextlabs.smartclassifier.database.entity.Rule;
import com.nextlabs.smartclassifier.dto.BaseDTO;
import com.nextlabs.smartclassifier.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RuleDTO
        extends BaseDTO {

    @Expose
    private RuleEngineDTO ruleEngine;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private Boolean enabled;
    @Expose
    private String scheduleType;
    @Expose
    private ExecutionFrequencyDTO executionFrequency;
    @Expose
    private Long effectiveFrom;
    @Expose
    private Long effectiveUntil;
    @Expose
    private List<CriteriaGroupDTO> criteriaGroups;
    @Expose
    private List<CriteriaGroupDTO> directories;
    @Expose
    private List<ActionDTO> actions;
    @Expose
    private Long lastExecutionDate;
    @Expose
    private String lastExecutionOutcome;
    @Expose
    private String repositoryType;
    @Expose
    private Integer version;
    
    public RuleDTO() {
        super();
    }

    public RuleDTO(Rule rule) {
        super();
        copy(rule);
    }

    public void copy(Rule rule) {
        if (rule != null) {
            this.id = rule.getId();

            if (rule.getRuleEngine() != null) {
                this.ruleEngine = new RuleEngineDTO(rule.getRuleEngine());
            }
            this.name = rule.getName();
            this.description = rule.getDescription();
            this.enabled = rule.isEnabled();
            this.scheduleType = rule.getScheduleType();
            this.executionFrequency = new ExecutionFrequencyDTO(rule.getScheduleType(), rule.getExecutionFrequency());
            if (rule.getEffectiveFrom() != null) {
                this.effectiveFrom = rule.getEffectiveFrom().getTime();
            }
            if (rule.getEffectiveUntil() != null) {
                this.effectiveUntil = rule.getEffectiveUntil().getTime();
            }
            if (rule.getCriteriaGroups() != null && rule.getCriteriaGroups().size() > 0) {
                this.criteriaGroups = new ArrayList<>();
                this.directories = new ArrayList<>();

                for (CriteriaGroup criteriaGroup : rule.getCriteriaGroups()) {
                    CriteriaGroupDTO group = new CriteriaGroupDTO(criteriaGroup);

                    if (group.getCriterias() != null
                            && group.getCriterias().size() > 0
                            && DataSection.DIRECTORY.getCode().equals(group.getCriterias().get(0).getDataSection())) {
                        this.directories.add(group);
                    } else {
                        this.criteriaGroups.add(group);
                    }
                }
            }
            if (rule.getActions() != null && rule.getActions().size() > 0) {
                this.actions = new ArrayList<>();

                for (Action action : rule.getActions()) {
                    this.actions.add(new ActionDTO(action));
                }
            }
            this.createdTimestamp = rule.getCreatedOn();
            this.createdOn = rule.getCreatedOn().getTime();
            this.modifiedTimestamp = rule.getModifiedOn();
            this.modifiedOn = rule.getModifiedOn().getTime();
            this.repositoryType = rule.getRepositoryType();
            this.version = rule.getVersion();
            if (rule.getLastExecutionDate() != null) {
                this.lastExecutionDate = rule.getLastExecutionDate().getTime();
            }
            if (rule.getLastExecutionOutcome() != null) {
                this.lastExecutionOutcome = rule.getLastExecutionOutcome().getName();
            }
        }
    }

    public Rule getEntity() {
        Rule rule = new Rule();

        if (this.ruleEngine != null) {
            rule.setRuleEngine(this.ruleEngine.getEntity());
        }

        rule.setId(this.id);
        rule.setName(this.name);
        rule.setDescription(this.description);
        rule.isEnabled(this.enabled == null ? false : this.enabled);
        rule.setScheduleType(this.scheduleType);
        rule.setRepositoryType(this.repositoryType);
        rule.setVersion(this.version);

        RepositoryType repositoryType = RepositoryType.getRepositoryType(this.repositoryType);

        if (this.executionFrequency != null) {
            rule.setExecutionFrequency(this.executionFrequency.getCronExpression(this.scheduleType));
        }

        if (this.effectiveFrom != null && this.effectiveFrom > 0) {
            rule.setEffectiveFrom(DateUtil.toStartOfTheDay(this.effectiveFrom));
        }

        if (this.effectiveUntil != null && this.effectiveUntil > 0) {
            rule.setEffectiveUntil(DateUtil.toEndOfTheDay(this.effectiveUntil));
        }

        if (this.criteriaGroups != null && this.criteriaGroups.size() > 0) {
            for (CriteriaGroupDTO criteriaGroup : this.criteriaGroups) {
                rule.getCriteriaGroups().add(criteriaGroup.getEntity(repositoryType));
            }
        }

        if (this.directories != null && this.directories.size() > 0
                && this.directories.get(0).getCriterias() != null && this.directories.get(0).getCriterias().size() > 0) {

            for (CriteriaGroupDTO criteriaGroup : this.directories) {
                rule.getCriteriaGroups().add(criteriaGroup.getEntity(repositoryType));
            }
        }

        if (this.actions != null && this.actions.size() > 0) {
            for (ActionDTO action : this.actions) {
                rule.getActions().add(action.getEntity());
            }
        }

        if (this.createdOn != null
                && this.createdOn > 0) {
            rule.setCreatedOn(new Date(this.createdOn));
        }

        if (this.modifiedOn != null
                && this.modifiedOn > 0) {
            rule.setModifiedOn(new Date(this.modifiedOn));
        }

        return rule;
    }

    public RuleEngineDTO getRuleEngine() {
        return ruleEngine;
    }

    public void setRuleEngine(RuleEngineDTO ruleEngine) {
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public ExecutionFrequencyDTO getExecutionFrequency() {
        return executionFrequency;
    }

    public void setExecutionFrequency(ExecutionFrequencyDTO executionFrequency) {
        this.executionFrequency = executionFrequency;
    }

    public Long getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Long effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public Long getEffectiveUntil() {
        return effectiveUntil;
    }

    public void setEffectiveUntil(Long effectiveUntil) {
        this.effectiveUntil = effectiveUntil;
    }

    public List<CriteriaGroupDTO> getCriteriaGroups() {
        return criteriaGroups;
    }

    public void setCriteriaGroups(List<CriteriaGroupDTO> criteriaGroups) {
        this.criteriaGroups = criteriaGroups;
    }

    public List<CriteriaGroupDTO> getDirectories() {
        return directories;
    }

    public void setDirectories(List<CriteriaGroupDTO> directories) {
        this.directories = directories;
    }

    public List<ActionDTO> getActions() {
        return actions;
    }

    public void setActions(List<ActionDTO> actions) {
        this.actions = actions;
    }

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }
    
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
}