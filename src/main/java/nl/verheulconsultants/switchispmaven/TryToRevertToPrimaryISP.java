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
    private LoggerImp myLogger;
    private SwitchOver so;
    private long doNotTryBefore; // in mS.

    TryToRevertToPrimaryISP(Globals g, Functions f, LoggerImp myLogger, SwitchOver so) {
        this.g = g;
        this.f = f;
        this.myLogger = myLogger;
        this.so = so;
        reset();
    }
    
    final void reset() {
        doNotTryBefore = System.currentTimeMillis() + g.retryInterval * 1000;
        myLogger.log(Level.INFO, "Trials left to autoswitch back to the primary ISP is reset to {0} and the initial delay to {1} seconds.", 
                new Object[]{g.maxRetries, g.retryInterval});
    }

    /**
     * Switchover to the other ISP if the max trial number is not exceeded.
     * If not successful double the interval for the next try.
     */
    void tryToRevert() {
        myLogger.log(Level.FINE, "trialsLeft = {0} time left (mS) = {1}", new Object[]{g.maxRetries, doNotTryBefore - System.currentTimeMillis()});
        if (g.maxRetries <= 0 || doNotTryBefore > System.currentTimeMillis()) {
            return;
        }

        String reason = "Probeer automatisch naar de primary ISP terug te schakelen";
        myLogger.log(Level.INFO, "Automatische omschakeling naar de primaire ISP is aangevraagd om de volgende reden: {0}", reason);
        if (!so.doSwitchOver(f.getScriptToSwitch(), true, false, reason)) {
            // try again later
            g.retryInterval = g.retryInterval * 2;
            doNotTryBefore = System.currentTimeMillis() + g.retryInterval * 1000;           
            g.maxRetries--;
            myLogger.log(Level.INFO, "Trials left to autoswitch back to the primary ISP is set to {0} and the initial delay to {1} seconds.", 
                new Object[]{g.maxRetries, g.retryInterval});
        } else {
            // succes
            reset();
        }
    }
}
