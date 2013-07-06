/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 *
 * @author Erik
 */
public class Globals {

    final static int primaryISP = 0;
    final static int backupISP = 1;
    List<String> hosts;
    long lastContactWithAnyHost;
    String propsFileName;
    Properties props;
    boolean propertiesSetTemporarely;
    long successfulChecks;
    long failedChecks;
    int switchoverCount;
    // this values which must be set to real values using JConsole
    int currentISP;
    long triggerDuration;
    long retryInterval;
    int maxRetries;
    boolean backupISPselected;
    String emailAddress;
    String logFileName;
    String primaryISPscript;
    String backupISPscript;
    String primarySMTPserver;
    String backupSMTPserver;
    Level currentLogLevel;
    // end of members which can be changed by the user
    boolean simulatePrimaryISPIsDown;
    boolean simulateBackupISPIsDown;
    boolean mockCheckISPisOK = false;

    Globals(String logFileName) {
        //set the default logfile name from spring.xml
        this.logFileName = logFileName;
        hosts = new ArrayList<String>();
        lastContactWithAnyHost = System.currentTimeMillis();
        propsFileName = "SwitchISPservice.properties"; // default
        props = new Properties();
        propertiesSetTemporarely = false;
        successfulChecks = 0L;
        failedChecks = 0L;
        switchoverCount = 0;
        simulatePrimaryISPIsDown = false;
        simulateBackupISPIsDown = false;

        // initialize with temporary values which must be set to real values using JConsole
        currentISP = primaryISP;
        triggerDuration = 30L; //seconds
        retryInterval = 300L; //seconds
        maxRetries = 5;
        backupISPselected = false;
        emailAddress = "me@mydomain.dom";
        primaryISPscript = "c:\\tmp\\a.bat";
        backupISPscript = "c:\\tmp\\b.bat";
        primarySMTPserver = "smtp.primarySMTPserver.dom";
        backupSMTPserver = "smtp.backupSMTPserver.dom";
        currentLogLevel = Level.INFO;
        // end of temporary initialization which can be changed by the user

    }
    
}
