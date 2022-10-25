package com.nextlabs.smartclassifier.constant;

import java.util.regex.Pattern;

/**
 * Created by pkalra on 11/21/2016.
 */
public final class Punctuation {
    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";
    public static final String SPACE = " ";
    public static final String HYPHEN = "-";
    public static final char COLON = ':';
    public static final String COMMA = ",";
    public static final String BACKSLASH_ASTERISK = Pattern.compile("\\\\\\*").toString();
    public static final String ESCAPED_ASTERISK = "\\*";
    public static final String ASTERISK = "*";
    public static final String ESCAPED_DOUBLE_QUOTE = Pattern.compile("\\\\\"").toString();
    public static final String DOUBLE_QUOTE = "\"";
    public static final String GENERAL_DELIMITER = "|";
    public static final String BACK_SLASH = "\\";
    public static final String FORWARD_SLASH = "/";
    public static final String DOUBLE_SLASHES = "//";
    public static final String PERIOD = ".";
    public static final String EMPTY_STRING = "";

    public static final String PERCENTAGE = "%";
}
