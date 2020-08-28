package org.codespeak.cmtt.objects.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.profiles.PluginDevelopmentProfile;
import org.codespeak.cmtt.profiles.Plugin;
import org.codespeak.cmtt.profiles.ServerDevelopmentProfile;
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
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            if (developmentProfile.getId() == ID) {
                return developmentProfile;
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
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            if (developmentProfile.getName().equalsIgnoreCase(name)) {
                return developmentProfile;
            }
        }
        
        return null;
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

        JSONArray pluginDevelopmentProfilesJson = new JSONArray();
        JSONArray serverDevelopmentProfilesJson = new JSONArray();
        
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            JSONObject profileJson = developmentProfile.toJSON();
            
            switch (developmentProfile.getDevelopmentType()) {
                case PLUGIN:
                    pluginDevelopmentProfilesJson.put(profileJson);
                    
                    break;
                case SERVER:
                    serverDevelopmentProfilesJson.put(profileJson);
                    
                    break;
            }
        }
        
        json.put("plugin_development_profiles", pluginDevelopmentProfilesJson);
        json.put("server_development_profiles", serverDevelopmentProfilesJson);
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
        
        if (json.has("plugin_development_profiles")) {
            JSONArray pluginDevelopmentProfilesJson = json.getJSONArray("plugin_development_profiles");
            
            for (int i = 0; i < pluginDevelopmentProfilesJson.length(); i++) {
                JSONObject obj = pluginDevelopmentProfilesJson.getJSONObject(i);
                developmentProfiles.add(PluginDevelopmentProfile.fromJSON(obj));
            }
        }
        
        if (json.has("server_development_profiles")) {
            JSONArray serverDevelopmentProfilesJson = json.getJSONArray("server_development_profiles");
            
            for (int i = 0; i < serverDevelopmentProfilesJson.length(); i++) {
                JSONObject obj = serverDevelopmentProfilesJson.getJSONObject(i);
                developmentProfiles.add(ServerDevelopmentProfile.fromJSON(obj));
            }
        }
    }
    
}
