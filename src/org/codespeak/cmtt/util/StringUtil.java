package org.codespeak.cmtt.util;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Takes a string and removes the duplicate words from it
     * @param str string to process
     * @return a string without any duplicated words
     */
    public static String getUnduplicatedString(String str) {
        List<String> unique = new ArrayList<String>();
        String[] parts = str.trim().split(" ");
        String finalString = "";
        
        for (String part : parts) {
            if (!unique.contains(part)) {
                unique.add(part);
                
                if (!finalString.isEmpty()) {
                    finalString += " ";
                }
                
                finalString += part;
            }
        }

        return finalString;
    }
    
    /**
     * Splits a given string and adds its elements to a list
     * @param input the input string
     * @return a list of elements split from a string
     */
    public static List<String> splitStringToList(String input) {
        List<String> ret = new ArrayList<String>();
        String[] parts = input.split(" ");
        
        for (String part : parts) {
            ret.add(part);
        }
        
        return ret;
    }
    
}
