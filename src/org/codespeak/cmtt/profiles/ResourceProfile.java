package org.codespeak.cmtt.profiles;

/**
 * An abstract class representing a resource profile. A resource profile
 * is a profile in which a resource is involved. This resource needs
 * methods to finish setup, update, and remove
 *
 * @author Vector
 */
public abstract class ResourceProfile extends Profile {
    
    public ResourceProfile(int ID, String name) {
        super(ID, name);
    }
    
    /**
     * Finishes setting up this resource profile
     */
    public abstract void finishSetup();
    
    /**
     * Updates this resource profile
     */
    public abstract void update();
    
    /**
     * Removes this resource profile
     */
    public abstract void remove();

}
