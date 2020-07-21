package org.codespeak.cmtt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
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
