package nl.verheulconsultants.switchispmaven;

import java.util.logging.Level;

/**
 *
 * @author Erik
 */
public class ControllerImp implements Controller {
    
    private Globals g;
    private Functions f;
    private LoggerImp myLogging;
    private boolean done = false;
    private boolean stop = false;
    private boolean exit = false;
    private SwitchOver so;
    private TryToRevertToPrimaryISP autoRevInstance;

    ControllerImp(Globals g, Functions f, LoggerImp myLogging, SwitchOver so, TryToRevertToPrimaryISP autoRevInstance) {
        this.g = g;
        this.f = f;
        this.myLogging = myLogging;
        this.so = so;
        this.autoRevInstance = autoRevInstance;
    }

    @Override
    public boolean isRunning() {
        return !done;
    }

    @Override
    public void stop() {
        stop = true;
    }

    @Override
    public void restart() {
        done = false;
        stop = false;
    }

    @Override
    public void exit() {
        stop = true;
        exit = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public void resetTryToRevertToPrimaryISP() {
        autoRevInstance.reset();
    }

    @SuppressWarnings("static-access")
    @Override
    public void doInBackground() {
        done = false;
        stop = false;
        myLogging.log(Level.INFO, "De controller is gestart.");

        do {
            // wait until the JMX client defines at least one host
            if (g.hosts.size() > 0) {
                while (!stop) {
                    if (!f.checkISP()) {
                        long timeLeftForSwitchOver = g.triggerDuration * 1000L - (System.currentTimeMillis() - g.lastContactWithAnyHost);
                        myLogging.log(Level.INFO, "De {0} ISP is niet bereikbaar. Tijd over tot omschakeling is {1} sec.",
                                new Object[]{f.getCurrentISPString(), timeLeftForSwitchOver / 1000});
                        if (timeLeftForSwitchOver < 0) {
                            if (so.doSwitchOver(f.getScriptToSwitch(), true, false, null)) {
                                myLogging.log(Level.INFO, "Er is automatisch overgeschakeld naar de {0} ISP.", f.getCurrentISPString());
                                g.lastContactWithAnyHost = System.currentTimeMillis();
                            }
                        }
                    } else { // if temporarely (backupISPselected = false) on the backup ISP try to automatically switch back to the primary ISP
                        if (!g.backupISPselected && g.currentISP == g.backupISP) {
                            autoRevInstance.tryToRevert();
                        }
                    }
                    f.waitMilis(5000);  // wait 5 seconds to check the ISP connection again
                }
            }
            if (!done) {
                myLogging.log(Level.INFO, "De controller is gestopt.\n");
                myLogging.log(Level.INFO, "Er zijn {0} succesvolle connectie checks uitgevoerd. {1} onnecties faalden.", new Object[]{g.successfulChecks, g.failedChecks});
            }
            done = true;
            // wait for instructions to restart or to exit completely
            f.waitMilis(1000);
        } while (!exit);
    }
}
