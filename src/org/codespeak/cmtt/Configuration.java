package org.codespeak.cmtt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import org.json.JSONObject;

/**
 * A configuration class
 *
 * @author Vector
 */
public class Configuration {

    public static final String PROGRAM_VERSION = "0.0.0";
    public static final String PROGRAM_NAME = "Custom Minecraft Testing Tool";
    public static final String PROGRAM_TITLE = PROGRAM_NAME + " v" + PROGRAM_VERSION;
    public static final String PROFILES_FOLDER = "profiles";
    public static final String DEVELOPMENT_FOLDER = PROFILES_FOLDER + File.separator + "development";
    public static final String SERVERS_FOLDER = PROFILES_FOLDER + File.separator + "servers";
    public static final String DATA_FILE = "data.json";
    public static final String SETTINGS_FILE = "settings.json";
    
    private static Settings settings = null;

    private static Settings loadSettings() {
        File settingsFile = new File(SETTINGS_FILE);
        
        if (settingsFile.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(settingsFile.toPath());
                String jsonString = new String(bytes);
                JSONObject obj = new JSONObject(jsonString);
                
                return Settings.fromJSON(obj);
            } catch (IOException ex) {
                
            }
        }
        
        return Settings.fromJSON(new JSONObject());
    }        

    /**
     * Gets the settings object
     * @return settings object
     */
    public static Settings getSettings() {
        if (settings == null) {
            settings = loadSettings();
        }
        
        return settings;
    }
    
    /**
     * Saves program settings
     */
    public static void saveSettings() {
        if (settings == null) {
            return;
        }

        File settingsFile = new File(SETTINGS_FILE);
        
        if (settingsFile.exists()) {
            settingsFile.delete();
        }
        
        JSONObject json = new JSONObject();
        
        json.put("settings", settings.toJSON());
        
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(settingsFile))) {
            writer.write(json.toString(4));
        } catch (IOException ex) {
            
        }
    }
    
}
