package org.codespeak.cmtt.objects;

/**
 * An enum that lists various error types
 *
 * @author Vector
 */
public enum ErrorType {
    
    ERROR_SEVERE(1),
    ERROR_WARNING(2),
    ERROR_PROGRAM(3);
    
    private final int id;
    
    private ErrorType(int id) {
        this.id = id;
    }
    
    /**
     * Gets the ID of this error type
     * @return ID of this error type
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets an error type from its ID
     * @param id ID of error type
     * @return error type from its ID
     */
    public static ErrorType fromId(int id) {
        for (ErrorType et : values()) {
            if (et.getId() == id) {
                return et;
            }
        }
        
        return null;
    }
    
}
