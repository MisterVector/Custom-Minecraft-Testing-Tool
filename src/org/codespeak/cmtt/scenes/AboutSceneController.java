package org.codespeak.cmtt.scenes;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the about scene
 *
 * @author Vector
 */
public class AboutSceneController implements Initializable {

    @FXML private Label codeSpeakLinkLabel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }    
 
    @FXML
    public void onCodeSpeakLinkClick() throws Exception {
        Desktop desktop = Desktop.getDesktop();
        
        desktop.browse(new URI(codeSpeakLinkLabel.getText()));
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
