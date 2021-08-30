package org.codespeak.cmtt.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.ConditionalAlert;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.profiles.JVMFlagsProfile;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller class for the Add/Edit JVM Profiles Scene
 *
 * @author Vector
 */
public class AddEditJVMFlagsProfileSceneController implements Initializable {

    private Stage controllerStage = null;
    private JVMFlagsProfilesSceneController controller = null;
    private JVMFlagsProfile editedProfile = null;
    private boolean editMode = false;
    
    @FXML private Label headerLabel;
    @FXML private TextField profileNameInput;
    @FXML private TextArea flagsStringInput;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    /**
     * Sets the stage representing this scene controller
     * @param controllerStage stage representing this scene controller
     */
    public void setControllerStage(Stage controllerStage) {
        this.controllerStage = controllerStage;
    }
    
    /**
     * Sets the JVM Flags Scene controller
     * @param controller JVM Flags Scene controller
     */
    public void setController(JVMFlagsProfilesSceneController controller) {
        this.controller = controller;
    }
    
    /**
     * Called when editing the specified JVM Flags profile
     * @param profile JVM Flags Profile to edit
     */
    public void editJVMFlagsProfile(JVMFlagsProfile profile) {
        profileNameInput.setText(profile.getName());
        flagsStringInput.setText(profile.getFlagsString());
        headerLabel.setText("Edit JVM Flags Profile");
        
        editedProfile = profile;
        editMode = true;
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) {
        String profileName = profileNameInput.getText();
        String flagsString = flagsStringInput.getText();
        
        ConditionalAlert ca = new ConditionalAlert()
                        .addCondition(StringUtil.isNullOrEmpty(profileName), "Profile name is eempty.")
                        .addCondition(StringUtil.isNullOrEmpty(flagsString), "Flags string is empty.");
                
        if (!StringUtil.isNullOrEmpty(profileName)) {
            JVMFlagsProfile existingProfile = JVMFlagsProfileHandler.getProfile(profileName);

            ca.addCondition(existingProfile != null && existingProfile != editedProfile, "A profile by that name already exists.");
        }
        
        Alert alert = ca.getAlert();
        
        if (alert != null) {
            alert.show();

            return;
        }

        flagsString = StringUtil.getUnduplicatedString(flagsString);

        JVMFlagsProfile profile = null;
        
        if (editMode) {
            editedProfile.setName(profileName);
            editedProfile.setFlagsString(flagsString);
            
            profile = editedProfile;
        } else {
            profile = new JVMFlagsProfile(profileName, flagsString);
            
            JVMFlagsProfileHandler.addProfile(profile);
        }
        
        controller.finishAddEditJVMFlagsProfile(profile, editMode);
        controllerStage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        controllerStage.close();
    }
    
}
