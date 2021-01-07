package org.codespeak.cmtt.scenes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.handlers.DevelopmentProfileHandler;
import org.codespeak.cmtt.profiles.Plugin;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.MiscUtil;

/**
 * Controller for the add/edit plugin scene
 *
 * @author Vector
 */
public class AddEditPluginSceneController implements Initializable {

    private AddEditDevelopmentProfileSceneController controller = null;
    private Path path = null;
    
    private Plugin editedPlugin = null;
    private Path pluginsFolder = null;
    private boolean editMode = false;
    
    @FXML private Label headerLabel;
    @FXML private Label pathLabel;
    @FXML private Label checksumLabel;
    @FXML private CheckBox autoUpdateCheck;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    /**
     * Sets the add/edit development profile scene controller
     * @param controller add/edit development profile scene controller
     */
    public void setController(AddEditDevelopmentProfileSceneController controller) {
        this.controller = controller;
    }
    
    /**
     * Edits an existing plugin
     * @param plugin existing plugin
     * @param pluginsFolder location of the plugins folder for this plugin
     */
    public void editPlugin(Plugin plugin, Path pluginsFolder) {
        path = plugin.getPath();
        pathLabel.setText(path.toString());
        checksumLabel.setText(plugin.getChecksum());
        autoUpdateCheck.setSelected(plugin.isAutoUpdate());
        
        headerLabel.setText("Edit Plugin");
        this.pluginsFolder = pluginsFolder;
        editedPlugin = plugin;
        editMode = true;
    }
    
    @FXML
    public void onSelectFileButtonClick(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        
        chooser.getExtensionFilters().add(new ExtensionFilter("Jarfile (*.jar)", "*.jar"));
        
        File fileChosen = chooser.showOpenDialog(null);
        
        if (fileChosen != null) {
            String checksum = MiscUtil.getChecksum(fileChosen.toPath());
            
            path = fileChosen.toPath();
            pathLabel.setText(fileChosen.toString());
            checksumLabel.setText(checksum);
        }
    }
    
    @FXML
    public void onOKButtonClick(ActionEvent event) throws IOException {
        boolean autoUpdate = autoUpdateCheck.isSelected();
        String checksum = checksumLabel.getText();
        Plugin plugin = null;
        
        if (path == null) {
            Alert alert = AlertUtil.createAlert("The plugin path has not been selected.");
            alert.show();

            return;
        }

        if (controller.isExistingPluginPath(path, editedPlugin)) {
            Alert alert = AlertUtil.createAlert("An existing plugin with this path exists.");
            alert.show();
            
            return;
        }

        if (editMode) {
            if (pluginsFolder != null) {
                String existingFileName = editedPlugin.getFileName();
                String fileName = path.getFileName().toString();

                if (!existingFileName.equals(fileName)) {
                    Path pluginFile = pluginsFolder.resolve(existingFileName);
                    File file = pluginFile.toFile();

                    if (file.exists()) {
                        file.delete();
                    }
                }        
            }

            editedPlugin.setPath(path);
            editedPlugin.setFileName(path.getFileName().toString());
            editedPlugin.setChecksum(checksum);
            editedPlugin.setAutoUpdate(autoUpdate);
            
            plugin = editedPlugin;
        } else {
            String fileName = path.getFileName().toString();
            int pluginID = DevelopmentProfileHandler.getNextPluginID();
            
            plugin = new Plugin(pluginID, path, fileName, checksum, autoUpdate);
        }
        
        controller.finishAddEditPlugin(plugin, editMode);
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    public void onCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
