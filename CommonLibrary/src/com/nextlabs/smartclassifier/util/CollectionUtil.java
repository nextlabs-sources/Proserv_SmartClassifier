package com.nextlabs.smartclassifier.util;

import java.util.List;
import java.util.ListIterator;

/**
 * Utility class for collections
 * Created by pkalra on 11/2/2016.
 */
public final class CollectionUtil {

    /**
     * Takes a list of Strings and converts all strings to lower case
     * @param list list of strings
     */
    public static void toLowerCase(List<String> list)
    {
        ListIterator<String> iterator = list.listIterator();
        while (iterator.hasNext())
        {
            iterator.set(iterator.next().toLowerCase());
        }
    }

    private CollectionUtil() {
        // don't call this ever
    }
}
