package org.codespeak.cmtt.scenes;

/**
 * An enum containing a list of scenes
 *
 * @author Vector
 */
public enum SceneTypes {
    
    MAIN("MainScene.fxml"),
    JVM_FLAGS("JVMFlagsScene.fxml"),
    SERVER_PROFILES("ServerProfilesScene.fxml"),
    ADD_EDIT_SERVER_PROFILE("AddEditServerProfileScene.fxml"),
    ADD_EDIT_PLUGIN_DEVELOPMENT_PROFILE("AddEditPluginDevelopmentProfileScene.fxml"),
    ADD_EDIT_PLUGIN("AddEditPluginScene.fxml");
    
    private final String fxmlName;
    
    private SceneTypes(String fxmlName) {
        this.fxmlName = fxmlName;
    }
    
    public String getPath() {
        return "/org/codespeak/cmtt/scenes/" + fxmlName;
    }
    
}
