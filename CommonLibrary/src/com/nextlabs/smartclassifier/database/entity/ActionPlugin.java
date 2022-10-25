package com.nextlabs.smartclassifier.database.entity;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "ACTION_PLUGINS", uniqueConstraints = {@UniqueConstraint(columnNames = {"CLASS_NAME"}),
													 @UniqueConstraint(columnNames = {"NAME"})})
public class ActionPlugin {
	
	// Field name for search criteria construction
	public static final String ID = "id";
	public static final String DISPLAY_ORDER = "displayOrder";
	public static final String CLASS_NAME = "className";
	public static final String NAME = "name";
	public static final String DISPLAY_NAME = "displayName";
	public static final String DESCRIPTION = "description";
	public static final String FIRE_ONCE_PER_RULE = "fireOncePerRule";
	public static final String REPOSITORY_TYPE = "repositoryType";
	
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
	@GenericGenerator(name = "SequenceGeneratorYYMMDDHH",
					  strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
					  parameters = {@Parameter(name = "sequence", value = "ACTION_PLUGINS_SEQ")})
	private Long id;

    @Column(name = "DISPLAY_ORDER", nullable = false)
	private Integer displayOrder;
	
	@Column(name = "CLASS_NAME", nullable = false, length = 320)
	private String className;
	
	@Column(name = "NAME", nullable = false, length = 50)
	private String name;
	
	@Column(name = "DISPLAY_NAME", nullable = false, length = 100)
	private String displayName;

	@Column(name = "REPOSITORY_TYPE", nullable = false, length = 30)
	private String repositoryType;

	@Column(name = "DESCRIPTION", nullable = true, length = 1024)
	private String description;
	
	@Column(name = "FIRE_ONCE_PER_RULE", nullable = false)
	private Boolean fireOncePerRule;
	
	@Column(name = "CREATED_ON", nullable = false)
	private Date createdOn;
	
	@Version
	@Column(name = "MODIFIED_ON", nullable = true)
	private Date modifiedOn;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "actionPlugin", cascade = CascadeType.ALL)
	@OrderBy ("displayOrder ASC")
	@NotFound(action = NotFoundAction.IGNORE)
	private Set<ActionPluginParam> pluginParams = new LinkedHashSet<ActionPluginParam>();
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Boolean isFireOncePerRule() {
		return fireOncePerRule;
	}

	public void isFireOncePerRule(Boolean fireOncePerRule) {
		this.fireOncePerRule = fireOncePerRule;
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
	
	public Set<ActionPluginParam> getPluginParams() {
		return pluginParams;
	}
	
	public void setPluginParams(Set<ActionPluginParam> pluginParams) {
		this.pluginParams = pluginParams;
	}
	
	public boolean hasFixedParameter() {
		if(pluginParams != null) {
			for(ActionPluginParam actionPluginParam : this.pluginParams) {
				if(actionPluginParam.isFixedParameter()) {
					return true;
				}
			}
		}
		
		return false;
	}

    public String getRepositoryType() {
        return repositoryType;
    }

    public void setRepositoryType(String repositoryType) {
        this.repositoryType = repositoryType;
    }
}
