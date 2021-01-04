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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.objects.DevelopmentProfileProcessor;
import org.codespeak.cmtt.objects.ProcessorContext;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;

/**
 * Controller for the main scene
 *
 * @author Vector
 */
public class MainSceneController implements Initializable, DevelopmentProfileProcessor {

    private List<DevelopmentProfile> availableDevelopmentProfiles = new ArrayList<DevelopmentProfile>();
    private int currentlySelectedIndex = -1;
    
    @FXML private ListView<String> developmentProfileList;
    
    private DevelopmentProfile getDevelopmentProfile(String profileName) {
        for (DevelopmentProfile developmentProfile : availableDevelopmentProfiles) {
            if (developmentProfile.getName().equalsIgnoreCase(profileName)) {
                return developmentProfile;
            }
        }
        
        return null;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<DevelopmentProfile> developmentProfiles = DevelopmentProfileHandler.getProfiles();
        ObservableList<String> developmentProfileItems = developmentProfileList.getItems();
        
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            developmentProfileItems.add(developmentProfile.getName());
            availableDevelopmentProfiles.add(developmentProfile);
        }
    }    

    @Override
    public void processDevelopmentProfile(DevelopmentProfile developmentProfile, boolean editMode) {
        ObservableList<String> developmentProfileItems = developmentProfileList.getItems();
        String profileName = developmentProfile.getName();
        
        if (editMode) {
            developmentProfileItems.set(currentlySelectedIndex, profileName);

            currentlySelectedIndex = -1;
        } else {
            developmentProfileItems.add(profileName);
            availableDevelopmentProfiles.add(developmentProfile);
        }
    }

    @Override
    public ProcessorContext getContext() {
        return ProcessorContext.MAIN_SCENE;
    }
    
    @FXML
    public void onSettingsMenuItemClick(ActionEvent event) throws IOException {
        StageController<SettingsSceneController> stageController = SceneUtil.getScene(SceneTypes.SETTINGS, "Settings for " + Configuration.PROGRAM_NAME);
        SettingsSceneController controller = stageController.getController();
        Stage stage = stageController.getStage();
        
        stage.show();
    }
    
    @FXML
    public void onQuitMenuItemClick(ActionEvent event) throws IOException {
        Platform.exit();
    }
    
    @FXML
    public void onJVMFlagsMenuItemClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.JVM_FLAGS, "JVM Flags Management").getStage();
        stage.show();
    }
    
    @FXML
    public void onServersMenuItemClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.SERVER_PROFILES, "Server Profiles Management").getStage();
        stage.show();
    }
    
    @FXML
    public void onAboutMenuItemClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.ABOUT, "About").getStage();
        stage.show();
    }
    
    @FXML
    public void onOpenDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a development profile first.");
            alert.show();
            
            return;
        }
        
        String profileName = developmentProfileList.getItems().get(selectedIndex);
        DevelopmentProfile profile = getDevelopmentProfile(profileName);
        StageController<OpenDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.OPEN_DEVELOPMENT_PROFILE, "Open Development Profile");
        Stage stage = stageController.getStage();
        OpenDevelopmentProfileSceneController controller = stageController.getController();

        stage.show();
        controller.openProfile(profile);
    }
    
    @FXML
    public void onAddDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        if (ServerProfileHandler.getProfiles().isEmpty()) {
            Alert alert = AlertUtil.createAlert(("Cannot create a development profile as no servers have been defined.\n\n"
                                               + "Go to Profiles -> Servers ... to setup one or more servers."));
            alert.show();
            
            return;
        }
        
        StageController<AddEditDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_DEVELOPMENT_PROFILE, "Add Development Profile");
        Stage stage = stageController.getStage();
        AddEditDevelopmentProfileSceneController controller = stageController.getController();
        
        stage.show();
        controller.setProcessor(this);
    }

    @FXML
    public void onEditDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a development profile first.");
            alert.show();
            
            return;
        }
        
        currentlySelectedIndex = selectedIndex;
        
        String profileName = developmentProfileList.getItems().get(selectedIndex);
        DevelopmentProfile profile = getDevelopmentProfile(profileName);
        
        StageController<AddEditDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_DEVELOPMENT_PROFILE, "Edit Development Profile");
        Stage stage = stageController.getStage();
        AddEditDevelopmentProfileSceneController controller = stageController.getController();
        
        stage.show();
        controller.setProcessor(this);
        
        controller.editDevelopmentProfile(profile);
    }

    @FXML
    public void onDeleteDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a development profile first.");
            alert.show();

            return;
        }
        
        String profileName = developmentProfileList.getItems().remove(selectedIndex);
        DevelopmentProfile profile = getDevelopmentProfile(profileName);

        profile.remove();
        DevelopmentProfileHandler.deleteProfile(profile.getId());
        availableDevelopmentProfiles.remove(profile);
    }
    
}
