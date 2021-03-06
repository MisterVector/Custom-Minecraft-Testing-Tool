package org.codespeak.cmtt.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Vector
 */
public class MiscUtil {

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
         
            return DatatypeConverter.printHexBinary(result);            
        } catch (IOException ex) {
            return "";
        }
    }

    /**
     * Generates EULA text used in eula.txt
     * @return EULA text used in eula.txt
     */
    public static String getEULAText() {
        return "#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).\n"
             + "#" + new Date() + "\n"
             + "eula=true";
    }
    
}
