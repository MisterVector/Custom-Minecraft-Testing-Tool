 package org.codespeak.cmtt.scenes;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.codespeak.cmtt.Configuration;
import org.codespeak.cmtt.Settings;
import org.codespeak.cmtt.Settings.SettingFields;
import org.codespeak.cmtt.objects.CheckVersionResponse;
import org.codespeak.cmtt.objects.DevelopmentProfileProcessor;
import org.codespeak.cmtt.objects.ProcessorContext;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.ServerProfileHandler;
import org.codespeak.cmtt.profiles.DevelopmentProfile;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.SceneUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the main scene
 *
 * @author Vector
 */
public class MainSceneController implements Initializable, DevelopmentProfileProcessor {

    private List<DevelopmentProfile> availableDevelopmentProfiles = new ArrayList<DevelopmentProfile>();
    private int currentlySelectedIndex = -1;
    
    @FXML private ListView<String> developmentProfileList;
    
    private DevelopmentProfile getDevelopmentProfile(String profileName) {
        for (DevelopmentProfile developmentProfile : availableDevelopmentProfiles) {
            if (developmentProfile.getName().equalsIgnoreCase(profileName)) {
                return developmentProfile;
            }
        }
        
        return null;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<DevelopmentProfile> developmentProfiles = DevelopmentProfileHandler.getProfiles();
        ObservableList<String> developmentProfileItems = developmentProfileList.getItems();
        
        for (DevelopmentProfile developmentProfile : developmentProfiles) {
            developmentProfileItems.add(developmentProfile.getName());
            availableDevelopmentProfiles.add(developmentProfile);
        }
    }    

    @Override
    public void processDevelopmentProfile(DevelopmentProfile developmentProfile, boolean editMode) {
        ObservableList<String> developmentProfileItems = developmentProfileList.getItems();
        String profileName = developmentProfile.getName();
        
        if (editMode) {
            developmentProfileItems.set(currentlySelectedIndex, profileName);

            currentlySelectedIndex = -1;
        } else {
            developmentProfileItems.add(profileName);
            availableDevelopmentProfiles.add(developmentProfile);
        }
    }

    @Override
    public ProcessorContext getContext() {
        return ProcessorContext.MAIN_SCENE;
    }

    /**
     * Checks for a new version of the program
     * @param startup if the program's version is being checked at startup
     * @throws IOException if there is an error while checking for updates, this
     * will be thrown
     */
    public void checkVersion(boolean startup) throws IOException {
        Settings settings = Configuration.getSettings();
        boolean checkUpdateOnStartup = settings.getSetting(SettingFields.CHECK_UPDATE_ON_STARTUP);
        
        if (!startup || checkUpdateOnStartup) {
            CheckVersionResponse response = CheckVersionResponse.checkVersion();
            String requestVersion = response.getRequestVersion();
            Timestamp requestReleaseTime = response.getRequestReleaseTime();
            String version = response.getVersion();
            Timestamp releaseTime = response.getReleaseTime();
            
            if (releaseTime.after(requestReleaseTime)) {
                Alert alert = AlertUtil.createAlert("A new version is currently available!\n\n"
                                                  + "Current version: " + requestVersion + "\n"
                                                  + "New version: " + version + "\n\n"
                                                  + "Would you like to view the changelog and download the latest version?", "Update for " + Configuration.PROGRAM_NAME);
                alert.getButtonTypes().setAll(new ButtonType[] {ButtonType.YES, ButtonType.NO});
                
                ButtonType result = alert.showAndWait().get();
                
                if (result == ButtonType.YES) {
                    Desktop desktop = Desktop.getDesktop();
                    
                    desktop.browse(URI.create(Configuration.UPDATE_SUMMARY_URL));
                }
            } else {
                if (!startup) {
                    Alert alert = AlertUtil.createAlert("This program is currently up-to-date.");
                    alert.show();
                }
            }
        }
    }
    
    @FXML
    public void onSettingsMenuItemClick(ActionEvent event) throws IOException {
        StageController<SettingsSceneController> stageController = SceneUtil.getScene(SceneTypes.SETTINGS, "Settings");
        SettingsSceneController controller = stageController.getController();
        Stage stage = stageController.getStage();
        
        stage.show();
    }

    @FXML
    public void onStartMinecraftMenuItemClick(ActionEvent event) throws IOException {
        Settings settings = Configuration.getSettings();
        String minecraftLauncherLocation = settings.getSetting(SettingFields.MINECRAFT_LAUNCHER_LOCATION);
        
        if (StringUtil.isNullOrEmpty(minecraftLauncherLocation)) {
            Alert alert = AlertUtil.createAlert("The Minecraft launcher location has not been set.");
            alert.show();
            
            return;
        }
        
        Path minecraftLauncherPath = Paths.get(minecraftLauncherLocation);
        
        if (!minecraftLauncherPath.toFile().exists()) {
            Alert alert = AlertUtil.createAlert("The Minecraft launcher file does not exist.");
            alert.show();
            
            return;
        }
        
        ProcessBuilder pb = new ProcessBuilder(minecraftLauncherLocation);
        pb.directory(minecraftLauncherPath.getParent().toFile());
        pb.start();
    }
    
    @FXML
    public void onQuitMenuItemClick(ActionEvent event) throws IOException {
        Platform.exit();
    }
    
    @FXML
    public void onJVMFlagsMenuItemClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.JVM_FLAGS, "JVM Flags Profile Management").getStage();
        stage.show();
    }
    
    @FXML
    public void onServersMenuItemClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.SERVER_PROFILES, "Server Profile Management").getStage();
        stage.show();
    }
    
    @FXML
    public void onAboutMenuItemClick(ActionEvent event) throws IOException {
        Stage stage = SceneUtil.getScene(SceneTypes.ABOUT, "About").getStage();
        stage.show();
    }
    
    @FXML
    public void onCheckForUpdateMenuItemClick(ActionEvent event) throws IOException {
        checkVersion(false);
    }
    
    @FXML
    public void onOpenDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }
        
        String profileName = developmentProfileList.getItems().get(selectedIndex);
        DevelopmentProfile profile = getDevelopmentProfile(profileName);
        StageController<OpenDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.OPEN_DEVELOPMENT_PROFILE, "Open Development Profile");
        Stage stage = stageController.getStage();
        OpenDevelopmentProfileSceneController controller = stageController.getController();

        stage.show();
        controller.openProfile(profile);
    }
    
    @FXML
    public void onAddDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        if (ServerProfileHandler.getProfiles().isEmpty()) {
            Alert alert = AlertUtil.createAlert(("Cannot create a profile as no servers have been defined.\n\n"
                                               + "Go to Profiles -> Servers ... to setup one or more servers."));
            alert.show();
            
            return;
        }
        
        StageController<AddEditDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_DEVELOPMENT_PROFILE, "Add Development Profile");
        Stage stage = stageController.getStage();
        AddEditDevelopmentProfileSceneController controller = stageController.getController();
        
        stage.show();
        controller.setProcessor(this);
    }

    @FXML
    public void onEditDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }
        
        currentlySelectedIndex = selectedIndex;
        
        String profileName = developmentProfileList.getItems().get(selectedIndex);
        DevelopmentProfile profile = getDevelopmentProfile(profileName);
        
        StageController<AddEditDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_DEVELOPMENT_PROFILE, "Edit Development Profile");
        Stage stage = stageController.getStage();
        AddEditDevelopmentProfileSceneController controller = stageController.getController();
        
        stage.show();
        controller.setProcessor(this);
        
        controller.editDevelopmentProfile(profile);
    }

    @FXML
    public void onDeleteDevelopmentProfileButtonClick(ActionEvent event) throws IOException {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();

            return;
        }
        
        String profileName = developmentProfileList.getItems().remove(selectedIndex);
        DevelopmentProfile profile = getDevelopmentProfile(profileName);

        profile.remove();
        DevelopmentProfileHandler.deleteProfile(profile.getId());
        availableDevelopmentProfiles.remove(profile);
    }
    
}
