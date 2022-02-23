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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.Main;
import org.codespeak.cmtt.objects.ProgramException;

/**
 * Controller for the about scene
 *
 * @author Vector
 */
public class AboutSceneController implements Initializable {

    private Stage controllerStage = null;
    
    @FXML private Label headerLabel;
    @FXML private Label codeSpeakLinkLabel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        headerLabel.setText(Configuration.PROGRAM_NAME + " v" + Configuration.PROGRAM_VERSION);
    }    
 
    /**
     * Sets the stage representing this scene controller
     * @param controllerStage stage representing this scene controller
     */
    public void setControllerStage(Stage controllerStage) {
        this.controllerStage = controllerStage;
    }
    
    @FXML
    public void onCodeSpeakLinkClick() {
        try {
            Desktop desktop = Desktop.getDesktop();

            desktop.browse(new URI(Configuration.SITE_URL));
        } catch (IOException | URISyntaxException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);

            Main.handleError(ex2);
        }
    }
    
    @FXML
    public void onCloseButtonClick(ActionEvent event) {
        controllerStage.close();
    }
    
}
