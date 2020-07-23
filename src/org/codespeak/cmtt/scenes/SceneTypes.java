package org.codespeak.cmtt.scenes;

/**
 * An enum containing a list of scenes
 *
 * @author Vector
 */
public enum SceneTypes {
    
    MAIN("MainScene.fxml"),
    JVM_FLAGS_SCENE("JVMFlagsScene.fxml");
    
    private final String fxmlName;
    
    private SceneTypes(String fxmlName) {
        this.fxmlName = fxmlName;
    }
    
    public String getPath() {
        return "/org/codespeak/cmtt/scenes/" + fxmlName;
    }
    
}
