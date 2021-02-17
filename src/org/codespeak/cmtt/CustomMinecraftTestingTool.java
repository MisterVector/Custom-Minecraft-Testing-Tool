package org.codespeak.cmtt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.ErrorType;
import org.codespeak.cmtt.objects.ProgramException;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.JVMFlagsProfileHandler;
import org.codespeak.cmtt.objects.handlers.MappedDataHandler;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.scenes.MainSceneController;
import org.codespeak.cmtt.scenes.SceneTypes;
import org.codespeak.cmtt.util.SceneUtil;
import org.json.JSONObject;

/**
 * The main class
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
                MappedDataHandler.loadFromJSON(json);
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
        MappedDataHandler.saveToJSON(json);
        
        PrintWriter writer = new PrintWriter(new FileOutputStream(new File(Configuration.DATA_FILE)));
        writer.write(json.toString(4));
        writer.close();
    }
    
    /**
     * Logs a program error
     * @param ex exception to log
     */
    public static void logError(ProgramException ex) {
        Date nowDate = new Date();
        SimpleDateFormat logFolderSDF = new SimpleDateFormat("M-d-yyyy");
        String fileDateFormat = logFolderSDF.format(nowDate);
        SimpleDateFormat exceptionTimeSDF = new SimpleDateFormat("M-d-yyyy h:mm:ss a");
        String logDateFormat = exceptionTimeSDF.format(nowDate);
        File logFolder = new File(Configuration.LOGS_FOLDER);
        File logFilename = logFolder.toPath().resolve("error-" + fileDateFormat + ".txt").toFile();

        if (!logFolder.exists()) {
            logFolder.mkdir();
        }

        try {
            PrintWriter writer = new PrintWriter(new FileWriter(logFilename, true));
            
            StackTraceElement[] ste = ex.getStackTrace();
            
            writer.println("Exception time: " + logDateFormat);
            writer.println();
            writer.println(ex.getLocalizedMessage());
            writer.println();
            
            for (StackTraceElement element : ste) {
                writer.println(element);
            }
            
            writer.println();
            writer.println();
            writer.close();
        } catch (IOException ioe) {
            
        }
    }
    
}
