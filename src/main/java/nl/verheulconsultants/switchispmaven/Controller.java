/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

/**
 *
 * @author Erik
 */
public interface Controller {

    boolean isRunning();

    void stop();

    void restart();

    void exit();

    boolean isDone();

    void resetTryToRevertToPrimaryISP();

    void doInBackground();
}
