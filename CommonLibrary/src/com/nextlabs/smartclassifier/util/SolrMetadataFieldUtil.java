package com.nextlabs.smartclassifier.util;

import com.nextlabs.smartclassifier.constant.MetadataMatchingCondition;
import com.nextlabs.smartclassifier.database.entity.MetadataField;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Map;

import static com.nextlabs.smartclassifier.constant.SolrConstant.DATE_POSTFIX;
import static com.nextlabs.smartclassifier.constant.SolrConstant.INTEGER_POSTFIX;
import static com.nextlabs.smartclassifier.constant.SolrConstant.STRING_POSTFIX;

public class SolrMetadataFieldUtil {

    private static final String REGEX_MULTIPLE_SPACE = "\\s+";
    private static final String SPACE = " ";
    private static final String UNDERSCORE = "_";
    private static final String QUOTE = "'";
    private static final String EMPTY = "";

    public static final String convertFieldNameToSolr(Map<String, MetadataField> metadataFieldNameToMetadataFieldMap, String fieldName) {
        if (StringUtils.isNotBlank(fieldName)) {
        	for(MetadataField metadataField : metadataFieldNameToMetadataFieldMap.values()) {
        		if(fieldName.equalsIgnoreCase(metadataField.getDisplayName()) && metadataField.isVisible()) {
        			return metadataField.getFieldName();
        		}
        	}
        	
            return fieldName.trim().replace(QUOTE, EMPTY)
                    .replaceAll(REGEX_MULTIPLE_SPACE, SPACE)
                    .replace(SPACE, UNDERSCORE)
                    .toLowerCase();
        }

        return fieldName;
    }

    public static final boolean isPredefinedField(Map<String, MetadataField> metadataFieldNameToMetadataFieldMap, String fieldName) {
        if (metadataFieldNameToMetadataFieldMap.containsKey(fieldName)) {
            return true;
        }

        return false;
    }

    public static final String resolveFieldName(Map<String, MetadataField> metadataFields, String key, String matchingCondition) {
        String fieldName = convertFieldNameToSolr(metadataFields, key);

        if (StringUtils.isNotBlank(fieldName) && StringUtils.isNotBlank(matchingCondition)) {
            if (isPredefinedField(metadataFields, fieldName)) {
                return fieldName;
            }

            if (MetadataMatchingCondition.NOT.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.NOT_CONTAINS.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.CONTAINS.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.MUST_CONTAINS.getCode().equals(matchingCondition)) {
                return fieldName + STRING_POSTFIX;
            } else if (MetadataMatchingCondition.NUM_EQUALS.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.NUM_GREATER.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.NUM_GREATER_OR_EQUAL.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.NUM_LESSER.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.NUM_LESSER_OR_EQUAL.getCode().equals(matchingCondition)) {
                return fieldName + INTEGER_POSTFIX;
            } else if (MetadataMatchingCondition.DATE_EQUALS.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.DATE_AFTER.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.DATE_AFTER_OR_EQUAL.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.DATE_BEFORE.getCode().equals(matchingCondition)
                    || MetadataMatchingCondition.DATE_BEFORE_OR_EQUAL.getCode().equals(matchingCondition)) {
                return fieldName + DATE_POSTFIX;
            }
        }

        return key;
    }

    public static final String resolveFieldName(Map<String, MetadataField> metadataFieldNameToMetadataFieldMap, String key, Object value) {
        String fieldName = convertFieldNameToSolr(metadataFieldNameToMetadataFieldMap, key);

        if (StringUtils.isNotBlank(fieldName) && value != null) {
            if (isPredefinedField(metadataFieldNameToMetadataFieldMap, fieldName)) {
                return fieldName;
            }

            if (value instanceof String) {
                return fieldName + STRING_POSTFIX;
            } else if (value instanceof Integer) {
                return fieldName + INTEGER_POSTFIX;
            } else if (value instanceof Date) {
                return fieldName + DATE_POSTFIX;
            }
        }

        return key;
    }
}
