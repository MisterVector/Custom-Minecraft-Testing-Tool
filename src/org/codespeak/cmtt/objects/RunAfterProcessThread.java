package org.codespeak.cmtt.objects;

import javafx.application.Platform;

/**
 * A thread that has a single finished method that runs after a process
 * has finished
 *
 * @author Vector
 */
public class RunAfterProcessThread extends Thread {

    private Process process;
    
    public RunAfterProcessThread(Process process) {
        this.process = process;
    }

    @Override
    public void run() {
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            return;
        }
        
        Platform.runLater(() -> {
            finished();
        });
    }
    
    /**
     * Called after the process has finished
     */
    public void finished() {

    }
    
}
