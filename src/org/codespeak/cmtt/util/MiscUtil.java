package org.codespeak.cmtt.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Vector
 */
public class MiscUtil {

    public static final ExtensionFilter JARFILE_ONLY_FILTER = new ExtensionFilter("Jarfile (*.jar)", "*.jar");
    
    private static MessageDigest digest = null;
    
    private static MessageDigest getDigest() {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");                
            } catch (NoSuchAlgorithmException ex) {
                
            }
        }
        
        return digest;
    }
    
    /**
     * Gets an MD5 checksum from the specified path
     * @param path path to file
     * @return checksum of specified bytes
     */
    public static String getChecksum(Path path) {
        try {
            byte[] bytes = Files.readAllBytes(path);
            
            MessageDigest d = getDigest();
            d.update(bytes);
            byte[] result = d.digest();
            d.reset();

            String output = "";
            
            for (byte b : result) {
                output += Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
                output += Character.toUpperCase(Character.forDigit(b & 0xF, 16));
            }
            
            return output;
        } catch (IOException ex) {
            return "";
        }
    }

    /**
     * Gets a working file from the specified path by checking its parent until
     * the file exists
     * @param path path to search for a working file
     * @return a working file from the specified path
     */
    public static File getWorkingFile(Path path) {
        File fileTest = path.toFile();

        while (!fileTest.exists()) {
            path = path.getParent();
            
            if (path == null) {
                return null;
            }
            
            fileTest = path.toFile();
        }
        
        return fileTest;
    }
    
    /**
     * Generates EULA text used in eula.txt
     * @param customServer If the EULA text generated is for a custom server
     * @return EULA text used in eula.txt
     */
    public static String getEULAText(boolean customServer) {
        String text = "";
        
        if (customServer) {
            text = "#If this server is using vanilla Minecraft code, the EULA below applies" + System.lineSeparator();
        }
        
        return text + "#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula)." + System.lineSeparator()
             + "#" + new Date() + System.lineSeparator()
             + "eula=true";
    }
    
}
