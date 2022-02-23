package org.codespeak.cmtt.scenes;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.Settings;
import org.codespeak.cmtt.Settings.SettingFields;
import org.codespeak.cmtt.objects.ConditionalAlert;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.codespeak.cmtt.objects.ServerTypes;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.util.MiscUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the AddEditServerProfile scene
 *
 * @author Vector
 */
public class AddEditServerProfileSceneController implements Initializable {

    private Stage controllerStage = null;
    private ServerProfilesSceneController controller;
    private Path serverPath = null;
    private ServerProfile editedServerProfile = null;
    private boolean editMode = false;
    
    @FXML private Label headerLabel;
    @FXML private TextField profileNameInput;
    @FXML private Label minecraftVersionLabel;
    @FXML private ComboBox<String> serverTypeChoices;
    @FXML private TextField customPluginsArgumentInput;
    @FXML private TextField customWorldNameArgumentInput;
    @FXML private TextField customWorldsArgumentInput;
    @FXML private Label serverPathLabel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> items = serverTypeChoices.getItems();
        
        for (ServerTypes st : ServerTypes.values()) {
            items.add(st.getName());
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
     * Sets the ServerProfilesSceneController controller
     * @param controller ServerProfilesSceneController controller
     */
    public void setController(ServerProfilesSceneController controller) {
        this.controller = controller;
    }
    
    /**
     * Edits the specified server profile
     * @param profile server profile to edit
     */
    public void editServerProfile(ServerProfile profile) {
        profileNameInput.setText(profile.getName());
        minecraftVersionLabel.setText(profile.getMinecraftVersion());
        serverTypeChoices.getSelectionModel().select(profile.getServerType().getName());
        customPluginsArgumentInput.setText(profile.getCustomPluginsArgument());
        customWorldNameArgumentInput.setText(profile.getCustomWorldNameArgument());
        customWorldsArgumentInput.setText(profile.getCustomWorldsArgument());
        serverPath = profile.getServerPath();
        serverPathLabel.setText(serverPath.toString());

        headerLabel.setText("Edit Server Profile");
        
        editedServerProfile = profile;
        editMode = true;        
    }

    @FXML
    public void onSelectServerType(ActionEvent event) {
        String currentServerTypeChoice = serverTypeChoices.getSelectionModel().getSelectedItem();
        ServerTypes currentServerType = ServerTypes.fromName(currentServerTypeChoice);
        boolean disableInput = currentServerType != ServerTypes.CUSTOM;
        
        customPluginsArgumentInput.setDisable(disableInput);
        customWorldNameArgumentInput.setDisable(disableInput);
        customWorldsArgumentInput.setDisable(disableInput);
    }
    
    @FXML
    public void onSelectServerFileButtonClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        File workingFile = null;

        if (serverPath != null) {
            workingFile = MiscUtil.getWorkingFile(serverPath.getParent());
        }
        
        if (workingFile != null) {
            chooser.setInitialDirectory(workingFile);
        } else {
            Settings settings = Configuration.getSettings();
            String serverJarfileBaseDirectoryRaw = settings.getSetting(SettingFields.SERVER_JARFILE_BASE_DIRECTORY);
            
            if (!StringUtil.isNullOrEmpty(serverJarfileBaseDirectoryRaw)) {
                File serverJarfileBaseDirectory = new File(serverJarfileBaseDirectoryRaw);
                
                if (serverJarfileBaseDirectory.exists()) {
                    chooser.setInitialDirectory(serverJarfileBaseDirectory);
                }
            }
        }
        
        File chosenFile = chooser.showOpenDialog(null);

        if (chosenFile != null) {
            Path chosenPath = chosenFile.toPath();
            
            serverPath = chosenFile.toPath();
            serverPathLabel.setText(serverPath.toString());
        }
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) {
        String profileName = profileNameInput.getText();
        String serverTypeChosen = serverTypeChoices.getSelectionModel().getSelectedItem();
        String customPluginsArgument = customPluginsArgumentInput.getText();
        String customWorldNameArgument = customWorldNameArgumentInput.getText();
        String customWorldsArgument = customWorldsArgumentInput.getText();
        ServerTypes serverType = null;
        
        if (!StringUtil.isNullOrEmpty(serverTypeChosen)) {
            serverType = ServerTypes.fromName(serverTypeChosen);
        }

        ConditionalAlert ca = new ConditionalAlert()
                        .addCondition(StringUtil.isNullOrEmpty(profileName), "Profile name is empty.")
                        .addCondition(serverType == null, "Server type has not been chosen.")
                        .addCondition(serverPath == null, "Select a file for the server.");

        if (serverPath != null) {
            ca.addCondition(!serverPath.toFile().exists(), "The server file no longer exists.");
        }
        
        if (!StringUtil.isNullOrEmpty(profileName)) {
            ServerProfile existingProfile = ServerProfileHandler.getProfile(profileName);

            ca.addCondition(existingProfile != null && existingProfile != editedServerProfile, "A profile by that name already exists.");
        }
        
        Alert alert = ca.getAlert();
        
        if (alert != null) {
            alert.show();

            return;
        }
        
        ServerProfile profile = null;
        
        if (editMode) {
            ServerTypes oldServerType = editedServerProfile.getServerType();
            
            if (oldServerType != serverType) {
                Path eulaPath = editedServerProfile.getEULALocation();
                File eulaFile = eulaPath.toFile();
                
                if (eulaFile.exists()) {
                    eulaFile.delete();
                }
            }
            
            editedServerProfile.setName(profileName);
            editedServerProfile.setServerType(serverType);
            editedServerProfile.setCustomPluginsArgument(customPluginsArgument);
            editedServerProfile.setCustomWorldNameArgument(customWorldNameArgument);
            editedServerProfile.setCustomWorldsArgument(customWorldsArgument);
            editedServerProfile.setServerPath(serverPath);

            if (editedServerProfile.hasUpdate()) {
                editedServerProfile.setChecksum("");
            }

            profile = editedServerProfile;
        } else {
            profile = new ServerProfile(profileName, serverType, customPluginsArgument,
                                        customWorldNameArgument, customWorldsArgument, serverPath, "");

            ServerProfileHandler.addProfile(profile);
        }

        controller.finishAddEditServerProfile(profile, editMode);
        controllerStage.close();
    }

    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        controllerStage.close();
    }
    
}
