package org.codespeak.cmtt.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.codespeak.cmtt.objects.ServerProfile;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;

/**
 * Controller for the ServerProfilesScene scene
 *
 * @author Vector
 */
public class ServerProfilesSceneController implements Initializable {

    private Map<String, Integer> idNameMap = new HashMap<String, Integer>();
    private int currentlySelectedIndex = -1;
    
    @FXML private ListView<String> profileList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<ServerProfile> profiles = ServerProfileHandler.getProfiles();
        ObservableList<String> items = profileList.getItems();
        
        for (ServerProfile profile : profiles) {
            int id = profile.getId();
            String profileName = profile.getName();
            
            items.add(profileName);
            idNameMap.put(profileName, id);
        }
    }    
    
    /**
     * Called when finished adding or editing a server profile
     * @param profile server profile being added or edited
     * @param editMode whether the server profile was edited
     */
    public void finishAddEditServerProfile(ServerProfile profile, boolean editMode) {
        ObservableList<String> items = profileList.getItems();
        
        if (editMode) {
            String oldProfileName = items.get(currentlySelectedIndex);
            int id = profile.getId();
            String profileName = profile.getName();
            
            if (!oldProfileName.equals(profileName)) {
                idNameMap.remove(oldProfileName);
                idNameMap.put(profileName, id);
                
                items.set(currentlySelectedIndex, profileName);
            }
        } else {
            int id = profile.getId();
            String profileName = profile.getName();
            
            items.add(profileName);
            idNameMap.put(profileName, id);
        }
    }
    
    @FXML
    public void onAddProfileButtonClick(ActionEvent event) throws IOException {
        StageController<AddEditServerProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_SERVER_PROFILE, "Add Server Profile");
        AddEditServerProfileSceneController controller = stageController.getController();
        Stage stage = stageController.getStage();

        stage.show();
        controller.setController(this);
    }
    
    @FXML
    public void onEditProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = profileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            Alert alert = AlertUtil.createAlert("Select a server profile first.");
            alert.show();
            
            return;
        }
        
        currentlySelectedIndex = selectedIndex;
        
        String profileName = profileList.getItems().get(selectedIndex);
        int profileId = idNameMap.get(profileName);
        ServerProfile profile = ServerProfileHandler.fromID(profileId);
        
        StageController<AddEditServerProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_SERVER_PROFILE, "Edit Server Profile");
        AddEditServerProfileSceneController controller = stageController.getController();
        Stage stage = stageController.getStage();
        
        stage.show();
        controller.setController(this);
        controller.editServerProfile(profile);
    }
    
    @FXML
    public void onDeleteProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = profileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            Alert alert = AlertUtil.createAlert("Select a server profile first.");
            alert.show();
            
            return;
        }
        
        Alert confirmAlert = AlertUtil.createAlert("Are you sure you want to delete this server profile?");
        
        confirmAlert.getButtonTypes().setAll(new ButtonType[] {ButtonType.YES, ButtonType.NO});
        
        ButtonType result = confirmAlert.showAndWait().get();
        
        if (result == ButtonType.YES) {
            ObservableList<String> items = profileList.getItems();
            String profileName = items.get(selectedIndex);
            int profileId = idNameMap.get(profileName);

            ServerProfile profile = ServerProfileHandler.deleteProfile(profileId);
            Path profileFolder = profile.getProfileLocation();
            
            Files.walk(profileFolder)
             .sorted(Comparator.reverseOrder())
             .map(Path::toFile)
             .forEach(File::delete);

            items.remove(selectedIndex);
            idNameMap.remove(profileName);
        }
    }
    
    @FXML
    public void onWipeServerButtonClick(ActionEvent event) {
        
    }
    
    @FXML
    public void onCloseWindowButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
