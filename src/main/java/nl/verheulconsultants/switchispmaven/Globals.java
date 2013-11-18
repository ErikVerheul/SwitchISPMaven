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
    static final long ONE_SECOND = 1000L;
    static final long FIVE_SECONDS = 5000L;
    static final long DEFAULT_TRIGGERDURATION = 30L; //seconds
    static final long DEFAULT_RETRYINTERVAL = 300L; //seconds
    static final int DEFAULT_MAXRETRIES = 5;
    static final int PRIMARY_ISP = 0;
    static final int BACKUP_ISP = 1;
    static final int HTTP_PORT = 80;
    static final int TIME_OUT = 2000;
    List<String> hosts;
    long lastContactWithAnyHost;
    String propsFileName;
    Properties props;
    boolean propertiesSetTemporarely;
    long successfulChecks;
    long failedChecks;
    int switchoverCount;
    // these values must be set to real values using JConsole
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
        propsFileName = "SwitchISPservice.properties";
        props = new Properties();
        propertiesSetTemporarely = false;
        successfulChecks = 0L;
        failedChecks = 0L;
        switchoverCount = 0;
        simulatePrimaryISPIsDown = false;
        simulateBackupISPIsDown = false;

        // initialize with temporary values which must be set to real values using JConsole
        currentISP = PRIMARY_ISP;
        triggerDuration = DEFAULT_TRIGGERDURATION;
        retryInterval = DEFAULT_RETRYINTERVAL;
        maxRetries = DEFAULT_MAXRETRIES;
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
