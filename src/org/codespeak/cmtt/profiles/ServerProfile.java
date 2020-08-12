package org.codespeak.cmtt.profiles;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.objects.ServerTypes;
import org.json.JSONObject;

/**
 * A class representing a server profile
 *
 * @author Vector
 */
public class ServerProfile extends Profile {
   
    private String minecraftVersion;
    private ServerTypes serverType;
    private String customPluginsArgument;
    private String customWorldsArgument;
    private Path updatePath;
    private boolean autoUpdate;
    
    public ServerProfile(String name, String minecraftVersion, ServerTypes serverType,
                         String customPluginsArgument, String customWorldsArgument, Path updatePath,
                         boolean autoUpdate) {
        this(-1, name, minecraftVersion, serverType, customPluginsArgument, customWorldsArgument, updatePath, autoUpdate);
    }
    
    public ServerProfile(int id, String name, String minecraftVersion, ServerTypes serverType,
                         String customPluginsArgument, String customWorldsArgument, Path updatePath,
                         boolean autoUpdate) {
        super(id, name);

        this.minecraftVersion = minecraftVersion;
        this.updatePath = updatePath;
        this.autoUpdate = autoUpdate;
        this.serverType = serverType;
        this.customPluginsArgument = customPluginsArgument;
        this.customWorldsArgument = customWorldsArgument;
    }
    
    /**
     * Gets the minecraft version this server is built for
     * @return minecraft version this server is built for
     */
    public String getMinecraftVersion() {
        return minecraftVersion;
    }
    
    /**
     * Sets the minecraft version this server is built for
     * @param minecraftVersion minecraft version this server is built for
     */
    public void setMinecraftVersion(String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
    }
    
    /**
     * Gets the server type of this server profile
     * @return server type of this server profile
     */
    public ServerTypes getServerType() {
        return serverType;
    }
    
    /**
     * Sets the server type of this server profile
     * @param serverType server type of this server profile
     */
    public void setServerType(ServerTypes serverType) {
        this.serverType = serverType;
    }
    
    /**
     * Gets the name of the custom argument for changing the plugins folder
     * @return name of the custom argument for changing the plugins folder
     */
    public String getCustomPluginsArgument() {
        return customPluginsArgument;
    }
    
    /**
     * Sets the name of the custom argument for changing the plugins folder
     * @param customPluginsArgument name of the custom argument for changing the
     * plugins folder
     */
    public void setCustomPluginsArgument(String customPluginsArgument) {
        this.customPluginsArgument = customPluginsArgument;
    }
    
    /**
     * Gets the name of the custom argument for changing the worlds folder
     * @return name of the custom argument for changing the worlds folder
     */
    public String getCustomWorldsArgument() {
        return customWorldsArgument;
    }

    /**
     * Sets the name of the custom argument for changing the worlds folder
     * @param customWorldsArgument name of the custom argument for changing the
     * worlds folder
     */
    public void setCustomWorldsArgument(String customWorldsArgument) {
        this.customWorldsArgument = customWorldsArgument;
    }
    
    /**
     * Gets the path this server will be updated from
     * @return path this server will be updated from
     */
    public Path getUpdatePath() {
        return updatePath;
    }
    
    /**
     * Sets the path this server will be updated from
     * @param updatePath path this server will be updated from
     */
    public void setUpdatePath(Path updatePath) {
        this.updatePath = updatePath;
    }
    
    /**
     * Gets if this server is being auto-updated
     * @return if this server is being auto-updated
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }
    
    /**
     * Gets if this server is being auto-updated
     * @param autoUpdate if this server is being auto-updated
     */
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
    
    /**
     * Gets the path to the folder for this server profile
     * @return path to the folder for this server profile
     */
    public Path getProfileLocation() {
        return Paths.get(Configuration.SERVERS_FOLDER + File.separator + super.getId());
    }
    
    /**
     * Gets the path to the server.jar file for this server profile
     * @return path to the server.jar file for this server profile
     */
    public Path getServerLocation() {
        return getProfileLocation().resolve("server.jar");
    }
    
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        int id = super.getId();
        String name = super.getName();
        
        json.put("id", id);
        json.put("name", name);
        json.put("minecraft_version", minecraftVersion);
        json.put("server_type", serverType.getName());
        json.put("custom_plugins_argument", customPluginsArgument);
        json.put("custom_worlds_argument", customWorldsArgument);
        json.put("update_path", (updatePath != null ? updatePath.toString() : ""));
        json.put("auto_update", autoUpdate);
        
        return json;
    }
    
    public static ServerProfile fromJSON(JSONObject json) {
        int id = 0;
        String name = "";
        String minecraftVersion = "";
        ServerTypes serverType = ServerTypes.BUKKIT;
        String customPluginsArgument = "";
        String customWorldsArgument = "";
        Path updatePath = null;
        boolean autoUpdate = false;
        
        if (json.has("id")) {
            id = json.getInt("id");
        }
        
        if (json.has("name")) {
            name = json.getString("name");
        }
        
        if (json.has("minecraft_version")) {
            minecraftVersion = json.getString("minecraft_version");
        }
        
        if (json.has("server_type")) {
            serverType = ServerTypes.fromName(json.getString("server_type"));
        }
        
        if (json.has("custom_plugins_argument")) {
            customPluginsArgument = json.getString("custom_plugins_argument");
        }
        
        if (json.has("custom_worlds_argument")) {
            customWorldsArgument = json.getString("custom_worlds_argument");
        }
        
        if (json.has("update_path")) {
            updatePath = Paths.get(json.getString("update_path"));
        }
        
        if (json.has("auto_update")) {
            autoUpdate = json.getBoolean("auto_update");
        }
        
        return new ServerProfile(id, name, minecraftVersion, serverType, customPluginsArgument,
                                 customWorldsArgument, updatePath, autoUpdate);
    }
    
}
