package org.codespeak.cmtt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import javafx.application.Application;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.scenes.MainSceneController;
import org.codespeak.cmtt.scenes.SceneTypes;
import org.codespeak.cmtt.util.SceneUtil;
import org.json.JSONObject;

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
        StageController<MainSceneController> stageController = SceneUtil.getScene(SceneTypes.MAIN, Configuration.PROGRAM_NAME);
        stage = stageController.getStage();
        
        MainSceneController controller = stageController.getController();
        
        stage.show();
        controller.checkVersion(true);
    }

    @Override
    public void stop() throws FileNotFoundException {
        saveData();
        Configuration.saveSettings();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        loadData();

        File serversFolder = new File(Configuration.SERVERS_FOLDER);
        
        if (!serversFolder.exists()) {
            serversFolder.mkdir();
        }
        
        File developmentFolder = new File(Configuration.DEVELOPMENT_FOLDER);
        
        if (!developmentFolder.exists()) {
            developmentFolder.mkdirs();
        }

        launch(args);
    }
    
    /**
     * Gets an instance of this class
     * @return instance of this class
     */
    public static CustomMinecraftTestingTool getInstance() {
        return instance;
    }
    
    /**
     * Loads data from data.json
     */
    public static void loadData() {
        File dataFile = new File(Configuration.DATA_FILE);
        
        if (dataFile.exists()) {
            try {
                byte[] bytes = Files.readAllBytes(dataFile.toPath());
                String jsonString = new String(bytes);
                JSONObject json = new JSONObject(jsonString);
                
                JVMFlagsProfileHandler.loadProfilesFromJSON(json);
                ServerProfileHandler.loadProfilesFromJSON(json);
                DevelopmentProfileHandler.loadProfilesFromJSON(json);
            } catch (IOException ex) {
                
            }
        }
    }

    /**
     * Saves data to data.json
     */
    public static void saveData() throws FileNotFoundException {
        JSONObject json = new JSONObject();
        
        JVMFlagsProfileHandler.saveProfilesToJSON(json);
        ServerProfileHandler.saveProfilesToJSON(json);
        DevelopmentProfileHandler.saveProfilesToJSON(json);
        
        PrintWriter writer = new PrintWriter(new FileOutputStream(new File(Configuration.DATA_FILE)));
        writer.write(json.toString(4));
        writer.close();
    }
    
}
