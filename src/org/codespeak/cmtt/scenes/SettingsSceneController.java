package org.codespeak.cmtt.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.Settings;

/**
 * FXML controller class for the settings scene
 *
 * @author Vector
 */
public class SettingsSceneController implements Initializable {

    private Settings settings = null;
    
    @FXML Label settingsLabel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        settings = Configuration.getSettings();
        
        settingsLabel.setText("Settings for " + Configuration.PROGRAM_NAME);
    }    

    @FXML
    public void onOKButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
