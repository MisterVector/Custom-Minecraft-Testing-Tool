package org.codespeak.cmtt.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.profiles.JVMFlagsProfile;
import org.codespeak.cmtt.profiles.ServerDevelopmentProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * FXML Controller class
 *
 * @author Vector
 */
public class AddEditServerDevelopmentProfileSceneController implements Initializable {

    private MainSceneController controller = null;
    private List<JVMFlagsProfile> jvmFlagProfiles = new ArrayList<JVMFlagsProfile>();
    private Path serverPath = null;
    private ServerDevelopmentProfile editedProfile = null;
    private boolean editMode = false;

    @FXML private Label headerLabel;
    @FXML private TextField profileNameInput;
    @FXML private TextField lowerMemoryInput;
    @FXML private TextField upperMemoryInput;
    @FXML private TextArea jvmFlagsStringInput;
    @FXML private ComboBox<String> jvmFlagsProfileChoice;
    @FXML private Label serverPathLabel;
    
    private JVMFlagsProfile getJVMFlagsProfile(String profileName) {
        for (JVMFlagsProfile profile : jvmFlagProfiles) {
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
        List<JVMFlagsProfile> allJVMFlagProfiles = JVMFlagsProfileHandler.getProfiles();
        ObservableList<String> jvmFlagsProfileItems = jvmFlagsProfileChoice.getItems();
        
        for (JVMFlagsProfile profile : allJVMFlagProfiles) {
            jvmFlagProfiles.add(profile);
            jvmFlagsProfileItems.add(profile.getName());
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
     * Edits an existing server development profile
     * @param profile server development profile
     */
    public void editServerDevelopmentProfile(ServerDevelopmentProfile profile) {
        profileNameInput.setText(profile.getName());
        lowerMemoryInput.setText(profile.getLowerMemory());
        upperMemoryInput.setText(profile.getUpperMemory());
        jvmFlagsStringInput.setText(profile.getJVMFlagsString());
        serverPath = profile.getServerPath();
        serverPathLabel.setText(serverPath.toString());
        
        editedProfile = profile;
        editMode = true;
        
        headerLabel.setText("Edit Server Development Profile");
    }
    
    @FXML
    public void onInsertButtonClick(ActionEvent event) {
        String jvmFlagsProfileName = jvmFlagsProfileChoice.getSelectionModel().getSelectedItem();
     
        if (StringUtil.isNullOrEmpty(jvmFlagsProfileName)) {
            Alert alert = AlertUtil.createAlert("Select a JVM flags profile first.");
            alert.show();
            
            return;
        }
        
        JVMFlagsProfile profile = getJVMFlagsProfile(jvmFlagsProfileName);
        String jvmFlagsString = jvmFlagsStringInput.getText();
        
        if (!jvmFlagsString.endsWith(" ")) {
            jvmFlagsString += " ";
        }
        
        jvmFlagsString += profile.getFlagsString();
        
        jvmFlagsStringInput.setText(jvmFlagsString);
    }
    
    @FXML
    public void onSelectServerFileButtonClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        File chosenFile = chooser.showOpenDialog(null);
        
        if (chosenFile != null) {
            serverPath = chosenFile.toPath();
            serverPathLabel.setText(chosenFile.toString());
        }
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) throws IOException {
        String profileName = profileNameInput.getText();
        String lowerMemory = lowerMemoryInput.getText();
        String upperMemory = upperMemoryInput.getText();
        String jvmFlagsString = jvmFlagsStringInput.getText();
        
        if (StringUtil.isNullOrEmpty(profileName)) {
            Alert alert = AlertUtil.createAlert("Profile name is blank.");
            alert.show();
            
            return;
        }
        
        if (StringUtil.isNullOrEmpty(lowerMemory)) {
            Alert alert = AlertUtil.createAlert("Lower memory is blank.");
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
        
        if (serverPath == null) {
            Alert alert = AlertUtil.createAlert("Server file has not been chosen.");
            alert.show();
            
            return;
        }
        
        jvmFlagsString = StringUtil.getUnduplicatedString(jvmFlagsString);
        
        ServerDevelopmentProfile profile = null;
        
        if (editMode) {
            editedProfile.setName(profileName);
            editedProfile.setLowerMemory(lowerMemory);
            editedProfile.setUpperMemory(upperMemory);
            editedProfile.setJVMFlagsString(jvmFlagsString);
            editedProfile.setServerPath(serverPath);
            
            Path profileLocation = editedProfile.getLocation();
            Path existingServerPath = profileLocation.resolve("server.jar");

            Files.copy(serverPath, existingServerPath, StandardCopyOption.REPLACE_EXISTING);

            profile = editedProfile;
        } else {
            profile = new ServerDevelopmentProfile(profileName, lowerMemory, upperMemory, jvmFlagsString, serverPath);

            DevelopmentProfileHandler.addDevelopmentProfile(profile);

            Path profileLocation = profile.getLocation();
            Path newServerPath = profileLocation.resolve("server.jar");

            profileLocation.toFile().mkdir();

            Files.copy(serverPath, newServerPath);
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
