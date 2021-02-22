package org.codespeak.cmtt.objects;

/**
 * An enum listing all officially supported server types
 *
 * @author Vector
 */
public enum ServerTypes {
    
    BUKKIT("Bukkit", "--plugins", "--world-dir"),
    SPIGOT("Spigot", "--plugins", "--world-dir"),
    PAPER("Paper", "--plugins", "--world-dir"),
    VANILLA("Vanilla", "", "universe"),
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
     * Checks to see if this server type is among the list of passed in
     * server types
     * @param types various server types to check for
     * @return if this server type exists in one of the passed in server types
     */
    public boolean is(ServerTypes... types) {
        for (ServerTypes type : types) {
            if (type == this) {
                return true;
            }
        }
        
        return false;
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
