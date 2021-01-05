package org.codespeak.cmtt.objects.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class that handles data for server profiles
 *
 * @author Vector
 */
public class ServerProfileHandler {

    private static List<ServerProfile> serverProfiles = new ArrayList<ServerProfile>();
    private static int nextID = 1;
    
    /**
     * Adds a server profile to the list
     * @param profile profile to add
     */
    public static void addProfile(ServerProfile profile) {
        profile.setId(nextID);

        serverProfiles.add(profile);
        nextID++;
    }
    
    /**
     * Gets a server profile from its ID
     * @param id ID of server profile
     * @return server profile from its ID
     */
    public static ServerProfile getProfile(int id) {
        for (ServerProfile profile : serverProfiles) {
            if (profile.getId() == id) {
                return profile;
            }
        }
        
        return null;
    }
    
    /**
     * Gets a server profile by its name
     * @param name name of server profile
     * @return server profile
     */
    public static ServerProfile getProfile(String name) {
        for (ServerProfile profile : serverProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        
        return null;
    }

    /**
     * Deletes a server profile from its ID
     * @param id ID of server profile
     * @return Server Profile object of deleted profile
     */
    public static ServerProfile deleteProfile(int id) {
        for (Iterator<ServerProfile> it = serverProfiles.iterator(); it.hasNext();) {
            ServerProfile profile = it.next();
            
            if (profile.getId() == id) {
                it.remove();
                return profile;
            }
        }
        
        return null;
    }
    
    /**
     * Gets an unmodifiable list of all server profiles
     * @return unmodifiable list of all server profiles
     */
    public static List<ServerProfile> getProfiles() {
        return Collections.unmodifiableList(serverProfiles);
    }
    
    /**
     * Load server profiles from JSON
     * @param json JSON representing data from data.json
     */
    public static void loadProfilesFromJSON(JSONObject json) {
        if (json.has("next_ids")) {
            JSONObject nextIDsJson = json.getJSONObject("next_ids");
            
            if (nextIDsJson.has("server_profile")) {
                nextID = nextIDsJson.getInt("server_profile");
            }
        }
        
        if (json.has("server_profiles")) {
            JSONArray serverProfilesJson = json.getJSONArray("server_profiles");
            
            for (int i = 0; i < serverProfilesJson.length(); i++) {
                JSONObject obj = serverProfilesJson.getJSONObject(i);
                serverProfiles.add(ServerProfile.fromJSON(obj));
            }
        }
    }

    /**
     * Saves server profiles to JSON
     * @param json JSON representing data for data.json
     */
    public static void saveProfilesToJSON(JSONObject json) {
        if (json.has("next_ids")) {
            JSONObject nextIDsJson = json.getJSONObject("next_ids");
            nextIDsJson.put("server_profile", nextID);
        } else {
            JSONObject nextIDsJson = new JSONObject();
            nextIDsJson.put("server_profile", nextID);
            
            json.put("next_ids", nextIDsJson);
        }
    
        JSONArray serverProfilesJson = new JSONArray();
        
        for (ServerProfile profile : serverProfiles) {
            serverProfilesJson.put(profile.toJSON());
        }
        
        json.put("server_profiles", serverProfilesJson);
    }
    
}
