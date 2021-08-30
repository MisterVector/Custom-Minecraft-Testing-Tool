package org.codespeak.cmtt.scenes;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.Settings;
import org.codespeak.cmtt.Settings.SettingFields;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller class for the settings scene
 *
 * @author Vector
 */
public class SettingsSceneController implements Initializable {

    private Stage controllerStage = null;
    private Settings settings = null;
    
    @FXML Label settingsLabel;
    @FXML Label minecraftLauncherLocationLabel;
    @FXML Label pluginJarfileBaseDirectoryLabel;
    @FXML Label serverJarfileBaseDirectoryLabel;
    @FXML CheckBox rememberSelectedDevelopmentProfileCheck;
    @FXML CheckBox checkUpdateOnStartupCheck;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        settings = Configuration.getSettings();
        
        minecraftLauncherLocationLabel.setText(settings.getSetting(SettingFields.MINECRAFT_LAUNCHER_LOCATION));
        pluginJarfileBaseDirectoryLabel.setText(settings.getSetting(SettingFields.PLUGIN_JARFILE_BASE_DIRECTORY));
        serverJarfileBaseDirectoryLabel.setText(settings.getSetting(SettingFields.SERVER_JARFILE_BASE_DIRECTORY));
        rememberSelectedDevelopmentProfileCheck.setSelected(settings.getSetting(SettingFields.REMEMBER_SELECTED_DEVELOPMENT_PROFILE));
        checkUpdateOnStartupCheck.setSelected(settings.getSetting(SettingFields.CHECK_UPDATE_ON_STARTUP));
    }    

    /**
     * Sets the stage representing this scene controller
     * @param controllerStage stage representing this scene controller
     */
    public void setControllerStage(Stage controllerStage) {
        this.controllerStage = controllerStage;
    }
    
    @FXML
    public void onMinecraftLauncherselectFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        File minecraftLauncherFile = chooser.showOpenDialog(null);
        
        if (minecraftLauncherFile != null) {
            minecraftLauncherLocationLabel.setText(minecraftLauncherFile.toString());
        }
    }
    
    @FXML
    public void onPluginJarfileBaseDirectorSelectDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        String pluginJarfileBaseDirectoryRaw = pluginJarfileBaseDirectoryLabel.getText();
        
        if (!StringUtil.isNullOrEmpty(pluginJarfileBaseDirectoryRaw)) {
            File pluginJarfileBaseDirectory = new File(pluginJarfileBaseDirectoryRaw);
            
            if (pluginJarfileBaseDirectory.exists()) {
                chooser.setInitialDirectory(pluginJarfileBaseDirectory);
            }
        }
        
        File chosenDirectory = chooser.showDialog(null);
        
        if (chosenDirectory != null) {
            pluginJarfileBaseDirectoryLabel.setText(chosenDirectory.toString());
        }
    }
    
    @FXML
    public void onServerJarfileBaseDirectorSelectDirectory(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        String serverJarfileBaseDirectoryRaw = serverJarfileBaseDirectoryLabel.getText();
        
        if (!StringUtil.isNullOrEmpty(serverJarfileBaseDirectoryRaw)) {
            File serverJarfileBaseDirectory = new File(serverJarfileBaseDirectoryRaw);
            
            if (serverJarfileBaseDirectory.exists()) {
                chooser.setInitialDirectory(serverJarfileBaseDirectory);
            }
        }
        
        File chosenDirectory = chooser.showDialog(null);
        
        if (chosenDirectory != null) {
            serverJarfileBaseDirectoryLabel.setText(chosenDirectory.toString());
        }
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) {
        settings.setSetting(SettingFields.MINECRAFT_LAUNCHER_LOCATION, minecraftLauncherLocationLabel.getText());
        settings.setSetting(SettingFields.PLUGIN_JARFILE_BASE_DIRECTORY, pluginJarfileBaseDirectoryLabel.getText());
        settings.setSetting(SettingFields.SERVER_JARFILE_BASE_DIRECTORY, serverJarfileBaseDirectoryLabel.getText());
        settings.setSetting(SettingFields.REMEMBER_SELECTED_DEVELOPMENT_PROFILE, rememberSelectedDevelopmentProfileCheck.isSelected());
        settings.setSetting(SettingFields.CHECK_UPDATE_ON_STARTUP, checkUpdateOnStartupCheck.isSelected());

        controllerStage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        controllerStage.close();
    }

}
