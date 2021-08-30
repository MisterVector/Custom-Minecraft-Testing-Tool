package org.codespeak.cmtt.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.codespeak.cmtt.CustomMinecraftTestingTool;
import org.codespeak.cmtt.objects.ProgramException;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.profiles.JVMFlagsProfile;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;

/**
 * Controller for the JVM flags profile scene
 *
 * @author Vector
 */
public class JVMFlagsProfilesSceneController implements Initializable {

    private Stage controllerStage = null;
    private List<JVMFlagsProfile> availableJVMFlagsProfiles = new ArrayList<JVMFlagsProfile>();
    private int currentlySelectedIndex = -1;
    
    @FXML private ListView<String> profileList;
    
    private JVMFlagsProfile getJVMFlagsProfile(String name) {
        for (JVMFlagsProfile profile : availableJVMFlagsProfiles) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return profile;
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
        ObservableList<String> items = profileList.getItems();
        
        for (JVMFlagsProfile profile : jvmFlagsProfiles) {
            String name = profile.getName();
            
            items.add(name);
            availableJVMFlagsProfiles.add(profile);
        }
    }    

    /**
     * Sets the stage representing this scene controller
     * @param controllerStage stage representing this scene controller
     */
    public void setControllerStage(Stage controllerStage) {
        this.controllerStage = controllerStage;
    }
    
    /**
     * Finishes adding or editing a JVM Flags profile
     * @param profile the JVM Flags profile being added or edited
     * @param editMode whether the profile is being edited
     */
    public void finishAddEditJVMFlagsProfile(JVMFlagsProfile profile, boolean editMode) {
        ObservableList<String> items = profileList.getItems();
        String profileName = profile.getName();
        
        if (editMode) {
            items.set(currentlySelectedIndex, profileName);
        } else {
            items.add(profileName);
            availableJVMFlagsProfiles.add(profile);
        }
    }
    
    @FXML
    public void onAddProfileButtonClick(ActionEvent event) {
        try {
            StageController<AddEditJVMFlagsProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_JVM_FLAGS_PROFILE, "Add JVM Flags Profile");
            AddEditJVMFlagsProfileSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();
    
            controller.setController(this);
            controller.setControllerStage(stage);
            stage.show();
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();
            
            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onEditProfileButtonClick(ActionEvent event) {
        int selectedIndex = profileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }

        currentlySelectedIndex = selectedIndex;
        
        String profileName = profileList.getItems().get(selectedIndex);
        JVMFlagsProfile profile = getJVMFlagsProfile(profileName);
        
        try {
            StageController<AddEditJVMFlagsProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_JVM_FLAGS_PROFILE, "Edit JVM Flags Profile");
            AddEditJVMFlagsProfileSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();
            
            controller.setController(this);
            controller.setControllerStage(stage);
            controller.editJVMFlagsProfile(profile);
            stage.show();
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();
            
            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onDeleteProfileButtonClick(ActionEvent event) {
        int selectedIndex = profileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }

        Alert alert = AlertUtil.createAlert("Are you sure you want to delete this profile?");
        alert.getButtonTypes().setAll(new ButtonType[] {ButtonType.YES, ButtonType.NO});
        
        ButtonType button = alert.showAndWait().get();
        
        if (button == ButtonType.YES) {
            ObservableList<String> items = profileList.getItems();
            String profileName = items.get(selectedIndex);
            JVMFlagsProfile profile = getJVMFlagsProfile(profileName);

            JVMFlagsProfileHandler.deleteProfile(profile.getId());
            
            items.remove(selectedIndex);
            availableJVMFlagsProfiles.remove(profile);
        }
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        controllerStage.close();
    }
    
}
