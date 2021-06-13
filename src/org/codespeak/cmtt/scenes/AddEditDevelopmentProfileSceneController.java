package org.codespeak.cmtt.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.Settings;
import org.codespeak.cmtt.Settings.SettingFields;
import org.codespeak.cmtt.objects.ConditionalAlert;
import org.codespeak.cmtt.objects.DevelopmentProfileProcessor;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.profiles.JVMFlagsProfile;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.objects.Plugin;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.MiscUtil;
import org.codespeak.cmtt.util.SceneUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the add/edit development profile scene
 *
 * @author Vector
 */
public class AddEditDevelopmentProfileSceneController implements Initializable {

    private static final long MIN_XMS = 1048576L;
    private static final long MIN_XMX = 1048576L;
    
    private DevelopmentProfileProcessor processor = null;
    private List<JVMFlagsProfile> availableJVMFlagsProfiles = new ArrayList<JVMFlagsProfile>();
    private List<ServerProfile> availableServerProfiles = new ArrayList<ServerProfile>();
    private List<Plugin> plugins = new ArrayList<Plugin>();
    private List<Path> pathsToDelete = new ArrayList<Path>();
    private DevelopmentProfile editedDevelopmentProfile = null;
    private boolean editMode = false;
    
    @FXML Label mainHeaderLabel;
    @FXML TextField profileNameInput;
    @FXML TextField lowerMemoryInput;
    @FXML TextField upperMemoryInput;
    @FXML TextArea jvmFlagsStringInput;
    @FXML TextArea minecraftServerArgumentsInput;
    @FXML ComboBox<String> jvmFlagsProfileChoice;
    @FXML ComboBox<String> serverProfilesChoice;
    @FXML TextField customServerWorldNameInput;
    @FXML CheckBox serverWorldsCheck;
    @FXML CheckBox updateOutdatedPluginsAutomaticallyCheck;
    @FXML CheckBox updateOutdatedServerAutomaticallyCheck;
    @FXML CheckBox useServerGUICheck;
    @FXML ListView<String> pluginList;

    private JVMFlagsProfile getJVMFlagsProfile(String name) {
        for (JVMFlagsProfile profile : availableJVMFlagsProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        
        return null;
    }
    
    private ServerProfile getServerProfile(String name) {
        for (ServerProfile profile : availableServerProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
            }
        }
        
        return null;
    }
    
    private Plugin getPlugin(String pathString) {
        for (Plugin plugin : plugins) {
            if (plugin.isPath(pathString)) {
                return plugin;
            }
        }
        
        return null;
    }

    private boolean isExistingPluginPath(Path path) {
        return isExistingPluginPath(path, null);
    }
    
    private boolean isExistingPluginPath(Path path, Plugin editedPlugin) {
        for (Plugin plugin : plugins) {
            Path p = plugin.getPath();
            
            if (p.equals(path) && (editedPlugin == null || !plugin.equals(editedPlugin))) {
                return true;
            }
        }
        
        return false;
    }
    
    private void undoDeletedPathIfPresent(Path path) {
        for (Iterator<Path> it = pathsToDelete.iterator(); it.hasNext();) {
            Path deletedPath = it.next();

            if (deletedPath.equals(path)) {
                it.remove();

                return;
            }
        }
    }
    
    private Plugin deletePlugin(String pathString) {
        for (Iterator<Plugin> it = plugins.iterator(); it.hasNext();) {
            Plugin plugin = it.next();
            
            if (plugin.isPath(pathString)) {
                it.remove();
                return plugin;
            }
        }
        
        return null;
    }
    
    private static long getLongValue(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    private static long getValueFromMemoryArg(String memoryArg) {
        long val = getLongValue(memoryArg);
        
        if (val == 0) {
            int lengthMinusOne = memoryArg.length() - 1;
            char endChar = Character.toUpperCase(memoryArg.charAt(lengthMinusOne));
            long valueWithoutChar = getLongValue(memoryArg.substring(0, lengthMinusOne));

            if (valueWithoutChar == 0) {
                return 0;
            }
            
            switch (endChar) {
                case 'K':
                        val = (valueWithoutChar * 1024);
                        break;
                case 'M':
                        val = (valueWithoutChar * 1048576);
                        break;
                case 'G':
                        val = (valueWithoutChar * 1073741824);
                        break;
            }
        }
        
        return val;
    }

    private static boolean isValidMemoryArg(String memoryArg, long minValue) {
        long val = getValueFromMemoryArg(memoryArg);
        
        return (val >= minValue && (val % 1024 == 0));
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<JVMFlagsProfile> jvmFlagsProfiles = JVMFlagsProfileHandler.getProfiles();
        ObservableList<String> jvmFlagsProfileItems = jvmFlagsProfileChoice.getItems();
        
        for (JVMFlagsProfile profile : jvmFlagsProfiles) {
            String profileName = profile.getName();
            
            jvmFlagsProfileItems.add(profileName);
            availableJVMFlagsProfiles.add(profile);
        }
        
        List<ServerProfile> serverProfiles = ServerProfileHandler.getProfiles();
        ObservableList<String> serverProfileItems = serverProfilesChoice.getItems();
        
        for (ServerProfile profile : serverProfiles) {
            String profileName = profile.getName();
            
            serverProfileItems.add(profileName);
            availableServerProfiles.add(profile);
        }
    }    

    /**
     * Sets a development profile processor
     * @param processor development profile processor
     */
    public void setProcessor(DevelopmentProfileProcessor processor) {
        this.processor = processor;
    }
    
    /**
     * Edits an existing development profile
     * @param developmentProfile existing development profile
     */
    public void editDevelopmentProfile(DevelopmentProfile developmentProfile) {
        mainHeaderLabel.setText("Edit Development Profile");
        
        profileNameInput.setText(developmentProfile.getName());
        lowerMemoryInput.setText(developmentProfile.getLowerMemory());
        upperMemoryInput.setText(developmentProfile.getUpperMemory());
        jvmFlagsStringInput.setText(developmentProfile.getJVMFlagsString());
        minecraftServerArgumentsInput.setText(developmentProfile.getMinecraftServerArguments());
        customServerWorldNameInput.setText(developmentProfile.getCustomServerWorldName());
        serverWorldsCheck.setSelected(developmentProfile.isUsingServerWorlds());
        updateOutdatedPluginsAutomaticallyCheck.setSelected(developmentProfile.isUpdatingOutdatedPluginsAutomatically());
        updateOutdatedServerAutomaticallyCheck.setSelected(developmentProfile.isUpdatingOutdatedServerAutomatically());
        useServerGUICheck.setSelected(developmentProfile.isUsingServerGUI());
        serverProfilesChoice.getSelectionModel().select(developmentProfile.getServerProfile().getName());
        plugins = developmentProfile.copyPlugins();
        
        ObservableList<String> pluginItems = pluginList.getItems();
        
        for (Plugin plugin : plugins) {
            pluginItems.add(plugin.getPath().toString());
        }
        
        editedDevelopmentProfile = developmentProfile;
        editMode = true;
    }

    @FXML
    public void onInsertButtonClick(ActionEvent event) {
        String jvmFlagsProfileName = jvmFlagsProfileChoice.getSelectionModel().getSelectedItem();
        
        if (StringUtil.isNullOrEmpty(jvmFlagsProfileName)) {
            Alert alert = AlertUtil.createAlert("Select a JVM Flags Profile first.");
            alert.show();
            
            return;
        }
         
        JVMFlagsProfile profile = getJVMFlagsProfile(jvmFlagsProfileName);
        String existingJVMFlagsString = jvmFlagsStringInput.getText();
        
        if (!existingJVMFlagsString.endsWith(" ")) {
            existingJVMFlagsString += " ";
        }
        
        existingJVMFlagsString += profile.getFlagsString();
        jvmFlagsStringInput.setText(existingJVMFlagsString);
    }
    
    @FXML
    public void onAddPluginButtonClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        Settings settings = Configuration.getSettings();
        String pluginJarfileBaseDirectoryRaw = settings.getSetting(SettingFields.PLUGIN_JARFILE_BASE_DIRECTORY);
        
        if (!StringUtil.isNullOrEmpty(pluginJarfileBaseDirectoryRaw)) {
            File pluginJarfileBaseDirectory = new File(pluginJarfileBaseDirectoryRaw);

            if (pluginJarfileBaseDirectory.exists()) {
                chooser.setInitialDirectory(pluginJarfileBaseDirectory);
            }
        }
        
        chooser.getExtensionFilters().add(new ExtensionFilter("Jarfile (*.jar)", "*.jar"));

        File fileChosen = chooser.showOpenDialog(null);
        
        if (fileChosen != null) {
            Path path = fileChosen.toPath();
            
            if (isExistingPluginPath(path)) {
                Alert alert = AlertUtil.createAlert("An existing plugin with this path exists.");
                alert.show();

                return;
            }
            
            String fileName = path.getFileName().toString();
            int pluginID = DevelopmentProfileHandler.getNextPluginID();
            Plugin plugin = new Plugin(pluginID, path);
            ObservableList<String> pluginItems = pluginList.getItems();
            
            pluginItems.add(path.toString());
            plugins.add(plugin);

            if (editedDevelopmentProfile != null) {
                Path pluginsFolder = editedDevelopmentProfile.getPluginsLocation();
                Path pluginFilePath = pluginsFolder.resolve(fileName);
                
                undoDeletedPathIfPresent(pluginFilePath);
            }
        }
    }
    
    @FXML
    public void onChangePluginButtonClick(ActionEvent event) {
        int selectedIndex = pluginList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a plugin first.");
            alert.show();
            
            return;
        }
        
        FileChooser chooser = new FileChooser();
        ObservableList<String> pluginItems = pluginList.getItems();
        String pluginPath = pluginItems.get(selectedIndex);
        Plugin plugin = getPlugin(pluginPath);
        Path existingPath = plugin.getPath();
        
        chooser.setInitialDirectory(existingPath.getParent().toFile());
        chooser.getExtensionFilters().add(new ExtensionFilter("Jarfile (*.jar)", "*.jar"));

        File fileChosen = chooser.showOpenDialog(null);
        
        if (fileChosen != null) {
            Path path = fileChosen.toPath();
            
            if (existingPath.equals(path)) {
                return;
            }

            if (isExistingPluginPath(path, plugin)) {
                Alert alert = AlertUtil.createAlert("An existing plugin with this path exists.");
                alert.show();

                return;
            }

            String fileName = path.getFileName().toString();
            
            if (editedDevelopmentProfile != null) {
                Path pluginsFolder = editedDevelopmentProfile.getPluginsLocation();
                Path pluginFilePath = pluginsFolder.resolve(plugin.getFileName());
                Path newPluginFilePath = pluginsFolder.resolve(fileName);
                
                if (pluginFilePath.toFile().exists()) {
                    pathsToDelete.add(pluginFilePath);
                }

                undoDeletedPathIfPresent(newPluginFilePath);
            }
            
            plugin.setPath(path);
            
            pluginItems.set(selectedIndex, path.toString());
        }
    }
    
    @FXML
    public void onDeletePluginButtonClick(ActionEvent event) {
        int selectedIndex = pluginList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a plugin first.");
            alert.show();
            
            return;
        }
        
        ObservableList<String> pluginItems = pluginList.getItems();
        String pathString = pluginItems.remove(selectedIndex);
        Plugin plugin = deletePlugin(pathString);
        
        if (editedDevelopmentProfile != null) {
            Path pluginsFolder = editedDevelopmentProfile.getPluginsLocation();
            Path pluginFilePath = pluginsFolder.resolve(plugin.getFileName());

            if (pluginFilePath.toFile().exists()) {
                pathsToDelete.add(pluginFilePath);                
            }
        }
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) {
        String profileName = profileNameInput.getText();
        String lowerMemory = lowerMemoryInput.getText();
        String upperMemory = upperMemoryInput.getText();
        String jvmFlagsString = jvmFlagsStringInput.getText();
        String minecraftServerArguments = minecraftServerArgumentsInput.getText();
        String serverProfileName = serverProfilesChoice.getSelectionModel().getSelectedItem();
        String customServerWorldName = customServerWorldNameInput.getText();
        boolean serverWorlds = serverWorldsCheck.isSelected();
        boolean updateOutdatedPluginsAutomatically = updateOutdatedPluginsAutomaticallyCheck.isSelected();
        boolean updateOutdatedServerAutomatically = updateOutdatedServerAutomaticallyCheck.isSelected();
        boolean useServerGUI = useServerGUICheck.isSelected();

        ConditionalAlert ca = new ConditionalAlert()
                        .addCondition(StringUtil.isNullOrEmpty(profileName), "Profile name is blank.")
                        .addCondition(StringUtil.isNullOrEmpty(serverProfileName), "Server for testing has not been chosen.")
                        .addCondition(!StringUtil.isNullOrEmpty(lowerMemory) && !isValidMemoryArg(lowerMemory, MIN_XMS), "Lower memory is not a valid value.")
                        .addCondition(!StringUtil.isNullOrEmpty(upperMemory) && !isValidMemoryArg(upperMemory, MIN_XMX), "Upper memory is not a valid value.");
        
        if (!StringUtil.isNullOrEmpty(lowerMemory) && !StringUtil.isNullOrEmpty(upperMemory)) {
            ca.addCondition(getValueFromMemoryArg(lowerMemory) > getValueFromMemoryArg(upperMemory), "Lower memory value is higher than the upper memory value.");
        }
        
        if (!StringUtil.isNullOrEmpty(profileName)) {
            DevelopmentProfile existingProfile = DevelopmentProfileHandler.getProfile(profileName);

            ca.addCondition(existingProfile != null && existingProfile != editedDevelopmentProfile, "A profile by that name already exists.");
        }
        
        Alert alert = ca.getAlert();
        
        if (alert != null) {
            alert.show();
            
            return;
        }

        if (!plugins.isEmpty()) {
            String pluginsNotFound = "";
            
            for (Plugin plugin : plugins) {
                Path path = plugin.getPath();
                
                if (!path.toFile().exists()) {
                    if (!pluginsNotFound.isEmpty()) {
                        pluginsNotFound += "\n";
                    }
                    
                    pluginsNotFound += plugin.getFileName();
                }
            }
            
            if (!pluginsNotFound.isEmpty()) {
                alert = AlertUtil.createAlert("The source file to the following plugins was not found:\n\n" + pluginsNotFound);
                alert.show();
                
                return;
            }
        }

        if (!StringUtil.isNullOrEmpty(jvmFlagsString)) {
            jvmFlagsString = StringUtil.getUnduplicatedString(jvmFlagsString);
        }
        
        if (!StringUtil.isNullOrEmpty(minecraftServerArguments)) {
            minecraftServerArguments = StringUtil.getUnduplicatedString(minecraftServerArguments);
        }
        
        ServerProfile serverProfile = getServerProfile(serverProfileName);
        DevelopmentProfile profile = null;

        if (editMode) {
            editedDevelopmentProfile.setName(profileName);
            editedDevelopmentProfile.setLowerMemory(lowerMemory);
            editedDevelopmentProfile.setUpperMemory(upperMemory);
            editedDevelopmentProfile.setJVMFlagsString(jvmFlagsString);
            editedDevelopmentProfile.setMinecraftServerArguments(minecraftServerArguments);
            editedDevelopmentProfile.setServerProfile(serverProfile);
            editedDevelopmentProfile.setPlugins(plugins);
            editedDevelopmentProfile.setCustomServerWorldName(customServerWorldName);
            editedDevelopmentProfile.setUsingServerWorlds(serverWorlds);
            editedDevelopmentProfile.setUpdatingOutdatedPluginsAutomatically(updateOutdatedPluginsAutomatically);
            editedDevelopmentProfile.setUpdateOutdatedServerAutomatically(updateOutdatedServerAutomatically);
            editedDevelopmentProfile.setUsingServerGUI(useServerGUI);

            for (Path path : pathsToDelete) {
                path.toFile().delete();
            }
            
            profile = editedDevelopmentProfile;
        } else {
            profile = new DevelopmentProfile(profileName, lowerMemory, upperMemory, jvmFlagsString, minecraftServerArguments, serverProfile,
                                             customServerWorldName, serverWorlds, updateOutdatedPluginsAutomatically,
                                             updateOutdatedServerAutomatically, useServerGUI, plugins);

            DevelopmentProfileHandler.addDevelopmentProfile(profile);
        }

        processor.processDevelopmentProfile(profile, editMode);
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
