package org.codespeak.cmtt.scenes;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.codespeak.cmtt.CustomMinecraftTestingTool;
import org.codespeak.cmtt.objects.ServerTypes;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.objects.Plugin;
import org.codespeak.cmtt.objects.ProgramException;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the open development profile scene
 *
 * @author Vector
 */
public class OpenDevelopmentProfileSceneController implements Initializable {

    private List<ServerProfile> allServerProfiles = new ArrayList<ServerProfile>();
    private DevelopmentProfile openedProfile = null;
    private ServerProfile serverProfile = null;
    
    @FXML private Label headerLabel;
    @FXML private ComboBox<String> serverProfileChoice;
    @FXML private Label minecraftVersionLabel;
    @FXML private Label serverTypeLabel;
    
    private List<String> getListFromJVMFlags(String jvmFlagsString) {
        List<String> ret = new ArrayList<String>();
        String[] parts = jvmFlagsString.split(" ");
        
        for (String part : parts) {
            ret.add(part);
        }
        
        return ret;
    }
    
    private ServerProfile getServerProfile(String profileName) {
        for (ServerProfile profile : allServerProfiles) {
            if (profile.getName().equalsIgnoreCase(profileName)) {
                return profile;
            }
        }
        
        return null;
    }
    
    private void selectServerProfile(ServerProfile serverProfile) {
        minecraftVersionLabel.setText(serverProfile.getMinecraftVersion());
        serverTypeLabel.setText(serverProfile.getServerType().getName());
        
        this.serverProfile = serverProfile;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<ServerProfile> serverProfiles = ServerProfileHandler.getProfiles();
        ObservableList<String> serverProfileChoiceItems = serverProfileChoice.getItems();
        
        for (ServerProfile profile : serverProfiles) {
            serverProfileChoiceItems.add(profile.getName());
            allServerProfiles.add(profile);
        }
    }    
    
    /**
     * Opens the specified development profile
     * @param profile development profile
     */
    public void openProfile(DevelopmentProfile profile) {
        String profileName = profile.getName();
        
        headerLabel.setText(profileName);
        
        serverProfile = profile.getServerProfile();
        openedProfile = profile;

        serverProfileChoice.getSelectionModel().select(serverProfile.getName());                
        selectServerProfile(serverProfile);
    }
    
    @FXML
    public void onSelectTestServer() {
        String profileName = serverProfileChoice.getSelectionModel().getSelectedItem();
        ServerProfile profile = getServerProfile(profileName);
        
        selectServerProfile(profile);
    }
    
    @FXML
    public void onUpdateProfileButtonClick(ActionEvent event) {
        openedProfile.setServerProfile(serverProfile);
        
        Alert alert = AlertUtil.createAlert("Profile has been updated with the new server.");
        alert.show();
    }
    
    @FXML
    public void onStartServerButtonClick(ActionEvent event) {
        List<String> commands = new ArrayList<String>();
        Path serverProfileLocation = serverProfile.getProfileLocation();
        String lowerMemory = openedProfile.getLowerMemory();
        String upperMemory = openedProfile.getUpperMemory();
        String jvmFlagsString = openedProfile.getJVMFlagsString();
        String OS = System.getProperty("os.name").toLowerCase();

        if (openedProfile.isUpdatingOutdatedServerAutomatically() && serverProfile.hasUpdate()) {
            serverProfile.update();
        }
        
        if (OS.indexOf("win") > -1) {
            commands.add("cmd");
            commands.add("/c");
            commands.add("start");
        } else {
            commands.add("/bin/bash");
            commands.add("-c");
        }
        
        commands.add("java");
        
        if (!StringUtil.isNullOrEmpty(jvmFlagsString)) {
            List<String> flagList = getListFromJVMFlags(jvmFlagsString);
            
            commands.addAll(flagList);
        }
        
        if (!StringUtil.isNullOrEmpty(lowerMemory)) {
            commands.add("-Xms" + lowerMemory);
        }
        
        if (!StringUtil.isNullOrEmpty(upperMemory)) {
            commands.add("-Xmx" + upperMemory);
        }

        commands.add("-jar");
        commands.add("server.jar");
        commands.add("--world");
        commands.add(openedProfile.getServerWorldName());

        ServerTypes serverType = serverProfile.getServerType();
        String worldsArgument = (serverType == ServerTypes.CUSTOM ? serverProfile.getCustomWorldsArgument() : serverType.getWorldsArgument());

        if (!openedProfile.isUsingServerWorlds()) {
            commands.add("--" + worldsArgument);

            Path worldsLocation = openedProfile.getLocation().resolve("worlds").resolve(Integer.toString(serverProfile.getId())).toAbsolutePath();
            File fileWorldsLocation = worldsLocation.toFile();

            if (!fileWorldsLocation.exists()) {
                fileWorldsLocation.mkdirs();
            }

            commands.add(worldsLocation.toString());
        }            

        if (serverType != ServerTypes.VANILLA) {
            String pluginsArgument = (serverType == ServerTypes.CUSTOM ? serverProfile.getCustomPluginsArgument() : serverType.getPluginsArgument());

            List<Plugin> plugins = openedProfile.getPlugins();
            Path pluginsLocation = openedProfile.getPluginsLocation().toAbsolutePath();
            
            if (!plugins.isEmpty()) {
                for (Plugin plugin : plugins) {
                    plugin.updateIfNeeded(pluginsLocation);
                }

                commands.add("--" + pluginsArgument);
                commands.add(pluginsLocation.toString());
            }
        }

        if (!openedProfile.isUsingServerGUI()) {
            commands.add("nogui");
        }
        
        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(serverProfileLocation.toFile());

            pb.start();
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }

    @FXML
    public void onOpenLogsFolderButtonClick(ActionEvent event) {
        Path profileLocation = serverProfile.getProfileLocation();
        Path logsFolder = profileLocation.resolve("logs");
        
        if (!logsFolder.toFile().exists()) {
            Alert alert = AlertUtil.createAlert("Logs folder doesn't exist.");
            alert.show();
            
            return;
        }
        
        try {
            Desktop desktop = Desktop.getDesktop();

            desktop.browse(logsFolder.toUri());
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
            
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
