package org.codespeak.cmtt.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.profiles.ServerProfile;
import org.codespeak.cmtt.objects.ServerTypes;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.util.AlertUtil;
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
    
    @FXML private Label headerLabel;
    @FXML private TextField profileNameInput;
    @FXML private TextField minecraftVersionInput;
    @FXML private ComboBox<String> serverTypeChoices;
    @FXML private TextField customPluginsArgumentInput;
    @FXML private TextField customWorldsArgumentInput;
    @FXML private Label serverPathLabel;
    @FXML private CheckBox autoUpdateCheck;
    
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
        customWorldsArgumentInput.setText(profile.getCustomWorldsArgument());
        serverPath = profile.getServerPath();
        serverPathLabel.setText(serverPath.toString());
        autoUpdateCheck.setSelected(profile.isAutoUpdate());

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
        customWorldsArgumentInput.setDisable(disableInput);
    }
    
    @FXML
    public void onSelectServerFileButtonClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        File chosenFile = chooser.showOpenDialog(null);
        
        if (chosenFile != null) {
            serverPath = chosenFile.toPath();
            serverPathLabel.setText(serverPath.toString());
        }
    }
    
    @FXML
    public void onSaveProfileButtonClick(ActionEvent event) throws IOException {
        String profileName = profileNameInput.getText();
        String minecraftVersion = minecraftVersionInput.getText();
        String serverTypeChosen = serverTypeChoices.getSelectionModel().getSelectedItem();
        String customPluginsArgument = customPluginsArgumentInput.getText();
        String customWorldsArgument = customWorldsArgumentInput.getText();
        boolean autoUpdate = autoUpdateCheck.isSelected();
        
        if (StringUtil.isNullOrEmpty(profileName)) {
            Alert alert = AlertUtil.createAlert("Profile name is empty.");
            alert.show();
            
            return;
        }
        
        if (StringUtil.isNullOrEmpty(minecraftVersion)) {
            Alert alert = AlertUtil.createAlert("Minecraft version is empty.");
            alert.show();
            
            return;
        }
        
        if (StringUtil.isNullOrEmpty(serverTypeChosen)) {
            Alert alert = AlertUtil.createAlert("Server type has not been chosen.");
            alert.show();
            
            return;
        }
        
        ServerTypes serverType = ServerTypes.fromName(serverTypeChosen);
        
        if (serverType == ServerTypes.CUSTOM) {
            if (StringUtil.isNullOrEmpty(customPluginsArgument)) {
                Alert alert = AlertUtil.createAlert("Custom plugins argument is empty.");
                alert.show();
                
                return;
            }
            
            if (StringUtil.isNullOrEmpty(customWorldsArgument)) {
                Alert alert = AlertUtil.createAlert("Custom worlds argument is empty.");
                alert.show();
                
                return;
            }
        }

        ServerProfile existingProfile = ServerProfileHandler.getProfile(profileName);
        
        if (existingProfile != null && existingProfile != editedServerProfile) {
            Alert alert = AlertUtil.createAlert("A profile by that name already exists.");
            alert.show();
            
            return;
        }
        
        ServerProfile profile = null;
        
        if (editMode) {
            editedServerProfile.setName(profileName);
            editedServerProfile.setMinecraftVersion(minecraftVersion);
            editedServerProfile.setServerType(serverType);
            editedServerProfile.setCustomPluginsArgument(customPluginsArgument);
            editedServerProfile.setCustomWorldsArgument(customWorldsArgument);
            editedServerProfile.setServerPath(serverPath);
            editedServerProfile.setAutoUpdate(autoUpdate);
            
            if (serverPath != null) {
                Path existingServerFile = Paths.get(Configuration.SERVERS_FOLDER + File.separator
                                        + editedServerProfile.getId() + File.separator
                                        + "server.jar");
                
                Files.copy(serverPath, existingServerFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            profile = editedServerProfile;
        } else {
            if (serverPath == null) {
                Alert alert = AlertUtil.createAlert("Select a file for the server.");
                alert.show();
                
                return;
            }
            
            profile = new ServerProfile(profileName, minecraftVersion, serverType, customPluginsArgument,
                                        customWorldsArgument, serverPath, autoUpdate);

            Path profileFolder = profile.getProfileLocation();
            Path profileServerFile = profile.getServerLocation();
            
            profileFolder.toFile().mkdir();
            Files.copy(serverPath, profileServerFile);
        }
    
        controller.finishAddEditServerProfile(profile, editMode);
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCloseWindowButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
