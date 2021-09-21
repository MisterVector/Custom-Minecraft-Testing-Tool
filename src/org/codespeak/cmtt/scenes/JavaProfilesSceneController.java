package org.codespeak.cmtt.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.codespeak.cmtt.CustomMinecraftTestingTool;
import org.codespeak.cmtt.objects.ProgramException;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.JavaProfileHandler;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.profiles.JavaProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;

/**
 * FXML Controller class
 *
 * @author Vector
 */
public class JavaProfilesSceneController implements Initializable {

    private Stage controllerStage = null;
    private int currentlySelectedIndex = -1;
    
    @FXML ListView<String> profilesList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<JavaProfile> profiles = JavaProfileHandler.getProfiles();
        ObservableList<String> items = profilesList.getItems();
        
        for (JavaProfile profile : profiles) {
            items.add(profile.getName());
        }
    }    
    
    /**
     * Sets the controller stage
     * @param controllerStage controller stage
     */
    public void setControllerStage(Stage controllerStage) {
        this.controllerStage = controllerStage;
    }
    
    
    public void addEditJavaProfile(JavaProfile profile, boolean edited) {
        String profileName = profile.getName();
        ObservableList<String> items = profilesList.getItems();
        
        if (edited) {
            items.set(currentlySelectedIndex, profileName);
            currentlySelectedIndex = -1;
        } else {
            items.add(profileName);
        }
    }
    
    @FXML
    public void onAddProfileButtonClick(ActionEvent event) {
        try {
            StageController<AddEditJavaProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_JAVA_PROFILE, "Add Java Profile");
            Stage stage = stageController.getStage();
            AddEditJavaProfileSceneController controller = stageController.getController();
            
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
        ObservableList<String> items = profilesList.getItems();
        int selectedIndex = profilesList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }

        String profileName = items.get(selectedIndex);
        JavaProfile profile = JavaProfileHandler.getProfile(profileName);
        currentlySelectedIndex = selectedIndex;
        
        try {
            StageController<AddEditJavaProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_JAVA_PROFILE, "Edit Java Profile");
            Stage stage = stageController.getStage();
            AddEditJavaProfileSceneController controller = stageController.getController();
            
            stage.show();
            controller.setController(this);
            controller.setControllerStage(stage);
            controller.editProfile(profile);
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();
            
            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onDeleteProfileButtonClick(ActionEvent event) {
        ObservableList<String> items = profilesList.getItems();
        int selectedIndex = profilesList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }

        Alert alert = AlertUtil.createAlert(AlertType.CONFIRMATION, "Are you sure you want to delete this profile?");
        
        alert.getButtonTypes().setAll(new ButtonType[] {ButtonType.YES, ButtonType.NO});
        
        ButtonType result = alert.showAndWait().get();
        
        if (result == ButtonType.YES) {
            String profileName = items.get(selectedIndex);
            JavaProfile javaProfile = JavaProfileHandler.getProfile(profileName);

            List<DevelopmentProfile> developmentProfiles = DevelopmentProfileHandler.getProfilesUsingJavaProfile(javaProfile);
            
            if (developmentProfiles.size() > 0) {
                String inUseMessage = "This profile is in use by the following development profiles:\n";
                
                for (DevelopmentProfile developmentProfile : developmentProfiles) {
                    inUseMessage += "\n" + developmentProfile.getName();
                }
                
                Alert alert2 = AlertUtil.createAlert(inUseMessage);
                alert2.show();
                
                return;
            }

            items.remove(selectedIndex);
            JavaProfileHandler.deleteProfile(profileName);
        }
    }

    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        controllerStage.close();
    }

}
