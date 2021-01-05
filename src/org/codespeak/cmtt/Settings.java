package org.codespeak.cmtt;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A class that contains the program's settings
 *
 * @author Vector
 */
public class Settings {

    public enum SettingFields {
        MINECRAFT_LAUNCHER_LOCATION("minecraft_launcher_location", "");
        
        private final String key;
        private final Object defaultValue;
        
        private SettingFields(String key, Object defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }
        
        /**
         * Gets the key of this setting
         * @return key of this setting
         */
        public String getKey() {
            return key;
        }
        
        /**
         * Gets the default value of this setting
         * @return default value of this setting
         */
        public Object getDefaultValue() {
            return defaultValue;
        }
    }
    
    private Map<SettingFields, Object> settings = new HashMap<SettingFields, Object>();
    
    private Settings(Map<SettingFields, Object> settings) {
        this.settings = settings;
    }
    
    /**
     * Gets the setting associated with the specified field
     * @param field the field to get the setting from
     * @return setting associated with the specified field
     */
    public <T> T getSetting(SettingFields field) {
        return (T) settings.get(field);
    }
    
    /**
     * Sets the setting indicated by the SettingFields field
     * @param field the field to set the setting for
     * @param value value of the setting
     */
    public void setSetting(SettingFields field, Object value) {
        settings.put(field, value);
    }

    /**
     * Gets a JSON object representing all the settings
     * @return JSON object representing all the settings
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        for (EnumMap.Entry<SettingFields, Object> entry : settings.entrySet()) {
            SettingFields field = entry.getKey();
            Object value = entry.getValue();
            
            json.put(field.getKey(), value);
        }
        
        return json;
    }

    /**
     * Creates a Settings object with settings from the specified JSON object
     * @param json JSON object with settings
     * @return Settings object with settings from a specified JSON object
     */
    public static Settings fromJSON(JSONObject json) {
        Map<SettingFields, Object> settings = new HashMap<SettingFields, Object>();

        for (SettingFields field : SettingFields.values()) {
            String key = field.getKey();
            Object defaultValue = field.getDefaultValue();
            Object value = null;
            
            if (json.has(key)) {
                try {
                    Object tempValue = json.get(key);

                    if (tempValue.getClass() == defaultValue.getClass()) {
                        value = tempValue;
                    }
                } catch (JSONException ex) {
                        
                }
            }
            
            if (value == null) {
                value = defaultValue;
            }
            
            settings.put(field, value);
        }
        
        return new Settings(settings);
    }
    
}
