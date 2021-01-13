package org.codespeak.cmtt.profiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.codespeak.cmtt.util.MiscUtil;
import org.json.JSONObject;

/**
 * A class representing a plugin that is used with a development profile
 *
 * @author Vector
 */
public class Plugin implements Cloneable {

    private int id;
    private Path path;
    private String fileName;
    private String checksum;
    
    public Plugin(int id, Path path, String fileName, String checksum) {
        this.id = id;
        this.path = path;
        this.fileName = fileName;
        this.checksum = checksum;
    }
    
    /**
     * Gets the ID of this plugin
     * @return ID of this plugin
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the path to this plugin
     * @return path to this plugin
     */
    public Path getPath() {
        return path;
    }
    
    /**
     * Sets the path to this plugin
     * @param path path to this plugin
     */
    public void setPath(Path path) {
        this.path = path;
    }
    
    /**
     * Gets the file name of this plugin
     * @return file name of this plugin
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Sets the file name of this plugin
     * @param fileName file name of this plugin
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Gets the checksum of this plugin
     * @return checksum of this plugin
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Sets the checksum of this plugin
     * @param checksum checksum of this plugin
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    /**
     * Checks if this path is equivalent to the specified path string
     * @param pathString path string to check
     * @return if this path is equivalent to the specified path string
     */
    public boolean isPath(String pathString) {
        String currentPathString = path.toString();
        
        return currentPathString.equals(pathString);
    }
    
    /**
     * Gets a copy of this Plugin object
     * @return copy of this Plugin object
     */
    public Plugin copy() {
        return new Plugin(id, Paths.get(path.toString()), fileName, checksum);
    }
    
    /**
     * Checks if this plugin has an update
     * @return if this plugin has an update
     */
    public boolean hasUpdate() {
        String checkChecksum = MiscUtil.getChecksum(path);
        
        return !checkChecksum.equals(checksum);
    }
    
    /**
     * Updates this plugin
     * @param pluginsLocation location of the plugins folder this plugin
     * resides in 
     */
    public void update(Path pluginsLocation) {
        Path pluginLocation = pluginsLocation.resolve(fileName);
        
        try {
            Files.copy(path, pluginLocation, StandardCopyOption.REPLACE_EXISTING);
            
            checksum = MiscUtil.getChecksum(path);
        } catch (IOException ex) {
            
        }
    }
    
    /**
     * Converts this Plugin object to JSON
     * @return JSON representation of this Plugin object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("id", id);
        json.put("path", (path != null ? path.toString() : ""));
        json.put("file_name", fileName);
        json.put("checksum", checksum);
        
        return json;
    }
    
    /**
     * Creates a Plugin object from JSON
     * @param json JSON representation of a Plugin object
     * @return Plugin object represented by JSON
     */
    public static Plugin fromJSON(JSONObject json) {
        int id = 0;
        Path path = null;
        String fileName = "";
        String checksum = "";
        
        if (json.has("id")) {
            id = json.getInt("id");
        }

        if (json.has("path")) {
            path = Paths.get(json.getString("path"));
        }
        
        if (json.has("file_name")) {
            fileName = json.getString("file_name");
        }
        
        if (json.has("checksum")) {
            checksum = json.getString("checksum");
        }
        
        return new Plugin(id, path, fileName, checksum);
    }
    
}
