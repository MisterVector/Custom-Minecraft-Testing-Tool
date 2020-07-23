package org.codespeak.cmtt.scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.codespeak.cmtt.util.SceneUtil;

/**
 * Controller for the main scene
 *
 * @author Vector
 */
public class MainSceneController implements Initializable {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    public void onJVMFlagsButtonClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.JVM_FLAGS_SCENE, "JVM Flags Management").getStage();
        stage.show();
    }
    
}
