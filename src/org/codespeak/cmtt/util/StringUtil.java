package org.codespeak.cmtt.util;

/**
 * A class containing utility functions for strings
 *
 * @author Vector
 */
public class StringUtil {
    
    /**
     * Checks if the specified string is null or empty
     * @param str string to check
     * @return if the specified string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty());
    }
    
}
