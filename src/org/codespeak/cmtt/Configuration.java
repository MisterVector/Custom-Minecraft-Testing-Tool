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
    public static final String PROFILES_FOLDER = "profiles";
    public static final String DEVELOPMENT_FOLDER = PROFILES_FOLDER + File.separator + "development";
    public static final String SERVERS_FOLDER = PROFILES_FOLDER + File.separator + "servers";
    public static final String DATA_FILE = "data.json";
    
}
