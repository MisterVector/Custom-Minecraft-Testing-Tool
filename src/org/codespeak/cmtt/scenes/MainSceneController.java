 package org.codespeak.cmtt.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.profiles.DevelopmentType;
import org.codespeak.cmtt.profiles.PluginDevelopmentProfile;
import org.codespeak.cmtt.profiles.ServerDevelopmentProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;

/**
 * Controller for the main scene
 *
 * @author Vector
 */
public class MainSceneController implements Initializable {

    private List<DevelopmentProfile> availableDevelopmentProfiles = new ArrayList<DevelopmentProfile>();
    private int currentlySelectedIndex = -1;
    
    @FXML private ListView<String> pluginDevelopmentProfileList;
    @FXML private ListView<String> serverDevelopmentProfileList;
    
    private <T extends DevelopmentProfile> T getDevelopmentProfile(String profileName, DevelopmentType dt) {
        for (DevelopmentProfile developmentProfile : availableDevelopmentProfiles) {
            if (developmentProfile.getDevelopmentType() == dt && developmentProfile.getName().equalsIgnoreCase(profileName)) {
                return (T) developmentProfile;
            }
        }
        
        return null;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<DevelopmentProfile> developmentProfiles = DevelopmentProfileHandler.getProfiles();
        ObservableList<String> pluginDevelopmentProfileItems = pluginDevelopmentProfileList.getItems();
        ObservableList<String> serverDevelopmentProfileItems = serverDevelopmentProfileList.getItems();
        
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            String profileName = developmentProfile.getName();

            switch (developmentProfile.getDevelopmentType()) {
                case PLUGIN:
                    pluginDevelopmentProfileItems.add(profileName);
                    
                    break;
                case SERVER:
                    serverDevelopmentProfileItems.add(profileName);
                    
                    break;
            }
            
            availableDevelopmentProfiles.add(developmentProfile);
        }
    }    

    /**
     * Finishes adding or editing a development profile
     * @param developmentProfile development profile to add or edit
     * @param editMode whether the development profile is edited
     */
    public void finishAddEditDevelopmentProfile(DevelopmentProfile developmentProfile, boolean editMode) {
        ObservableList<String> pluginDevelopmentProfileItems = pluginDevelopmentProfileList.getItems();
        ObservableList<String> serverDevelopmentProfileItems = serverDevelopmentProfileList.getItems();
        DevelopmentType developmentType = developmentProfile.getDevelopmentType();
        String profileName = developmentProfile.getName();
        
        if (editMode) {
            switch (developmentType) {
                case PLUGIN:
                    pluginDevelopmentProfileItems.set(currentlySelectedIndex, profileName);
                    
                    break;
                    
                case SERVER:
                    serverDevelopmentProfileItems.set(currentlySelectedIndex, profileName);
                    
                    break;
            }
            
            currentlySelectedIndex = -1;
        } else {
            switch (developmentType) {
                case PLUGIN:
                    pluginDevelopmentProfileItems.add(profileName);
                    
                    break;
                    
                case SERVER:
                    serverDevelopmentProfileItems.add(profileName);
                    
                    break;
            }
            
            availableDevelopmentProfiles.add(developmentProfile);
        }
    }

    @FXML
    public void onJVMFlagsMenuClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.JVM_FLAGS, "JVM Flags Management").getStage();
        stage.show();
    }
    
    @FXML
    public void onServersMenuItemClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.SERVER_PROFILES, "Server Profiles Management").getStage();
        stage.show();
    }
    
    @FXML
    public void onAddPluginDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        StageController<AddEditPluginDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_PLUGIN_DEVELOPMENT_PROFILE, "Add Plugin Development Profile");
        Stage stage = stageController.getStage();
        AddEditPluginDevelopmentProfileSceneController controller = stageController.getController();
        
        stage.show();
        controller.setController(this);
    }

    @FXML
    public void onEditPluginDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = pluginDevelopmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a plugin development profile first.");
            alert.show();
            
            return;
        }
        
        currentlySelectedIndex = selectedIndex;
        
        String profileName = pluginDevelopmentProfileList.getItems().get(selectedIndex);
        PluginDevelopmentProfile profile = getDevelopmentProfile(profileName, DevelopmentType.PLUGIN);
        
        StageController<AddEditPluginDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_PLUGIN_DEVELOPMENT_PROFILE, "Edit Plugin Development Profile");
        Stage stage = stageController.getStage();
        AddEditPluginDevelopmentProfileSceneController controller = stageController.getController();
        
        stage.show();
        controller.setController(this);
        
        controller.editPluginDevelopmentProfile(profile);
    }

    @FXML
    public void onDeletePluginDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = pluginDevelopmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a plugin development profile first.");
            alert.show();

            return;
        }
        
        String profileName = pluginDevelopmentProfileList.getItems().remove(selectedIndex);
        PluginDevelopmentProfile profile = getDevelopmentProfile(profileName, DevelopmentType.PLUGIN);
        Path profileLocation = profile.getLocation();
        
        if (profileLocation.toFile().exists()) {
            Files.walk(profile.getLocation())
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
                
        DevelopmentProfileHandler.deleteProfile(profile.getId());
        
        availableDevelopmentProfiles.remove(profile);
    }
    
    @FXML
    public void onAddServerDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        StageController<AddEditServerDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_SERVER_DEVELOPMENT_PROFILE, "Add Server Development Profile");
        Stage stage = stageController.getStage();
        AddEditServerDevelopmentProfileSceneController controller = stageController.getController();
        
        stage.show();
        controller.setController(this);
    }

    @FXML
    public void onEditServerDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = serverDevelopmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a server development profile first.");
            alert.show();
            
            return;
        }
        
        currentlySelectedIndex = selectedIndex;
        
        String profileName = serverDevelopmentProfileList.getItems().get(selectedIndex);
        ServerDevelopmentProfile profile = getDevelopmentProfile(profileName, DevelopmentType.SERVER);
        
        StageController<AddEditServerDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_SERVER_DEVELOPMENT_PROFILE, "Edit Server Development Profile");
        Stage stage = stageController.getStage();
        AddEditServerDevelopmentProfileSceneController controller = stageController.getController();
        
        stage.show();
        controller.setController(this);
        
        controller.editServerDevelopmentProfile(profile);
    }

    @FXML
    public void onDeleteServerDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = serverDevelopmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a server development profile first.");
            alert.show();

            return;
        }
        
        String profileName = serverDevelopmentProfileList.getItems().remove(selectedIndex);
        ServerDevelopmentProfile profile = getDevelopmentProfile(profileName, DevelopmentType.SERVER);
        Path profileLocation = profile.getLocation();
        
        if (profileLocation.toFile().exists()) {
            Files.walk(profile.getLocation())
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
                
        DevelopmentProfileHandler.deleteProfile(profile.getId());
        
        availableDevelopmentProfiles.remove(profile);
    }
    
}
