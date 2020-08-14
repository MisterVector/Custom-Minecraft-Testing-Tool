package org.codespeak.cmtt.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.profiles.DevelopmentType;
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
    
    private DevelopmentProfile getDevelopmentProfile(String profileName, DevelopmentType dt) {
        for (DevelopmentProfile developmentProfile : availableDevelopmentProfiles) {
            if (developmentProfile.getDevelopmentType() == dt && developmentProfile.getName().equalsIgnoreCase(profileName)) {
                return developmentProfile;
            }
        }
        
        return null;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<DevelopmentProfile> developmentProfiles = DevelopmentProfileHandler.getProfiles();
        ObservableList<String> pluginDevelopmentProfileItems = pluginDevelopmentProfileList.getItems();
        
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            String profileName = developmentProfile.getName();

            switch (developmentProfile.getDevelopmentType()) {
                case PLUGIN:
                    pluginDevelopmentProfileItems.add(profileName);
                    
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
        String profileName = developmentProfile.getName();
        
        if (editMode) {
            pluginDevelopmentProfileItems.set(currentlySelectedIndex, profileName);
            
            currentlySelectedIndex = -1;
        } else {
            pluginDevelopmentProfileItems.add(profileName);
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

}
