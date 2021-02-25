package org.codespeak.cmtt.profiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.objects.ServerTypes;
import org.codespeak.cmtt.util.MiscUtil;
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
    private String customWorldNameArgument;
    private String customWorldsArgument;
    private Path serverPath;
    private String checksum;
    
    public ServerProfile(String name, String minecraftVersion, ServerTypes serverType,
                         String customPluginsArgument, String customWorldNameArgument,
                         String customWorldsArgument, Path serverPath, String checksum) {
        this(-1, name, minecraftVersion, serverType, customPluginsArgument, customWorldNameArgument, customWorldsArgument, serverPath, checksum);
    }
    
    public ServerProfile(int id, String name, String minecraftVersion, ServerTypes serverType,
                         String customPluginsArgument, String customWorldNameArgument,
                         String customWorldsArgument, Path serverPath, String checksum) {
        super(id, name);

        this.minecraftVersion = minecraftVersion;
        this.serverPath = serverPath;
        this.checksum = checksum;
        this.serverType = serverType;
        this.customPluginsArgument = customPluginsArgument;
        this.customWorldNameArgument = customWorldNameArgument;
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
     * Gets the name of the custom argument for changing the world name
     * @return name of the custom argument for changing the world name
     */
    public String getCustomWorldNameArgument() {
        return customWorldNameArgument;
    }
    
    /**
     * Sets the name of the custom argument for changing the world name
     * @param customWorldNameArgument name of the custom argument for changing
     * the world name
     */
    public void setCustomWorldNameArgument(String customWorldNameArgument) {
        this.customWorldNameArgument = customWorldNameArgument;
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
     * Gets the path to this server
     * @return path to this server
     */
    public Path getServerPath() {
        return serverPath;
    }
    
    /**
     * Sets the path to this server
     * @param serverPath path to this server
     */
    public void setServerPath(Path serverPath) {
        this.serverPath = serverPath;
    }
    
    /**
     * Gets the checksum of this server profile
     * @return checksum of this server profile
     */
    public String getChecksum() {
        return checksum;
    }
    
    /**
     * Gets the checksum of this server profile
     * @param checksum checksum of this server profile
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
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

    /**
     * Gets the path to the eula.txt file for this server profile
     * @return 
     */
    public Path getEULALocation() {
        return getProfileLocation().resolve("eula.txt");
    }
    
    /**
     * Checks if the server file in the profile directory is present
     * @return if the server file in the profile directory is present
     */
    public boolean hasNecessaryFiles() {
        Path serverLocation = getServerLocation();
        Path eulaLocation = getEULALocation();
        
        return serverLocation.toFile().exists() && eulaLocation.toFile().exists();
    }
    
    /**
     * Checks if this server can update
     * @return if this server can update
     */
    public boolean canUpdate() {
        if (!serverPath.toFile().exists()) {
            return false;
        }
        
        return hasUpdate();
    }
    
    /**
     * Checks if this server has an update
     * @return if this server has an update 
     */
    public boolean hasUpdate() {
        String checkChecksum = MiscUtil.getChecksum(serverPath);
        
        return !checkChecksum.equals(checksum);
    }
    
    /**
     * Updates this server profile. Makes sure all files are in place as well
     */
    public void update() {
        File fileProfileLocation = getProfileLocation().toFile();
        Path serverFile = getServerLocation();
        Path eulaFile = getEULALocation();
        
        if (!fileProfileLocation.exists()) {
            fileProfileLocation.mkdirs();
        }
        
        try {
            Files.copy(serverPath, serverFile, StandardCopyOption.REPLACE_EXISTING);
            
            checksum = MiscUtil.getChecksum(serverPath);
            
            if (!eulaFile.toFile().exists()) {
                String eulaText = MiscUtil.getEULAText();

                Files.write(eulaFile, eulaText.getBytes());
            }
        } catch (IOException ex) {

        }            
    }
    
    /**
     * Removes this profile and all files associated with it
     */
    public void remove() {
        Path profileFolder = getProfileLocation();

        if (profileFolder.toFile().exists()) {
            try {
                Files.walk(profileFolder)
                     .sorted(Comparator.reverseOrder())
                     .map(Path::toFile)
                     .forEach(File::delete);
            } catch (IOException ex) {

            }            
        }
    }

    /**
     * Converts this object to JSON
     * @return JSON representation of this object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        int id = super.getId();
        String name = super.getName();
        
        json.put("id", id);
        json.put("name", name);
        json.put("minecraft_version", minecraftVersion);
        json.put("server_type", serverType.getName());
        json.put("custom_plugins_argument", customPluginsArgument);
        json.put("custom_world_name_argument", customWorldNameArgument);
        json.put("custom_worlds_argument", customWorldsArgument);
        json.put("server_path", serverPath);
        json.put("checksum", checksum);
        
        return json;
    }
    
    /**
     * Constructs a ServerProfile object from JSON
     * @param json JSON object to construct a ServerProfile object from
     * @return ServerProfile object from JSON
     */
    public static ServerProfile fromJSON(JSONObject json) {
        int id = 0;
        String name = "";
        String minecraftVersion = "";
        ServerTypes serverType = ServerTypes.BUKKIT;
        String customPluginsArgument = "";
        String customWorldNameArgument = "";
        String customWorldsArgument = "";
        Path serverPath = null;
        String checksum = "";
        
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
        
        if (json.has("custom_world_name_argument")) {
            customWorldNameArgument = json.getString("custom_world_name_argument");
        }
        
        if (json.has("custom_worlds_argument")) {
            customWorldsArgument = json.getString("custom_worlds_argument");
        }

        if (json.has("server_path")) {
            serverPath = Paths.get(json.getString("server_path"));
        }
        
        if (json.has("checksum")) {
            checksum = json.getString("checksum");
        }
        
        return new ServerProfile(id, name, minecraftVersion, serverType, customPluginsArgument,
                                 customWorldNameArgument, customWorldsArgument, serverPath, checksum);
    }
    
}
