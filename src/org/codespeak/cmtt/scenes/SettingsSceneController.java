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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.Settings;
import org.codespeak.cmtt.Settings.SettingFields;

/**
 * Controller class for the settings scene
 *
 * @author Vector
 */
public class SettingsSceneController implements Initializable {

    private Settings settings = null;
    
    @FXML Label settingsLabel;
    @FXML Label minecraftLauncherLocationLabel;
    @FXML CheckBox checkUpdateOnStartupCheck;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        settings = Configuration.getSettings();
        
        minecraftLauncherLocationLabel.setText(settings.getSetting(SettingFields.MINECRAFT_LAUNCHER_LOCATION));
        checkUpdateOnStartupCheck.setSelected(settings.getSetting(SettingFields.CHECK_UPDATE_ON_STARTUP));
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
    public void onOKButtonClick(ActionEvent event) {
        settings.setSetting(SettingFields.MINECRAFT_LAUNCHER_LOCATION, minecraftLauncherLocationLabel.getText());
        settings.setSetting(SettingFields.CHECK_UPDATE_ON_STARTUP, checkUpdateOnStartupCheck.isSelected());

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
