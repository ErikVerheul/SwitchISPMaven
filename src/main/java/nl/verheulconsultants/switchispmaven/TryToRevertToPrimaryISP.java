/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

import java.util.logging.Level;

/**
 *
 * @author Erik
 */
public class TryToRevertToPrimaryISP {

    private Globals g;
    private Functions f;
    private MyLogger myLogger;
    private SwitchOver so;
    private int retries;
    private long interval;
    private long doNotTryBefore; // in mS.

    TryToRevertToPrimaryISP(Globals g, Functions f, MyLogger myLogger, SwitchOver so) {
        this.g = g;
        this.f = f;
        this.myLogger = myLogger;
        this.so = so;
        reset();
    }

    final void reset() {
        retries = g.maxRetries;
        interval = g.retryInterval;
        doNotTryBefore = System.currentTimeMillis() + interval * 1000;
    }

    int getRetries() {
        return retries;
    }

    long getInterval() {
        return interval;
    }
    
    void setDoNotTryBefore(long time) {
        doNotTryBefore = time;
    }

    /**
     * If temporarely (backupISPselected = false) on the backup ISP try to automatically switch back to the primary ISP.
     * Switchover to the other ISP if the max trial number is not exceeded. 
     * If not successful double the interval for the next try.
     */
    void tryToRevert() {
        if (!g.backupISPselected && g.currentISP == Globals.backupISP) {
            myLogger.log(Level.INFO, "Try to revert to the primary ISP: trialsLeft = {0}, time left (S) = {1}",
                    new Object[]{retries, (doNotTryBefore - System.currentTimeMillis()) / 1000});
            if (retries <= 0 || doNotTryBefore > System.currentTimeMillis()) {
                return;
            }

            String reason = "Probeer automatisch naar de primary ISP terug te schakelen";
            myLogger.log(Level.INFO, "Automatische omschakeling naar de primaire ISP is aangevraagd om de volgende reden: {0}", reason);
            if (!so.doSwitchOver(f.getScriptToSwitch(), true, false, reason)) {
                // try again later
                interval = interval * 2;
                doNotTryBefore = System.currentTimeMillis() + interval * 1000;
                retries--;
                myLogger.log(Level.INFO, "Trials left to autoswitch back to the primary ISP is set to {0} and the initial delay to {1} seconds.",
                        new Object[]{retries, interval});
            } else {
                // succes
                reset();
            }
        }
    }
}
