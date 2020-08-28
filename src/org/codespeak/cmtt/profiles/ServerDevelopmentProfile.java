package org.codespeak.cmtt.profiles;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.codespeak.cmtt.Configuration;
import org.json.JSONObject;

/**
 * A class representing a server development profile 
 *
 * @author Vector
 */
public class ServerDevelopmentProfile extends DevelopmentProfile {
   
    private Path serverPath;
    
    public ServerDevelopmentProfile(String name, String lowerMemory, String upperMemory, String jvmFlagsString, Path serverPath) {
        this(-1, name, lowerMemory, upperMemory, jvmFlagsString, serverPath);
    }
    
    public ServerDevelopmentProfile(int id, String name, String lowerMemory, String upperMemory, String jvmFlagsString, Path serverPath) {
        super(id, name, lowerMemory, upperMemory, jvmFlagsString);
        
        this.serverPath = serverPath;
    }
    
    /**
     * Gets the server path for this server development profile
     * @return server path for this server development profile
     */
    public Path getServerPath() {
        return serverPath;
    }
    
    /**
     * Sets the server path for this server development profile
     * @param serverPath server path for this server development profile
     */
    public void setServerPath(Path serverPath) {
        this.serverPath = serverPath;
    }
    
    /**
     * Gets the location of this server development profile
     * @return location of this server development profile
     */
    public Path getLocation() {
        return Paths.get(Configuration.SERVER_DEVELOPMENT_FOLDER + File.separator + super.getId());
    }
    
    @Override
    public DevelopmentType getDevelopmentType() {
        return DevelopmentType.SERVER;
    }
    
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        
        json.put("id", super.getId());
        json.put("name", super.getName());
        json.put("lower_memory", super.getLowerMemory());
        json.put("upper_memory", super.getUpperMemory());
        json.put("jvm_flags_string", super.getJVMFlagsString());
        json.put("server_path", serverPath.toString());
        
        return json;
    }

    /**
     * Creates a ServerDevelopmentProfile from JSON
     * @param json JSON object representing a ServerDevelopmentProfile object
     * @return ServerDevelopmentProfile object represented from JSON
     */
    public static ServerDevelopmentProfile fromJSON(JSONObject json) {
        int id = 0;
        String name = "";
        String lowerMemory = "";
        String upperMemory = "";
        String jvmFlagsString = "";
        Path serverPath = null;
        
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
        
        if (json.has("server_path")) {
            serverPath = Paths.get(json.getString("server_path"));
        }
        
        return new ServerDevelopmentProfile(id, name, lowerMemory, upperMemory, jvmFlagsString, serverPath);
    }
    
}
