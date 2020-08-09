package org.codespeak.cmtt.objects.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.profiles.PluginDevelopmentProfile;
import org.codespeak.cmtt.profiles.Plugin;
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
    
    // TODO: Don't do this. Instead, pass in a DevelopmentProfile object and set its ID later
    public static PluginDevelopmentProfile addPluginDevelopmentProfile(String name, String lowerMemory, String upperMemory, String jvmFlagsString,
                                                   ServerProfile serverProfile, boolean separateWorlds, List<Plugin> pluginProfiles) {
        PluginDevelopmentProfile profile = new PluginDevelopmentProfile(nextDevelopmentProfileID, name, lowerMemory, upperMemory, jvmFlagsString,
                                                                        serverProfile, separateWorlds, pluginProfiles);
        nextDevelopmentProfileID++;
        
        developmentProfiles.add(profile);
        
        return profile;
    }
    
    /**
     * Gets a development profile by its ID
     * @param ID ID of development profile
     * @return development profile represented by its ID
     */
    public static DevelopmentProfile fromID(int ID) {
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            if (developmentProfile.getId() == ID) {
                return developmentProfile;
            }
        }
        
        return null;
    }
    
    public static DevelopmentProfile deleteProfile(int ID) {
        return null;
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
        
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            pluginDevelopmentProfilesJson.put(developmentProfile.toJSON());
            
        }
        
        json.put("plugin_development_profiles", pluginDevelopmentProfilesJson);
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
    }
    
}
