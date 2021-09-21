package org.codespeak.cmtt.profiles;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 * A class representing a Java profile
 *
 * @author Vector
 */
public class JavaProfile extends Profile {

    private Path javaExecutablePath;
    
    public JavaProfile(String name, Path javaExecutablePath) {
        this(-1, name, javaExecutablePath);
    }
    
    public JavaProfile(int id, String name, Path javaExecutablePath) {
        super(id, name);
        
        this.javaExecutablePath = javaExecutablePath;
    }
    
    /**
     * Gets the path to the Java executable
     * @return path to the Java executable
     */
    public Path getJavaExecutablePath() {
        return javaExecutablePath;
    }
    
    /**
     * Sets the path to the Java executable
     * @param javaExecutablePath path to the Java executable
     */
    public void setJavaExecutablePath(Path javaExecutablePath) {
        this.javaExecutablePath = javaExecutablePath;
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
        json.put("java_executable_path", javaExecutablePath);
        
        return json;
    }
    
    /**
     * Gets a JavaProfile object from JSON
     * @param json JSON representation of a JavaProfile object
     * @return JavaProfile object represented by JSON
     */
    public static JavaProfile fromJSON(JSONObject json) {
        int id = 0;
        String name = "";
        Path javaExecutablePath = null;
        
        if (json.has("id")) {
            id = json.getInt("id");
        }
        
        if (json.has("name")) {
            name = json.getString("name");
        }
        
        if (json.has("java_executable_path")) {
            javaExecutablePath = Paths.get(json.getString("java_executable_path"));
        }
        
        return new JavaProfile(id, name, javaExecutablePath);
    }
    
}
