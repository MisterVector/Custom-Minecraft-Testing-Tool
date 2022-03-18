package org.codespeak.cmtt.scenes;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.Main;
import org.codespeak.cmtt.Settings;
import org.codespeak.cmtt.Settings.SettingFields;
import org.codespeak.cmtt.objects.ServerTypes;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.objects.Plugin;
import org.codespeak.cmtt.objects.ProgramException;
import org.codespeak.cmtt.objects.ReadServerInformationThread;
import org.codespeak.cmtt.objects.RunAfterProcessThread;
import org.codespeak.cmtt.objects.handlers.JavaProfileHandler;
import org.codespeak.cmtt.profiles.JavaProfile;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the open development profile scene
 *
 * @author Vector
 */
public class OpenDevelopmentProfileSceneController implements Initializable {

    private Stage controllerStage = null;
    private Process process = null;
    private RunAfterProcessThread afterThread = null;
    private ReadServerInformationThread readThread = null;
    private DevelopmentProfile openedProfile = null;
    private ServerProfile serverProfile = null;
    private JavaProfile javaProfile = null;
    
    @FXML private MenuItem debugServerMenuItem;
    @FXML private MenuItem deleteLocalWorldsMenuItem;
    @FXML private Label headerLabel;
    @FXML private ComboBox<String> serverProfileChoice;
    @FXML private Button updateProfileButton;
    @FXML private ComboBox<String> javaProfileChoice;
    @FXML private Label minecraftVersionLabel;
    @FXML private Label serverDetailsLabel;
    @FXML private Label serverTypeLabel;
    @FXML private Button startServerButton;
    @FXML private Button updatePluginsButton;
    @FXML private Button updateServerButton;
    
    private void disableControls(boolean disabled) {
        debugServerMenuItem.setDisable(disabled);
        deleteLocalWorldsMenuItem.setDisable(disabled);
        serverProfileChoice.setDisable(disabled);
        updateProfileButton.setDisable(disabled);
        javaProfileChoice.setDisable(disabled);
        startServerButton.setDisable(disabled);
        
        if (!openedProfile.isUpdatingOutdatedServerAutomatically()) {
            updateServerButton.setDisable(disabled);
        }
        
        if (!openedProfile.isUpdatingOutdatedPluginsAutomatically()) {
            updatePluginsButton.setDisable(disabled);
        }
    }
    
    private void selectServerProfile(ServerProfile serverProfile) {
        minecraftVersionLabel.setText(serverProfile.getMinecraftVersion());
        serverDetailsLabel.setText(serverProfile.getServerDetails());
        serverTypeLabel.setText(serverProfile.getServerType().getName());
        
        this.serverProfile = serverProfile;
    }
    
    private Alert getFailedStartAlert() {
        if (!serverProfile.hasNecessaryFiles()) {
            Path serverPath = serverProfile.getServerPath();
            
            if (!serverPath.toFile().exists()) {
                return AlertUtil.createAlert("The server file cannot be found. Unable to start Minecraft server.");                
            }
        }

        ServerTypes serverType = serverProfile.getServerType();
        String pluginsArgument = (serverType == ServerTypes.CUSTOM ? serverProfile.getCustomPluginsArgument() : serverType.getPluginsArgument());
        
        if (!StringUtil.isNullOrEmpty(pluginsArgument)) {
            List<Plugin> plugins = openedProfile.getPlugins();
            Path pluginsFolderLocation = openedProfile.getPluginsLocation();
            
            for (Plugin plugin : plugins) {
                if (!plugin.hasPluginFile(pluginsFolderLocation)) {
                    Path pluginPath = plugin.getPath();
                    
                    if (!pluginPath.toFile().exists()) {
                        return AlertUtil.createAlert("One or more plugins cannot be found. Unable to start Minecraft server.");
                    }
                }
            }
        }
        
        return null;
    }

    private List<String> getServerStartupArguments(boolean debugMode) {
        List<String> commands = new ArrayList<String>();
        
        String lowerMemory = openedProfile.getLowerMemory();
        String upperMemory = openedProfile.getUpperMemory();
        String jvmFlagsString = openedProfile.getJVMFlagsString();
        String OS = System.getProperty("os.name").toLowerCase();
        boolean isWindows = OS.indexOf("win") > -1;
        String windowTitle = "Deveopment profile: " + openedProfile.getName() + " Selected server: " + serverProfile.getName();
        
        if (isWindows) {
            commands.add("cmd");
            commands.add("/c");
            commands.add("start");
            commands.add(windowTitle);
            commands.add("/wait");
            commands.add("cmd");
            commands.add("/c");
        } else {
            commands.add("/bin/bash");
            commands.add("-c");
        }
        
        commands.add(javaProfile != null ? javaProfile.getJavaExecutablePath().toString() : "java");
        
        if (!StringUtil.isNullOrEmpty(jvmFlagsString)) {
            List<String> flagList = StringUtil.splitStringToList(jvmFlagsString);
            
            commands.addAll(flagList);
        }
        
        if (!StringUtil.isNullOrEmpty(lowerMemory)) {
            commands.add("-Xms" + lowerMemory);
        }
        
        if (!StringUtil.isNullOrEmpty(upperMemory)) {
            commands.add("-Xmx" + upperMemory);
        }

        ServerTypes serverType = serverProfile.getServerType();
        
        if (serverType.is(ServerTypes.BUKKIT, ServerTypes.SPIGOT)) {
            commands.add("-DIReallyKnowWhatIAmDoingISwear");
        }
        
        commands.add("-jar");
        commands.add("server.jar");
        
        String minecraftServerArguments = openedProfile.getMinecraftServerArguments();
        
        if (!StringUtil.isNullOrEmpty(minecraftServerArguments)) {
            List<String> args = StringUtil.splitStringToList(minecraftServerArguments);
            
            for (String arg : args) {
                commands.add(arg);
            }
        }
        
        String customWorldNameArgument = (serverType == ServerTypes.CUSTOM ? serverProfile.getCustomWorldNameArgument() : serverType.getWorldNameArgument());
        String customServerWorldName = openedProfile.getCustomServerWorldName();
        
        if (!StringUtil.isNullOrEmpty(customWorldNameArgument) && !StringUtil.isNullOrEmpty((customServerWorldName))) {
            commands.add(customWorldNameArgument);
            commands.add(customServerWorldName);            
        }

        String worldsArgument = (serverType == ServerTypes.CUSTOM ? serverProfile.getCustomWorldsArgument() : serverType.getWorldsArgument());

        if (!StringUtil.isNullOrEmpty(worldsArgument) && !openedProfile.isUsingServerWorlds()) {
            commands.add(worldsArgument);

            Path worldsLocation = openedProfile.getWorldLocation(serverProfile);
            File fileWorldsLocation = worldsLocation.toFile();

            if (!fileWorldsLocation.exists()) {
                fileWorldsLocation.mkdirs();
            }

            commands.add(worldsLocation.toString());
        }            

        String pluginsArgument = (serverType == ServerTypes.CUSTOM ? serverProfile.getCustomPluginsArgument() : serverType.getPluginsArgument());
        
        if (!StringUtil.isNullOrEmpty(pluginsArgument)) {
            List<Plugin> plugins = openedProfile.getPlugins();
            Path pluginsLocation = openedProfile.getPluginsLocation().toAbsolutePath();
            
            if (!plugins.isEmpty()) {
                for (Plugin plugin : plugins) {
                    if (!plugin.hasPluginFile(pluginsLocation) || (openedProfile.isUpdatingOutdatedPluginsAutomatically() && plugin.canUpdate())) {
                        plugin.update(pluginsLocation);                        
                    }
                }

                commands.add(pluginsArgument);
                commands.add(pluginsLocation.toString());
            }
        }

        if (!openedProfile.isUsingServerGUI()) {
            commands.add("nogui");
        }
        
        if (debugMode) {
            if (isWindows) {
                commands.add("^&");
                commands.add("pause");
            }
        }
        
        return commands;
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
        }
        
        List<JavaProfile> javaProfiles = JavaProfileHandler.getProfiles();
        ObservableList<String> javaProfileItems = javaProfileChoice.getItems();
        
        javaProfileItems.add("System");
        
        for (JavaProfile profile : javaProfiles) {
            String profileName = profile.getName();
            
            javaProfileItems.add(profileName);
        }
    }    
    
    /**
     * Sets the stage representing this scene controller
     * @param controllerStage stage representing this scene controller
     */
    public void setControllerStage(Stage controllerStage) {
        this.controllerStage = controllerStage;

        this.controllerStage.setOnHidden((WindowEvent windowEvent) -> {
            if (afterThread != null && afterThread.isAlive()) {
                afterThread.interrupt();
            }
            
            if (readThread != null && readThread.isAlive()) {
                readThread.interrupt();
            }
        });
    }
    
    /**
     * Opens the specified development profile
     * @param profile development profile
     */
    public void openProfile(DevelopmentProfile profile) {
        String profileName = profile.getName();
        
        headerLabel.setText(profileName);
        
        serverProfile = profile.getServerProfile();
        javaProfile = profile.getJavaProfile();
        openedProfile = profile;

        serverProfileChoice.getSelectionModel().select(serverProfile.getName());                
        selectServerProfile(serverProfile);
        
        javaProfileChoice.getSelectionModel().select(javaProfile != null ? javaProfile.getName() : "System");
        
        if (openedProfile.isUpdatingOutdatedPluginsAutomatically()) {
            updatePluginsButton.setDisable(true);
        }
        
        if (openedProfile.isUpdatingOutdatedServerAutomatically()) {
            updateServerButton.setDisable(true);
        }
    }
    
    @FXML
    public void onSelectTestServer() {
        String profileName = serverProfileChoice.getSelectionModel().getSelectedItem();
        ServerProfile profile = ServerProfileHandler.getProfile(profileName);
        
        selectServerProfile(profile);
    }
    
    @FXML
    public void onSelectJavaProfile() {
        String profileName = javaProfileChoice.getSelectionModel().getSelectedItem();
        JavaProfile javaProfile = null;
        
        if (!profileName.equalsIgnoreCase("system")) {
            javaProfile = JavaProfileHandler.getProfile(profileName);            
        }
        
        this.javaProfile = javaProfile;
    }

    @FXML
    public void onStartMinecraftLauncherMenuItemClick(ActionEvent event) {
        Settings settings = Configuration.getSettings();
        String minecraftLauncherLocation = settings.getSetting(SettingFields.MINECRAFT_LAUNCHER_LOCATION);
        
        if (StringUtil.isNullOrEmpty(minecraftLauncherLocation)) {
            Alert alert = AlertUtil.createAlert("The Minecraft launcher location has not been set.");
            alert.show();
            
            return;
        }
        
        Path minecraftLauncherPath = Paths.get(minecraftLauncherLocation);
        
        if (!minecraftLauncherPath.toFile().exists()) {
            Alert alert = AlertUtil.createAlert("The Minecraft launcher file does not exist.");
            alert.show();
            
            return;
        }

        try {
            ProcessBuilder pb = new ProcessBuilder(minecraftLauncherLocation);
            pb.directory(minecraftLauncherPath.getParent().toFile());
            pb.start();
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);

            Main.handleError(ex2);
        }
    }
    
    @FXML
    public void onCloseMenuItemClick(ActionEvent event) {
        controllerStage.close();
    }

    @FXML
    public void onDebugServerMenuItemClick(ActionEvent event) {
        Path profilePath = serverProfile.getProfilePath();

        Alert checkAlert = getFailedStartAlert();
        
        if (checkAlert != null) {
            checkAlert.show();
            
            return;
        }
        
        if (!serverProfile.hasNecessaryFiles() || (openedProfile.isUpdatingOutdatedServerAutomatically() && serverProfile.canUpdate())) {
            serverProfile.update();
        }

        List<String> commands = getServerStartupArguments(true);
        
        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(profilePath.toFile());

            pb.start();
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);

            Main.handleError(ex2);
        }
    }
    
    @FXML
    public void onOpenLatestLogMenuItemClick(ActionEvent event) {
        Path targetPath = serverProfile.getProfilePath().resolve("logs").resolve("latest.log");
        
        if (!targetPath.toFile().exists()) {
            Alert alert = AlertUtil.createAlert("The latest log file does not exist. The logs folder will be opened instead.");
            alert.showAndWait();
            
            targetPath = targetPath.getParent();

            if (!targetPath.toFile().exists()) {
                alert = AlertUtil.createAlert("The logs folder could not be found.");
                alert.showAndWait();

                return;
            }
        }
        
        Desktop desktop = Desktop.getDesktop();
        
        try {
            desktop.browse(targetPath.toUri());
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);

            Main.handleError(ex2);
        }
    }
    
    @FXML
    public void onOpenLogsFolderMenuItemClick(ActionEvent event) {
        Path profilePath = serverProfile.getProfilePath();
        Path logsFolder = profilePath.resolve("logs");
        
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

            Main.handleError(ex2);
        }
    }
            
    @FXML
    public void onOpenLocalWorldsFolderMenuItemClick(ActionEvent event) {
        Path localWorldsPath = openedProfile.getWorldLocation(serverProfile);
        File localWorldsFolder = localWorldsPath.toFile();
        
        if (!localWorldsFolder.exists()) {
            Alert alert = AlertUtil.createAlert("The local worlds for this server do not exist!");
            alert.show();
            
            return;
        }
        
        Desktop desktop = Desktop.getDesktop();
        
        try {
            desktop.open(localWorldsFolder);
        } catch (IOException ex) {
            Alert alert = AlertUtil.createAlert("Unable to open the local worlds folder for this server.");
            alert.show();
        }
    }
    
    @FXML
    public void onDeleteLocalWorldsMenuItemClick(ActionEvent event) {
        Path localWorldsPath = openedProfile.getWorldLocation(serverProfile);
        File localWorldsFile = localWorldsPath.toFile();

        if (!localWorldsFile.exists()) {
            Alert alert = AlertUtil.createAlert("The local worlds for the selected server do not exist.");
            alert.show();
            
            return;
        }

        String message = "";
        
        try {
            Files.walk(localWorldsPath)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);

            message = "The local worlds for the selected server have been deleted.";
        } catch (IOException ex) {
            message = "The local worlds for the selected server could not be completely deleted.";
        }

        Alert alert = AlertUtil.createAlert(message);
        alert.show();
    }
    
    @FXML
    public void onUpdateProfileButtonClick(ActionEvent event) {
        openedProfile.setServerProfile(serverProfile);
        openedProfile.setJavaProfile(javaProfile);
        
        Alert alert = AlertUtil.createAlert("Profile has been updated with the new server and Java profile.");
        alert.show();
    }

    @FXML
    public void onStartServerButtonClick(ActionEvent event) {
        Path profilePath = serverProfile.getProfilePath();

        Alert checkAlert = getFailedStartAlert();
        
        if (checkAlert != null) {
            checkAlert.show();
            
            return;
        }
        
        boolean updated = false;
        
        if (!serverProfile.hasNecessaryFiles() || (openedProfile.isUpdatingOutdatedServerAutomatically() && serverProfile.canUpdate())) {
            serverProfile.update();
            
            updated = true;
        }

        List<String> commands = getServerStartupArguments(false);
        
        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(profilePath.toFile());

            process = pb.start();
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);

            Main.handleError(ex2);
            
            return;
        }

        disableControls(true);
        
        afterThread = new RunAfterProcessThread(process) {
            @Override
            public void finished() {
                disableControls(false);
            }
        };
        
        afterThread.start();
        
        if (updated) {
            readThread = new ReadServerInformationThread(process, serverProfile, minecraftVersionLabel, serverDetailsLabel);

            readThread.start();            
        }
    }

    @FXML
    public void onUpdateServerButtonClick(ActionEvent event) {
        Path serverPath = serverProfile.getServerPath();
        
        if (!serverPath.toFile().exists()) {
            Alert alert = AlertUtil.createAlert("Cannot update server. The source file does not exist.");
            alert.show();
            
            return;
        }
        
        if (!serverProfile.hasNecessaryFiles() || serverProfile.hasUpdate()) {
            serverProfile.update();
            
            Alert alert = AlertUtil.createAlert("The server has been updated.");
            alert.show();
            
            return;
        }
        
        Alert alert = AlertUtil.createAlert("The server is already up-to-date.");
        alert.show();
    }

    @FXML
    public void onUpdatePluginsButtonClick(ActionEvent event) {
        List<Plugin> plugins = openedProfile.getPlugins();
        
        if (plugins.isEmpty()) {
            Alert alert = AlertUtil.createAlert("This profile has no plugins.");
            alert.show();
            
            return;
        }

        Path pluginsLocation = openedProfile.getPluginsLocation().toAbsolutePath();
        int upToDatePlugins = plugins.size();
        int pluginsUpdated = 0;
        int pluginsFailedUpdate = 0;

        for (Plugin plugin : plugins) {
            Path path = plugin.getPath();
            
            if (path.toFile().exists()) {
                if (!plugin.hasPluginFile(pluginsLocation) || plugin.hasUpdate()) {
                    plugin.update(pluginsLocation);

                    pluginsUpdated++;
                    upToDatePlugins--;
                }
            } else {
                pluginsFailedUpdate++;
                upToDatePlugins--;
            }
        }
        
        String updateMsg = "";

        if (pluginsUpdated > 0 || pluginsFailedUpdate > 0) {
            if (pluginsUpdated > 0) {
                updateMsg = pluginsUpdated + " plugin" + (pluginsUpdated > 1 ? "s were" : " was") + " updated.";
            }

            if (pluginsFailedUpdate > 0) {
                if (!updateMsg.isEmpty()) {
                    updateMsg += "\n";
                }
                
                updateMsg += pluginsFailedUpdate + " plugin" + (pluginsFailedUpdate > 1 ? "s" : "") + " failed to update.";
            }
            
            if (upToDatePlugins > 0) {
                updateMsg += "\n" + upToDatePlugins + " plugin" + (upToDatePlugins > 1 ? "s are" : " is") + " up-to-date.";
            }
        } else {
            updateMsg = "All plugins are up-to-date.";
        }
        
        Alert alert = AlertUtil.createAlert(updateMsg);
        alert.show();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        controllerStage.close();
    }
    
}
