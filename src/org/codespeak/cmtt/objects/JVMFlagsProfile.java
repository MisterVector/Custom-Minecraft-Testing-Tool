package org.codespeak.cmtt.objects;

import org.json.JSONObject;

/**
 * A class representing a JVM flags profile
 *
 * @author Vector
 */
public class JVMFlagsProfile extends Profile {

    private String flagsString;
    
    public JVMFlagsProfile(int id, String name, String flagsString) {
        super(id, name);
        
        this.flagsString = flagsString;
    }
    
    /**
     * Gets the flags string for this JVM flags profile
     * @return flags string for this JVM flags profile
     */
    public String getFlagsString() {
        return flagsString;
    }
    
    /**
     * Sets the flags string for this JVM flags profile
     * @param flagsString flags string for this JVM flags profile
     */
    public void setFlagsString(String flagsString) {
        this.flagsString = flagsString;
    }
    
    /**
     * Gets a JSON object from this class
     * @return JSON representation of this class
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        int id = super.getId();
        String name = super.getName();
        
        json.put("id", id);
        json.put("name", name);
        json.put("flags_string", flagsString);
        
        return json;
    }

    /**
     * Gets a JVMFlagsProfile object from JSON
     * @param json JSON representation of a JVMFlagsProfile object
     * @return JVMFlagsProfile object represented by JSON
     */
    public static JVMFlagsProfile fromJSON(JSONObject json) {
        int id = 0;
        String name = "";
        String flagsString = "";
        
        if (json.has("id")) {
            id = json.getInt("id");
        }
        
        if (json.has("name")) {
            name = json.getString("name");
        }
        
        if (json.has("flags_string")) {
            flagsString = json.getString("flags_string");
        }
        
        return new JVMFlagsProfile(id, name, flagsString);
    }
    
}
