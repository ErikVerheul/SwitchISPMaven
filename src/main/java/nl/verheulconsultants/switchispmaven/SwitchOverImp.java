/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

import java.sql.Timestamp;
import java.util.logging.Level;

/**
 *
 * @author Erik
 */
@SuppressWarnings("static-access")
public class SwitchOverImp implements SwitchOver {

    private Globals g;
    private Functions f;
    private MyLogger myLogger;
    private EMailClient eMailClient;
    private int retries;
    private long interval;
    private long doNotTryBefore; // in mS.

    SwitchOverImp(Globals g, Functions f, MyLogger myLogger, EMailClient eMailClient) {
        this.g = g;
        this.f = f;
        this.myLogger = myLogger;
        this.eMailClient = eMailClient;
    }

    private void reverseCurrentISP() {
        if (g.currentISP == g.primaryISP) {
            g.currentISP = g.backupISP;
        } else {
            g.currentISP = g.primaryISP;
        }
        g.props.setProperty("currentISP", (g.currentISP == g.primaryISP) ? "primaryISP" : "backupISP");
        myLogger.log(Level.INFO, "De ISP is gewisseld naar de {0}", ((g.currentISP == g.primaryISP) ? "primary ISP" : "backup ISP"));
        f.writeProperties();
    }
    
    /**
     * Switch to the other ISP. No attempt is made to check if the other ISP is available.
     * 
     * @param command The script to execute
     * @param manualSwitch
     * @return false on error
     */
    private boolean switchNow(boolean manualSwitch) {
        String command = f.getScriptToSwitch();
        if (command.length() > 0) {
            try {
                Process proc = Runtime.getRuntime().exec(command);
                myLogger.log(Level.INFO, "Het script {0} wordt uitgevoerd.\n", command);
                proc.waitFor();
                myLogger.log(Level.INFO, "Het script om {0} van ISP om te schakelen is uitgevoerd.", manualSwitch ? "manueel" : "automatisch");
                // wait a second for the new connection to settle
                f.waitMilis(g.ONE_SECOND);
                return true;
            } catch (java.io.IOException e) {
                myLogger.log(Level.SEVERE, "Het is niet mogelijk dit script uit te voeren. De oorzaak is:", e);
                return false;
            } catch (InterruptedException e) {
                myLogger.log(Level.SEVERE, "De uitvoering van script is onderbroken. De oorzaak is:", e);
                return false;
            }
        } else {
            myLogger.severe("Kan de overschakeling niet uitvoeren: De scriptnaam is leeg.");
            return false;
        }
    }

    /**
     * Switch over to target ISP by executing a script. Assume that the script
     * works and the target ISP is available.
     *
     * @param command The script to execute
     * @param sendMail If true send a confirmation mail
     * @param set to true if this is a manual switch, false if automatically
     * @return true if successful
     *
     */
    @Override
    public boolean doSwitchOver(boolean sendMail, boolean manualSwitch, String reason) {
        myLogger.log(Level.INFO, "Nu wordt overgeschakeld.");
        if (switchNow(manualSwitch) && f.checkISP()) {
            reverseCurrentISP();
            g.switchoverCount++;
            resetAutoSwitch();
            if (sendMail) {
                doSendMail(manualSwitch, reason);
            }
            return true;
        } else {
            myLogger.log(Level.INFO, "De automatisch overgeschakeling naar de {0} ISP is mislukt", f.getOtherISPString());
            // Continue trying the original ISP as the alternative ISP can not be reached either
            return false;
        }
    }

    private void doSendMail(boolean manualSwitch, String reason) {
        java.util.Date date = new java.util.Date();
        if (eMailClient.sendEMail(g.emailAddress, g.emailAddress,
                "SwitchISP is " + (manualSwitch ? "manueel" : "automatisch") + " naar de " + f.getCurrentISPString() + " ISP overgeschakeld.\n"
                + "Dit bericht is verzonden op " + new Timestamp(date.getTime()) + "\n" + (reason != null ? "De reden is: " + reason : ""),
                "Melding van een " + (manualSwitch ? "manuele" : "automatische") + " SwitchISP overschakeling\n")) {
            myLogger.info("Een melding van de overschakeling is verzonden.");
        } else {
            myLogger.log(Level.SEVERE, "{0} De verzending van het overschakelbericht is mislukt.\n", new Timestamp(date.getTime()));
        }
    }

    @Override
    public final void resetAutoSwitch() {
        retries = g.maxRetries;
        interval = g.retryInterval;
        doNotTryBefore = System.currentTimeMillis() + interval * 1000;
    }

    /**
     * If temporarily (backupISPselected = false) on the backup ISP try to
     * automatically switch back to the primary ISP. Switchover to the other ISP
     * if the max trial number is not exceeded. If not successful double the
     * interval for the next try.
     */
    @Override
    public void tryToRevert() {
        if (!g.backupISPselected && g.currentISP == Globals.backupISP) {
            myLogger.log(Level.INFO, "Try to revert to the primary ISP: trialsLeft = {0}, time left (S) = {1}",
                    new Object[]{retries, (doNotTryBefore - System.currentTimeMillis()) / 1000});
            if (retries <= 0 || doNotTryBefore > System.currentTimeMillis()) {
                return;
            }

            String reason = "Probeer automatisch naar de primary ISP terug te schakelen";
            myLogger.log(Level.INFO, "Automatische omschakeling naar de primaire ISP is aangevraagd om de volgende reden: {0}", reason);
            if (doSwitchOver(true, false, reason)) {
                // succes
                resetAutoSwitch();
            } else {
                // try again later
                interval = interval * 2;
                doNotTryBefore = System.currentTimeMillis() + interval * 1000;
                retries--;
                myLogger.log(Level.INFO, "Trials left to autoswitch back to the primary ISP is set to {0} and the initial delay to {1} seconds.",
                        new Object[]{retries, interval});
            }
        }
    }
}
