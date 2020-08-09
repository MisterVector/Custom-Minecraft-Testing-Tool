package org.codespeak.cmtt.profiles;

/**
 * An enum with a list of development types
 *
 * @author Vector
 */
public enum DevelopmentType {
    
    PLUGIN(1);
    
    private final int typeId;
    
    private DevelopmentType(int typeId) {
        this.typeId = typeId;
    }
    
    /**
     * Gets the ID of this development type
     * @return ID of this development type
     */
    public int getId() {
        return typeId;
    }
    
    /**
     * Gets a development type by its ID
     * @param ID ID of development type
     * @return development type by its ID
     */
    public static DevelopmentType fromID(int ID) {
        for (DevelopmentType dpt : values()) {
            if (dpt.getId() == ID) {
                return dpt;
            }
        }
        
        return null;
    }
    
}
