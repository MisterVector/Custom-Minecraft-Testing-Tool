package org.codespeak.cmtt.objects;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.Alert;
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
    private Boolean extraCondition = null;
    
    public ConditionalAlert() {

    }
    
    /**
     * Adds a condition with a message if true
     * @param condition a condition representing an expression
     * @param message a message to be used if the condition is true
     * @return this object
     */
    public ConditionalAlert addCondition(Boolean condition, String message) {
        if (extraCondition == null || extraCondition) {
            conditions.add(new ConditionMessage(condition, message));
        }
        
        return this;
    }
    
    /**
     * Adds an extra condition to evaluate
     * @param extraCondition extra condition to evaluate
     * @return this object
     */
    public ConditionalAlert ifTrue(boolean extraCondition) {
        this.extraCondition = extraCondition;
        
        return this;
    }
    
    /**
     * Removes the extra condition
     * @return this object
     */
    public ConditionalAlert endIf() {
        extraCondition = null;
        
        return this;
    }
    
    /**
     * Gets an alert representing the first true condition
     * @return this object
     */
    public Alert getAlert() {
        Alert ret = null;
        
        for (ConditionMessage cm : conditions) {
            Boolean condition = cm.getCondition();
            String message = cm.getMessage();

            if (condition) {
                ret = new Alert(Alert.AlertType.INFORMATION, message);
                ret.setTitle(Configuration.PROGRAM_TITLE);
                ret.setHeaderText("");
                
                break;
            }
        }
        
        return ret;
    }

}
