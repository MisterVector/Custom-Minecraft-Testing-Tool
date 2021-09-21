package org.codespeak.cmtt.scenes;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.ConditionalAlert;
import org.codespeak.cmtt.objects.handlers.JavaProfileHandler;
import org.codespeak.cmtt.profiles.JavaProfile;
import org.codespeak.cmtt.util.StringUtil;

/**
 * FXML Controller class
 *
 * @author Vector
 */
public class AddEditJavaProfileSceneController implements Initializable {

    private JavaProfilesSceneController controller = null;
    private Stage controllerStage = null;
    private JavaProfile editedProfile = null;
    private boolean editMode = false;
    
    @FXML private TextField profileNameInput;
    @FXML private Label javaExecutablePathLabel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    /**
     * Sets the java profiles scene controller
     * @param controller java profiles scene controller
     */
    public void setController(JavaProfilesSceneController controller) {
        this.controller = controller;
    }
    
    /**
     * Sets the stage representing this controller
     * @param controllerStage stage representing this controller
     */
    public void setControllerStage(Stage controllerStage) {
        this.controllerStage = controllerStage;
    }
    
    /**
     * Edit the specified java profile
     * @param profile java profile to edit
     */
    public void editProfile(JavaProfile profile) {
        profileNameInput.setText(profile.getName());
        javaExecutablePathLabel.setText(profile.getJavaExecutablePath().toString());
        
        editedProfile = profile;
        editMode = true;
    }
    
    @FXML
    public void onSelectFileButtonClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        
        if (editMode) {
            Path javaExecutablePath = editedProfile.getJavaExecutablePath();
            
            chooser.setInitialDirectory(javaExecutablePath.toFile());
        }
        
        File chosenFile = chooser.showOpenDialog(null);
        
        if (chosenFile != null) {
            javaExecutablePathLabel.setText(chosenFile.toString());
        }
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) {
        String profileName = profileNameInput.getText();
        String javaExecutablePathRaw = javaExecutablePathLabel.getText();
        
        ConditionalAlert ca = new ConditionalAlert()
                        .addCondition(StringUtil.isNullOrEmpty(profileName), "Profile name is blank.")
                        .addCondition(StringUtil.isNullOrEmpty(javaExecutablePathRaw), "The path to the Java executable is not set.");
                
        if (!StringUtil.isNullOrEmpty(profileName)) {
            JavaProfile testProfile = JavaProfileHandler.getProfile(profileName);
            
            ca.addCondition(profileName.equalsIgnoreCase("system"), "You cannot use this profile name.");
            ca.addCondition(testProfile != null && testProfile != editedProfile, "That profile already exists.");
        }
                
        Alert alert = ca.getAlert();
        
        if (alert != null) {
            alert.show();
            
            return;
        }
        
        Path javaExecutablePath = Paths.get(javaExecutablePathRaw);
        JavaProfile profile = null;
        
        if (editMode) {
            editedProfile.setName(profileName);
            editedProfile.setJavaExecutablePath(javaExecutablePath);
            
            profile = editedProfile;
        } else {
            profile = new JavaProfile(profileName, javaExecutablePath);
            
            JavaProfileHandler.addProfile(profile);
        }
        
        controller.addEditJavaProfile(profile, editMode);
        controllerStage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        controllerStage.close();
    }
    
}
