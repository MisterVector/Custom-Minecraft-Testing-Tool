package org.codespeak.cmtt.objects;

/**
 * An enum listing all officially supported server types
 *
 * @author Vector
 */
public enum ServerTypes {
    
    BUKKIT("Bukkit", "--plugins", "--world", "--world-dir"),
    SPIGOT("Spigot", "--plugins", "--world", "--world-dir"),
    PAPER("Paper", "--plugins", "--world", "--world-dir"),
    GLOWSTONE("Glowstone", "--plugins-dir", "--world-name", "--worlds-dir"),
    VANILLA("Vanilla", "", "--world", "--universe"),
    CUSTOM("Custom");
    
    private final String name;
    private final String pluginsFolderArgument;
    private final String worldNameArgument;
    private final String worldsFolderArgument;
    
    private ServerTypes(String name) {
        this(name, "", "", "");
    }
    
    private ServerTypes(String name, String pluginsFolderArgument, String worldNameArgument, String worldsFolderArgument) {
        this.name = name;
        this.pluginsFolderArgument = pluginsFolderArgument;
        this.worldNameArgument = worldNameArgument;
        this.worldsFolderArgument = worldsFolderArgument;
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
    public String getPluginsFolderArgument() {
        return pluginsFolderArgument;
    }
    
    /**
     * Gets the name of the argument for changing the world name
     * @return name of the argument for changing the world name
     */
    public String getWorldNameArgument() {
        return worldNameArgument;
    }
    
    /**
     * Gets the name of the argument for changing the worlds folder
     * @return name of the argument for changing the worlds folder
     */
    public String getWorldsFolderArgument() {
        return worldsFolderArgument;
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
