package org.codespeak.cmtt.scenes;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.CustomMinecraftTestingTool;
import org.codespeak.cmtt.objects.ProgramException;
import org.codespeak.cmtt.util.AlertUtil;

/**
 * Controller for the about scene
 *
 * @author Vector
 */
public class AboutSceneController implements Initializable {

    @FXML private Label headerLabel;
    @FXML private Label codeSpeakLinkLabel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        headerLabel.setText(Configuration.PROGRAM_NAME + " v" + Configuration.PROGRAM_VERSION);
    }    
 
    @FXML
    public void onCodeSpeakLinkClick() {
        try {
            Desktop desktop = Desktop.getDesktop();

            desktop.browse(new URI(Configuration.SITE_URL));
        } catch (IOException | URISyntaxException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
