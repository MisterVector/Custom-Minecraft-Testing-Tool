package org.codespeak.cmtt.profiles;

import org.codespeak.cmtt.objects.Plugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.util.MiscUtil;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class representing a development profile
 *
 * @author Vector
 */
public class DevelopmentProfile extends Profile {

    private String lowerMemory;
    private String upperMemory;
    private String jvmFlagsString;
    private String minecraftServerArguments;
    private ServerProfile serverProfile;
    private String customServerWorldName;
    private boolean serverWorlds;
    private boolean updateOudatedPluginsAutomatically;
    private boolean updateOutdatedServerAutomatically;
    private boolean useServerGUI;
    private List<Plugin> plugins;
    
    public DevelopmentProfile(String name, String lowerMemory, String upperMemory, String jvmFlagsString,
                                    String minecraftServerArguments, ServerProfile serverProfile, String customServerWorldName,
                                    boolean serverWorlds, boolean updateOutdatedPluginsAutomatically, boolean updateOutdatedServerAutomatically,
                                    boolean useServerGUI, List<Plugin> plugins) {
        this(-1, name, lowerMemory, upperMemory, jvmFlagsString, minecraftServerArguments, serverProfile, customServerWorldName, serverWorlds, updateOutdatedPluginsAutomatically, updateOutdatedServerAutomatically, useServerGUI, plugins);
    }
    
    public DevelopmentProfile(int id, String name, String lowerMemory, String upperMemory, String jvmFlagsString,
                                     String minecraftServerArguments, ServerProfile serverProfile, String customServerWorldName,
                                     boolean serverWorlds, boolean updateOutdatedPluginsAutomatically, boolean updateOutdatedServerAutomatically,
                                     boolean useServerGUI, List<Plugin> plugins) {
        super(id, name);
        
        this.lowerMemory = lowerMemory;
        this.upperMemory = upperMemory;
        this.jvmFlagsString = jvmFlagsString;
        this.minecraftServerArguments = minecraftServerArguments;
        this.serverProfile = serverProfile;
        this.customServerWorldName = customServerWorldName;
        this.serverWorlds = serverWorlds;
        this.updateOudatedPluginsAutomatically = updateOutdatedPluginsAutomatically;
        this.updateOutdatedServerAutomatically = updateOutdatedServerAutomatically;
        this.useServerGUI = useServerGUI;
        this.plugins = plugins;
    }
    
    /**
     * Gets the lowest allocated server memory this profile is using
     * @return lowest allocated server memory this profile is using
     */
    public String getLowerMemory() {
        return lowerMemory;
    }
    
    /**
     * Sets the lowest allocated server memory this profile is using
     * @param lowerMemory lowest allocated server memory this profile is using
     */
    public void setLowerMemory(String lowerMemory) {
        this.lowerMemory = lowerMemory;
    }
    
    /**
     * Gets the highest allocated server memory this profile is using
     * @return highest allocated server memory this profile is using
     */
    public String getUpperMemory() {
        return upperMemory;
    }
    
    /**
     * Sets the highest allocated server memory this profile is using
     * @param upperMemory highest allocated server memory this profile is using
     */
    public void setUpperMemory(String upperMemory) {
        this.upperMemory = upperMemory;
    }
    
    /**
     * Gets the JVM flags used with this development profile
     * @return JVM flags used with this development profile
     */
    public String getJVMFlagsString() {
        return jvmFlagsString;
    }
    
    /**
     * Gets the JVM flags used with this development profile
     * @param jvmFlagsString JVM flags used with this development profile
     */
    public void setJVMFlagsString(String jvmFlagsString) {
        this.jvmFlagsString = jvmFlagsString;
    }
    
    /**
     * Gets the minecraft server arguments for this development profile
     * @return minecraft server arguments for this development profile
     */
    public String getMinecraftServerArguments() {
        return minecraftServerArguments;
    }
    
    /**
     * Sets the minecraft server arguments for this development profile
     * @param minecraftServerArguments minecraft server arguments for this
     * development profile
     */
    public void setMinecraftServerArguments(String minecraftServerArguments) {
        this.minecraftServerArguments = minecraftServerArguments;
    }

    /**
     * Gets the server profile used with this development profile
     * @return server profile used with this development profile
     */
    public ServerProfile getServerProfile() {
        return serverProfile;
    }
    
    /**
     * Sets the server profile used with this development profile
     * @param serverProfile server profile used with this plugin
     * development profile
     */
    public void setServerProfile(ServerProfile serverProfile) {
        this.serverProfile = serverProfile;
    }
    
    /**
     * Gets the custom server world name
     * @return custom server world name
     */
    public String getCustomServerWorldName() {
        return customServerWorldName;
    }
    
    /**
     * Sets the custom server world name
     * @param customServerWorldName custom server world name
     */
    public void setCustomServerWorldName(String customServerWorldName) {
        this.customServerWorldName = customServerWorldName;
    }
    
    /**
     * Gets if this development profile is using the server's worlds
     * @return if this development profile is using the server's worlds
     */
    public boolean isUsingServerWorlds() {
        return serverWorlds;
    }
    
    /**
     * Sets if this development profile is using the server's worlds
     * @param serverWorlds if this development profile is using the
     * server's worlds
     */
    public void setUsingServerWorlds(boolean serverWorlds) {
        this.serverWorlds = serverWorlds;
    }
    
    /**
     * Gets if this development profile is updating outdated
     * plugins automatically
     * @return if this development profile is updating outdated
     * plugins automatically
     */
    public boolean isUpdatingOutdatedPluginsAutomatically() {
        return updateOudatedPluginsAutomatically;
    }
    
    /**
     * Gets if this development profile is updating outdated
     * plugins automatically
     * @param updateOutdatedPluginsAutomatically if this development profile is
     * updating outdated plugins automatically
     */
    public void setUpdatingOutdatedPluginsAutomatically(boolean updateOutdatedPluginsAutomatically) {
        this.updateOudatedPluginsAutomatically = updateOutdatedPluginsAutomatically;
    }
    
    /**
     * Checks if the server is automatically updated if it is outdated
     * @return if the server is automatically updated if it is outdated
     */
    public boolean isUpdatingOutdatedServerAutomatically() {
        return updateOutdatedServerAutomatically;
    }
    
    /**
     * Sets if the server is automatically updated if it is outdated
     * @param updateOutdatedServerAutomatically if the server is automatically
     * updated if it is outdated
     */
    public void setUpdateOutdatedServerAutomatically(boolean updateOutdatedServerAutomatically) {
        this.updateOutdatedServerAutomatically = updateOutdatedServerAutomatically;
    }
    
    /**
     * Gets if this development profile is using the server's GUI
     * @return if this development profile is using the server's GUI
     */
    public boolean isUsingServerGUI() {
        return useServerGUI;
    }
    
    /**
     * Sets if this development profile is using the server's GUI
     * @param useServerGUI if this development profile is using the server's GUI
     */
    public void setUsingServerGUI(boolean useServerGUI) {
        this.useServerGUI = useServerGUI;
    }
    
    /**
     * Gets the plugins used by this development profile
     * @return plugins used by this development profile
     */
    public List<Plugin> getPlugins() {
        return plugins;
    }
    
    /**
     * Sets the plugins used by this development profile
     * @param plugins plugins used by this development profile
     */
    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }
    
    /**
     * Gets a copy of the plugins used by this development profile
     * @return copy of the plugins used by this development profile
     */
    public List<Plugin> copyPlugins() {
        List<Plugin> ret = new ArrayList<Plugin>();
        
        for (Plugin plugin : plugins) {
            ret.add(plugin.copy());
        }
        
        return ret;
    }
    
    /**
     * Gets the location of this development profile
     * @return location of this development profile
     */
    public Path getLocation() {
        return Paths.get(Configuration.DEVELOPMENT_FOLDER + File.separator + super.getId());
    }
    
    /**
     * Gets the world location from the specified server profile
     * @param profile server profile to get world location from
     * @return location of server world
     */
    public Path getWorldLocation(ServerProfile profile) {
        return getLocation().resolve("worlds").resolve(Integer.toString(profile.getId())).toAbsolutePath();        
    }
    
    /**
     * Gets the location of this development profile's plugins folder
     * @return location of this development profile's plugins folder
     */
    public Path getPluginsLocation() {
        return getLocation().resolve("plugins");
    }
    
    /**
     * Removes this profile and all files associated with it
     */
    public void remove() {
        Path profileLocation = getLocation();
        
        if (profileLocation.toFile().exists()) {
            try {
                Files.walk(profileLocation)
                     .sorted(Comparator.reverseOrder())
                     .map(Path::toFile)
                     .forEach(File::delete);
            } catch (IOException ex) {
                
            }
        }
    }
    
    /**
     * Converts this object to JSON
     * @return JSON object representing this object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONArray pluginsJson = new JSONArray();
        
        for (Plugin plugin : plugins) {
            pluginsJson.put(plugin.toJSON());
        }
        
        int id = super.getId();
        String name = super.getName();
        
        json.put("id", id);
        json.put("name", name);
        json.put("lower_memory", lowerMemory);
        json.put("upper_memory", upperMemory);
        json.put("jvm_flags_string", jvmFlagsString);
        json.put("minecraft_server_arguments", minecraftServerArguments);
        json.put("server_profile", serverProfile.getId());
        json.put("custom_server_world_name", customServerWorldName);
        json.put("server_worlds", serverWorlds);
        json.put("update_outdated_plugins_automatically", updateOudatedPluginsAutomatically);
        json.put("update_outdated_server_automatically", updateOutdatedServerAutomatically);
        json.put("use_server_gui", useServerGUI);
        json.put("plugins", pluginsJson);
        
        return json;
    }
    
    /**
     * Gets a DevelopmentProfile object from JSON
     * @param json JSON representation of a DevelopmentProfile object
     * @return DevelopmentProfile object represented by JSON
     */
    public static DevelopmentProfile fromJSON(JSONObject json) {
        int id = 0;
        String name = "";
        String lowerMemory = "";
        String upperMemory = "";
        String jvmFlagsString = "";
        String minecraftServerArguments = "";
        ServerProfile serverProfile = null;
        String customServerWorldName = "";
        boolean serverWorlds = false;
        boolean updateOutdatedPluginsAutomatically = false;
        boolean updateOutdatedServerAutomatically = false;
        boolean useServerGUI = false;
        List<Plugin> plugins = new ArrayList<Plugin>();
        
        if (json.has("id")) {
            id = json.getInt("id");
        }
        
        if (json.has("name")) {
            name = json.getString("name");
        }
        
        if (json.has("lower_memory")) {
            lowerMemory = json.getString("lower_memory");
        }
        
        if (json.has("upper_memory")) {
            upperMemory = json.getString("upper_memory");
        }
        
        if (json.has("jvm_flags_string")) {
            jvmFlagsString = json.getString("jvm_flags_string");
        }
        
        if (json.has("minecraft_server_arguments")) {
            minecraftServerArguments = json.getString("minecraft_server_arguments");
        }
        
        if (json.has("server_profile")) {
            serverProfile = ServerProfileHandler.getProfile(json.getInt("server_profile"));
        }
        
        if (json.has("custom_server_world_name")) {
            customServerWorldName = json.getString("custom_server_world_name");
        }
        
        if (json.has("server_worlds")) {
            serverWorlds = json.getBoolean("server_worlds");
        }
        
        if (json.has("update_outdated_plugins_automatically")) {
            updateOutdatedPluginsAutomatically = json.getBoolean("update_outdated_plugins_automatically");
        }
        
        if (json.has("update_outdated_server_automatically")) {
            updateOutdatedServerAutomatically = json.getBoolean("update_outdated_server_automatically");
        }
        
        if (json.has("use_server_gui")) {
            useServerGUI = json.getBoolean("use_server_gui");
        }
        
        if (json.has("plugins")) {
            JSONArray pluginsJson = json.getJSONArray("plugins");
            
            for (int i = 0; i < pluginsJson.length(); i++) {
                JSONObject obj = pluginsJson.getJSONObject(i);
                plugins.add(Plugin.fromJSON(obj));
            }
        }
        
        return new DevelopmentProfile(id, name, lowerMemory, upperMemory, jvmFlagsString, minecraftServerArguments,
                                            serverProfile, customServerWorldName, serverWorlds, updateOutdatedPluginsAutomatically,
                                            updateOutdatedServerAutomatically, useServerGUI, plugins);
    }
    
}
