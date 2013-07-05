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
     * Switch over to target ISP by executing a script. Assume that the script
     * works and the target ISP is available.
     *
     * @param command The script to execute
     * @param sendMail If true send a confirmation mail
     * @param set to true if this ia a manual switch, false if automatically
     * @return true if successfull
     *
     *
     */
    @Override
    public boolean doSwitchOver(String command, boolean sendMail, boolean manualSwitch, String reason) {
        if (command.length() > 0) {
            myLogger.log(Level.INFO, "Nu wordt overgeschakeld.");
            try {
                Process proc = Runtime.getRuntime().exec(command);
                myLogger.log(Level.INFO, "Het script {0} wordt uitgevoerd.\n", command);
                proc.waitFor();
                myLogger.log(Level.INFO, "Het script om {0} van ISP om te schakelen is uitgevoerd.", manualSwitch ? "manueel" : "automatisch");
                f.waitMilis(1000); // wait a second for the new connection to settle
                reverseCurrentISP();
                if (f.checkISP()) {
                    g.switchoverCount++;
                    if (sendMail) {
                        doSendMail(manualSwitch, reason);
                    }
                    return true;
                } else {
                    myLogger.log(Level.INFO, "De automatisch overgeschakeling naar de {0} ISP is mislukt", f.getCurrentISPString());
                    reverseCurrentISP(); // go back to the original ISP as the alternative ISP can not be reached either
                    return false;
                }
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
}
