package org.codespeak.cmtt.scenes;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.profiles.JVMFlagsProfile;
import org.codespeak.cmtt.profiles.PluginDevelopmentProfile;
import org.codespeak.cmtt.profiles.Plugin;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the add/edit plugin development profile scene
 *
 * @author Vector
 */
public class AddEditPluginDevelopmentProfileSceneController implements Initializable {

    private MainSceneController controller = null;
    private Map<String, Integer> jvmFlagsProfileIDNameMap = new HashMap<String, Integer>();
    private Map<String, Integer> serverProfileIDNameMap = new HashMap<String, Integer>();
    private List<Plugin> plugins = new ArrayList<Plugin>();
    private List<Plugin> deletedPlugins = new ArrayList<Plugin>();
    private PluginDevelopmentProfile editedPluginDevelopmentProfile = null;
    private int currentlySelectedIndex = -1;
    private boolean editMode = false;
    
    @FXML TextField profileNameInput;
    @FXML TextField lowerMemoryInput;
    @FXML TextField upperMemoryInput;
    @FXML TextArea jvmFlagsStringInput;
    @FXML ComboBox<String> jvmFlagsProfileChoice;
    @FXML ComboBox<String> serverProfilesChoice;
    @FXML CheckBox separateWorldsCheck;
    @FXML ListView<String> pluginList;

    private Plugin getPlugin(String pluginName) {
        for (Plugin plugin : plugins) {
            if (plugin.getFileName().equals(pluginName)) {
                return plugin;
            }
        }
        
        return null;
    }
    
    private Plugin deletePlugin(String pluginName) {
        for (Iterator<Plugin> it = plugins.iterator(); it.hasNext();) {
            Plugin plugin = it.next();
            
            if (plugin.getFileName().equals(pluginName)) {
                it.remove();
                return plugin;
            }
        }
        
        return null;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<JVMFlagsProfile> jvmFlagsProfiles = JVMFlagsProfileHandler.getProfiles();
        ObservableList<String> jvmFlagsProfileItems = jvmFlagsProfileChoice.getItems();
        
        for (JVMFlagsProfile profile : jvmFlagsProfiles) {
            int id = profile.getId();
            String profileName = profile.getName();
            
            jvmFlagsProfileItems.add(profileName);
            jvmFlagsProfileIDNameMap.put(profileName, id);
        }
        
        List<ServerProfile> serverProfiles = ServerProfileHandler.getProfiles();
        ObservableList<String> serverProfileItems = serverProfilesChoice.getItems();
        
        for (ServerProfile serverProfile : serverProfiles) {
            int id = serverProfile.getId();
            String profileName = serverProfile.getName();
            
            serverProfileItems.add(profileName);
            serverProfileIDNameMap.put(profileName, id);
        }
    }    
    
    /**
     * Sets the main scene controller
     * @param controller main scene controller
     */
    public void setController(MainSceneController controller) {
        this.controller = controller;
    }
    
    /**
     * Edits an existing plugin development profile
     * @param pluginDevelopmentProfile existing plugin development profile
     */
    public void editPluginDevelopmentProfile(PluginDevelopmentProfile pluginDevelopmentProfile) {
        profileNameInput.setText(pluginDevelopmentProfile.getName());
        lowerMemoryInput.setText(pluginDevelopmentProfile.getLowerMemory());
        upperMemoryInput.setText(pluginDevelopmentProfile.getUpperMemory());
        jvmFlagsStringInput.setText(pluginDevelopmentProfile.getJVMFlagsString());
        separateWorldsCheck.setSelected(pluginDevelopmentProfile.isSeparateWorlds());
        plugins = pluginDevelopmentProfile.getPlugins();
        
        ObservableList<String> pluginItems = pluginList.getItems();
        
        for (Plugin plugin : plugins) {
            pluginItems.add(plugin.getFileName());
        }
        
        editedPluginDevelopmentProfile = pluginDevelopmentProfile;
        editMode = true;
    }

    /**
     * Finish adding or editing a plugin
     * @param plugin the plugin being added or edited
     * @param editMode whether the plugin is being edited
     */
    public void finishAddEditPlugin(Plugin plugin, boolean editMode) {
        ObservableList<String> items = pluginList.getItems();
        String fileName = plugin.getFileName();
        
        if (editMode) {
            items.set(currentlySelectedIndex, fileName);
            
            currentlySelectedIndex = -1;
        } else {
            plugins.add(plugin);
            items.add(fileName);
        }
    }
 
    @FXML
    public void onInsertButtonClick(ActionEvent event) {
        String jvmFlagsProfileName = jvmFlagsProfileChoice.getSelectionModel().getSelectedItem();
        
        if (StringUtil.isNullOrEmpty(jvmFlagsProfileName)) {
            Alert alert = AlertUtil.createAlert("Select a JVM Flags Profile first.");
            alert.show();
            
            return;
        }
        
        int id = jvmFlagsProfileIDNameMap.get(jvmFlagsProfileName);
        JVMFlagsProfile profile = JVMFlagsProfileHandler.getProfile(id);
        String existingJVMFlagsString = jvmFlagsStringInput.getText();
        
        if (!existingJVMFlagsString.endsWith(" ")) {
            existingJVMFlagsString += " ";
        }
        
        existingJVMFlagsString += profile.getFlagsString();
        jvmFlagsStringInput.setText(existingJVMFlagsString);
    }
    
    @FXML
    public void onAddPluginButtonClick(ActionEvent event) throws IOException {
        StageController<AddEditPluginSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_PLUGIN, "Add Plugin");
        AddEditPluginSceneController controller = stageController.getController();
        Stage stage = stageController.getStage();
        
        stage.show();
        controller.setController(this);
    }
    
    @FXML
    public void onEditPluginButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = pluginList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a plugin first.");
            alert.show();
            
            return;
        }
        
        String fileName = pluginList.getItems().get(selectedIndex);
        Plugin plugin = getPlugin(fileName);
        StageController<AddEditPluginSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_PLUGIN, "Edit Plugin");
        AddEditPluginSceneController controller = stageController.getController();
        Stage stage = stageController.getStage();
        
        stage.show();
        controller.setController(this);
        controller.editPlugin(plugin, (editedPluginDevelopmentProfile != null ? editedPluginDevelopmentProfile.getPluginsLocation() : null));
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
        String pluginName = pluginItems.remove(selectedIndex);
        Plugin plugin = deletePlugin(pluginName);
        
        deletedPlugins.add(plugin);
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) {
        String profileName = profileNameInput.getText();
        String lowerMemory = lowerMemoryInput.getText();
        String upperMemory = upperMemoryInput.getText();
        String jvmFlagsString = jvmFlagsStringInput.getText();
        String serverProfileName = serverProfilesChoice.getSelectionModel().getSelectedItem();
        boolean separateWorlds = separateWorldsCheck.isSelected();
        
        if (StringUtil.isNullOrEmpty(profileName)) {
            Alert alert = AlertUtil.createAlert("Profile name is blank.");
            alert.show();
            
            return;
        }
        
        if (StringUtil.isNullOrEmpty(lowerMemory)) {
            Alert alert = AlertUtil.createAlert("Lower memory is blank..");
            alert.show();
            
            return;
        }

        if (StringUtil.isNullOrEmpty(upperMemory)) {
            Alert alert = AlertUtil.createAlert("Upper memory is blank.");
            alert.show();
            
            return;
        }

        if (StringUtil.isNullOrEmpty(jvmFlagsString)) {
            Alert alert = AlertUtil.createAlert("JVM flags string is blank.");
            alert.show();
            
            return;
        }
        
        if (StringUtil.isNullOrEmpty(serverProfileName)) {
            Alert alert = AlertUtil.createAlert("Server profile has not been chosen.");
            alert.show();
            
            return;
        }
        
        if (plugins.isEmpty()) {
            Alert alert = AlertUtil.createAlert("You must add at least one plugin.");
            alert.show();
            
            return;
        }

        List<String> unique = new ArrayList<String>();
        String[] parts = jvmFlagsString.trim().split(" ");
        
        jvmFlagsString = "";
        
        for (String part : parts) {
            if (!unique.contains(part)) {
                unique.add(part);
                
                if (!jvmFlagsString.isEmpty()) {
                    jvmFlagsString += " ";
                }
                
                jvmFlagsString += part;
            }
        }
        
        int serverProfileId = serverProfileIDNameMap.get(serverProfileName);
        ServerProfile serverProfile = ServerProfileHandler.fromID(serverProfileId);
        PluginDevelopmentProfile profile = null;
        
        if (editMode) {
            editedPluginDevelopmentProfile.setName(profileName);
            editedPluginDevelopmentProfile.setLowerMemory(lowerMemory);
            editedPluginDevelopmentProfile.setUpperMemory(upperMemory);
            editedPluginDevelopmentProfile.setJVMFlagsString(jvmFlagsString);
            editedPluginDevelopmentProfile.setServerProfile(serverProfile);
            editedPluginDevelopmentProfile.setPlugins(plugins);
            editedPluginDevelopmentProfile.setSeparateWorlds(separateWorlds);

            Path pluginsLocation = editedPluginDevelopmentProfile.getPluginsLocation();

            for (Plugin plugin : deletedPlugins) {
                plugin.uninstall(pluginsLocation);
            }
            
            profile = editedPluginDevelopmentProfile;
        } else {
            profile = DevelopmentProfileHandler.addPluginDevelopmentProfile(profileName, lowerMemory, upperMemory, jvmFlagsString, serverProfile, separateWorlds, plugins);
            
            Path profileLocation = profile.getLocation();
            
            profileLocation.toFile().mkdirs();
        }
        
        controller.finishAddEditPluginDevelopmentProfile(profile, editMode);
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
