package org.codespeak.cmtt.objects;

/**
 * An enum listing all officially supported server types
 *
 * @author Vector
 */
public enum ServerTypes {
    
    BUKKIT("Bukkit", "plugins", "world-dir"),
    VANILLA("Vanilla"),
    CUSTOM("Custom");
    
    private final String name;
    private final String pluginsArgument;
    private final String worldsArgument;
    
    private ServerTypes(String name) {
        this(name, "", "");
    }
    
    private ServerTypes(String name, String pluginsArgument, String worldsArgument) {
        this.name = name;
        this.pluginsArgument = pluginsArgument;
        this.worldsArgument = worldsArgument;
    }
    
    /**
     * Gets the name of this server type
     * @return name of this server type
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the name of the argument for changing the plugins folder
     * @return name of the argument for changing the plugins folder
     */
    public String getPluginsArgument() {
        return pluginsArgument;
    }
    
    /**
     * Gets the name of the argument for changing the worlds folder
     * @return name of the argument for changing the worlds folder
     */
    public String getWorldsArgument() {
        return worldsArgument;
    }
    
    /**
     * Gets a server type from its name
     * @param name name of server type
     * @return server type from its name
     */
    public static ServerTypes fromName(String name) {
        for (ServerTypes st : values()) {
            if (st.getName().equalsIgnoreCase(name)) {
                return st;
            }
        }
        
        return null;
    }

}
