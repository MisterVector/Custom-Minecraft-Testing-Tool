package org.codespeak.cmtt.profiles;

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
public class DevelopmentProfile extends ResourceProfile {

    private String lowerMemory;
    private String upperMemory;
    private String jvmFlagsString;
    private ServerProfile serverProfile;
    private boolean serverWorlds;
    private boolean updateOutdatedServerAutomatically;
    private boolean useServerGUI;
    private List<Plugin> plugins;
    
    public DevelopmentProfile(String name, String lowerMemory, String upperMemory, String jvmFlagsString,
                                    ServerProfile serverProfile, boolean serverWorlds, boolean updateOutdatedServerAutomatically,
                                    boolean useServerGUI, List<Plugin> plugins) {
        this(-1, name, lowerMemory, upperMemory, jvmFlagsString, serverProfile, serverWorlds, updateOutdatedServerAutomatically, useServerGUI, plugins);
    }
    
    public DevelopmentProfile(int id, String name, String lowerMemory, String upperMemory, String jvmFlagsString,
                                     ServerProfile serverProfile, boolean serverWorlds, boolean updateOutdatedServerAutomatically,
                                     boolean useServerGUI, List<Plugin> plugins) {
        super(id, name);
        
        this.lowerMemory = lowerMemory;
        this.upperMemory = upperMemory;
        this.jvmFlagsString = jvmFlagsString;
        this.serverProfile = serverProfile;
        this.serverWorlds = serverWorlds;
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
     * Gets the location of this development profile's plugins folder
     * @return location of this development profile's plugins folder
     */
    public Path getPluginsLocation() {
        return getLocation().resolve("plugins");
    }
    
    @Override
    public void finishSetup() {
        Path profileLocation = getLocation();
        Path pluginsLocation = getPluginsLocation();
        
        profileLocation.toFile().mkdirs();
        pluginsLocation.toFile().mkdirs();
        
        for (Plugin plugin : plugins) {
            Path pluginSourcePath = plugin.getPath();
            Path pluginPath = pluginsLocation.resolve(plugin.getFileName());
            
            try {
                Files.copy(pluginSourcePath, pluginPath);
            } catch (IOException ex) {
                
            }
        }
    }
    
    @Override
    public void update() {
        Path pluginsLocation = getPluginsLocation();
        
        for (Plugin plugin : plugins) {
            Path sourcePath = plugin.getPath();
            String checksum = MiscUtil.getChecksum(sourcePath);
            String originalChecksum = plugin.getChecksum();
            
            if (!checksum.equals(originalChecksum)) {
                Path localPath = pluginsLocation.resolve(plugin.getFileName());
                
                try {
                    Files.copy(sourcePath, localPath, StandardCopyOption.REPLACE_EXISTING);                    
                } catch (IOException ex) {
                    
                }
            }
        }
    }
    
    @Override
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
        json.put("server_profile", serverProfile.getId());
        json.put("server_worlds", serverWorlds);
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
        ServerProfile serverProfile = null;
        boolean serverWorlds = false;
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
        
        if (json.has("server_profile")) {
            serverProfile = ServerProfileHandler.getProfile(json.getInt("server_profile"));
        }
        
        if (json.has("server_worlds")) {
            serverWorlds = json.getBoolean("server_worlds");
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
        
        return new DevelopmentProfile(id, name, lowerMemory, upperMemory, jvmFlagsString,
                                            serverProfile, serverWorlds, updateOutdatedServerAutomatically,
                                            useServerGUI, plugins);
    }
    
}
