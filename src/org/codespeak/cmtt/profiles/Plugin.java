package org.codespeak.cmtt.profiles;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 * A class representing a plugin that is used with a development profile
 *
 * @author Vector
 */
public class Plugin implements Cloneable {

    private int id;
    private String fileName;
    private Path path;
    private boolean autoUpdate;
    
    public Plugin(int id, String fileName, Path path, boolean autoUpdate) {
        this.id = id;
        this.fileName = fileName;
        this.path = path;
        this.autoUpdate = autoUpdate;
    }
    
    /**
     * Gets the ID of this plugin
     * @return ID of this plugin
     */
    public int getId() {
        return id;
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
     * Gets whether this plugin is being automatically updated
     * @return whether this plugin is being automatically updated
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }
    
    /**
     * Sets whether this plugin is being automatically updated
     * @param autoUpdate whether this plugin is being automatically updated
     */
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    /**
     * Gets a copy of this Plugin object
     * @return copy of this Plugin object
     */
    public Plugin copy() {
        return new Plugin(new Integer(id), new String(fileName), Paths.get(path.toString()), new Boolean(autoUpdate));
    }
    
    /**
     * Uninstalls this plugin
     * @param pluginsLocation path to the plugins folder
     */
    public void uninstall(Path pluginsLocation) {
        if (pluginsLocation != null) {
            Path pluginPath = pluginsLocation.resolve(fileName);
            pluginPath.toFile().delete();
        }
    }
    
    /**
     * Converts this Plugin object to JSON
     * @return JSON representation of this Plugin object
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("id", id);
        json.put("file_name", fileName);
        json.put("path", (path != null ? path.toString() : ""));
        json.put("auto_update", autoUpdate);
        
        return json;
    }
    
    /**
     * Creates a Plugin object from JSON
     * @param json JSON representation of a Plugin object
     * @return Plugin object represented by JSON
     */
    public static Plugin fromJSON(JSONObject json) {
        int id = 0;
        String fileName = "";
        Path path = null;
        boolean autoUpdate = false;
        
        if (json.has("id")) {
            id = json.getInt("id");
        }

        if (json.has("file_name")) {
            fileName = json.getString("file_name");
        }
        
        if (json.has("path")) {
            path = Paths.get(json.getString("path"));
        }
        
        if (json.has("auto_update")) {
            autoUpdate = json.getBoolean("auto_update");
        }
        
        return new Plugin(id, fileName, path, autoUpdate);
    }
    
}
