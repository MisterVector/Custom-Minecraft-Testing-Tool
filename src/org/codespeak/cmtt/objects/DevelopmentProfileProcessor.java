package org.codespeak.cmtt.objects;

import org.codespeak.cmtt.profiles.DevelopmentProfile;

/**
 * An interface marking a development profile processor
 *
 * @author Vector
 */
public interface DevelopmentProfileProcessor {
    
    /**
     * Processes a development profile
     * @param profile development profile to be processed
     */
    default void processDevelopmentProfile(DevelopmentProfile profile) {
        
    }
    
    /**
     * Processes a development profile
     * @param profile development profile to be processed
     * @param editMode whether the profile is being edited
     */
    default void processDevelopmentProfile(DevelopmentProfile profile, boolean editMode) {
        
    }
    
    public ProcessorContext getContext();
    
}
