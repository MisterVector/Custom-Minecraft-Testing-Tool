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
import org.codespeak.cmtt.CustomMinecraftTestingTool;
import org.codespeak.cmtt.Settings;
import org.codespeak.cmtt.Settings.SettingFields;
import org.codespeak.cmtt.objects.CheckVersionResponse;
import org.codespeak.cmtt.objects.DevelopmentProfileProcessor;
import org.codespeak.cmtt.objects.ProcessorContext;
import org.codespeak.cmtt.objects.ProgramException;
import org.codespeak.cmtt.objects.StageController;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.objects.handlers.MappedDataHandler;
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
        for (DevelopmentProfile profile : availableDevelopmentProfiles) {
            if (profile.getName().equalsIgnoreCase(profileName)) {
                return profile;
            }
        }
        
        return null;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Settings settings = Configuration.getSettings();
        List<DevelopmentProfile> developmentProfiles = DevelopmentProfileHandler.getProfiles();
        ObservableList<String> developmentProfileItems = developmentProfileList.getItems();
        boolean rememberSelectedDevelopmentProfile = settings.getSetting(SettingFields.REMEMBER_SELECTED_DEVELOPMENT_PROFILE);
        int selectedDevelopmentProfile = -1;

        if (MappedDataHandler.hasMappedData("selected_development_profile")) {
            selectedDevelopmentProfile = MappedDataHandler.getMappedData("selected_development_profile");
        }
        
        for (DevelopmentProfile profile : developmentProfiles) {
            developmentProfileItems.add(profile.getName());
            availableDevelopmentProfiles.add(profile);
        }
        
        if (rememberSelectedDevelopmentProfile && selectedDevelopmentProfile > -1
                && selectedDevelopmentProfile <= developmentProfileItems.size() - 1) {
            developmentProfileList.getSelectionModel().select(selectedDevelopmentProfile);
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
     * Gets the currently selected development profile index
     * @return currently selected development profile index
     */
    public int getSelectedDevelopmentProfileIndex() {
        return developmentProfileList.getSelectionModel().getSelectedIndex();
    }
    
    /**
     * Checks for a new version of the program
     * @param startup if the program's version is being checked at startup
     */
    public void checkVersion(boolean startup) {
        Settings settings = Configuration.getSettings();
        boolean checkUpdateOnStartup = settings.getSetting(SettingFields.CHECK_UPDATE_ON_STARTUP);
        
        if (!startup || checkUpdateOnStartup) {
            try {
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
            } catch (ProgramException | IOException ex) {
                ProgramException ex2 = ProgramException.fromException(ex);
                Alert alert = ex2.buildAlert();

                alert.show();
                CustomMinecraftTestingTool.logError(ex2);
            }
        }
    }
    
    @FXML
    public void onSettingsMenuItemClick(ActionEvent event) {
        try {
            StageController<SettingsSceneController> stageController = SceneUtil.getScene(SceneTypes.SETTINGS, "Settings");
            Stage stage = stageController.getStage();
            SettingsSceneController controller = stageController.getController();
            
            stage.show();            
            controller.setControllerStage(stage);            
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }

    @FXML
    public void onStartMinecraftMenuItemClick(ActionEvent event) {
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

        try {
            ProcessBuilder pb = new ProcessBuilder(minecraftLauncherLocation);
            pb.directory(minecraftLauncherPath.getParent().toFile());
            pb.start();
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onQuitMenuItemClick(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    public void onJVMFlagsMenuItemClick(ActionEvent event) {
        try {
            StageController<JVMFlagsProfilesSceneController> stageController = SceneUtil.getScene(SceneTypes.JVM_FLAGS_PROFILES, "JVM Flags Profile Management");
            Stage stage = stageController.getStage();
            JVMFlagsProfilesSceneController controller = stageController.getController();

            stage.show();
            controller.setControllerStage(stage);
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onServersMenuItemClick(ActionEvent event) {
        try {
            StageController<ServerProfilesSceneController> stageController = SceneUtil.getScene(SceneTypes.SERVER_PROFILES, "Server Profile Management");
            Stage stage = stageController.getStage();
            ServerProfilesSceneController controller = stageController.getController();

            stage.show();
            controller.setControllerStage(stage);
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onAboutMenuItemClick(ActionEvent event) {
        try {
            StageController<AboutSceneController> stageController = SceneUtil.getScene(SceneTypes.ABOUT, "About " + Configuration.PROGRAM_NAME);
            Stage stage = stageController.getStage();
            AboutSceneController controller = stageController.getController();
            
            stage.show();
            controller.setControllerStage(stage);
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onCheckForUpdateMenuItemClick(ActionEvent event) {
        checkVersion(false);
    }
    
    @FXML
    public void onOpenDevelopmentProfileButtonClick(ActionEvent event) {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }
        
        try {
            String profileName = developmentProfileList.getItems().get(selectedIndex);
            DevelopmentProfile profile = getDevelopmentProfile(profileName);
            StageController<OpenDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.OPEN_DEVELOPMENT_PROFILE, "Open Development Profile");
            Stage stage = stageController.getStage();
            OpenDevelopmentProfileSceneController controller = stageController.getController();

            stage.show();
            controller.setControllerStage(stage);
            controller.openProfile(profile);
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }
    
    @FXML
    public void onAddDevelopmentProfileButtonClick(ActionEvent event) {
        if (ServerProfileHandler.getProfiles().isEmpty()) {
            Alert alert = AlertUtil.createAlert(("Cannot create a profile as no servers have been defined.\n\n"
                                               + "Go to Profiles -> Servers ... to setup one or more servers."));
            alert.show();
            
            return;
        }
        
        try {
            StageController<AddEditDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_DEVELOPMENT_PROFILE, "Add Development Profile");
            Stage stage = stageController.getStage();
            AddEditDevelopmentProfileSceneController controller = stageController.getController();

            stage.show();
            controller.setControllerStage(stage);
            controller.setProcessor(this);
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }

    @FXML
    public void onEditDevelopmentProfileButtonClick(ActionEvent event) {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();
            
            return;
        }

        try {
            String profileName = developmentProfileList.getItems().get(selectedIndex);
            DevelopmentProfile profile = getDevelopmentProfile(profileName);

            StageController<AddEditDevelopmentProfileSceneController> stageController = SceneUtil.getScene(SceneTypes.ADD_EDIT_DEVELOPMENT_PROFILE, "Edit Development Profile");
            Stage stage = stageController.getStage();
            AddEditDevelopmentProfileSceneController controller = stageController.getController();

            stage.show();
            controller.setControllerStage(stage);
            controller.setProcessor(this);
            controller.editDevelopmentProfile(profile);

            currentlySelectedIndex = selectedIndex;
        } catch (IOException ex) {
            ProgramException ex2 = ProgramException.fromException(ex);
            Alert alert = ex2.buildAlert();

            alert.show();
            CustomMinecraftTestingTool.logError(ex2);
        }
    }

    @FXML
    public void onDeleteDevelopmentProfileButtonClick(ActionEvent event) {
        int selectedIndex = developmentProfileList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex == -1) {
            Alert alert = AlertUtil.createAlert("Select a profile first.");
            alert.show();

            return;
        }
        
        Alert alert = AlertUtil.createAlert("Are you sure you want to delete this profile?");
        
        alert.getButtonTypes().setAll(new ButtonType[] {ButtonType.YES, ButtonType.NO});
        
        ButtonType result = alert.showAndWait().get();
        
        if (result == ButtonType.YES) {
            String profileName = developmentProfileList.getItems().remove(selectedIndex);
            DevelopmentProfile profile = getDevelopmentProfile(profileName);

            profile.remove();
            DevelopmentProfileHandler.deleteProfile(profile.getId());
            availableDevelopmentProfiles.remove(profile);
        }
    }
    
}
