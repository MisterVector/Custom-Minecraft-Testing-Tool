package org.codespeak.cmtt.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

    private ServerProfilesSceneController controller;
    private Path serverPath = null;
    private ServerProfile editedServerProfile = null;
    private boolean editMode = false;
    private Path deleteServerPath = null;
    
    @FXML private Label headerLabel;
    @FXML private TextField profileNameInput;
    @FXML private TextField minecraftVersionInput;
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
        minecraftVersionInput.setText(profile.getMinecraftVersion());
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

        if (serverPath != null) {
            chooser.setInitialDirectory(serverPath.getParent().toFile());
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
            
            if (serverPath != null) {
                if (serverPath.equals(chosenPath)) {
                    return;
                }
        
                if (serverPath.toFile().exists()) {
                    deleteServerPath = serverPath;
                }
            }
            
            serverPath = chosenFile.toPath();
            serverPathLabel.setText(serverPath.toString());
            
            if (deleteServerPath != null && deleteServerPath.equals(chosenPath)) {
                deleteServerPath = null;
            }
        }
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) {
        String profileName = profileNameInput.getText();
        String minecraftVersion = minecraftVersionInput.getText();
        String serverTypeChosen = serverTypeChoices.getSelectionModel().getSelectedItem();
        String customPluginsArgument = customPluginsArgumentInput.getText();
        String customWorldNameArgument = customWorldNameArgumentInput.getText();
        String customWorldsArgument = customWorldsArgumentInput.getText();
        ServerTypes serverType = null;
        
        if (!StringUtil.isNullOrEmpty(serverTypeChosen)) {
            serverType = ServerTypes.fromName(serverTypeChosen);
        }

        ConditionalAlert ca = new ConditionalAlert();
        Alert alert = ca.addCondition(StringUtil.isNullOrEmpty(profileName), "Profile name is empty.")
                        .addCondition(StringUtil.isNullOrEmpty(minecraftVersion), "Minecraft version is empty.")
                        .addCondition(serverType == null, "Server type has not been chosen.")
                        .addCondition(serverPath == null, "Select a file for the server.")
                        .getAlert();

        if (alert == null) {
            alert = ca.addCondition(!serverPath.toFile().exists(), "The server file no longer exists.")
                      .getAlert();
        }
        
        if (alert == null) {
            ServerProfile existingProfile = ServerProfileHandler.getProfile(profileName);

            alert = ca.addCondition(existingProfile != null && existingProfile != editedServerProfile, "A profile by that name already exists.")
                      .getAlert();
        }
        
        if (alert != null) {
            alert.show();

            return;
        }
        
        ServerProfile profile = null;
        
        if (editMode) {
            editedServerProfile.setName(profileName);
            editedServerProfile.setMinecraftVersion(minecraftVersion);
            editedServerProfile.setServerType(serverType);
            editedServerProfile.setCustomPluginsArgument(customPluginsArgument);
            editedServerProfile.setCustomWorldNameArgument(customWorldNameArgument);
            editedServerProfile.setCustomWorldsArgument(customWorldsArgument);
            editedServerProfile.setServerPath(serverPath);
            
            if (deleteServerPath != null) {
                deleteServerPath.toFile().delete();
                editedServerProfile.setChecksum("");
            }

            profile = editedServerProfile;
        } else {
            profile = new ServerProfile(profileName, minecraftVersion, serverType, customPluginsArgument,
                                        customWorldNameArgument, customWorldsArgument, serverPath, "");

            ServerProfileHandler.addProfile(profile);
        }

        controller.finishAddEditServerProfile(profile, editMode);
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
