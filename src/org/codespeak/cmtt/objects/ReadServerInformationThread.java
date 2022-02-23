package org.codespeak.cmtt.objects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javafx.application.Platform;
import javafx.scene.control.Label;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A thread to read the latest log file of a Minecraft server after it has
 * terminated, looking for the version of the server
 *
 * @author Vector
 */
public class ReadServerInformationThread extends Thread {
    private final String MINECRAFT_VERSION_TEXT = "Starting minecraft server version ";
    
    private List<String> quitStrings = new ArrayList<String>(Arrays.asList("Done", "Ready for connections."));
    private final Process process;
    private final ServerProfile serverProfile;
    private final Path serverProfilePath;
    private final Path minecraftServerPath;
    private final Label minecraftVersionLabel;
    
    private boolean isQuitString(String line) {
        for (String quitString : quitStrings) {
            if (line.contains(quitString)) {
                return true;
            }
        }

        return false;
    }
    
    private String readMinecraftVersion(Path minecraftServerPath) {
        try {
            JarFile jarfile = new JarFile(minecraftServerPath.toFile());
            JarEntry entry = jarfile.getJarEntry("version.json");

            if (entry != null) {
                InputStream stream = jarfile.getInputStream(entry);
                byte[] bytes = new byte[1024];
                String contents = "";
                
                while (stream.read(bytes, 0, 1024) > 0) {
                    contents += new String(bytes);
                }

                stream.close();
                
                JSONObject json = new JSONObject(contents);
                
                return json.getString("name");
            }
        } catch (JSONException | IOException ex) {
            
        }

        return "Unknown";
    }

    private Path getServerLogFile(Path serverFolder) {
        Path logsPath = serverFolder.resolve("logs");

        if (!logsPath.toFile().exists()) {
            return null;
        }

        Path logFilePath = logsPath.resolve("latest.log");

        if (!logFilePath.toFile().exists()) {
            long lastCreated = -1;

            for (File file : logsPath.toFile().listFiles()) {
                if (file.isFile()) {
                    String name = file.getName();
                    String ext = name.substring(name.indexOf("."));

                    if (ext.equalsIgnoreCase(".log") || ext.equalsIgnoreCase(".txt")) {
                        BasicFileAttributes attr = null;

                        try {
                            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                        } catch (IOException ex) {
                            return null;
                        }

                        long currentLastCreated = attr.creationTime().toMillis();

                        if (currentLastCreated > lastCreated) {
                            logFilePath = logsPath.resolve(file.getName());
                            lastCreated = currentLastCreated;
                        }
                    }
                }
            }
        }

        if (!logFilePath.toFile().exists()) {
            return null;
        }

        return logFilePath;
    }

    public ReadServerInformationThread(Process process, ServerProfile serverProfile, Label minecraftVersionLabel) {
        this.process = process;
        this.serverProfile = serverProfile;
        this.minecraftVersionLabel = minecraftVersionLabel;
        
        this.serverProfilePath = serverProfile.getProfileLocation();
        this.minecraftServerPath = serverProfile.getServerLocation();
    }
    
    @Override
    public void run() {
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            return;
        }

        String minecraftVersion = readMinecraftVersion(minecraftServerPath);
        
        if (minecraftVersion.equals("Unknown")) {
            Path logFilePath = getServerLogFile(serverProfilePath);
            
            if (logFilePath != null) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(logFilePath.toFile()));
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        if (line.contains(MINECRAFT_VERSION_TEXT)) {
                            int minecraftVersionIdx = line.indexOf(MINECRAFT_VERSION_TEXT) + MINECRAFT_VERSION_TEXT.length();

                            minecraftVersion = line.substring(minecraftVersionIdx);
                            
                            break;
                        }

                        if (isQuitString(line)) {
                            break;
                        }
                    }
                } catch (IOException ex) {

                }
            }
        }
        
        final String finalMinecraftVersion = minecraftVersion;

        Platform.runLater(() -> {
            serverProfile.setMinecraftVersion(finalMinecraftVersion);
            minecraftVersionLabel.setText(finalMinecraftVersion);
        });
    }

}