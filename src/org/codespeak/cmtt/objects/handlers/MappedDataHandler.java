package org.codespeak.cmtt.objects.handlers;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 * A handler for mapped data with a key and value
 *
 * @author Vector
 */
public class MappedDataHandler {

    private static Map<String, Object> mappedData = new HashMap<String, Object>();
    
    /**
     * Sets mapped data with a key and value
     * @param key the key of the mapped data
     * @param value the value of the mapped data
     */
    public static void setMappedData(String key, Object value) {
        mappedData.put(key, value);
    }
    
    /**
     * Gets mapped data represented by the specified key
     * @param key the key of the mapped data
     * @return data represented by a key
     */
    public static <T> T getMappedData(String key) {
        return (T) mappedData.get(key);
    }

    /**
     * Gets if the mapped data contains the specified key
     * @param key key being checked
     * @return if the mapped data contains the specified key
     */
    public boolean hasMappedData(String key) {
        return mappedData.containsKey(key);
    }
    
    /**
     * Saves mapped data to JSON
     * @param json JSON object representing a data file
     */
    public static void saveToJSON(JSONObject json) {
        JSONObject mappedDataJson = new JSONObject();
        
        for (EnumMap.Entry<String, Object> entry : mappedData.entrySet()) {
            mappedDataJson.put(entry.getKey(), entry.getValue());
        }
        
        json.put("mapped_data", mappedDataJson);
    }
    
    /**
     * Loads mapped data from JSON
     * @param json JSON object representing a data file
     */
    public static void loadFromJSON(JSONObject json) {
        if (json.has("mapped_data")) {
            JSONObject mappedDataJson = json.getJSONObject("mapped_data");

            for (String key : mappedDataJson.keySet()) {
                Object value = mappedDataJson.get(key);
                
                mappedData.put(key, value);
            }
        }
    }

}
