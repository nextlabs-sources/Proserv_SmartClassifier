package com.nextlabs.smartclassifier.dto;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.nextlabs.smartclassifier.constant.Punctuation.GENERAL_DELIMITER;

public class DocumentExtractor {

    private Map<String, String> parameters;

    private String className;

    private int maximumFileSize;

    public DocumentExtractor(String extractorClassName, int maximumFileSize, String parameter) {
        super();
        this.className = extractorClassName;
        this.maximumFileSize = maximumFileSize;
        this.parameters = parseParameter(parameter);
    }

    public String getClassName() {
        return className;
    }

    public int getMaximumFileSize() {
        return maximumFileSize;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    private Map<String, String> parseParameter(String parameter) {
        Map<String, String> parameters = new HashMap<>();

        if (StringUtils.isNotBlank(parameter)) {
            String[] keyValues = parameter.split(Pattern.quote(GENERAL_DELIMITER));

            for (String keyValue : keyValues) {
                String[] pair = keyValue.split("=");

                if (pair.length == 2) {
                    parameters.put(pair[0], pair[1]);
                }
            }
        }

        return parameters;
    }
}
