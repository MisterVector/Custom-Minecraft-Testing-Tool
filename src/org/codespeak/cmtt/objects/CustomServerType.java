package org.codespeak.cmtt.objects;

import org.json.JSONObject;

/**
 * A class representing a custom server type. A custom server type is a server
 * type that is not supported out-of-the-box with this program. It contains
 * enough information that is required by the tool when starting up a
 * custom server.
 *
 * @author Vector
 */
public class CustomServerType {
    
    private final int id;
    private String serverName;
    private String pluginsArgument;
    private String worldsArgument;
    
    public CustomServerType(int id, String serverName, String pluginsArgument, String worldsArgument) {
        this.id = id;
        this.serverName = serverName;
        this.pluginsArgument = pluginsArgument;
        this.worldsArgument = worldsArgument;
    }
    
    /**
     * Gets the ID of this custom server type
     * @return ID of this custom server type
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of this custom server type
     * @return name of this custom server type
     */
    public String getName() {
        return serverName;
    }
    
    /**
     * Sets the name of this custom server type
     * @param serverName name of this custom server type
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    /**
     * Gets the name of the argument to change the plugins folder
     * @return name of the argument to change the plugins folder
     */
    public String getPluginsArgument() {
        return pluginsArgument;
    }
    
    /**
     * Sets the name of the argument to change the plugins folder
     * @param pluginsArgument name of the argument to change the plugins folder
     */
    public void setPluginsArgument(String pluginsArgument) {
        this.pluginsArgument = pluginsArgument;
    }
    
    /**
     * Gets the name of the argument to change the worlds folder
     * @return name of the argument to change the worlds folder
     */
    public String getWorldsArgument() {
        return worldsArgument;
    }

    /**
     * Sets the name of the argument to change the worlds folder
     * @param worldsArgument name of the argument to change the worlds folder
     */
    public void setWorldsArgument(String worldsArgument) {
        this.worldsArgument = worldsArgument;
    }
    
    /**
     * Converts this CustomServerType object to JSON
     * @return JSON representation of this CustomServerType object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("id", id);
        json.put("server_name", serverName);
        json.put("plugins_argument", pluginsArgument);
        json.put("worlds_argument", worldsArgument);
        
        return json;
    }
    
    /**
     * Creates a CustomServerType object from JSON
     * @param json JSON to convert to a CustomServerType object
     * @return CustomServerType object created from JSON
     */
    public static CustomServerType fromJSON(JSONObject json) {
        int id = 0;
        String serverName = "";
        String pluginsArgument = "";
        String worldsArgument = "";
        
        if (json.has("id")) {
            id = json.getInt("id");
        }
        
        if (json.has("server_name")) {
            serverName = json.getString("server_name");
        }
        
        if (json.has("plugins_argument")) {
            pluginsArgument = json.getString("plugins_argument");
        }
        
        if (json.has("worlds_argument")) {
            worldsArgument = json.getString("worlds_argument");
        }
        
        return new CustomServerType(id, serverName, pluginsArgument, worldsArgument);
    }
    
}
