/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import javax.management.JMException;
import javax.management.ObjectName;

/**
 *
 * @author Erik
 */
public class TheServiceImp implements TheService {

    private Globals g;
    private Functions f;
    private MyLogger myLogger;
    private SwitchOver so;
    private Controller controller;

    TheServiceImp(Globals g, Functions f, MyLogger logger, SwitchOver so, Controller controller) {
        this.g = g;
        this.f = f;
        this.myLogger = logger;
        this.so = so;
        this.controller = controller;
    }

    private String getScriptToSwitchToCurrentISP() {
        if (g.getCurrentISP() == Globals.PRIMARY_ISP) {
            return g.getPrimaryISPscript();
        } else {
            return g.getBackupISPscript();
        }
    }

    /**
     * Switch to the current ISP. No attempt is made to check if this ISP is
     * available.
     *
     * @return false on error
     */
    private boolean switchToCurrentISP() {
        String command = getScriptToSwitchToCurrentISP();
        if (command.length() > 0) {
            try {
                Process proc = Runtime.getRuntime().exec(command);
                myLogger.log(Level.INFO, "Het script {0} wordt uitgevoerd.\n", command);
                proc.waitFor();
                myLogger.log(Level.INFO, "Het script om naar de {0} ISP te schakelen is uitgevoerd.", g.getCurrentISP() == Globals.PRIMARY_ISP ? "primaire" : "backup");
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

    @Override
    public void runTheService(String[] args) {
        if (args.length >= 1) {
            // overrule the default
            g.setPropsFileName(args[0]);
        }
        f.setProperties();
        if (g.isPropertiesSetTemporarely()) {
            myLogger.log(Level.WARNING, "Kan de properties file met de applicatie parameters niet lezen.\n"
                    + "Tijdelijke waarden zijn ingesteld.\n"
                    + "Gebruik JConsole om de correcte waarden in te vullen.");
        }
        // set the logFileName value up front
        g.setLogFileName(g.getProps().getProperty("logFileName"));
        if (g.getLogFileName() != null) {
            if (myLogger.initLogger(g.getLogFileName())) {
                myLogger.log(Level.INFO, "De applicatie wordt gestart met de {0} ISP en log file {1}", new Object[]{f.getCurrentISPString(), g.getLogFileName()});
                String missingVariable = f.setVars();
                if (missingVariable.isEmpty()) {
                    // make sure that when restarting the service the ISP as stored in the properties file is used.
                    if (switchToCurrentISP()) {
                        so.resetAutoSwitch();
                        try {
                            // Register MBean in Platform MBeanServer
                            ManagementFactory.getPlatformMBeanServer().
                                    registerMBean(new MBeanFromMain(g, f, myLogger, controller, so),
                                    new ObjectName("switchispservice:type=MBeanFromMain"));
                            ManagementFactory.getPlatformMBeanServer().
                                    registerMBean(new Fifo(myLogger.getOutputQueue()),
                                    new ObjectName("switchispservice:type=FifoMBean"));
                            // Initialize and start the controller
                            controller.doInBackground();
                        } catch (JMException ex) {
                            myLogger.log(Level.SEVERE, "De registratie van de MBeans is mislukt. De oorzaak is {0}", ex);
                        }
                    } else {
                        myLogger.log(Level.SEVERE, "De property file mist de variabelen {0}.", missingVariable);
                    }
                } else {
                    myLogger.log(Level.SEVERE, "De schakeling naar de huidige ISP is mislukt");
                }
            } else {
                System.err.println("SwitchISPservice: De logfile kan niet worden geinitialiseerd.");
            }
        } else {
            System.err.println("SwitchISPservice: De logfile naam is niet gespecificeerd.");
        }
    }
}
