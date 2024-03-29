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
    private final String SERVER_DETAILS_TEXT = "This server is running ";
    
    private List<String> quitStrings = new ArrayList<String>(Arrays.asList("Done", "Ready for connections."));
    private final Process process;
    private final ServerProfile serverProfile;
    private final Path profilePath;
    private final Path profileServerPath;
    private final Label minecraftVersionLabel;
    private final Label serverDetailsLabel;
    
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

    public ReadServerInformationThread(Process process, ServerProfile serverProfile, Label minecraftVersionLabel, Label serverDetailsLabel) {
        this.process = process;
        this.serverProfile = serverProfile;
        this.minecraftVersionLabel = minecraftVersionLabel;
        this.serverDetailsLabel = serverDetailsLabel;
        
        this.profilePath = serverProfile.getProfilePath();
        this.profileServerPath = serverProfile.getProfileServerPath();
    }
    
    @Override
    public void run() {
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            return;
        }

        String minecraftVersion = readMinecraftVersion(profileServerPath);
        String serverDetails = "Unknown";
        Path logFilePath = getServerLogFile(profilePath);

        if (logFilePath != null) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(logFilePath.toFile()));
                String line = null;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(MINECRAFT_VERSION_TEXT) && minecraftVersion.equals("Unknown")) {
                        int minecraftVersionIdx = line.indexOf(MINECRAFT_VERSION_TEXT) + MINECRAFT_VERSION_TEXT.length();

                        minecraftVersion = line.substring(minecraftVersionIdx);
                    } else if (line.contains(SERVER_DETAILS_TEXT)) {
                        int serverDetailsIdx = line.indexOf(SERVER_DETAILS_TEXT) + SERVER_DETAILS_TEXT.length();

                        serverDetails = line.substring(serverDetailsIdx);
                    }

                    if (isQuitString(line)) {
                        break;
                    }

                    if (!minecraftVersion.equals("Unknown") && !serverDetails.equals("Unknown")) {
                        break;
                    }
                }
            } catch (IOException ex) {

            }
        }

        final String finalMinecraftVersion = minecraftVersion;
        final String finalServerDetails = serverDetails;
        
        Platform.runLater(() -> {
            serverProfile.setMinecraftVersion(finalMinecraftVersion);
            serverProfile.setServerDetails(finalServerDetails);
            minecraftVersionLabel.setText(finalMinecraftVersion);
            serverDetailsLabel.setText(finalServerDetails);
        });
    }

}