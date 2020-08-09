package org.codespeak.cmtt;

import java.io.File;

/**
 * A configuration class
 *
 * @author Vector
 */
public class Configuration {

    public static final String PROGRAM_VERSION = "0.0.0";
    public static final String PROGRAM_NAME = "Custom Minecraft Testing Tool";
    public static final String PROGRAM_TITLE = PROGRAM_NAME + " v" + PROGRAM_VERSION;
    public static final String SERVERS_FOLDER = "servers";
    public static final String PROFILES_FOLDER = "profiles";
    public static final String PLUGIN_DEVELOPMENT_FOLDER = PROFILES_FOLDER + File.separator + "plugin_development";
    public static final String DATA_FILE = "data.json";
    
}
