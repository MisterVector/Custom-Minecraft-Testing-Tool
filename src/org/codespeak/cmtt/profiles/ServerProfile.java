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
    private String serverDetails;
    private ServerTypes serverType;
    private String customPluginsFolderArgument;
    private String customWorldNameArgument;
    private String customWorldsFolderArgument;
    private Path serverPath;
    private String checksum;
    
    public ServerProfile(String name, ServerTypes serverType, String customPluginsFolderArgument,
                         String customWorldNameArgument, String customWorldsFolderArgument,
                         Path serverPath, String checksum) {
        this(-1, name, "Unknown", "Unknown", serverType, customPluginsFolderArgument, customWorldNameArgument, customWorldsFolderArgument, serverPath, checksum);
    }
    
    public ServerProfile(int id, String name, String minecraftVersion, String serverDetails,
                         ServerTypes serverType, String customPluginsFolderArgument,
                         String customWorldNameArgument, String customWorldsFolderArgument,
                         Path serverPath, String checksum) {
        super(id, name);

        this.minecraftVersion = minecraftVersion;
        this.serverDetails = serverDetails;
        this.serverPath = serverPath;
        this.checksum = checksum;
        this.serverType = serverType;
        this.customPluginsFolderArgument = customPluginsFolderArgument;
        this.customWorldNameArgument = customWorldNameArgument;
        this.customWorldsFolderArgument = customWorldsFolderArgument;
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
     * Gets the details of this server
     * @return details of this server
     */
    public String getServerDetails() {
        return serverDetails;
    }
    
    /**
     * Sets the details of this server
     * @param serverDetails details of this server
     */
    public void setServerDetails(String serverDetails) {
        this.serverDetails = serverDetails;
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
    public String getCustomPluginsFolderArgument() {
        return customPluginsFolderArgument;
    }
    
    /**
     * Sets the name of the custom argument for changing the plugins folder
     * @param customPluginsFolderArgument name of the custom argument for changing the
     * plugins folder
     */
    public void setCustomPluginsFolderArgument(String customPluginsFolderArgument) {
        this.customPluginsFolderArgument = customPluginsFolderArgument;
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
    public String getCustomWorldsFolderArgument() {
        return customWorldsFolderArgument;
    }

    /**
     * Sets the name of the custom argument for changing the worlds folder
     * @param customWorldsFolderArgument name of the custom argument for
     * changing the worlds folder
     */
    public void setCustomWorldsFolderArgument(String customWorldsFolderArgument) {
        this.customWorldsFolderArgument = customWorldsFolderArgument;
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
    public Path getProfilePath() {
        return Paths.get(Configuration.SERVERS_FOLDER + File.separator + super.getId());
    }
    
    /**
     * Gets the path to the server.jar file for this server profile
     * @return path to the server.jar file for this server profile
     */
    public Path getProfileServerPath() {
        return getProfilePath().resolve("server.jar");
    }

    /**
     * Gets the path to the eula.txt file for this server profile
     * @return
     */
    public Path getProfileEULAPath() {
        return getProfilePath().resolve("eula.txt");
    }
    
    /**
     * Checks if the server file in the profile directory is present
     * @return if the server file in the profile directory is present
     */
    public boolean hasNecessaryFiles() {
        Path serverLocation = getProfileServerPath();
        boolean serverExists = serverLocation.toFile().exists();
        
        if (serverType == ServerTypes.GLOWSTONE) {
            return serverExists;
        }
        
        Path eulaLocation = getProfileEULAPath();
        
        return serverExists && eulaLocation.toFile().exists();
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
        File fileProfilePath = getProfilePath().toFile();
        Path profileServerPath = getProfileServerPath();
        
        if (!fileProfilePath.exists()) {
            fileProfilePath.mkdirs();
        }
        
        try {
            Files.copy(serverPath, profileServerPath, StandardCopyOption.REPLACE_EXISTING);
            
            checksum = MiscUtil.getChecksum(serverPath);

            if (serverType != ServerTypes.GLOWSTONE) {
                Path profileEulaPath = getProfileEULAPath();
                boolean customServer = serverType == ServerTypes.CUSTOM;

                if (!profileEulaPath.toFile().exists()) {
                    String eulaText = MiscUtil.getEULAText(customServer);

                    Files.write(profileEulaPath, eulaText.getBytes());
                }
            }
        } catch (IOException ex) {

        }            
    }
    
    /**
     * Removes this profile and all files associated with it
     */
    public void remove() {
        Path profilePath = getProfilePath();

        if (profilePath.toFile().exists()) {
            try {
                Files.walk(profilePath)
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
        json.put("server_details", serverDetails);
        json.put("server_type", serverType.getName());
        json.put("custom_plugins_folder_argument", customPluginsFolderArgument);
        json.put("custom_world_name_argument", customWorldNameArgument);
        json.put("custom_worlds_folder_argument", customWorldsFolderArgument);
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
        String serverDetails = "";
        ServerTypes serverType = ServerTypes.BUKKIT;
        String customPluginsFolderArgument = "";
        String customWorldNameArgument = "";
        String customWorldsFolderArgument = "";
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
        
        if (json.has("server_details")) {
            serverDetails = json.getString("server_details");
        }
        
        if (json.has("server_type")) {
            serverType = ServerTypes.fromName(json.getString("server_type"));
        }
        
        if (json.has("custom_plugins_folder_argument")) {
            customPluginsFolderArgument = json.getString("custom_plugins_folder_argument");
        }
        
        if (json.has("custom_world_name_argument")) {
            customWorldNameArgument = json.getString("custom_world_name_argument");
        }
        
        if (json.has("custom_worlds_folder_argument")) {
            customWorldsFolderArgument = json.getString("custom_worlds_folder_argument");
        }

        if (json.has("server_path")) {
            serverPath = Paths.get(json.getString("server_path"));
        }
        
        if (json.has("checksum")) {
            checksum = json.getString("checksum");
        }
        
        return new ServerProfile(id, name, minecraftVersion, serverDetails, serverType, customPluginsFolderArgument,
                                 customWorldNameArgument, customWorldsFolderArgument, serverPath, checksum);
    }
    
}
