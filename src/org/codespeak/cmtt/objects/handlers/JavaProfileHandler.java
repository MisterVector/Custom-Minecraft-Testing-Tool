package org.codespeak.cmtt.objects.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.cmtt.profiles.JavaProfile;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A handler for java profiles
 *
 * @author Vector
 */
public class JavaProfileHandler {
    
    private static List<JavaProfile> javaProfiles = new ArrayList<JavaProfile>();
    private static int nextID = 1;
    
    /**
     * Adds a java profile to the profiles list
     * @param profile java profile to add
     */
    public static void addProfile(JavaProfile profile) {
        profile.setId(nextID);

        nextID++;
        javaProfiles.add(profile);
    }
    
    /**
     * Gets a java profile by its name
     * @param ID ID of java profile
     * @return java profile by its name
     */
    public static JavaProfile getProfile(int ID) {
        for (JavaProfile profile : javaProfiles) {
            if (profile.getId() == ID) {
                return profile;
            }
        }
        
        return null;
    }

    /**
     * Gets a java profile by its name
     * @param name name of java profile
     * @return java profile by its name
     */
    public static JavaProfile getProfile(String name) {
        for (JavaProfile profile : javaProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        
        return null;
    }

    /**
     * Gets an unmodifiable map of all java profiles
     * @return unmodifiable map of all java profiles
     */
    public static List<JavaProfile> getProfiles() {
        return Collections.unmodifiableList(javaProfiles);
    }
    
    /**
     * Deletes a java profile by its name
     * @param name name of java profile
     */
    public static void deleteProfile(String name) {
        for (Iterator<JavaProfile> it = javaProfiles.iterator(); it.hasNext();) {
            JavaProfile profile = it.next();
            
            if (profile.getName().equalsIgnoreCase(name)) {
                it.remove();
            }
        }
    }
    
    /**
     * Saves profiles to JSON
     * @param dataJson JSON object representing a data files
     */
    public static void saveProfilesToJson(JSONObject dataJson) {
        if (!dataJson.has("next_ids")) {
            JSONObject nextIDsJson = new JSONObject();

            nextIDsJson.put("java_profiles", nextID);
            dataJson.put("next_ids", nextIDsJson);
        } else {
            JSONObject nextIDsJson = dataJson.getJSONObject("next_ids");
            nextIDsJson.put("java_profiles", nextID);
        }
        
        JSONArray profilesJson = new JSONArray();
        
        for (JavaProfile profile : javaProfiles) {
            profilesJson.put(profile.toJSON());
        }
        
        dataJson.put("java_profiles", profilesJson);
    }
    
    /**
     * Loads profiles from JSON
     * @param json JSON object representing a data file
     */
    public static void loadProfilesFromJson(JSONObject json) {
        if (json.has("next_ids")) {
            JSONObject nextIDsJson = json.getJSONObject("next_ids");
            
            if (nextIDsJson.has("java_profiles")) {
                nextID = nextIDsJson.getInt("java_profiles");
            }
        }
        
        if (json.has("java_profiles")) {
            JSONArray javaProfilesJson = json.getJSONArray("java_profiles");
            
            for (int i = 0; i < javaProfilesJson.length(); i++) {
                JSONObject obj = javaProfilesJson.getJSONObject(i);
                javaProfiles.add(JavaProfile.fromJSON(obj));
            }
        }
    }
    
}
