package org.codespeak.cmtt.objects;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.codespeak.cmtt.Configuration;

/**
 * A class that produces an alert based on one or more conditions
 *
 * @author Vector
 */
public class ConditionalAlert {

    public class ConditionMessage {
        
        private Boolean condition;
        private String message;
        
        public ConditionMessage(Boolean condition, String message) {
            this.condition = condition;
            this.message = message;
        }
        
        /**
         * Gets a condition representing the result of an expression
         * @return condition representing the result of an expression
         */
        public Boolean getCondition() {
            return condition;
        }
        
        /**
         * Gets a message representing a condition that is true
         * @return message representing a condition that is true 
         */
        public String getMessage() {
            return message;
        }
        
    }
    
    private List<ConditionMessage> conditions = new ArrayList<ConditionMessage>();
    
    public ConditionalAlert() {

    }
    
    /**
     * Adds a condition with a message if true
     * @param condition a condition representing an expression
     * @param message a message to be used if the condition is true
     * @return this object
     */
    public ConditionalAlert addCondition(Boolean condition, String message) {
        conditions.add(new ConditionMessage(condition, message));
        
        return this;
    }
   
    /**
     * Gets an alert representing all true conditions
     * @return an alert object if at least one condition is true
     */
    public Alert getAlert() {
        String finalMessage = "";
        
        for (ConditionMessage cm : conditions) {
            Boolean condition = cm.getCondition();
            String message = cm.getMessage();

            if (condition) {
                if (!finalMessage.isEmpty()) {
                    finalMessage += "\n";
                }
                
                finalMessage += message;
            }
        }
        
        if (!finalMessage.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING, finalMessage);

            alert.setTitle(Configuration.PROGRAM_NAME);
            alert.setHeaderText("Configuration Issues");
            
            return alert;
        } else {
            return null;
        }
    }

}
