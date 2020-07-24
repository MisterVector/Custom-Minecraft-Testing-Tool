package org.codespeak.cmtt.objects.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.codespeak.cmtt.objects.CustomServerType;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class that handles data for custom server types
 *
 * @author Vector
 */
public class CustomServerTypeHandler {

    private static List<CustomServerType> customServerTypes = new ArrayList<CustomServerType>();
    private static int nextID = 1;
    
    /**
     * Creates a new CustomServerType and returns it
     * @param serverName the name of the custom server type
     * @param pluginsArgument argument to change the plugins folder
     * @param worldsArgument argument to change the worlds folder
     * @return CustomServerType object that was just created
     */
    public static CustomServerType createCustomServerType(String serverName, String pluginsArgument, String worldsArgument) {
        CustomServerType customServerType = new CustomServerType(nextID, serverName, pluginsArgument, worldsArgument);
        customServerTypes.add(customServerType);
        nextID++;
        
        return customServerType;
    }
    
    /**
     * Gets a CustomServerType object from its ID
     * @param id ID of a custom server type
     * @return CustomServerType object represented by its ID
     */
    public static CustomServerType fromID(int id) {
        for (CustomServerType customServerType : customServerTypes) {
            if (customServerType.getId() == id) {
                return customServerType;
            }
        }
        
        return null;
    }
 
    /**
     * Deletes a custom server type by its ID
     * @param ID ID of custom server type
     */
    public static void deleteCustomServerType(int ID) {
        for (Iterator<CustomServerType> it = customServerTypes.iterator(); it.hasNext();) {
            CustomServerType customServerType = it.next();
            
            if (customServerType.getId()== ID) {
                it.remove();
            }
        }
    }
    
    /**
     * Gets an unmodifiable list of all custom server types
     * @return unmodifiable list of all custom server types
     */
    public static List<CustomServerType> getCustomServerTypes() {
        return Collections.unmodifiableList(customServerTypes);
    }
    
    /**
     * Loads custom server types from JSON
     * @param dataJson JSON representing data from data.json
     */
    public static void loadCustomServerTypesFromJSON(JSONObject dataJson) {
        if (dataJson.has("next_ids")) {
            JSONObject nextIDsJson = dataJson.getJSONObject("next_ids");
            
            if (nextIDsJson.has("custom_server_type")) {
                nextID = nextIDsJson.getInt("custom_server_type");
            }
        }

        if (dataJson.has("custom_server_types")) {
            JSONArray customServerTypesJson = dataJson.getJSONArray("custom_server_types");

            System.out.println(customServerTypesJson.length()
            );
            
            for (int i = 0; i < customServerTypesJson.length(); i++) {
                JSONObject customServerTypeJson = customServerTypesJson.getJSONObject(i);
                customServerTypes.add(CustomServerType.fromJSON(customServerTypeJson));
            }
        }
    }
    
    /**
     * Saves custom server types to JSON
     * @param dataJson JSON representing data for data.json
     */
    public static void saveCustomServerTypesToJSON(JSONObject dataJson) {
        if (dataJson.has("next_ids")) {
            JSONObject nextIDsJson = dataJson.getJSONObject("next_ids");
            nextIDsJson.put("custom_server_type", nextID);
        } else {
            JSONObject nextIDsJson = new JSONObject();
            nextIDsJson.put("custom_server_type", nextID);
            dataJson.put("next_ids", nextIDsJson);
        }
        
        JSONArray customServerTypesJson = new JSONArray();
        
        for (CustomServerType customServerType : customServerTypes) {
            customServerTypesJson.put(customServerType.toJSON());
        }
        
        dataJson.put("custom_server_types", customServerTypesJson);
    }
    
}
