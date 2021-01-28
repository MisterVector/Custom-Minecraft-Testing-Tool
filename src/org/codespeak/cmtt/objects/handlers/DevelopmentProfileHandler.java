package org.codespeak.cmtt.objects.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class representing a handler for development profiles
 *
 * @author Vector
 */
public class DevelopmentProfileHandler {
    
    private static List<DevelopmentProfile> developmentProfiles = new ArrayList<DevelopmentProfile>();
    private static int nextDevelopmentProfileID = 1;
    private static int nextPluginID = 1;
    
    /**
     * Adds a development profile to the list
     * @param profile profile to add
     */
    public static void addDevelopmentProfile(DevelopmentProfile profile) {
        profile.setId(nextDevelopmentProfileID);
        
        nextDevelopmentProfileID++;
        developmentProfiles.add(profile);
    }
    
    /**
     * Gets a development profile by its ID
     * @param ID ID of development profile
     * @return development profile represented by its ID
     */
    public static DevelopmentProfile getProfile(int ID) {
        for (DevelopmentProfile profile : developmentProfiles) {
            if (profile.getId() == ID) {
                return profile;
            }
        }
        
        return null;
    }
    
    /**
     * Gets a development profile by name
     * @param name name of development profile
     * @return development profile
     */
    public static DevelopmentProfile getProfile(String name) {
        for (DevelopmentProfile profile : developmentProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        
        return null;
    }

    /**
     * Gets a list of development profiles using the specified server profile
     * @param serverProfile server profile to check for each development profile
     * @return list of development profiles using the specified server profile
     */
    public static List<DevelopmentProfile> getProfilesUsingServerProfile(ServerProfile serverProfile) {
        List<DevelopmentProfile> ret = new ArrayList<DevelopmentProfile>();
        
        for (DevelopmentProfile profile : developmentProfiles) {
            ServerProfile currentServerProfile = profile.getServerProfile();
            
            if (currentServerProfile.equals(serverProfile)) {
                ret.add(profile);
            }
        }
        
        return ret;
    }
    
    /**
     * Deletes the specified development profile by its ID
     * @param ID ID of development profile
     */
    public static void deleteProfile(int ID) {
        for (Iterator<DevelopmentProfile> it = developmentProfiles.iterator(); it.hasNext();) {
            DevelopmentProfile profile = it.next();
            
            if (profile.getId() == ID) {
                it.remove();
            }
        }
    }
    
    /**
     * Gets an unmodifiable list of all development profiles
     * @return unmodifiable list of all development profiles
     */
    public static List<DevelopmentProfile> getProfiles() {
        return Collections.unmodifiableList(developmentProfiles);
    }
    
    /**
     * Gets the next plugin ID and increments it
     * @return next plugin ID
     */
    public static int getNextPluginID() {
        return nextPluginID++;
    }

    /**
     * Saves profiles to JSON
     * @param json JSON object representing a data file
     */
    public static void saveProfilesToJSON(JSONObject json) {
        JSONObject nextIDsJson = new JSONObject();
        
        if (json.has("next_ids")) {
            nextIDsJson = json.getJSONObject("next_ids");
        } else {
            json.put("next_ids", nextIDsJson);
        }

        nextIDsJson.put("development_profile", nextDevelopmentProfileID);
        nextIDsJson.put("plugin", nextPluginID);

        JSONArray developmentProfilesJson = new JSONArray();
        
        for (DevelopmentProfile profile : developmentProfiles) {
            developmentProfilesJson.put(profile.toJSON());
        }
        
        json.put("development_profiles", developmentProfilesJson);
    }
    
    /**
     * Loads profiles from JSON
     * @param json JSON object representing a data file
     */
    public static void loadProfilesFromJSON(JSONObject json) {
        if (json.has("next_ids")) {
            JSONObject nextIDsJson = json.getJSONObject("next_ids");
            
            if (nextIDsJson.has("development_profile")) {
                nextDevelopmentProfileID = nextIDsJson.getInt("development_profile");
            }
            
            if (nextIDsJson.has("plugin")) {
                nextPluginID = nextIDsJson.getInt("plugin");
            }
        }
        
        if (json.has("development_profiles")) {
            JSONArray developmentProfilesJson = json.getJSONArray("development_profiles");
            
            for (int i = 0; i < developmentProfilesJson.length(); i++) {
                JSONObject obj = developmentProfilesJson.getJSONObject(i);
                developmentProfiles.add(DevelopmentProfile.fromJSON(obj));
            }
        }
    }
    
}
