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
import org.codespeak.cmtt.objects.ConditionalAlert;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.profiles.JVMFlagsProfile;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.profiles.Plugin;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the add/edit development profile scene
 *
 * @author Vector
 */
public class AddEditDevelopmentProfileSceneController implements Initializable {

    private MainSceneController controller = null;
    private List<JVMFlagsProfile> availableJVMFlagsProfiles = new ArrayList<JVMFlagsProfile>();
    private List<ServerProfile> availableServerProfiles = new ArrayList<ServerProfile>();
    private List<Plugin> plugins = new ArrayList<Plugin>();
    private List<Plugin> deletedPlugins = new ArrayList<Plugin>();
    private DevelopmentProfile editedDevelopmentProfile = null;
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

    private JVMFlagsProfile getJVMFlagsProfile(String name) {
        for (JVMFlagsProfile jvmFlagsProfile : availableJVMFlagsProfiles) {
            if (jvmFlagsProfile.getName().equalsIgnoreCase(name)) {
                return jvmFlagsProfile;
            }
        }
        
        return null;
    }
    
    private ServerProfile getServerProfile(String name) {
        for (ServerProfile serverProfile : availableServerProfiles) {
            if (serverProfile.getName().equalsIgnoreCase(name)) {
                return serverProfile;
            }
        }
        
        return null;
    }
    
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
     * Sets the main scene controller
     * @param controller main scene controller
     */
    public void setController(MainSceneController controller) {
        this.controller = controller;
    }
    
    /**
     * Edits an existing development profile
     * @param developmentProfile existing development profile
     */
    public void editDevelopmentProfile(DevelopmentProfile developmentProfile) {
        profileNameInput.setText(developmentProfile.getName());
        lowerMemoryInput.setText(developmentProfile.getLowerMemory());
        upperMemoryInput.setText(developmentProfile.getUpperMemory());
        jvmFlagsStringInput.setText(developmentProfile.getJVMFlagsString());
        separateWorldsCheck.setSelected(developmentProfile.isSeparateWorlds());
        serverProfilesChoice.getSelectionModel().select(developmentProfile.getServerProfile().getName());
        plugins = developmentProfile.getPlugins();
        
        ObservableList<String> pluginItems = pluginList.getItems();
        
        for (Plugin plugin : plugins) {
            pluginItems.add(plugin.getFileName());
        }
        
        editedDevelopmentProfile = developmentProfile;
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
         
        JVMFlagsProfile profile = getJVMFlagsProfile(jvmFlagsProfileName);
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
        controller.editPlugin(plugin, (editedDevelopmentProfile != null ? editedDevelopmentProfile.getPluginsLocation() : null));
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

        ConditionalAlert ca = new ConditionalAlert();
        Alert alert = ca.addCondition(StringUtil.isNullOrEmpty(profileName), "Profile name is blank.")
                        .addCondition(StringUtil.isNullOrEmpty(lowerMemory), "Lower memory is blank.")
                        .addCondition(StringUtil.isNullOrEmpty(upperMemory), "Upper memory is blank.")
                        .addCondition(StringUtil.isNullOrEmpty(upperMemory), "Server profile has not been chosen.")
                        .addCondition(StringUtil.isNullOrEmpty(serverProfileName), "Server profile has not been chosen.")
                        .addCondition(plugins.isEmpty(), "You must add at least one plugin.")
                        .getAlert();
        
        if (alert == null) {
            DevelopmentProfile existingProfile = DevelopmentProfileHandler.getProfile(profileName);

            alert = ca.addCondition(existingProfile != null && existingProfile != editedDevelopmentProfile, "A profile by that name already exists.")
                      .getAlert();
        }
        
        if (alert != null) {
            alert.show();
            
            return;
        }
        
        if (!StringUtil.isNullOrEmpty(jvmFlagsString)) {
            jvmFlagsString = StringUtil.getUnduplicatedString(jvmFlagsString);
        }
        
        ServerProfile serverProfile = getServerProfile(serverProfileName);
        DevelopmentProfile profile = null;
        
        if (editMode) {
            editedDevelopmentProfile.setName(profileName);
            editedDevelopmentProfile.setLowerMemory(lowerMemory);
            editedDevelopmentProfile.setUpperMemory(upperMemory);
            editedDevelopmentProfile.setJVMFlagsString(jvmFlagsString);
            editedDevelopmentProfile.setServerProfile(serverProfile);
            editedDevelopmentProfile.setPlugins(plugins);
            editedDevelopmentProfile.setSeparateWorlds(separateWorlds);

            Path pluginsLocation = editedDevelopmentProfile.getPluginsLocation();

            for (Plugin plugin : deletedPlugins) {
                plugin.uninstall(pluginsLocation);
            }
            
            profile = editedDevelopmentProfile;
        } else {
            profile = new DevelopmentProfile(profileName, lowerMemory, upperMemory, jvmFlagsString, serverProfile, separateWorlds, plugins);
        }
        
        controller.finishAddEditDevelopmentProfile(profile, editMode);
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}