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
        if (g.props.getProperty("currentISP") == null) {
            missing.append("currentISP,");
        } else {
            g.currentISP = g.props.getProperty("currentISP").equals("primaryISP") ? g.primaryISP : g.backupISP;
        }

        if (g.props.getProperty("triggerDuration") == null) {
            missing.append("triggerDuration,");
        } else {
            g.triggerDuration = new Integer(g.props.getProperty("triggerDuration"));
        }

        if (g.props.getProperty("retryInterval") == null) {
            missing.append("retryInterval,");
        } else {
            g.retryInterval = new Integer(g.props.getProperty("retryInterval"));
        }

        if (g.props.getProperty("maxRetries") == null) {
            missing.append("maxRetries,");
        } else {
            g.maxRetries = new Integer(g.props.getProperty("maxRetries"));
        }

        if (g.props.getProperty("backupISPselected") == null) {
            missing.append("backupISPselected,");
        } else {
            g.backupISPselected = Boolean.valueOf(g.props.getProperty("backupISPselected"));
        }

        g.emailAddress = g.props.getProperty("emailAddress");
        if (g.emailAddress == null) {
            missing.append("emailAddress,");
        }

        g.backupISPscript = g.props.getProperty("backupISPscript");
        if (g.backupISPscript == null) {
            missing.append("backupISPscript,");
        }

        g.primaryISPscript = g.props.getProperty("primaryISPscript");
        if (g.primaryISPscript == null) {
            missing.append("primaryISPscript,");
        }

        g.primarySMTPserver = g.props.getProperty("primarySMTPserver");
        if (g.primarySMTPserver == null) {
            missing.append("primarySMTPserver,");
        }

        g.backupSMTPserver = g.props.getProperty("backupSMTPserver");
        if (g.backupSMTPserver == null) {
            missing.append("backupSMTPserver,");
        }

        // load the host names if a key with fotmat host<n> exists and the property value has more than zero characters
        g.hosts.clear();
        int i = 0;
        while (g.props.getProperty("host" + i) != null && g.props.getProperty("host" + i).length() > 0) {
            g.hosts.add(g.props.getProperty("host" + i));
            i++;
        }
        if (g.hosts.isEmpty()) {
            myLogger.log(Level.WARNING, "Er zijn geen hosts opgegeven. De service wacht totdat dit alsnog is gedaan met JConsole.");
        }
        return missing.toString();
    }
    
    String getScriptToSwitch() {
        if (g.currentISP == g.primaryISP) {
            return g.backupISPscript;
        } else {
            return g.primaryISPscript;
        }
    }

    String getCurrentISPString() {
        if (g.currentISP == g.primaryISP) {
            return "primary";
        } else {
            return "backup";
        }
    }

    String getCurrentSMTPserver() {
        if (g.currentISP == g.primaryISP) {
            return g.primarySMTPserver;
        } else {
            return g.backupSMTPserver;
        }
    }

    /**
     * Write the properties file with new or changed key/value pairs.
     */
    @SuppressWarnings("static-access")
    void writeProperties() {
        FileOutputStream propsOutputStream = null;
        try {
            propsOutputStream = new FileOutputStream(g.propsFileName);
            g.props.store(propsOutputStream, "SwitchISP property file");
            myLogger.log(Level.INFO, "De huidige parameterwaarden zijn opgeslagen.");
        } catch (IOException e) {
            myLogger.log(Level.SEVERE, "Fout bij schrijven van de properties, de oorzaak is {0}", e);
        } finally {
            try {
                if (propsOutputStream != null) {
                    propsOutputStream.close();
                }
            } catch (IOException ignore) {
                // ignore
            }
        }
    }

    private boolean readProperties() {
        // read the properties file if existing
        FileInputStream propsInputStream = null;
        try {
            propsInputStream = new FileInputStream(g.propsFileName);
            g.props.load(propsInputStream);
            return true;
        } catch (IOException e) {
            myLogger.log(Level.WARNING, "Fout bij lezen van de properties file " + g.propsFileName + ", de oorzaak is: ", e);
            return false;
        } finally {
            try {
                if (propsInputStream != null) {
                    propsInputStream.close();
                }
            } catch (IOException ignore) {
                // ignore
            }
        }
    }

    void setProperties() {
        if (!readProperties()) {
            // set temporary values
            g.props.setProperty("currentISP", "primaryISP");
            g.props.setProperty("logFileName", g.logFileName);
            g.props.setProperty("triggerDuration", g.triggerDuration + "");
            g.props.setProperty("retryInterval", g.retryInterval + "");
            g.props.setProperty("maxRetries", g.maxRetries + "");
            g.props.setProperty("backupISPselected", g.backupISPselected ? "true" : "false");
            g.props.setProperty("emailAddress", g.emailAddress);
            g.props.setProperty("primaryISPscript", g.primaryISPscript);
            g.props.setProperty("backupISPscript", g.backupISPscript);
            g.props.setProperty("primarySMTPserver", g.primarySMTPserver);
            g.props.setProperty("backupSMTPserver", g.backupSMTPserver);
            g.propertiesSetTemporarely = true;
        }
    }

    /**
     * Put this thread to sleep for ms miliseconds
     *
     * @param ms the sleep time
     */
    void waitMilis(long ms) {
        try {
            Thread.sleep(ms);
        } catch (java.util.concurrent.CancellationException ignore1) {
            // is OK, interrupt by thread cancellation
        } catch (java.lang.InterruptedException ignore2) {
            // is OK, interrupt caused by thread cancellation
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
        if (g.currentISP == Globals.primaryISP && g.simulatePrimaryISPIsDown) {
            return false;
        }
        if (g.currentISP == Globals.backupISP && g.simulateBackupISPIsDown) {
            return false;
        }
        if (g.mockCheckISPisOK) {
            g.mockCheckISPisOK = false;
            return true;
        }

        boolean hostFound = false;
        for (String host : g.hosts) {
            // test a TCP connection on port 80 with the destination host and a time-out of 2000 ms.
            if (testConnection(host, 80, 2000)) {
                g.lastContactWithAnyHost = System.currentTimeMillis();
                hostFound = true;
                g.successfulChecks++;
                break; // when successfull there is no need to try the other hosts
            } else {
                g.failedChecks++;
                waitMilis(1000);  // wait 1 second before contacting the next host in the list
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
