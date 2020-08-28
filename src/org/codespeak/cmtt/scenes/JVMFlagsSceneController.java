package org.codespeak.cmtt.scenes;

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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.codespeak.cmtt.profiles.JVMFlagsProfile;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the JVM flags profile scene
 *
 * @author Vector
 */
public class JVMFlagsSceneController implements Initializable {

    private List<JVMFlagsProfile> availableJVMFlagsProfiles = new ArrayList<JVMFlagsProfile>();
    private JVMFlagsProfile editedProfile = null;
    private int editedIndex = -1;
    private boolean isEditMode = false;
    
    @FXML private TextField profileNameInput;
    @FXML private TextArea flagsStringInput;
    @FXML private ListView<String> profileList;
    @FXML private Button cancelEditButton;
    
    private JVMFlagsProfile getJVMFlagsProfile(String name) {
        for (JVMFlagsProfile jvmFlagsProfile : availableJVMFlagsProfiles) {
            if (jvmFlagsProfile.getName().equalsIgnoreCase(name)) {
                return jvmFlagsProfile;
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
    
    @FXML
    public void onAddProfileButtonClick(ActionEvent event) {
        String profileName = profileNameInput.getText();
        String flagsString = flagsStringInput.getText();
        
        if (StringUtil.isNullOrEmpty(profileName)) {
            Alert alert = AlertUtil.createAlert("Profile name is eempty.");
            alert.show();
            
            return;
        }
        
        if (StringUtil.isNullOrEmpty(flagsString)) {
            Alert alert = AlertUtil.createAlert("Flags string is empty.");
            alert.show();
            
            return;
        }
        
        flagsString = StringUtil.getUnduplicatedString(flagsString);
        
        JVMFlagsProfile existingProfile = JVMFlagsProfileHandler.getProfile(profileName);
        
        if (existingProfile != null && existingProfile != editedProfile) {
            Alert alert = AlertUtil.createAlert("A profile by that name already exists.");
            alert.show();
            
            return;
        }
        
        if (isEditMode) {
            editedProfile.setName(profileName);
            editedProfile.setFlagsString(flagsString);
            
            profileList.getItems().set(editedIndex, profileName);
            
            cancelEditButton.setDisable(true);
            editedProfile = null;
            editedIndex = -1;
            isEditMode = false;
        } else {
            JVMFlagsProfile profile = new JVMFlagsProfile(profileName, flagsString);
            
            JVMFlagsProfileHandler.addProfile(profile);

            profileList.getItems().add(profileName);
            availableJVMFlagsProfiles.add(profile);
        }
        
        profileNameInput.clear();
        flagsStringInput.clear();
    }
    
    @FXML
    public void onEditProfileButtonClick(ActionEvent event) {
        int selectedIndex = profileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            Alert alert = AlertUtil.createAlert("Select a JVM Flags profile first.");
            alert.show();
            
            return;
        }

        String profileName = profileList.getItems().get(selectedIndex);
        editedProfile = getJVMFlagsProfile(profileName);
        
        profileNameInput.setText(profileName);
        flagsStringInput.setText(editedProfile.getFlagsString());
        
        isEditMode = true;
        editedIndex = selectedIndex;
        cancelEditButton.setDisable(false);
    }
    
    @FXML
    public void onCancelEditButtonClick(ActionEvent event) {
        profileNameInput.clear();
        flagsStringInput.clear();
        
        editedProfile = null;
        editedIndex = -1;
        isEditMode = false;
        
        cancelEditButton.setDisable(true);
    }
    
    @FXML
    public void onDeleteProfileButtonClick(ActionEvent event) {
        if (isEditMode) {
            Alert alert = AlertUtil.createAlert("Cancel edit first before deleting a profile.");
            alert.show();
            
            return;
        }
        
        int selectedIndex = profileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            Alert alert = AlertUtil.createAlert("Select a JVM Flags profile first.");
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
    public void onCloseWindowButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
