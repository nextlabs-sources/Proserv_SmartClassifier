package com.nextlabs.smartclassifier.validator.v1.rule;

import com.nextlabs.smartclassifier.constant.DataSection;
import com.nextlabs.smartclassifier.constant.MetadataMatchingCondition;
import com.nextlabs.smartclassifier.constant.RepositoryType;
import com.nextlabs.smartclassifier.constant.ScheduleType;
import com.nextlabs.smartclassifier.dto.v1.*;
import com.nextlabs.smartclassifier.dto.v1.request.UpdateRequest;
import com.nextlabs.smartclassifier.util.MessageUtil;
import com.nextlabs.smartclassifier.util.StringFunctions;
import com.nextlabs.smartclassifier.validator.v1.Validation;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class RuleUpdateValidator {
    private static final Logger logger = LogManager.getLogger(RuleUpdateValidator.class);

    public static Validation validate(UpdateRequest request)
            throws Exception {
        Validation validation = new Validation();
        RuleDTO ruleDTO = (RuleDTO) request.getData();
        RepositoryType repositoryType = RepositoryType.getRepositoryType(ruleDTO.getRepositoryType());

        // Target record id is missing
        if (ruleDTO.getId() == null || ruleDTO.getId() == 0) {
            validation.isValid(false);
            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero", "RuleID"));

            return validation;
        }

        // Name should not be blank
        if (StringUtils.isBlank(ruleDTO.getName())) {
            validation.isValid(false);
            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Name"));

            return validation;
        }

        // Rule engine id should not be blank
        if (ruleDTO.getRuleEngine() == null
                || ruleDTO.getRuleEngine().getId() == null
        		|| ruleDTO.getRuleEngine().getId() == 0) {
            validation.isValid(false);
            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Rule engine"));

            return validation;
        }

        // Repository Type should not be blank
        if (StringUtils.isBlank(ruleDTO.getRepositoryType())) {
            validation.isValid(false);
            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Repository Type"));

            return validation;
        }
        /* If repository type = shared_folders, directory = startswithhttp
        * If repository type = sharepoint, directory = url */

        // Criteria object check
        if (ruleDTO.getCriteriaGroups() != null) {
            for (CriteriaGroupDTO criteriaGroupDTO : ruleDTO.getCriteriaGroups()) {
                if (criteriaGroupDTO.getCriterias() != null) {
                    for (CriteriaDTO criteriaDTO : criteriaGroupDTO.getCriterias()) {
                        if (StringUtils.isBlank(criteriaDTO.getDataSection())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Data section"));

                            return validation;
                        }

                        if (StringUtils.isBlank(criteriaDTO.getMatchingCondition())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Matching condition"));

                            return validation;
                        }

                        if (DataSection.METADATA.getCode().equals(criteriaDTO.getDataSection())
                                && StringUtils.isBlank(criteriaDTO.getFieldName())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Metadata field name"));

                            return validation;
                        }

                        if (StringUtils.isBlank(criteriaDTO.getValue())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Criteria value"));

                            return validation;
                        }

                        if (MetadataMatchingCondition.NUM_EQUALS.getCode().equals(criteriaDTO.getMatchingCondition())
                                || MetadataMatchingCondition.NUM_GREATER.getCode().equals(criteriaDTO.getMatchingCondition())
                                || MetadataMatchingCondition.NUM_GREATER_OR_EQUAL.getCode().equals(criteriaDTO.getMatchingCondition())
                                || MetadataMatchingCondition.NUM_LESSER.getCode().equals(criteriaDTO.getMatchingCondition())
                                || MetadataMatchingCondition.NUM_LESSER_OR_EQUAL.getCode().equals(criteriaDTO.getMatchingCondition())) {
                            try {
                                Integer.parseInt(criteriaDTO.getValue());
                            } catch (NumberFormatException err) {
                                validation.isValid(false);
                                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.maxvalue.code"));
                                validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.maxvalue", criteriaDTO.getField(), Integer.MAX_VALUE));

                                break;
                            }
                        }
                    }
                }
            }
        }

        // Directory object check
        List<CriteriaGroupDTO> dirCriteriaGroups = ruleDTO.getDirectories();

        if (dirCriteriaGroups != null) {
            logger.debug("dirCriteriaGroups.size() = " + dirCriteriaGroups.size());

            for (CriteriaGroupDTO dirCriteriaGroup : dirCriteriaGroups) {
                List<CriteriaDTO> criteria = dirCriteriaGroup.getCriterias();

                if (criteria != null) {
                    logger.debug("criteria.size() = " + criteria.size());
                    for (CriteriaDTO criterion : criteria) {
                        if (StringUtils.isBlank(criterion.getDataSection())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Data section"));

                            return validation;
                        }

                        logger.debug(criterion.getDataSection());

                        if (DataSection.getSection(criterion.getDataSection()) == DataSection.DIRECTORY &&
                                repositoryType == RepositoryType.SHAREPOINT) {
                            logger.debug("Removing white spaces if any from the directory!");
                            criterion.setValue(StringFunctions.removeWhiteSpacesInURL(criterion.getValue()));
                        }

                        if (StringUtils.isBlank(criterion.getMatchingCondition())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Matching condition"));

                            return validation;
                        }

                        if (DataSection.METADATA.getCode().equals(criterion.getDataSection())
                                && StringUtils.isBlank(criterion.getFieldName())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Metadata field name"));

                            return validation;
                        }

                        if (StringUtils.isBlank(criterion.getValue())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Criteria value"));

                            return validation;
                        }
                    }
                }
            }
        }

        if ((ruleDTO.getEffectiveFrom() != null && ruleDTO.getEffectiveFrom() > 0)
        		&& (ruleDTO.getEffectiveUntil() != null && ruleDTO.getEffectiveUntil() > 0)) {
            if (ruleDTO.getEffectiveUntil() < ruleDTO.getEffectiveFrom()) {
                validation.isValid(false);
                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.minvalue.code"));
                validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.minvalue", "Effective end date", "effective start date"));

                return validation;
            }
        }

        // Schedule type should not be blank
        if (StringUtils.isBlank(ruleDTO.getScheduleType())) {
            validation.isValid(false);
            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Schedule type"));

            return validation;
        }

        // Action should not be blank
        if (ruleDTO.getActions() == null || ruleDTO.getActions().size() == 0) {
            validation.isValid(false);
            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Action"));

            return validation;
        } else {
            for (ActionDTO actionDTO : ruleDTO.getActions()) {
                if (actionDTO.getActionPlugin() == null
                		|| actionDTO.getActionPlugin().getId() == null
                        || actionDTO.getActionPlugin().getId() == 0) {
                    validation.isValid(false);
                    validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.zero.code"));
                    validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.zero.code", "ActionPluginID"));

                    return validation;
                }

                if (actionDTO.getActionParams() != null) {
                    for (ActionParamDTO actionParamDTO : actionDTO.getActionParams()) {
                        if (StringUtils.isBlank(actionParamDTO.getIdentifier())) {
                            validation.isValid(false);
                            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Identifier"));

                            return validation;
                        }

                        for (KeyValueDTO keyValue : actionParamDTO.getValues()) {
                            if (StringUtils.isBlank(keyValue.getValue())) {
                                validation.isValid(false);
                                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                                validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Value"));

                                return validation;
                            }
                        }
                    }
                }
            }
        }

        // Execution frequency
        if (ruleDTO.getExecutionFrequency() != null) {
            if (StringUtils.isBlank(ruleDTO.getExecutionFrequency().getTime())) {
                validation.isValid(false);
                validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Time"));

                return validation;
            }

            if (ScheduleType.WEEKLY.getCode().equalsIgnoreCase(ruleDTO.getScheduleType())) {
                if (ruleDTO.getExecutionFrequency().getDayOfWeek() == null
                        || ruleDTO.getExecutionFrequency().getDayOfWeek().size() == 0) {
                    validation.isValid(false);
                    validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                    validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Day of week"));

                    return validation;
                }
            }

            if (ScheduleType.MONTHLY.getCode().equalsIgnoreCase(ruleDTO.getScheduleType())) {
                if (ruleDTO.getExecutionFrequency().getDayOfMonth() == null
                        || ruleDTO.getExecutionFrequency().getDayOfMonth().size() == 0) {
                    validation.isValid(false);
                    validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
                    validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Day of month"));

                    return validation;
                }
            }
        } else {
            validation.isValid(false);
            validation.setErrorCode(MessageUtil.getMessage("invalid.input.field.blank.code"));
            validation.setErrorMessage(MessageUtil.getMessage("invalid.input.field.blank", "Execution frequency"));

            return validation;
        }

        return validation;
    }
}
