/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

/**
 *
 * @author Erik
 */
public class Functions {
    private Globals g;
    private MyLogger myLogger;
    
    Functions (Globals g, MyLogger myLogger) {
        this.g = g;
        this.myLogger = myLogger;
    }
    /*
     * Set the application values except the logFileName which is set by the initLogger() it self.
     */
    String setVars() {
        StringBuilder missing = new StringBuilder();
        if (g.getProps().getProperty("currentISP") == null) {
            missing.append("currentISP,");
        } else {
            g.setCurrentISP(g.getProps().getProperty("currentISP").equals("primaryISP") ? Globals.PRIMARY_ISP : Globals.BACKUP_ISP);
        }

        if (g.getProps().getProperty("triggerDuration") == null) {
            missing.append("triggerDuration,");
        } else {
            g.setTriggerDuration((long) Integer.valueOf(g.getProps().getProperty("triggerDuration")));
        }

        if (g.getProps().getProperty("retryInterval") == null) {
            missing.append("retryInterval,");
        } else {
            g.setRetryInterval((long) Integer.valueOf(g.getProps().getProperty("retryInterval")));
        }

        if (g.getProps().getProperty("maxRetries") == null) {
            missing.append("maxRetries,");
        } else {
            g.setMaxRetries((int) Integer.valueOf(g.getProps().getProperty("maxRetries")));
        }

        if (g.getProps().getProperty("backupISPselected") == null) {
            missing.append("backupISPselected,");
        } else {
            g.setBackupISPselected((boolean) Boolean.valueOf(g.getProps().getProperty("backupISPselected")));
        }

        g.setEmailAddress(g.getProps().getProperty("emailAddress"));
        if (g.getEmailAddress() == null) {
            missing.append("emailAddress,");
        }

        g.setBackupISPscript(g.getProps().getProperty("backupISPscript"));
        if (g.getBackupISPscript() == null) {
            missing.append("backupISPscript,");
        }

        g.setPrimaryISPscript(g.getProps().getProperty("primaryISPscript"));
        if (g.getPrimaryISPscript() == null) {
            missing.append("primaryISPscript,");
        }

        g.setPrimarySMTPserver(g.getProps().getProperty("primarySMTPserver"));
        if (g.getPrimarySMTPserver() == null) {
            missing.append("primarySMTPserver,");
        }

        g.setBackupSMTPserver(g.getProps().getProperty("backupSMTPserver"));
        if (g.getBackupSMTPserver() == null) {
            missing.append("backupSMTPserver,");
        }

        // load the host names if a key with fotmat host<n> exists and the property value has more than zero characters
        g.getHosts().clear();
        int i = 0;
        while (g.getProps().getProperty("host" + i) != null && g.getProps().getProperty("host" + i).length() > 0) {
            g.getHosts().add(g.getProps().getProperty("host" + i));
            i++;
        }
        if (g.getHosts().isEmpty()) {
            myLogger.log(Level.WARNING, "Er zijn geen hosts opgegeven. De service wacht totdat dit alsnog is gedaan met JConsole.");
        }
        return missing.toString();
    }
    
    String getScriptToSwitch() {
        if (g.getCurrentISP() == Globals.PRIMARY_ISP) {
            return g.getBackupISPscript();
        } else {
            return g.getPrimaryISPscript();
        }
    }

    String getCurrentISPString() {
        if (g.getCurrentISP() == Globals.PRIMARY_ISP) {
            return "primary";
        } else {
            return "backup";
        }
    }
    
    String getOtherISPString() {
        if (g.getCurrentISP() == Globals.PRIMARY_ISP) {
            return "backup";
        } else {
            return "primary";
        }
    }

    String getCurrentSMTPserver() {
        if (g.getCurrentISP() == Globals.PRIMARY_ISP) {
            return g.getPrimarySMTPserver();
        } else {
            return g.getBackupSMTPserver();
        }
    }

    /**
     * Write the properties file with new or changed key/value pairs.
     */
    @SuppressWarnings("static-access")
    void writeProperties() {
        try (FileOutputStream propsOutputStream = new FileOutputStream(g.getPropsFileName())) {           
            g.getProps().store(propsOutputStream, "SwitchISP property file");
            myLogger.log(Level.INFO, "De huidige parameterwaarden zijn opgeslagen.");
        } catch (IOException e) {
            myLogger.log(Level.SEVERE, "Fout bij schrijven van de properties, de oorzaak is {0}", e);
        }
    }

    private boolean readProperties() {
        // read the properties file in the Globals variables, if it exists
        try (FileInputStream propsInputStream = new FileInputStream(g.getPropsFileName())) {           
            g.getProps().load(propsInputStream);
            return true;
        } catch (IOException e) {
            myLogger.log(Level.WARNING, "Fout bij lezen van de properties file " + g.getPropsFileName() + ", de oorzaak is: ", e);
            return false;
        }
    }

    /**
     * Read the properties from file. On fail set default values.
     * Update the propertiesSetTemporarely value and save the properties file.
     */
    void setProperties() {
        if (!readProperties()) {
            // set temporary values
            g.getProps().setProperty("currentISP", "primaryISP");
            g.getProps().setProperty("logFileName", g.getLogFileName());
            g.getProps().setProperty("triggerDuration", g.getTriggerDuration() + "");
            g.getProps().setProperty("retryInterval", g.getRetryInterval() + "");
            g.getProps().setProperty("maxRetries", g.getMaxRetries() + "");
            g.getProps().setProperty("backupISPselected", g.isBackupISPselected() ? "true" : "false");
            g.getProps().setProperty("emailAddress", g.getEmailAddress());
            g.getProps().setProperty("primaryISPscript", g.getPrimaryISPscript());
            g.getProps().setProperty("backupISPscript", g.getBackupISPscript());
            g.getProps().setProperty("primarySMTPserver", g.getPrimarySMTPserver());
            g.getProps().setProperty("backupSMTPserver", g.getBackupSMTPserver());
            g.setPropertiesSetTemporarely(true);         
        }
        g.setPropertiesSetTemporarely(false);
        writeProperties();
    }

    /**
     * Put this thread to sleep for ms milliseconds
     *
     * @param ms the sleep time
     */
    void waitMilis(long ms) {
        try {
            Thread.sleep(ms);
        } catch (java.util.concurrent.CancellationException | java.lang.InterruptedException ignore1) {
            // is OK, interrupt by thread cancellation
        }      
    }

    /**
     * Try to connect to any host in the list
     *
     * @return true if a host can be contacted and false if not one host from
     * the list can be reached.
     */
    boolean checkISP() {
        // include some test options
        if (g.getCurrentISP() == Globals.PRIMARY_ISP && g.isSimulatePrimaryISPIsDown()) {
            return false;
        }
        if (g.getCurrentISP() == Globals.BACKUP_ISP && g.isSimulateBackupISPIsDown()) {
            return false;
        }
        // reset test mock setting
        if (g.isMockCheckISPisOK()) {
            g.setMockCheckISPisOK(false);
            return true;
        }

        boolean hostFound = false;
        for (String host : g.getHosts()) {
            // test a TCP connection with the destination host and a time-out.
            if (testConnection(host, Globals.HTTP_PORT, Globals.TIME_OUT)) {
                g.setLastContactWithAnyHost(System.currentTimeMillis());
                hostFound = true;
                g.increaseSuccessfulChecks();
                // when successfull there is no need to try the other hosts
                break;
            } else {
                g.increaseFailedChecks();
                // wait 1 second before contacting the next host in the list
                waitMilis(Globals.ONE_SECOND);
            }
        }
        return hostFound;
    }

    /**
     * Connect using layer4 (sockets)
     *
     * @see http://www.mindchasers.com/topics/ping.htm
     * @param
     * @return true is a connection could be made within the time-out interval
     */
    boolean testConnection(String host, Integer port, int timeout) {
        InetAddress inetAddress;
        InetSocketAddress socketAddress;
        SocketChannel sc = null;

        try {
            inetAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            myLogger.log(Level.INFO, "De host {0} is onbekend. Oorzaak = {1}", new Object[]{host, e});
            return false;
        }

        try {
            socketAddress = new InetSocketAddress(inetAddress, port);
        } catch (IllegalArgumentException e) {
            myLogger.log(Level.INFO, "De poort {0} kan niet valide zijn. Oorzaak = {1}", new Object[]{port, e});
            return false;
        }

        // Open the channel, set it to blocking, initiate connect
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(true);
            sc.socket().connect(socketAddress, timeout);
            myLogger.log(Level.FINE, "{0}/{1} is bereikbaar.", new Object[]{host, inetAddress.getHostAddress()});
            return true;
        } catch (IOException e) {
            myLogger.log(Level.INFO, "{0}/{1} is niet bereikbaar. De oorzaak is {2}", new Object[]{host, inetAddress.getHostAddress(), e});
            return false;
        } finally {
            if (sc != null) {
                try {
                    sc.close();
                } catch (IOException e) {
                    myLogger.log(Level.WARNING, "Het socket kanaal met host {0} kon niet worden gesloten. De oorzaak is {1}", new Object[]{host, e});
                }
            }
        }
    }
}
