package nl.verheulconsultants.switchispmaven;

import java.util.logging.Level;

/**
 *
 * @author Erik
 */
public class ControllerImp implements Controller {

    private Globals g;
    private Functions f;
    private MyLogger myLogging;
    private boolean done = false;
    private boolean stop = false;
    private boolean exit = false;
    private SwitchOver so;

    ControllerImp(Globals g, Functions f, MyLogger myLogging, SwitchOver so) {
        this.g = g;
        this.f = f;
        this.myLogging = myLogging;
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
                            if (so.doSwitchOver(true, false, null)) {
                                myLogging.log(Level.INFO, "Er is automatisch overgeschakeld naar de {0} ISP.", f.getCurrentISPString());
                                g.lastContactWithAnyHost = System.currentTimeMillis();
                            }
                        }
                    } else {
                        so.tryToRevert();
                    }
                    f.waitMilis(5000);  // wait 5 seconds to check the ISP connection again
                }
            }
            if (!done) {
                myLogging.log(Level.INFO, "De controller is gestopt.\n");
                myLogging.log(Level.INFO, "Er zijn {0} succesvolle connectie checks uitgevoerd. {1} Connecties faalden.", new Object[]{g.successfulChecks, g.failedChecks});
            }
            done = true;
            // wait for instructions to restart or to exit completely
            f.waitMilis(1000);
        } while (!exit);
    }
}
