package com.nextlabs.smartclassifier.solr;

import com.nextlabs.smartclassifier.constant.*;
import com.nextlabs.smartclassifier.database.entity.Criteria;
import com.nextlabs.smartclassifier.database.entity.MetadataField;
import com.nextlabs.smartclassifier.plugin.DataProviderManager;
import com.nextlabs.smartclassifier.plugin.dataprovider.DataProvider;
import com.nextlabs.smartclassifier.util.SolrMetadataFieldUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.util.ClientUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static com.nextlabs.smartclassifier.constant.Punctuation.*;

public class QueryBuilder {

    private static final Logger logger = LogManager.getLogger(QueryBuilder.class);

    // This value needs to be align with Solr's date format
    public static final SimpleDateFormat solrDateFormatter = new SimpleDateFormat(SCConstant.SOLR_DATETIME_FORMAT);

    public static final String DIRECTORY_FIELD = SolrPredefinedField.DIRECTORY_LCASE + COLON;
    public static final String DOCUMENT_NAME_FIELD = SolrPredefinedField.DOCUMENT_NAME_LCASE + COLON;
    public static final String HEADER_FIELD = SolrPredefinedField.HEADER + COLON;
    public static final String BODY_FIELD = SolrPredefinedField.BODY + COLON;
    public static final String FOOTER_FIELD = SolrPredefinedField.FOOTER + COLON;
    public static final String CONTENT_FIELD = SolrPredefinedField.CONTENT + COLON;
    public static final String REPOSITORY_TYPE_FIELD = SolrPredefinedField.REPOSITORY_TYPE + COLON;
    public static final String FOLDER_URL_FIELD = SolrPredefinedField.FOLDER_URL + COLON;


    public static Map<DataSection, String> createQueryString(Map<String, MetadataField> metadataFieldNameToMetadataFieldMap, Set<Criteria> criteria, String repositoryType) {

        Map<DataSection, String> queries = new LinkedHashMap<DataSection, String>();
        StringBuilder directoryBuilder = new StringBuilder();
        StringBuilder fileNameBuilder = new StringBuilder();
        StringBuilder headerBuilder = new StringBuilder();
        StringBuilder contentBuilder = new StringBuilder();
        StringBuilder footerBuilder = new StringBuilder();
        StringBuilder metadataBuilder = new StringBuilder();

        // Store operator value for following up criterion
        String previousOperator = "";

        if (criteria != null) {
            for (Criteria criterion : criteria) {
                logger.debug("Criterion: " + criterion.toString());

                switch (DataSection.getSection(criterion.getDataSection())) {
                    case DIRECTORY:
                        parseDirectoryCriteria(directoryBuilder, previousOperator, criterion, repositoryType);
                        break;

                    case FILE_NAME:
                        parseDocumentNameCriteria(fileNameBuilder, previousOperator, criterion);
                        break;

                    case ALL:
                        parseCriteria(headerBuilder, previousOperator, criterion, metadataFieldNameToMetadataFieldMap);
                        parseCriteria(contentBuilder, previousOperator, criterion, metadataFieldNameToMetadataFieldMap);
                        parseCriteria(footerBuilder, previousOperator, criterion, metadataFieldNameToMetadataFieldMap);
                        break;

                    case HEADER:
                        parseCriteria(headerBuilder, previousOperator, criterion, metadataFieldNameToMetadataFieldMap);
                        break;

                    case CONTENT:
                        parseCriteria(contentBuilder, previousOperator, criterion, metadataFieldNameToMetadataFieldMap);
                        break;

                    case FOOTER:
                        parseCriteria(footerBuilder, previousOperator, criterion, metadataFieldNameToMetadataFieldMap);
                        break;

                    case METADATA:
                        parseCriteria(metadataBuilder, previousOperator, criterion, metadataFieldNameToMetadataFieldMap);
                        break;

                    default:
                        logger.info("Unrecognized data section [" + criterion.getDataSection() + "], skipped.");
                        continue;
                }

                previousOperator = criterion.getOperator();
            }
        }

        queries.put(DataSection.DIRECTORY, directoryBuilder.toString());
        queries.put(DataSection.FILE_NAME, fileNameBuilder.toString());
        queries.put(DataSection.HEADER, headerBuilder.toString());
        queries.put(DataSection.CONTENT, contentBuilder.toString());
        queries.put(DataSection.FOOTER, footerBuilder.toString());
        queries.put(DataSection.METADATA, metadataBuilder.toString());

        return queries;
    }

    /**
     * Create query string for given criteria.
     *
     * @param directoryBuilder String builder for related data section.
     * @param criteria         Criteria information.
     */
    private static void parseDirectoryCriteria(StringBuilder directoryBuilder, String previousOperator, Criteria criteria, String repositoryType) {
        if (directoryBuilder.length() > 0) {
            addNotBlank(directoryBuilder, previousOperator, true);
        }

        addNotBlank(directoryBuilder, criteria.getOpenBracket(), false);
        addSingleFieldValue(directoryBuilder,
                criteria.getMatchingCondition(),
                escapeSpecialCharacters(criteria.getValue().toLowerCase()
                        .replaceAll(Pattern.quote(BACK_SLASH), FORWARD_SLASH)),
                StringUtils.isBlank(criteria.getCloseBracket()));

        addNotBlank(directoryBuilder, criteria.getCloseBracket(), true);
    }

    /**
     * Create query string for given criteria.
     *
     * @param builder  String builder for related data section.
     * @param criteria Criteria information.
     */
    private static void parseDocumentNameCriteria(StringBuilder builder, String previousOperator, Criteria criteria) {
        if (builder.length() > 0) {
            addNotBlank(builder, previousOperator, true);
        }
        addNotBlank(builder, criteria.getOpenBracket(), false);
        addMultiFieldValue(builder, criteria.getMatchingCondition(), escapeSpecialCharacters(criteria.getValue().toLowerCase()), StringUtils.isBlank(criteria.getCloseBracket()));
        addNotBlank(builder, criteria.getCloseBracket(), true);
    }

    /**
     * Create query string for given criteria.
     *
     * @param builder        String builder for related data section.
     * @param criteria       Criteria information.
     * @param metadataFields Collection of defined Solr metadata fields
     */
    private static void parseCriteria(StringBuilder builder, String previousOperator, Criteria criteria, Map<String, MetadataField> metadataFields) {
        if (builder.length() > 0) {
            addNotBlank(builder, previousOperator, true);
        }
        addNotBlank(builder, criteria.getOpenBracket(), false);

        // Field name only appear for metadata section, which require special handling
        if (StringUtils.isNotBlank(criteria.getFieldName())) {
            String fieldName = SolrMetadataFieldUtil.resolveFieldName(metadataFields, criteria.getFieldName(), criteria.getMatchingCondition());
            addFieldName(builder, fieldName, criteria.getMatchingCondition());
            addMetadataFieldValue(builder, fieldName, criteria.getMatchingCondition(), escapeSpecialCharacters(criteria.getValue()), metadataFields.get(fieldName));
        } else {
            addMultiFieldValue(builder, criteria.getMatchingCondition(), escapeSpecialCharacters(criteria.getValue()), StringUtils.isBlank(criteria.getCloseBracket()));
        }

        addNotBlank(builder, criteria.getCloseBracket(), true);
    }

    /**
     * Only add given value into string builder if given value is not blank.
     *
     * @param builder     String builder for related data section.
     * @param value       Value to be added into string builder.
     * @param appendSpace Flag if space need to append after value.
     */
    private static void addNotBlank(StringBuilder builder, String value, boolean appendSpace) {
        if (StringUtils.isNotBlank(value)) {
            builder.append(value);

            if (appendSpace) {
                builder.append(SPACE);
            }
        }
    }

    /**
     * Convert metadata field name into query string.
     * Metadata specific string will be appended based on the matching condition given.
     *
     * @param builder           Metadata section string builder.
     * @param fieldName         Field name of metadata.
     * @param matchingCondition Matching condition string.
     */
    private static void addFieldName(StringBuilder builder, String fieldName, String matchingCondition) {
        // Negate appearance of value on this field
        if (MetadataMatchingCondition.NOT.getCode().equals(matchingCondition)) {
            builder.append("*:* AND ");
            builder.append(MetadataMatchingCondition.NOT.getSign());
        }

        if (MetadataMatchingCondition.NOT_CONTAINS.getCode().equals(matchingCondition)) {
            builder.append("*:* AND ");
            builder.append(MetadataMatchingCondition.NOT_CONTAINS.getSign());
        }

        String parsedFieldName = fieldName;
        // Data provider expression check
        if (fieldName.startsWith(SCConstant.DATA_PROVIDER_EXPRESSION_PREFIX)) {
            parsedFieldName = evaluateDataProviderExpression(fieldName);
        }

        builder.append(escapeSpecialCharacters(parsedFieldName.toLowerCase()));
        builder.append(COLON);
    }

    /**
     * Construct metadata matching operator together with field value to search query.
     *
     * @param builder           String builder for metadata section.
     * @param matchingCondition Metadata matching condition in criteria.
     * @param fieldValue        Metadata field value in criteria.
     */
    private static void addMetadataFieldValue(StringBuilder builder, String fieldName, String matchingCondition, String fieldValue, MetadataField metadataField) {
        builder.append(OPEN_BRACKET);

//		// Special handling for BETWEEN
//		if(MetadataMatchingCondition.BETWEEN.getCode().equals(matchingCondition)) {
//			String[] values = fieldValue.split(COMMA);
//			
//			if(values[0].startsWith(SCConstant.DATA_PROVIDER_EXPRESSION_PREFIX)) {
//				values[0] = evaluateDataProviderExpression(values[0]);
//			}
//			
//			if(values[1].startsWith(SCConstant.DATA_PROVIDER_EXPRESSION_PREFIX)) {
//				values[1] = evaluateDataProviderExpression(values[1]);
//			}
//			
//			builder.append(String.format(MetadataMatchingCondition.BETWEEN.getSign(), values[0].trim(), values[1].trim()));
//		} else {
        // Get array of multiple values
        List<String> fieldValues = parseMultiValue(fieldValue);

        for (String value : fieldValues) {
            String parsedFieldValue = value;

            // Data provider expression check
            if (value.startsWith(SCConstant.DATA_PROVIDER_EXPRESSION_PREFIX)) {
                parsedFieldValue = evaluateDataProviderExpression(value);
            }

            if (MetadataMatchingCondition.CONTAINS.getCode().equals(matchingCondition)) {
                builder.append(parsedFieldValue);
            } else if (MetadataMatchingCondition.NOT.getCode().equals(matchingCondition)) {
                builder.append(parsedFieldValue);
            } else if (MetadataMatchingCondition.NOT_CONTAINS.getCode().equals(matchingCondition)) {
                builder.append(parsedFieldValue);
            } else if (MetadataMatchingCondition.MUST_CONTAINS.getCode().equals(matchingCondition)) {
                builder.append(MetadataMatchingCondition.MUST_CONTAINS.getSign());
                builder.append(parsedFieldValue);
            } else if (MetadataMatchingCondition.NUM_LESSER.getCode().equals(matchingCondition)) {
                builder.append(String.format(MetadataMatchingCondition.NUM_LESSER.getSign(), parsedFieldValue));
            } else if (MetadataMatchingCondition.NUM_LESSER_OR_EQUAL.getCode().equals(matchingCondition)) {
                builder.append(String.format(MetadataMatchingCondition.NUM_LESSER_OR_EQUAL.getSign(), parsedFieldValue));
            } else if (MetadataMatchingCondition.NUM_EQUALS.getCode().equals(matchingCondition)) {
                builder.append(String.format(MetadataMatchingCondition.NUM_EQUALS.getSign(), parsedFieldValue, parsedFieldValue));
            } else if (MetadataMatchingCondition.NUM_GREATER_OR_EQUAL.getCode().equals(matchingCondition)) {
                builder.append(String.format(MetadataMatchingCondition.NUM_GREATER_OR_EQUAL.getSign(), parsedFieldValue));
            } else if (MetadataMatchingCondition.NUM_GREATER.getCode().equals(matchingCondition)) {
                builder.append(String.format(MetadataMatchingCondition.NUM_GREATER.getSign(), parsedFieldValue));
            } else if (MetadataMatchingCondition.DATE_BEFORE.getCode().equals(matchingCondition)) {
                if (metadataField != null && "LONG".equalsIgnoreCase(metadataField.getStorageDataType())) {
                    builder.append(String.format(MetadataMatchingCondition.NUM_LESSER.getSign(), toDateMillisecond(parsedFieldValue, TimeType.START_OF_THE_DAY)));
                } else {
                    builder.append(String.format(MetadataMatchingCondition.DATE_BEFORE.getSign(), toSolrDate(parsedFieldValue, TimeType.START_OF_THE_DAY)));
                }
            } else if (MetadataMatchingCondition.DATE_BEFORE_OR_EQUAL.getCode().equals(matchingCondition)) {
                if (metadataField != null && "LONG".equalsIgnoreCase(metadataField.getStorageDataType())) {
                    builder.append(String.format(MetadataMatchingCondition.NUM_LESSER_OR_EQUAL.getSign(), toDateMillisecond(parsedFieldValue, TimeType.END_OF_THE_DAY)));
                } else {
                    builder.append(String.format(MetadataMatchingCondition.DATE_BEFORE_OR_EQUAL.getSign(), toSolrDate(parsedFieldValue, TimeType.END_OF_THE_DAY)));
                }
            } else if (MetadataMatchingCondition.DATE_EQUALS.getCode().equals(matchingCondition)) {
                if (metadataField != null && "LONG".equalsIgnoreCase(metadataField.getStorageDataType())) {
                    String[] wholeDay = getWholeDayMillisecond(parsedFieldValue);
                    if (wholeDay != null) {
                        builder.append(String.format(MetadataMatchingCondition.NUM_EQUALS.getSign(), wholeDay[0], wholeDay[1]));
                    } else {
                        builder.append(String.format(MetadataMatchingCondition.NUM_EQUALS.getSign(), toDateMillisecond(parsedFieldValue, TimeType.START_OF_THE_DAY), toDateMillisecond(parsedFieldValue, TimeType.END_OF_THE_DAY)));
                    }
                } else {
                    String[] wholeDay = getSolrWholeDayDate(parsedFieldValue);
                    if (wholeDay != null) {
                        builder.append(String.format(MetadataMatchingCondition.DATE_EQUALS.getSign(), wholeDay[0], wholeDay[1]));
                    } else {
                        builder.append(String.format(MetadataMatchingCondition.DATE_EQUALS.getSign(), toSolrDate(parsedFieldValue, TimeType.START_OF_THE_DAY), toSolrDate(parsedFieldValue, TimeType.END_OF_THE_DAY)));
                    }
                }
            } else if (MetadataMatchingCondition.DATE_AFTER_OR_EQUAL.getCode().equals(matchingCondition)) {
                if (metadataField != null && "LONG".equalsIgnoreCase(metadataField.getStorageDataType())) {
                    builder.append(String.format(MetadataMatchingCondition.NUM_GREATER_OR_EQUAL.getSign(), toDateMillisecond(parsedFieldValue, TimeType.START_OF_THE_DAY)));
                } else {
                    builder.append(String.format(MetadataMatchingCondition.DATE_AFTER_OR_EQUAL.getSign(), toSolrDate(parsedFieldValue, TimeType.START_OF_THE_DAY)));
                }
            } else if (MetadataMatchingCondition.DATE_AFTER.getCode().equals(matchingCondition)) {
                if (metadataField != null && "LONG".equalsIgnoreCase(metadataField.getStorageDataType())) {
                    builder.append(String.format(MetadataMatchingCondition.NUM_GREATER.getSign(), toDateMillisecond(parsedFieldValue, TimeType.END_OF_THE_DAY)));
                } else {
                    builder.append(String.format(MetadataMatchingCondition.DATE_AFTER.getSign(), toSolrDate(parsedFieldValue, TimeType.END_OF_THE_DAY)));
                }
            }

            builder.append(SPACE);
        }
//		}

        builder.append(CLOSE_BRACKET);
    }

    /**
     * Append matching condition and field value in criteria to search query.
     * This method should be call for input field which contains single value (String)
     *
     * @param builder           String builder for data section.
     * @param matchingCondition Matching condition in criteria.
     * @param fieldValue        Field value pattern in criteria.
     * @param appendSpace       Flag if space need to append after value.
     */
    private static void addSingleFieldValue(StringBuilder builder, String matchingCondition, String fieldValue, boolean appendSpace) {
        logger.debug(String.format("Field Value = %s", fieldValue));
        MatchingCondition condition = MatchingCondition.getCondition(matchingCondition);

        String parsedFieldValue = fieldValue;

        // Data provider expression check
        if (fieldValue.startsWith(SCConstant.DATA_PROVIDER_EXPRESSION_PREFIX)) {
            parsedFieldValue = evaluateDataProviderExpression(fieldValue);
        }

        // Skip in line operator for matching condition sign prepend
        if (!isInlineOperator(parsedFieldValue)) {
            builder.append(condition.getSign());
        }
        builder.append(parsedFieldValue);

        if (appendSpace) {
            builder.append(SPACE);
        }
    }

    /**
     * Append matching condition and field value in criteria to search query.
     * This method should be call for input field which contains multiple value (text)
     *
     * @param builder           String builder for data section.
     * @param matchingCondition Matching condition in criteria.
     * @param fieldValue        Field value pattern in criteria.
     * @param appendSpace       Flag if space need to append after value.
     */
    private static void addMultiFieldValue(StringBuilder builder, String matchingCondition, String fieldValue, boolean appendSpace) {
        // Get array of multiple values
        List<String> fieldValues = parseMultiValue(fieldValue);
        logger.debug(String.format("Field Values [%s] = %s", fieldValues.size(), fieldValues));
        MatchingCondition condition = MatchingCondition.getCondition(matchingCondition);

        // Prepend matching condition sign to value(s)
        for (String value : fieldValues) {
            String parsedFieldValue = value;

            // Data provider expression check
            if (value.startsWith(SCConstant.DATA_PROVIDER_EXPRESSION_PREFIX)) {
                parsedFieldValue = evaluateDataProviderExpression(value);
            }

            // Skip in line operator for matching condition sign prepend
            if (!isInlineOperator(parsedFieldValue)) {
                builder.append(condition.getSign());
            }
            builder.append(parsedFieldValue);

            if (appendSpace) {
                builder.append(SPACE);
            }
        }
    }

    /**
     * Split string with space which not found within quotes.
     * Support escape quote.
     *
     * @param valueString String to tokenize.
     * @return List of string value which split with space.
     */
    private static List<String> parseMultiValue(String valueString) {
        List<String> values = new ArrayList<>();

        if (StringUtils.isNotBlank(valueString)) {
            // Split valueString with space that not found within quotes, support escape quote
            String[] tokens = valueString.split("\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\"])*$)");
            // Split valueString with space that not found within quotes, not support escape quote
            // String[] tokens = valueString.split("\\s+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            Collections.addAll(values, tokens);
        }

        return values;
    }

    /**
     * Call to evaluate data provider expression.
     *
     * @param expression Complete data provider expression string.
     * @return Evaluated value of found data provider. If data provider not found, return original expression string.
     */
    private static String evaluateDataProviderExpression(String expression) {
        try {
            DataProvider dataProvider = DataProviderManager.getDataProvider(expression);

            if (dataProvider != null) {
                return dataProvider.evaluate();
            }

            logger.warn("Data provider not found for expression: " + expression + ".");
        } catch (Exception err) {
            logger.error("Failed to evaluate data provider expression: " + expression + ".", err);
        }

        return expression;
    }

    private static boolean isInlineOperator(String value) {
        return (value.equals("&&") || value.equals("AND") || value.equals("||") || value.equals("OR"));
    }

    private static String[] getSolrWholeDayDate(String value) {
        String[] wholeDayDate = new String[2];

        if (StringUtils.isNotBlank(value)) {
            try {
                long dateInMilliseconds = Long.parseLong(value);
                if (dateInMilliseconds > 0) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(dateInMilliseconds);
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.SECOND, 0);
                    startTime.set(Calendar.MILLISECOND, 0);
                    wholeDayDate[0] = solrDateFormatter.format(new Date(startTime.getTimeInMillis()));

                    Calendar endTime = Calendar.getInstance();
                    endTime.setTimeInMillis(dateInMilliseconds);
                    endTime.set(Calendar.HOUR_OF_DAY, 23);
                    endTime.set(Calendar.MINUTE, 59);
                    endTime.set(Calendar.SECOND, 59);
                    endTime.set(Calendar.MILLISECOND, 999);
                    wholeDayDate[1] = solrDateFormatter.format(new Date(endTime.getTimeInMillis()));

                    return wholeDayDate;
                }
            } catch (Exception err) {
                logger.error(err);
            }
        }

        return null;
    }

    private static String[] getWholeDayMillisecond(String value) {
        String[] wholeDayMillisecond = new String[2];

        if (StringUtils.isNotBlank(value)) {
            try {
                long dateInMillisecond = Long.parseLong(value);
                
                logger.info("Pass in value is "+ value);

                if (dateInMillisecond > 0) {
                	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                	
                    Calendar startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(dateInMillisecond);
                    

                    Calendar newTime = Calendar.getInstance();
                    newTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH));
                    newTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                    newTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                    newTime.set(Calendar.HOUR_OF_DAY, 0);
                    newTime.set(Calendar.MINUTE, 0);
                    newTime.set(Calendar.SECOND, 0);
                    newTime.set(Calendar.MILLISECOND, 0);
                    wholeDayMillisecond[0] = Long.toString(newTime.getTimeInMillis());
                    
                    logger.info("Converted value is "+ newTime.getTimeInMillis());
                    
                    newTime.set(Calendar.HOUR_OF_DAY, 23);
                    newTime.set(Calendar.MINUTE, 59);
                    newTime.set(Calendar.SECOND, 59);
                    newTime.set(Calendar.MILLISECOND, 999);
                    wholeDayMillisecond[1] = Long.toString(newTime.getTimeInMillis());
                    
                    logger.info("Converted value for end time is "+ newTime.getTimeInMillis());

                    return wholeDayMillisecond;
                }
            } catch (Exception err) {
                logger.error(err);
            }
        }

        return null;
    }

    /**
     * Try to parse long value to date format string in Solr
     *
     * @param value
     * @return
     */
    private static String toSolrDate(String value, TimeType type) {
        try {
            if (StringUtils.isNotBlank(value)) {
            	
                long dateInMilliseconds = Long.parseLong(value);
                
                logger.debug("Pass in value is "+ value);

                if (dateInMilliseconds > 0) {
                    if (TimeType.START_OF_THE_DAY.equals(type)) {
                    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(dateInMilliseconds);
                        Calendar newTime = Calendar.getInstance();
                        newTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH));
                        newTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                        newTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                        newTime.set(Calendar.HOUR_OF_DAY, 0);
                        newTime.set(Calendar.MINUTE, 0);
                        newTime.set(Calendar.SECOND, 0);
                        newTime.set(Calendar.MILLISECOND, 0);
                        
                        logger.debug("Converted value is "+ newTime.getTimeInMillis());

                        return solrDateFormatter.format(new Date(newTime.getTimeInMillis()));
                        
                    } else if (TimeType.END_OF_THE_DAY.equals(type)) {
                    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                        Calendar endTime = Calendar.getInstance();
                        endTime.setTimeInMillis(dateInMilliseconds);
                        Calendar newTime = Calendar.getInstance();
                        newTime.set(Calendar.DAY_OF_MONTH, endTime.get(Calendar.DAY_OF_MONTH));
                        newTime.set(Calendar.MONTH, endTime.get(Calendar.MONTH));
                        newTime.set(Calendar.YEAR, endTime.get(Calendar.YEAR));
                        newTime.set(Calendar.HOUR_OF_DAY, 23);
                        newTime.set(Calendar.MINUTE, 59);
                        newTime.set(Calendar.SECOND, 59);
                        newTime.set(Calendar.MILLISECOND, 999);
                        logger.info("Converted value is "+ newTime.getTimeInMillis());

                        return solrDateFormatter.format(new Date(newTime.getTimeInMillis()));
                    }
                }
            }
        } catch (Exception err) {
            logger.error(err);
            // Ignore
        }

        return value;
    }

    private static String toDateMillisecond(String value, TimeType type) {
        try {
            if (StringUtils.isNotBlank(value)) {
                long dateInMilliseconds = Long.parseLong(value);
                
                logger.info("Pass in value is "+ value);
                		
                if (dateInMilliseconds > 0) {
                    if (TimeType.START_OF_THE_DAY.equals(type)) {
                    	
                    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(dateInMilliseconds);
                        Calendar newTime = Calendar.getInstance();
                        newTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH));
                        newTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                        newTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                        newTime.set(Calendar.HOUR_OF_DAY, 0);
                        newTime.set(Calendar.MINUTE, 0);
                        newTime.set(Calendar.SECOND, 0);
                        newTime.set(Calendar.MILLISECOND, 0);
                        
                        logger.info("Converted value is "+ newTime.getTimeInMillis());

                        return Long.toString(newTime.getTimeInMillis());
                    } else if (TimeType.END_OF_THE_DAY.equals(type)) {
                    	
                    	TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
                        Calendar endTime = Calendar.getInstance();
                        endTime.setTimeInMillis(dateInMilliseconds);
                        Calendar newTime = Calendar.getInstance();
                        newTime.set(Calendar.DAY_OF_MONTH, endTime.get(Calendar.DAY_OF_MONTH));
                        newTime.set(Calendar.MONTH, endTime.get(Calendar.MONTH));
                        newTime.set(Calendar.YEAR, endTime.get(Calendar.YEAR));
                        newTime.set(Calendar.HOUR_OF_DAY, 23);
                        newTime.set(Calendar.MINUTE, 59);
                        newTime.set(Calendar.SECOND, 59);
                        newTime.set(Calendar.MILLISECOND, 999);
                        
                        logger.info("Converted value is "+ newTime.getTimeInMillis());

                        return Long.toString(newTime.getTimeInMillis());
                    }
                }
            }
        } catch (Exception err) {
            logger.error(err);
        }

        return value;
    }

    /**
     * Handle special characters in field value which has special meanings in Solr query string.
     *
     * @param inputString Original string which may contains special character
     * @return Parsed string with escaped characters and recover \* and * as in original string
     */
    public static String escapeSpecialCharacters(String inputString) {
        try {
            if (inputString != null) {
                // Solr's RegEx string, do not perform any escape handling
                if (inputString.startsWith(FORWARD_SLASH)
                        && !inputString.startsWith(DOUBLE_SLASHES)
                        && inputString.endsWith(FORWARD_SLASH)) {
                    return inputString;
                } else {
                    String escapeAsterisk = ClientUtils.escapeQueryChars(inputString)
                            .replaceAll(BACKSLASH_ASTERISK, ASTERISK)
                            .replaceAll(Pattern.quote(ESCAPED_ASTERISK), ESCAPED_ASTERISK);
                    return escapeAsterisk.replaceAll(ESCAPED_DOUBLE_QUOTE, DOUBLE_QUOTE);
                }
            }
        } catch (Exception err) {
            logger.error(err.getMessage(), err);
        }

        return inputString;
    }

    public static StringBuilder getRepositoryTypeQueryString(String repositoryType) {
        StringBuilder repositoryTypeQueryString = new StringBuilder();

        repositoryTypeQueryString.append(OPEN_BRACKET)
                .append(REPOSITORY_TYPE_FIELD).append(escapeSpecialCharacters(repositoryType))
                .append(CLOSE_BRACKET);

        logger.debug("Repository Type Query String = " + repositoryTypeQueryString);
        return repositoryTypeQueryString;
    }
}

enum TimeType {

    START_OF_THE_DAY,
    END_OF_THE_DAY;

}
