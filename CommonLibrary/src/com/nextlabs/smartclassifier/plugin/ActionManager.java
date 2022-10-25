package com.nextlabs.smartclassifier.plugin;

import com.nextlabs.smartclassifier.constant.SCConstant;
import com.nextlabs.smartclassifier.database.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ActionManager {

    private static final Logger logger = LogManager.getLogger(ActionManager.class);

    /**
     * @param rule                                The Rule to be executed
     * @param systemConfigsIdToValueMap           Map from SystemConfig Id to Value Map
     * @param metadataFieldNameToMetadataFieldMap metadata field Name To MetadataField Map
     * @return List of Action (s) for this Rule
     */
    public static List<com.nextlabs.smartclassifier.plugin.action.Action> getActions(Rule rule, Map<String, String> systemConfigsIdToValueMap, Map<String, MetadataField> metadataFieldNameToMetadataFieldMap) {

        if (rule != null) {
            try {
                Set<com.nextlabs.smartclassifier.database.entity.Action> configuredActions = rule.getActions();

                if (configuredActions == null) {
                    throw new IllegalArgumentException("No action is configured for the rule \"" + rule.getName() + "\".");
                }

                List<com.nextlabs.smartclassifier.plugin.action.Action> actions = new ArrayList<>();
                //List<Action> actions = new ArrayList<>();

                for (com.nextlabs.smartclassifier.database.entity.Action action : configuredActions) {

                    ActionPlugin plugin = action.getActionPlugin();

                    if (plugin == null) {
                        throw new IllegalArgumentException("No action plugin is configured for this rule \"" + rule.getName() + "\".");
                    }

                    String actionPluginName = plugin.getName();
                    logger.debug(actionPluginName + " is configured for the rule \"" + rule.getName() + "\".");

                    Set<ActionParam> actionParams = action.getActionParams();

                    if (actionPluginName != null && actionPluginName.length() > 0) {
                        com.nextlabs.smartclassifier.plugin.action.Action actionToPerform = PluginManager.getInstance().getActionNameToActionMap().get(actionPluginName);

                        if (actionToPerform != null) {
                            com.nextlabs.smartclassifier.plugin.action.Action cloneOfAction = (com.nextlabs.smartclassifier.plugin.action.Action) Class.forName(actionToPerform.getClass().getName()).newInstance();

                            cloneOfAction.setId(action.getId());
                            cloneOfAction.setDisplayName(plugin.getDisplayName());
                            cloneOfAction.setRuleId(rule.getId());
                            cloneOfAction.setPluginId(plugin.getId());
                            cloneOfAction.isFireOncePerRule(plugin.isFireOncePerRule());
                            cloneOfAction.setToleranceLevel(action.getToleranceLevel());
                            cloneOfAction.setSystemConfigs(systemConfigsIdToValueMap);
                            cloneOfAction.setMetadataFieldByName(metadataFieldNameToMetadataFieldMap);

                            setFixedParameters(cloneOfAction, plugin);
                            setDynamicParameters(cloneOfAction, actionParams);

                            actions.add(cloneOfAction);
                        }
                    }
                }

                return actions;
            } catch (Exception err) {
                logger.error(err.getMessage(), err);
            }
        }

        return null;
    }

    /**
     * Set action generic parameter value.
     *
     * @param action       Action to perform.
     * @param actionPlugin Set of fixed action parameters.
     *                     <br>When there is no action parameter required, actionParams can be null.
     */
    private static void setFixedParameters(com.nextlabs.smartclassifier.plugin.action.Action action, ActionPlugin actionPlugin) {
        if (actionPlugin != null && actionPlugin.getPluginParams() != null && actionPlugin.getPluginParams().size() > 0) {
            for (ActionPluginParam actionPluginParam : actionPlugin.getPluginParams()) {
                if (actionPluginParam.isFixedParameter()) {
                    logger.debug("Set fixed action property " + actionPluginParam.getIdentifier() + " as " + actionPluginParam.getFixedValue() + " for " + action.getName());
                    if (action.getPropertyByName().containsKey(actionPluginParam.getIdentifier())) {
                        action.getPropertyByName().get(actionPluginParam.getIdentifier()).add(actionPluginParam.getFixedValue());
                    } else {
                        Set<String> valueSet = new LinkedHashSet<>();
                        valueSet.add(actionPluginParam.getFixedValue());
                        action.getPropertyByName().put(actionPluginParam.getIdentifier(), valueSet);
                    }
                }
            }
        }
    }

    /**
     * Set action specific parameter value.
     *
     * @param action       Action to perform.
     * @param actionParams Set of action parameters required by action to perform.
     *                     <br>When there is no action parameter required, actionParams can be null.
     */
    private static void setDynamicParameters(com.nextlabs.smartclassifier.plugin.action.Action action, Set<ActionParam> actionParams) {

        if (actionParams != null && actionParams.size() > 0) {

            for (ActionParam actionParam : actionParams) {
                // Check this is tag or parameter
                if (SCConstant.ACTION_PARAM_TAG_IDENTIFIER.equals(actionParam.getIdentifier())) {
                    logger.debug("Add action tag property " + actionParam.getMetadataKey() + " as " + actionParam.getValue() + " for " + action.getName());
                    if (action.getTagsByName().containsKey(actionParam.getMetadataKey())) {
                        action.getTagsByName().get(actionParam.getMetadataKey()).add(actionParam.getValue());
                    } else {
                        Set<String> valueSet = new LinkedHashSet<>();
                        valueSet.add(actionParam.getValue());
                        action.getTagsByName().put(actionParam.getMetadataKey(), valueSet);
                    }
                } else {
                    logger.debug("Add action property " + actionParam.getIdentifier() + " as " + actionParam.getValue() + " for " + action.getName());

                    if (action.getPropertyByName().containsKey(actionParam.getIdentifier())) {
                        action.getPropertyByName().get(actionParam.getIdentifier()).add(actionParam.getValue());
                    } else {
                        Set<String> valueSet = new LinkedHashSet<>();
                        valueSet.add(actionParam.getValue());
                        action.getPropertyByName().put(actionParam.getIdentifier(), valueSet);
                    }
                }
            }
        }
    }
}
