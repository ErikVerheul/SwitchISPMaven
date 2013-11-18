package nl.verheulconsultants.switchispmaven;

import java.util.logging.Level;

/**
 *
 * @author Erik
 */
public class ControllerImp implements Controller {

    private Globals g;
    private Functions f;
    private MyLogger myLogger;
    private boolean done = false;
    private boolean stop = false;
    private boolean exit = false;
    private SwitchOver so;

    ControllerImp(Globals g, Functions f, MyLogger myLogging, SwitchOver so) {
        this.g = g;
        this.f = f;
        this.myLogger = myLogging;
        this.so = so;
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

    @SuppressWarnings("static-access")
    @Override
    public void doInBackground() {
        done = false;
        stop = false;
        myLogger.log(Level.INFO, "De controller is gestart.");

        do {
            // wait until the JMX client defines at least one host
            if (g.getHosts().size() > 0) {
                while (!stop) {
                    if (!f.checkISP()) {
                        long timeLeftForSwitchOver = g.getTriggerDuration() * g.ONE_SECOND - (System.currentTimeMillis() - g.getLastContactWithAnyHost());
                        myLogger.log(Level.INFO, "De {0} ISP is niet bereikbaar. Tijd over tot omschakeling is {1} sec.",
                                new Object[]{f.getCurrentISPString(), timeLeftForSwitchOver / g.ONE_SECOND});
                        if (timeLeftForSwitchOver < 0 && so.doSwitchOver(true, false, null)) {
                                myLogger.log(Level.INFO, "Er is automatisch overgeschakeld naar de {0} ISP.", f.getCurrentISPString());
                                g.setLastContactWithAnyHost(System.currentTimeMillis());
                        }
                    } else {
                        so.tryToRevert();
                    }
                    // wait 5 seconds to check the ISP connection again
                    f.waitMilis(g.FIVE_SECONDS);
                }
            }
            if (!done) {
                myLogger.log(Level.INFO, "De controller is gestopt.\n");
                myLogger.log(Level.INFO, "Er zijn {0} succesvolle connectie checks uitgevoerd. {1} Connecties faalden.", new Object[]{g.getSuccessfulChecks(), g.getFailedChecks()});
            }
            done = true;
            // wait for instructions to restart or to exit completely
            f.waitMilis(g.ONE_SECOND);
        } while (!exit);
    }
}
