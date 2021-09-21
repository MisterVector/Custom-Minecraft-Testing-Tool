package org.codespeak.cmtt.objects;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.codespeak.cmtt.util.AlertUtil;

/**
 * A class representing a program exception
 *
 * @author Vector
 */
public class ProgramException extends Exception {
    
    private ErrorType errorType;

    public ProgramException(ErrorType errorType, Exception ex) {
        super(ex);
        
        this.errorType = errorType;
    }

    /**
     * Builds an alert from this program exception
     * @return alert from this program exception
     */
    public Alert buildAlert() {
        AlertType alertType = null;
        
        switch (errorType) {
            case ERROR_SEVERE:
                alertType = AlertType.ERROR;
            
                break;
            case ERROR_WARNING:
            case ERROR_PROGRAM:
            default:
                alertType = AlertType.WARNING;
        }
        
        return AlertUtil.createAlert(alertType, "An error has occurred!\n\n" + super.getLocalizedMessage());
    }
            
    /**
     * Creates a program exception from the specified exception
     * @param ex the exception to convert to a program exception
     * @return a program exception
     */
    public static ProgramException fromException(Exception ex) {
        if (ex instanceof ProgramException) {
            return (ProgramException) ex;
        }
        
        return new ProgramException(ErrorType.ERROR_PROGRAM, ex);
    }
    
}
