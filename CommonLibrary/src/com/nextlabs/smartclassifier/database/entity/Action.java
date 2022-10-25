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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;

@Entity
@Table(
  name = "ACTIONS",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"RULE_ID", "ACTION_PLUGIN_ID", "DISPLAY_ORDER"})
  }
)
public class Action {

  @Id
  @Column(name = "ID", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SequenceGeneratorYYMMDDHH")
  @GenericGenerator(
    name = "SequenceGeneratorYYMMDDHH",
    strategy = "com.nextlabs.smartclassifier.database.generator.SequenceGeneratorYYMMDDHH",
    parameters = {@Parameter(name = "sequence", value = "ACTIONS_SEQ")}
  )
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RULE_ID")
  private Rule rule;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ACTION_PLUGIN_ID")
  private ActionPlugin actionPlugin;

  @Column(name = "DISPLAY_ORDER", nullable = false)
  private Integer displayOrder;

  @Column(name = "TOLERANCE_LEVEL", nullable = false, length = 1)
  private String toleranceLevel;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "action", cascade = CascadeType.ALL)
  @OrderBy("id ASC")
  @NotFound(action = NotFoundAction.IGNORE)
  private Set<ActionParam> actionParams = new LinkedHashSet<ActionParam>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Rule getRule() {
    return rule;
  }

  public void setRule(Rule rule) {
    this.rule = rule;
  }

  public ActionPlugin getActionPlugin() {
    return actionPlugin;
  }

  public void setActionPlugin(ActionPlugin actionPlugin) {
    this.actionPlugin = actionPlugin;
  }

  public Integer getDisplayOrder() {
    return displayOrder;
  }

  public void setDisplayOrder(Integer displayOrder) {
    this.displayOrder = displayOrder;
  }

  public String getToleranceLevel() {
    return toleranceLevel;
  }

  public void setToleranceLevel(String toleranceLevel) {
    this.toleranceLevel = toleranceLevel;
  }

  public Set<ActionParam> getActionParams() {
    return actionParams;
  }

  public void setActionParams(Set<ActionParam> actionParams) {
    this.actionParams = actionParams;
  }
}
