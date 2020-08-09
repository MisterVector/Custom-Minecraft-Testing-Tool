package org.codespeak.cmtt.profiles;

import org.json.JSONObject;

/**
 * An abstract class representing a development profile
 *
 * @author Vector
 */
public abstract class DevelopmentProfile extends Profile {
    
    private String lowerMemory;
    private String upperMemory;
    private String jvmFlagsString;
    
    public DevelopmentProfile(int id, String name, String lowerMemory, String upperMemory,
                              String jvmFlagsString) {
        super(id, name);
        
        this.lowerMemory = lowerMemory;
        this.upperMemory = upperMemory;
        this.jvmFlagsString = jvmFlagsString;
    }
    
    /**
     * Gets the lowest allocated server memory this profile is using
     * @return lowest allocated server memory this profile is using
     */
    public String getLowerMemory() {
        return lowerMemory;
    }
    
    /**
     * Sets the lowest allocated server memory this profile is using
     * @param lowerMemory lowest allocated server memory this profile is using
     */
    public void setLowerMemory(String lowerMemory) {
        this.lowerMemory = lowerMemory;
    }
    
    /**
     * Gets the highest allocated server memory this profile is using
     * @return highest allocated server memory this profile is using
     */
    public String getUpperMemory() {
        return upperMemory;
    }
    
    /**
     * Sets the highest allocated server memory this profile is using
     * @param upperMemory highest allocated server memory this profile is using
     */
    public void setUpperMemory(String upperMemory) {
        this.upperMemory = upperMemory;
    }
    
    /**
     * Gets the JVM flags used with this development profile
     * @return JVM flags used with this development profile
     */
    public String getJVMFlagsString() {
        return jvmFlagsString;
    }
    
    /**
     * Gets the JVM flags used with this development profile
     * @param jvmFlagsString JVM flags used with this development profile
     */
    public void setJVMFlagsString(String jvmFlagsString) {
        this.jvmFlagsString = jvmFlagsString;
    }
    
    /**
     * Gets the development type of this development profile
     * @return development type of this development profile
     */
    public abstract DevelopmentType getDevelopmentType();
    
    /**
     * Converts this DevelopmentProfile object to JSON
     * @return JSON representation of this DevelopmentProfile object
     */
    public abstract JSONObject toJSON();
}
