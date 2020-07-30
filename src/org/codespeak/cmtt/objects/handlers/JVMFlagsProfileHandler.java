package org.codespeak.cmtt.objects.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.cmtt.profiles.JVMFlagsProfile;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class that handles data for JVM flag profiles
 *
 * @author Vector
 */
public class JVMFlagsProfileHandler {
    
    private static List<JVMFlagsProfile> profiles = new ArrayList<JVMFlagsProfile>();
    private static int nextID = 1;
    
    /**
     * Creates a new JVM flags profile and returns it
     * @param name name of the JVM flags profile
     * @param flagsString JVM flags string
     * @return newly created JVM flags profile object
     */
    public static JVMFlagsProfile addProfile(String name, String flagsString) {
        JVMFlagsProfile profile = new JVMFlagsProfile(nextID, name, flagsString);
        profiles.add(profile);
        nextID++;

        return profile;
    }
    
    /**
     * Gets a JVM flags profile by its ID
     * @param ID ID of JVM flags profile
     * @return JVM flags profile represented by its ID
     */
    public static JVMFlagsProfile getProfile(int ID) {
        for (JVMFlagsProfile profile : profiles) {
            if (profile.getId() == ID) {
                return profile;
            }
        }
        
        return null;
    }
    
    /**
     * Removes a JVM flags profile by its ID
     * @param ID ID of the JVM Flags profile
     */
    public static void deleteProfile(int ID) {
        for (Iterator<JVMFlagsProfile> it = profiles.iterator(); it.hasNext();) {
            JVMFlagsProfile profile = it.next();
            
            if (profile.getId() == ID) {
                it.remove();
            }
        }
    }
    
    /**
     * Gets an unmodifiable list of all profiles
     * @return unmodifiable list of all profiles
     */
    public static List<JVMFlagsProfile> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }
    
    /**
     * Saves profile data to JSON
     * @param dataJson JSON object representing a data file
     */
    public static void saveProfilesToJSON(JSONObject dataJson) {
        if (!dataJson.has("next_ids")) {
            JSONObject nextIDsJson = new JSONObject();

            nextIDsJson.put("jvm_flags", nextID);
            dataJson.put("next_ids", nextIDsJson);
        } else {
            JSONObject nextIDsJson = dataJson.getJSONObject("next_ids");
            nextIDsJson.put("jvm_flags", nextID);
        }
        
        JSONArray profilesJson = new JSONArray();
        
        for (JVMFlagsProfile profile : profiles) {
            profilesJson.put(profile.toJSON());
        }
        
        dataJson.put("jvm_flags", profilesJson);
    }
    
    /**
     * Loads profile data from JSON
     * @param dataJson JSON object representing a data file
     */
    public static void loadProfilesFromJSON(JSONObject dataJson) {
        if (dataJson.has("next_ids")) {
            JSONObject jsonIDs = dataJson.getJSONObject("next_ids");
            
            if (jsonIDs.has("jvm_flags")) {
                nextID = jsonIDs.getInt("jvm_flags");
            }
        }
        
        if (dataJson.has("jvm_flags")) {
            JSONArray jsonJVMFlags = dataJson.getJSONArray("jvm_flags");
            
            for (int i = 0; i < jsonJVMFlags.length(); i++) {
                JSONObject data = jsonJVMFlags.getJSONObject(i);
                JVMFlagsProfile profile = JVMFlagsProfile.fromJSON(data);
                profiles.add(profile);
            }
        }
    }
    
}
