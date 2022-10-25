package com.nextlabs.smartclassifier.util;

import com.nextlabs.smartclassifier.constant.Punctuation;

/**
 * Created by pkalra on 2/28/2017.
 */
public class StringFunctions {

    private StringFunctions() {
    }

    public static String appendBackSlash(String str) {
        return (str.charAt(str.length() - 1) == '\\') ? str : str + "\\";
    }

    public static String removeTrailingForwardSlash(String str) {
        return (str.charAt(str.length() - 1) == '/') ? str.substring(0, str.length() - 1) : str;
    }

    public static String removeWhiteSpacesInURL(String str) {
        return str.replaceAll(Punctuation.SPACE, "%20");
    }

    public static String changeForwardSlashToBackSlash(String str) {
        return str.replaceAll("/", "\\\\");
    }
}
