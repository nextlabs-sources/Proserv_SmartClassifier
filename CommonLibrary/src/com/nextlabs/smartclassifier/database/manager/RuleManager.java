package com.nextlabs.smartclassifier.database.manager;

import com.google.gson.Gson;
import com.nextlabs.smartclassifier.constant.ExecutionType;
import com.nextlabs.smartclassifier.constant.RuleExecutionStatus;
import com.nextlabs.smartclassifier.database.dao.ActionDAO;
import com.nextlabs.smartclassifier.database.dao.ActionPluginDAO;
import com.nextlabs.smartclassifier.database.dao.CriteriaGroupDAO;
import com.nextlabs.smartclassifier.database.dao.PageInfo;
import com.nextlabs.smartclassifier.database.dao.RuleDAO;
import com.nextlabs.smartclassifier.database.dao.RuleEngineDAO;
import com.nextlabs.smartclassifier.database.dao.RuleExecutionDAO;
import com.nextlabs.smartclassifier.database.dao.RuleHistoryDAO;
import com.nextlabs.smartclassifier.database.entity.Action;
import com.nextlabs.smartclassifier.database.entity.ActionParam;
import com.nextlabs.smartclassifier.database.entity.ActionPlugin;
import com.nextlabs.smartclassifier.database.entity.Criteria;
import com.nextlabs.smartclassifier.database.entity.CriteriaGroup;
import com.nextlabs.smartclassifier.database.entity.Rule;
import com.nextlabs.smartclassifier.database.entity.RuleEngine;
import com.nextlabs.smartclassifier.database.entity.RuleExecution;
import com.nextlabs.smartclassifier.database.entity.RuleHistory;
import com.nextlabs.smartclassifier.exception.ManagerException;
import com.nextlabs.smartclassifier.exception.RecordInUseException;
import com.nextlabs.smartclassifier.exception.RecordNotFoundException;
import com.nextlabs.smartclassifier.exception.RecordUnmatchedException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class RuleManager
        extends Manager {

    private RuleEngineDAO ruleEngineDAO;

    private RuleDAO ruleDAO;

    public RuleManager(SessionFactory sessionFactory, Session session) {
        super(sessionFactory, session);
        this.ruleDAO = new RuleDAO(sessionFactory, session);
        this.ruleEngineDAO = new RuleEngineDAO(sessionFactory, session);
    }

    public List<Rule> getRules()
            throws ManagerException {
        try {
            // Only return non-deleted rule
            List<Criterion> criteria = new ArrayList<>();
            criteria.add(Restrictions.eq(Rule.DELETED, false));

            List<Order> orders = new ArrayList<>();
            orders.add(Order.asc(Rule.NAME));

            return ruleDAO.findByCriteria(criteria, orders);
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public Long addRule(Rule rule)
            throws ManagerException, IllegalArgumentException {
        try {
            logger.debug("Create rule.");

            Date now = new Date();
            Rule entity = new Rule();

            RuleEngine ruleEngine = ruleEngineDAO.get(rule.getRuleEngine().getId());

            if (ruleEngine == null) {
                throw new IllegalArgumentException("Invalid RuleEngineId. Unable to retrieve rule engine record.");
            }

            entity.setRuleEngine(ruleEngine);
            entity.setName(rule.getName());
            entity.setDescription(rule.getDescription());
            entity.setRepositoryType(rule.getRepositoryType());
            entity.isEnabled(rule.isEnabled());
            entity.setScheduleType(rule.getScheduleType());
            entity.setExecutionFrequency(rule.getExecutionFrequency());
            entity.setEffectiveFrom(rule.getEffectiveFrom());
            entity.setEffectiveUntil(rule.getEffectiveUntil());
            entity.isDeleted(false);
            entity.setVersion(1);
            entity.setCreatedOn(now);
            entity.setModifiedOn(now);


            // Create criteria group and criteria
            if (rule.getCriteriaGroups() != null && rule.getCriteriaGroups().size() > 0) {
                int criteriaGroupOrder = 1;

                for (CriteriaGroup criteriaGroup : rule.getCriteriaGroups()) {
                    CriteriaGroup criteriaGroupEntity = new CriteriaGroup();

                    criteriaGroupEntity.setRule(entity);
                    criteriaGroupEntity.setDisplayOrder(criteriaGroupOrder);
                    criteriaGroupEntity.setOperator(criteriaGroup.getOperator());

                    if (criteriaGroup.getCriterias() != null && criteriaGroup.getCriterias().size() > 0) {
                        int criteriaOrder = 1;

                        for (Criteria criteria : criteriaGroup.getCriterias()) {
                            Criteria criteriaEntity = new Criteria();

                            criteriaEntity.setCriteriaGroup(criteriaGroupEntity);
                            criteriaEntity.setDisplayOrder(criteriaOrder);
                            /*
                If the repository type is sharepoint, then for D section, remove white spaces
             */
                            criteriaEntity.setDataSection(criteria.getDataSection());
                            criteriaEntity.setOperator(criteria.getOperator());
                            criteriaEntity.setOpenBracket(criteria.getOpenBracket());
                            criteriaEntity.setFieldName(criteria.getFieldName());
                            criteriaEntity.setMatchingCondition(criteria.getMatchingCondition());
                            criteriaEntity.setValue(criteria.getValue());
                            criteriaEntity.setCloseBracket(criteria.getCloseBracket());

                            criteriaGroupEntity.getCriterias().add(criteriaEntity);
                            criteriaOrder++;
                        }
                    }

                    entity.getCriteriaGroups().add(criteriaGroupEntity);
                    criteriaGroupOrder++;
                }
            }

            ActionPluginDAO actionPluginDAO = new ActionPluginDAO(sessionFactory, session);
            int actionOrder = 1;

            // Create action(s) entry for the rule
            for (Action action : rule.getActions()) {
                ActionPlugin actionPlugin = actionPluginDAO.get(action.getActionPlugin().getId());


                if (actionPlugin == null) {
                    throw new IllegalArgumentException("Invalid ActionPluginId. Unable to retrieve action plugin record.");
                }

                Action actionEntity = new Action();
                actionEntity.setRule(entity);
                actionEntity.setActionPlugin(actionPlugin);
                actionEntity.setDisplayOrder(actionOrder);
                actionEntity.setToleranceLevel(action.getToleranceLevel());

                // Dynamic parameter will be stored, fixed parameter will be fetch upon runtime
                if (action.getActionParams() != null && action.getActionParams().size() > 0) {
                    for (ActionParam actionParam : action.getActionParams()) {
                        ActionParam actionParamEntity = new ActionParam();
                        actionParamEntity.setAction(actionEntity);
                        actionParamEntity.setIdentifier(actionParam.getIdentifier());
                        actionParamEntity.setMetadataKey(actionParam.getMetadataKey());
                        actionParamEntity.setValue(actionParam.getValue());

                        actionEntity.getActionParams().add(actionParamEntity);
                    }
                }

                actionOrder++;
                entity.getActions().add(actionEntity);
            }

            ruleDAO.saveOrUpdate(entity);
            ruleDAO.flush();

            addRuleHistory(entity, rule);

            // Schedule for execution
            if (entity.isEnabled()) {
                RuleExecutionManager ruleExecutionManager = new RuleExecutionManager(sessionFactory, session);
                ruleExecutionManager.scheduleExecution(entity, ExecutionType.SCHEDULED.getCode());
            }

            return entity.getId();
        } catch (Exception err) {
            throw new ManagerException(err.getMessage(), err);
        }
    }

    public List<Rule> getRules(String hostname)
            throws ManagerException {
        if (StringUtils.isBlank(hostname)) {
            throw new ManagerException("Hostname cannot be null.");
        }

        List<Rule> rules = new ArrayList<Rule>();

        try {
            List<RuleEngine> ruleEngines = ruleEngineDAO.findByCriteria(Restrictions.eq(RuleEngine.HOSTNAME, hostname));

            if (ruleEngines != null && ruleEngines.size() > 0) {
                for (Rule rule : ruleEngines.get(0).getRules()) {
                    if (!rule.isDeleted()) {
                        rules.add(rule);
                    }
                }
            }
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        return rules;
    }

    public List<Rule> getRules(List<Criterion> criterion, List<Order> order, PageInfo pageInfo)
            throws ManagerException {
        try {
            // Only display non-deleted rule
            criterion.add(Restrictions.eq(Rule.DELETED, Boolean.FALSE));

            List<Rule> rules = ruleDAO.findByCriteria(criterion, order, pageInfo);

            if (rules != null) {
                for (Rule rule : rules) {
                    if (rule.getCriteriaGroups() != null) {
                        for (CriteriaGroup criteriaGroup : rule.getCriteriaGroups()) {
                            criteriaGroup.getCriterias();
                        }
                    }
                }
            }

            return rules;
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }
    }

    public Rule getRuleById(Long id)
            throws ManagerException {
        try {
            logger.debug("Get rule with id " + id);
            Rule rule = ruleDAO.get(id);

            if (rule != null) {
                logger.debug("Rule found with id " + id);

                if (rule.getCriteriaGroups() != null) {
                    for (CriteriaGroup criteriaGroup : rule.getCriteriaGroups()) {
                        criteriaGroup.getCriterias();
                    }
                }

                return rule;
            }
        } catch (HibernateException err) {
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            throw new ManagerException(err);
        }

        return null;
    }

    public void updateRule(Rule rule)
            throws ManagerException, RecordNotFoundException, IllegalArgumentException {
        try {
            logger.debug("Update rule for rule id " + rule.getId());

            Rule entity = ruleDAO.get(rule.getId());
            if (entity == null) {
                throw new RecordNotFoundException("Rule record not found for the given RuleID.");
            }

            if (entity.isDeleted()) {
                throw new RecordNotFoundException("Rule record not found for the given RuleID.");
            }

            RuleEngine ruleEngine = ruleEngineDAO.get(rule.getRuleEngine().getId());
            if (ruleEngine == null) {
                throw new IllegalArgumentException("Invalid RuleEngineId. Unable to retrieve rule engine record.");
            }

            entity.setRuleEngine(ruleEngine);
            entity.setName(rule.getName());
            entity.setDescription(rule.getDescription());
            entity.isEnabled(rule.isEnabled());
            entity.setScheduleType(rule.getScheduleType());
            entity.setExecutionFrequency(rule.getExecutionFrequency());
            entity.setEffectiveFrom(rule.getEffectiveFrom());
            entity.setEffectiveUntil(rule.getEffectiveUntil());
            entity.setVersion(entity.getVersion() + 1);
            entity.setModifiedOn(new Date());
            entity.setRepositoryType(rule.getRepositoryType());

            // Remove original criteria groups, criteria will be removed due to cascading type
            if (entity.getCriteriaGroups() != null && entity.getCriteriaGroups().size() > 0) {
                CriteriaGroupDAO criteriaGroupDAO = new CriteriaGroupDAO(sessionFactory, session);
                Iterator<CriteriaGroup> criteriaGroupIterator = entity.getCriteriaGroups().iterator();

                while (criteriaGroupIterator.hasNext()) {
                    CriteriaGroup criteriaGroup = criteriaGroupIterator.next();
                    criteriaGroupIterator.remove();
                    criteriaGroupDAO.delete(criteriaGroup);
                }

                criteriaGroupDAO.flush();
            }

            // Create criteria group and criteria
            if (rule.getCriteriaGroups() != null && rule.getCriteriaGroups().size() > 0) {
                int criteriaGroupOrder = 1;

                for (CriteriaGroup criteriaGroup : rule.getCriteriaGroups()) {
                    CriteriaGroup criteriaGroupEntity = new CriteriaGroup();

                    criteriaGroupEntity.setRule(entity);
                    criteriaGroupEntity.setDisplayOrder(criteriaGroupOrder);
                    criteriaGroupEntity.setOperator(criteriaGroup.getOperator());

                    if (criteriaGroup.getCriterias() != null && criteriaGroup.getCriterias().size() > 0) {
                        int criteriaOrder = 1;

                        for (Criteria criteria : criteriaGroup.getCriterias()) {
                            Criteria criteriaEntity = new Criteria();

                            criteriaEntity.setCriteriaGroup(criteriaGroupEntity);
                            criteriaEntity.setDisplayOrder(criteriaOrder);
                            criteriaEntity.setDataSection(criteria.getDataSection());
                            criteriaEntity.setOperator(criteria.getOperator());
                            criteriaEntity.setOpenBracket(criteria.getOpenBracket());
                            criteriaEntity.setFieldName(criteria.getFieldName());
                            criteriaEntity.setMatchingCondition(criteria.getMatchingCondition());
                            criteriaEntity.setValue(criteria.getValue());
                            criteriaEntity.setCloseBracket(criteria.getCloseBracket());

                            criteriaGroupEntity.getCriterias().add(criteriaEntity);
                            criteriaOrder++;
                        }
                    }

                    entity.getCriteriaGroups().add(criteriaGroupEntity);
                    criteriaGroupOrder++;
                }
            }

            // Remove original actions, action parameters will be removed due to cascading type
            if (entity.getActions() != null && entity.getActions().size() > 0) {
                ActionDAO actionDAO = new ActionDAO(sessionFactory, session);
                Iterator<Action> actionIterator = entity.getActions().iterator();

                while (actionIterator.hasNext()) {
                    Action action = actionIterator.next();
                    actionIterator.remove();
                    actionDAO.delete(action);
                }

                actionDAO.flush();
            }

            ActionPluginDAO actionPluginDAO = new ActionPluginDAO(sessionFactory, session);
            int actionOrder = 1;

            // Create action(s) entry for the rule
            for (Action action : rule.getActions()) {
                ActionPlugin actionPlugin = actionPluginDAO.get(action.getActionPlugin().getId());

                if (actionPlugin == null) {
                    throw new IllegalArgumentException("Invalid ActionPluginId. Unable to retrieve action plugin record.");
                }

                Action actionEntity = new Action();
                actionEntity.setRule(entity);
                actionEntity.setActionPlugin(actionPlugin);
                actionEntity.setDisplayOrder(actionOrder);
                actionEntity.setToleranceLevel(action.getToleranceLevel());

                // Dynamic parameter will be stored, fixed parameter will be fetch upon runtime
                if (action.getActionParams() != null && action.getActionParams().size() > 0) {
                    for (ActionParam actionParam : action.getActionParams()) {
                        ActionParam actionParamEntity = new ActionParam();
                        actionParamEntity.setAction(actionEntity);
                        actionParamEntity.setIdentifier(actionParam.getIdentifier());
                        actionParamEntity.setMetadataKey(actionParam.getMetadataKey());
                        actionParamEntity.setValue(actionParam.getValue());

                        actionEntity.getActionParams().add(actionParamEntity);
                    }
                }

                entity.getActions().add(actionEntity);
                actionOrder++;
            }

            ruleDAO.saveOrUpdate(entity);

            addRuleHistory(entity, rule);

            // Update rule execution
            RuleExecutionManager ruleExecutionManager = new RuleExecutionManager(sessionFactory, session);
            ruleExecutionManager.updateExecution(entity);
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }
    }

    // Soft delete
    public void deleteRule(Rule rule)
            throws ManagerException, RecordNotFoundException, RecordInUseException, RecordUnmatchedException {
        try {
            logger.debug("Delete rule for rule id " + rule.getId());

            Rule entity = ruleDAO.get(rule.getId());

            if (entity != null) {
                Date now = new Date();

                if (entity.getModifiedOn().getTime() == rule.getModifiedOn().getTime()) {
                    entity.setRuleEngine(null);
                    entity.isDeleted(true);
                    entity.setModifiedOn(now);

                    ruleDAO.saveOrUpdate(entity);
                } else {
                    throw new RecordUnmatchedException("Row was updated or deleted by another transaction.");
                }

                RuleExecutionDAO ruleExecutionDAO = new RuleExecutionDAO(sessionFactory, session);
                List<Criterion> criterion = new ArrayList<Criterion>();
                criterion.add(Restrictions.eq(RuleExecution.RULE_ID, entity.getId()));
                criterion.add(Restrictions.eq(RuleExecution.STATUS, RuleExecutionStatus.QUEUE.getCode()));

                List<RuleExecution> ruleExecutions = ruleExecutionDAO.findByCriteria(criterion);

                if (ruleExecutions != null) {
                    for (RuleExecution ruleExecution : ruleExecutions) {
                        ruleExecution.setStatus(RuleExecutionStatus.DELETED.getCode());
                        ruleExecution.setModifiedOn(now);
                    }

                    ruleExecutionDAO.saveOrUpdateAll(ruleExecutions);
                }
            } else {
                throw new RecordNotFoundException("Rule record not found for the given RuleID.");
            }
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }
    }

    public void importRule(List<Rule> rules)
            throws ManagerException {
        try {
            if (rules != null) {
                RuleExecutionManager ruleExecutionManager = new RuleExecutionManager(sessionFactory, session);
                Date now = new Date();

                for (Rule rule : rules) {
                    Rule entity = new Rule();
                    List<RuleEngine> ruleEngine = null;

                    if (rule.getRuleEngine() != null && StringUtils.isNotBlank(rule.getRuleEngine().getName())) {
                        ruleEngine = ruleEngineDAO.findByCriteria(Restrictions.eq(RuleEngine.NAME, rule.getRuleEngine().getName()));
                    }

                    if (ruleEngine != null && ruleEngine.size() > 0) {
                        entity.setRuleEngine(ruleEngine.get(0));
                    }
                    entity.setName(rule.getName());
                    entity.setDescription(rule.getDescription());
                    entity.isEnabled(rule.isEnabled());
                    entity.setScheduleType(rule.getScheduleType());
                    entity.setExecutionFrequency(rule.getExecutionFrequency());
                    entity.setEffectiveFrom(rule.getEffectiveFrom());
                    entity.setEffectiveUntil(rule.getEffectiveUntil());
                    entity.isDeleted(false);
                    entity.setVersion(rule.getVersion() == 0 ? 1 : rule.getVersion());
                    entity.setCreatedOn(rule.getCreatedOn() == null ? now : rule.getCreatedOn());
                    entity.setModifiedOn(rule.getModifiedOn() == null ? now : rule.getModifiedOn());
                    entity.setRepositoryType(rule.getRepositoryType());

                    // Create criteria group and criteria
                    if (rule.getCriteriaGroups() != null && rule.getCriteriaGroups().size() > 0) {
                        int criteriaGroupOrder = 1;

                        for (CriteriaGroup criteriaGroup : rule.getCriteriaGroups()) {
                            CriteriaGroup criteriaGroupEntity = new CriteriaGroup();

                            criteriaGroupEntity.setRule(entity);
                            criteriaGroupEntity.setDisplayOrder(criteriaGroupOrder);
                            criteriaGroupEntity.setOperator(criteriaGroup.getOperator());

                            if (criteriaGroup.getCriterias() != null && criteriaGroup.getCriterias().size() > 0) {
                                int criteriaOrder = 1;

                                for (Criteria criteria : criteriaGroup.getCriterias()) {
                                    Criteria criteriaEntity = new Criteria();

                                    criteriaEntity.setCriteriaGroup(criteriaGroupEntity);
                                    criteriaEntity.setDisplayOrder(criteriaOrder);
                                    criteriaEntity.setDataSection(criteria.getDataSection());
                                    criteriaEntity.setOperator(criteria.getOperator());
                                    criteriaEntity.setOpenBracket(criteria.getOpenBracket());
                                    criteriaEntity.setFieldName(criteria.getFieldName());
                                    criteriaEntity.setMatchingCondition(criteria.getMatchingCondition());
                                    criteriaEntity.setValue(criteria.getValue());
                                    criteriaEntity.setCloseBracket(criteria.getCloseBracket());

                                    criteriaGroupEntity.getCriterias().add(criteriaEntity);
                                    criteriaOrder++;
                                }
                            }

                            entity.getCriteriaGroups().add(criteriaGroupEntity);
                            criteriaGroupOrder++;
                        }
                    }

                    ActionPluginDAO actionPluginDAO = new ActionPluginDAO(sessionFactory, session);
                    int actionOrder = 1;

                    // Create action(s) entry for the rule
                    for (Action action : rule.getActions()) {
                        ActionPlugin actionPlugin = actionPluginDAO.get(action.getActionPlugin().getId());

                        if (actionPlugin == null) {
                            throw new IllegalArgumentException("Invalid ActionPluginId. Unable to retrieve action plugin record.");
                        }

                        Action actionEntity = new Action();
                        actionEntity.setRule(entity);
                        actionEntity.setActionPlugin(actionPlugin);
                        actionEntity.setDisplayOrder(actionOrder);
                        actionEntity.setToleranceLevel(action.getToleranceLevel());

                        // Dynamic parameter will be stored, fixed parameter will be fetch upon runtime
                        if (action.getActionParams() != null && action.getActionParams().size() > 0) {
                            for (ActionParam actionParam : action.getActionParams()) {
                                ActionParam actionParamEntity = new ActionParam();
                                actionParamEntity.setAction(actionEntity);
                                actionParamEntity.setIdentifier(actionParam.getIdentifier());
                                actionParamEntity.setMetadataKey(actionParam.getMetadataKey());
                                actionParamEntity.setValue(actionParam.getValue());

                                actionEntity.getActionParams().add(actionParamEntity);
                            }
                        }

                        actionOrder++;
                        entity.getActions().add(actionEntity);
                    }

                    ruleDAO.saveOrUpdate(entity);
                    ruleDAO.flush();

                    addRuleHistory(entity, rule);

                    // Schedule for execution
                    if (entity.isEnabled()) {
                        ruleExecutionManager.scheduleExecution(entity, ExecutionType.SCHEDULED.getCode());
                    }
                }
            }
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }
    }

    public long getRecordCount(List<Criterion> criterion)
            throws ManagerException {
        try {
            return ruleDAO.getCount(criterion);
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }
    }

    private void addRuleHistory(Rule entity, Rule dto)
            throws ManagerException {
        try {
            RuleHistoryDAO ruleHistoryDAO = new RuleHistoryDAO(sessionFactory, session);

            RuleHistory history = new RuleHistory();
            history.setRuleId(entity.getId());
            history.setRuleVersion(entity.getVersion());
            history.setSummary(new Gson().toJson(dto));
            history.setCreatedBy("SYSTEM");
            history.setCreatedOn(new Date());

            ruleHistoryDAO.save(history);
        } catch (HibernateException err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err.getMessage(), err);
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
            throw new ManagerException(err);
        }
    }
}
