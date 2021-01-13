package org.codespeak.cmtt.objects;

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
    private String checksum;
    
    public Plugin(int id, Path path, String checksum) {
        this.id = id;
        this.path = path;
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
        return path.getFileName().toString();
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
        return new Plugin(id, Paths.get(path.toString()), checksum);
    }
    
    /**
     * Updates this plugin if it is outdated or hasn't been copied yet
     * @param pluginsLocation location of the plugins folder this plugin
     * resides in 
     */
    public void updateIfNeeded(Path pluginsLocation) {
        Path pluginFilePath = pluginsLocation.resolve(path.getFileName().toString());
        String tempChecksum = null;
        boolean update = false;
        
        if (!pluginFilePath.toFile().exists()) {
            update = true;
        }
        
        if (!update) {
            String checkChecksum = MiscUtil.getChecksum(path);

            if (!checkChecksum.equals(checksum)) {
                update = true;
                tempChecksum = checkChecksum;
            }
        }

        if (update) {
            try {
                Files.copy(path, pluginFilePath, StandardCopyOption.REPLACE_EXISTING);

                if (tempChecksum != null) {
                    checksum = tempChecksum;
                } else {
                    checksum = MiscUtil.getChecksum(path);
                }
            } catch (IOException ex) {

            }
        }
    }
    
    /**
     * Converts this Plugin object to JSON
     * @return JSON representation of this Plugin object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("id", id);
        json.put("path", path.toString());
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
        String checksum = "";
        
        if (json.has("id")) {
            id = json.getInt("id");
        }

        if (json.has("path")) {
            path = Paths.get(json.getString("path"));
        }
        
        if (json.has("checksum")) {
            checksum = json.getString("checksum");
        }
        
        return new Plugin(id, path, checksum);
    }
    
}
