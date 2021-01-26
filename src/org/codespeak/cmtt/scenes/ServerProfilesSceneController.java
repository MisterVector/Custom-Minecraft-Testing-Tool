package org.codespeak.cmtt.scenes;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
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
import javafx.scene.control.MultipleSelectionModel;
import javafx.stage.Stage;
import org.codespeak.cmtt.CustomMinecraftTestingTool;
import org.codespeak.cmtt.objects.ProgramException;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;

/**
 * Controller for the server profiles scene
 *
 * @author Vector
 */
public class ServerProfilesSceneController implements Initializable {

    private List<ServerProfile> availableServerProfiles = new ArrayList<ServerProfile>();
    private int currentlySelectedIndex = -1;
    
    @FXML private ListView<String> profileList;
    
    private ServerProfile getServerProfile(String profileName) {
        for (ServerProfile profile : availableServerProfiles) {
            if (profile.getName().equalsIgnoreCase(profileName)) {
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
        List<ServerProfile> serverProfiles = ServerProfileHandler.getProfiles();
        ObservableList<String> items = profileList.getItems();
        
        for (ServerProfile profile : serverProfiles) {
            String profileName = profile.getName();
            
            items.add(profileName);
            availableServerProfiles.add(profile);
        }
    }    
    
    /**
     * Called when finished adding or editing a server profile
     * @param profile server profile being added or edited
     * @param editMode whether the server profile was edited
     */
    public void finishAddEditServerProfile(ServerProfile profile, boolean editMode) {
        ObservableList<String> items = profileList.getItems();
        String profileName = profile.getName();
        
        if (editMode) {
            items.set(currentlySelectedIndex, profileName);
        } else {
            items.add(profileName);
            availableServerProfiles.add(profile);
        }
    }
    
    @FXML
    public void onAddProfileButtonClick(ActionEvent event) {
        try {
            StageController<AddEditServerProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_SERVER_PROFILE, "Add Server Profile");
            AddEditServerProfileSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();

            stage.show();
            controller.setController(this);
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
        
        try {
            String profileName = profileList.getItems().get(selectedIndex);
            ServerProfile profile = getServerProfile(profileName);

            StageController<AddEditServerProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_SERVER_PROFILE, "Edit Server Profile");
            AddEditServerProfileSceneController controller = stageController.getController();
            Stage stage = stageController.getStage();

            stage.show();
            controller.setController(this);
            controller.editServerProfile(profile);

            currentlySelectedIndex = selectedIndex;
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
        
        Alert confirmAlert = AlertUtil.createAlert("Are you sure you want to delete this profile?");
        
        confirmAlert.getButtonTypes().setAll(new ButtonType[] {ButtonType.YES, ButtonType.NO});
        
        ButtonType result = confirmAlert.showAndWait().get();
        
        if (result == ButtonType.YES) {
            ObservableList<String> items = profileList.getItems();
            String profileName = items.get(selectedIndex);
            ServerProfile profile = getServerProfile(profileName);
            
            profile.remove();
            
            items.remove(selectedIndex);
            availableServerProfiles.remove(profile);
        }
    }
    
    @FXML
    public void onBrowseFilesButtonClick(ActionEvent event) {
        MultipleSelectionModel<String> selectionModel = profileList.getSelectionModel();
        int selectedIndex = selectionModel.getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }
        
        try {
            String serverName = selectionModel.getSelectedItem();
            ServerProfile serverProfile = ServerProfileHandler.getProfile(serverName);
            Path profileLocation = serverProfile.getProfileLocation().toAbsolutePath();
            Desktop desktop = Desktop.getDesktop();

            desktop.open(profileLocation.toFile());
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
