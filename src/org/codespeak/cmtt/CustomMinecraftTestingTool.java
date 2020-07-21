package org.codespeak.cmtt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.codespeak.cmtt.scenes.SceneTypes;
import org.codespeak.cmtt.util.SceneUtil;

/**
 *
 * @author Vector
 */
public class CustomMinecraftTestingTool extends Application {
    
    private static CustomMinecraftTestingTool instance = null;
    
    public CustomMinecraftTestingTool() {
        instance = this;
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        stage = SceneUtil.getScene(SceneTypes.MAIN, Configuration.PROGRAM_TITLE).getStage();
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Gets an instance of this class
     * @return instance of this class
     */
    public static CustomMinecraftTestingTool getInstance() {
        return instance;
    }
    
}
