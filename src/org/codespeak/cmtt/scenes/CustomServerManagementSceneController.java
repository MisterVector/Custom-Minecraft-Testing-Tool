package org.codespeak.cmtt.scenes;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.codespeak.cmtt.objects.CustomServerType;
import org.codespeak.cmtt.objects.handlers.CustomServerTypeHandler;
import org.codespeak.cmtt.util.AlertUtil;
import org.codespeak.cmtt.util.StringUtil;

/**
 * Controller for the custom server management scene
 *
 * @author Vector
 */
public class CustomServerManagementSceneController implements Initializable {

    private Map<String, Integer> idNameMap = new HashMap<String, Integer>();
    private CustomServerType editedCustomServerType = null;
    private int editedIndex = -1;
    private boolean isEditMode = false;
    
    @FXML private TextField serverNameInput;
    @FXML private TextField worldsArgumentInput;
    @FXML private TextField pluginsArgumentInput;
    @FXML private ListView<String> customServerTypeList;
    @FXML private Button cancelEditButton;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<CustomServerType> customServerTypes = CustomServerTypeHandler.getCustomServerTypes();
        ObservableList<String> items = customServerTypeList.getItems();
        
        for (CustomServerType customServerType : customServerTypes) {
            int id = customServerType.getId();
            String serverName = customServerType.getName();
            
            items.add(serverName);
            idNameMap.put(serverName, id);
        }
    }    
    
    @FXML
    public void onAddServerTypeButtonClick(ActionEvent event) {
        String serverName = serverNameInput.getText();
        String pluginsArgument = pluginsArgumentInput.getText();
        String worldsArgument = worldsArgumentInput.getText();
        
        if (StringUtil.isNullOrEmpty(serverName)) {
            Alert alert = AlertUtil.createAlert("Server name is eempty.");
            alert.show();
            
            return;
        }
        
        if (StringUtil.isNullOrEmpty(pluginsArgument)) {
            Alert alert = AlertUtil.createAlert("Plugins argument is eempty.");
            alert.show();
            
            return;
        }
        
        if (StringUtil.isNullOrEmpty(worldsArgument)) {
            Alert alert = AlertUtil.createAlert("Worlds argument is empty.");
            alert.show();
            
            return;
        }
        
        if (isEditMode) {
            String oldServerName = editedCustomServerType.getName();
            
            if (!oldServerName.equals(serverName)) {
                idNameMap.remove(oldServerName);
                idNameMap.put(serverName, editedCustomServerType.getId());
            }
            
            editedCustomServerType.setServerName(serverName);
            editedCustomServerType.setPluginsArgument(pluginsArgument);
            editedCustomServerType.setWorldsArgument(worldsArgument);
            
            customServerTypeList.getItems().set(editedIndex, serverName);
            
            cancelEditButton.setDisable(true);
            editedCustomServerType = null;
            editedIndex = -1;
            isEditMode = false;
        } else {
            for (String existingServerName : idNameMap.keySet()) {
                if (existingServerName.equalsIgnoreCase(serverName)) {
                    Alert alert = AlertUtil.createAlert("That server name already exists.");
                    alert.show();
                    
                    return;
                }
            }
            
            CustomServerType customServerType = CustomServerTypeHandler.createCustomServerType(serverName, pluginsArgument, worldsArgument);

            customServerTypeList.getItems().add(serverName);
            idNameMap.put(serverName, customServerType.getId());
        }
        
        serverNameInput.clear();
        pluginsArgumentInput.clear();
        worldsArgumentInput.clear();
    }
    
    @FXML
    public void onDeleteServerTypeButtonClick(ActionEvent event) {
        if (isEditMode) {
            Alert alert = AlertUtil.createAlert("Cancel edit first before deleting a server type.");
            alert.show();
            
            return;
        }
        
        int selectedIndex = customServerTypeList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            Alert alert = AlertUtil.createAlert("Select a server type first.");
            alert.show();
            
            return;
        }

        Alert alert = AlertUtil.createAlert("Are you sure you want to delete this server type?");
        alert.getButtonTypes().setAll(new ButtonType[] {ButtonType.YES, ButtonType.NO});
        
        ButtonType button = alert.showAndWait().get();
        
        if (button == ButtonType.YES) {
            ObservableList<String> items = customServerTypeList.getItems();
            String serverName = items.get(selectedIndex);
            int id = idNameMap.get(serverName);

            CustomServerTypeHandler.deleteCustomServerType(id);
            
            items.remove(selectedIndex);
            idNameMap.remove(serverName);
        }
    }
    
    @FXML
    public void onEditServerTypeButtonClick(ActionEvent event) {
        int selectedIndex = customServerTypeList.getSelectionModel().getSelectedIndex();
        
        if (selectedIndex < 0) {
            Alert alert = AlertUtil.createAlert("Select a server first.");
            alert.show();
            
            return;
        }

        String serverName = customServerTypeList.getItems().get(selectedIndex);
        int id = idNameMap.get(serverName);
        editedCustomServerType = CustomServerTypeHandler.fromID(id);
        
        serverNameInput.setText(serverName);
        pluginsArgumentInput.setText(editedCustomServerType.getPluginsArgument());
        worldsArgumentInput.setText(editedCustomServerType.getWorldsArgument());
        
        isEditMode = true;
        editedIndex = selectedIndex;
        cancelEditButton.setDisable(false);
    }
    
    @FXML
    public void onCancelEditButtonClick(ActionEvent event) {
        serverNameInput.clear();
        pluginsArgumentInput.clear();
        worldsArgumentInput.clear();
        
        editedCustomServerType = null;
        editedIndex = -1;
        isEditMode = false;
        
        cancelEditButton.setDisable(true);
    }
    
    @FXML
    public void onCloseWindowButtonClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
}
