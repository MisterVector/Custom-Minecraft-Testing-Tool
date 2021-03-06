package org.codespeak.cmtt.profiles;

/**
 * A abstract class representing a profile
 *
 * @author Vector
 */
public abstract class Profile {

    private int id;
    private String name;
    
    public Profile(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    /**
     * Gets the ID of this profile
     * @return ID of this profile
     */
    public int getId() {
        return id;
    }
    
   /**
   * Sets the ID of this profile
   * @param id ID of this profile
   */
   public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the name of this profile
     * @return name of this profile
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of this profile
     * @param name name of this profile
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
