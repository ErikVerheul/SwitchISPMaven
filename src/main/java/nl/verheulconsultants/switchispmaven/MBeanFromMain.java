/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

import java.io.File;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

/**
 *
 * @author Erik
 */
@SuppressWarnings("static-access")
public class MBeanFromMain extends StandardMBean implements MBeanFromMainMBean {

    private Globals g;
    private Functions f;
    private MyLogger myLogger;
    private Controller controller;
    private SwitchOver so;
    private Level[] levels = {Level.ALL, Level.FINEST, Level.FINER, Level.FINE, Level.CONFIG, Level.INFO, Level.WARNING, Level.SEVERE, Level.OFF};

    MBeanFromMain(Globals g, Functions f, MyLogger myLogger, Controller controller, SwitchOver so) throws NotCompliantMBeanException {
        super(MBeanFromMainMBean.class);
        this.g = g;
        this.f = f;
        this.myLogger = myLogger;
        this.controller = controller;
        this.so = so;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        MBeanInfo mbinfo = super.getMBeanInfo();
        return new MBeanInfo(mbinfo.getClassName(),
                mbinfo.getDescription(),
                mbinfo.getAttributes(),
                mbinfo.getConstructors(),
                mbinfo.getOperations(),
                getNotificationInfo());
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[]{};
    }

    /**
     * Override customization hook: You can supply a customized description for
     * MBeanInfo.getDescription()
     */
    @Override
    protected String getDescription(MBeanInfo info) {
        return "Wraps the static parameters of this application.";
    }

    /**
     * Override customization hook: You can supply a customized description for
     * MBeanAttributeInfo.getDescription()
     */
    @Override
    protected String getDescription(MBeanAttributeInfo info) {
        String description = null;
        if (info.getName().equals("ProgramVersion")) {
            description = "The program name and current version";
        } else if (info.getName().equals("CurrentISP")) {
            description = "The current ISP connection";
        } else if (info.getName().equals("EmailAddress")) {
            description = "Where the switchover messages go";
        } else if (info.getName().equals("Triggerduration")) {
            description = "The wait time for switchover in seconds";
        } else if (info.getName().equals("RetryInterval")) {
            description = "The initial interval between retries to switch back to the primary ISP in sec. Will be doubled next try";
        } else if (info.getName().equals("MaxRetries")) {
            description = "The maximum number of retries before giving up";
        } else if (info.getName().equals("LogFileName")) {
            description = "Path and name of the log file";
        } else if (info.getName().equals("PrimaryISPscript")) {
            description = "Path and name of the script to switch to the primary ISP";
        } else if (info.getName().equals("BackupISPscript")) {
            description = "Path and name of the script to switch to the backup ISP";
        } else if (info.getName().equals("PrimarySMTPserver")) {
            description = "SMTP server of primary ISP";
        } else if (info.getName().equals("BackupSMTPserver")) {
            description = "SMTP server of backup ISP";
        } else if (info.getName().equals("SuccessfulChecks")) {
            description = "Het aantal succesvolle checks van de verbinding";
        } else if (info.getName().equals("FailedChecks")) {
            description = "Het aantal mislukte checks van de verbinding";
        } else if (info.getName().equals("SwitchoverCount")) {
            description = "Het aantal overschakelingen sinds de start van het programma";
        }
        return description;
    }

    /**
     * Customization hook: Get the description that will be used for the
     * <var>sequence</var> MBeanParameterInfo of the MBeanOperationInfo returned
     * by this MBean. <br> Subclasses may redefine this method in order to
     * supply their custom description. The default implementation returns
     * {@link MBeanParameterInfo#getDescription() param.getDescription()}.
     *
     * @param op The default MBeanOperationInfo derived by reflection.
     * @param param The default MBeanParameterInfo derived by reflection.
     * @param sequence The sequence number of the parameter considered ("0" for
     * the first parameter, "1" for the second parameter, etc...).
     * @return the description for the given MBeanParameterInfo.
     *
     */
    @Override
    protected String getDescription(MBeanOperationInfo op,
            MBeanParameterInfo param,
            int sequence) {
        String varDescription = null;
        if (op.getName().equalsIgnoreCase("revertToPrimayISP") || op.getName().equalsIgnoreCase("switchToBackupISP")) {
            varDescription = "Wat is de reden voor deze manuele switch?";
        } else if (op.getName().equalsIgnoreCase("changeHost") || op.getName().equalsIgnoreCase("removeHost")) {
            if (sequence == 0) {
                varDescription = "De index van een bestaande hostnaam van 0.." + (g.hosts.size() - 1);
            }
            if (sequence == 1) {
                varDescription = "Voer hier een host naam in";
            }
        }
        return varDescription;
    }

    /**
     * Override customization hook: You can supply a customized description for
     * MBeanParameterInfo.getName()
     */
    @Override
    protected String getParameterName(MBeanOperationInfo op, MBeanParameterInfo param, int sequence) {
        return null;
    }

    /**
     * Override customization hook: You can supply a customized description for
     * MBeanOperationInfo.getDescription()
     */
    @Override
    protected String getDescription(MBeanOperationInfo info) {
        String description = null;
        if (info.getName().equals("setCoarserLogLevel")) {
            description = "Set the log level one step finer is possible";
        } else if (info.getName().equals("setFinerLogLevel")) {
            description = "Set the log level one step less finer is possible";
        } else if (info.getName().equals("stop")) {
            description = "Stop the hosts scanning";
        } else if (info.getName().equals("start")) {
            description = "Start the hosts scanning";
        } else if (info.getName().equals("restart")) {
            description = "Stops if running and start hosts scanning wih current parameters";
        } else if (info.getName().equals("restart")) {
            description = "Stops if running and start hosts scanning wih current parameters";
        } else if (info.getName().equals("restartWithSavedParams")) {
            description = "Stops if running en starts hosts scanning with saved parameters";
        } else if (info.getName().equals("saveParams")) {
            description = "Save the current parameters";
        } else if (info.getName().equals("stopTheService")) {
            description = "Stops this service. The connection will be lost.";
        } else if (info.getName().equals("changeHost")) {
            description = "Verander een host";
        } else if (info.getName().equals("removeHost")) {
            description = "Verwijder een host";
        } else if (info.getName().equals("setPrimaryISPscript")) {
            description = "Set Path and name of the script to switch to the primary ISP";
        } else if (info.getName().equals("setBackupISPscript")) {
            description = "Set Path and name of the script to switch to the backup ISP";
        } else if (info.getName().equals("addHost")) {
            description = "Voeg een host toe om de verbinding te controleren.";
        } else if (info.getName().equals("setEmailAddress")) {
            description = "Set where the switchover messages go";
        } else if (info.getName().equals("setPrimarySMTPserver")) {
            description = "Set SMTP server of primary ISP";
        } else if (info.getName().equals("setBackupSMTPserver")) {
            description = "Set SMTP server of backup ISP";
        } else if (info.getName().equals("changeCurrentISP")) {
            description = "DO NOT SWITCH but change the current ISP to primary or visa versa";
        } else if (info.getName().equals("setTriggerduration")) {
            description = "Set the wait time for switchover in seconds";
        } else if (info.getName().equals("setRetryInterval")) {
            description = "Set the wait time for trying to revert back to the primary ISP in seconds";
        } else if (info.getName().equals("setMaxRetries")) {
            description = "Set the maximum number of retries to revert back to the primary ISP";
        } else if (info.getName().equals("showControlledHosts")) {
            description = "Toon de hosts waarmee de verbinding wordt gecontroleerd";
        } else if (info.getName().equals("revertToPrimayISP")) {
            description = "Revert (switch back) to the primary ISP";
        } else if (info.getName().equals("switchToBackupISP")) {
            description = "Switch over to the backup ISP";
        } else if (info.getName().equals("setLogFileName")) {
            description = "Set Path and name of the log file and try to log to this file";
        }
        return description;
    }
    
    /**
     * Get the program version
     */
    @Override
    public String getProgramVersion() {
        return "SwitchISPMaven v.1.0";
    }

    /**
     * Get The current ISP connection
     */
    @Override
    public String getCurrentISP() {
        if (g.currentISP == g.primaryISP) {
            return "Primaire ISP";
        }
        if (g.currentISP == g.backupISP) {
            return "Backup ISP";
        }
        return "De ISP is fout ingesteld op de waarde " + g.currentISP;
    }

    /**
     * Get Where the switchover messages go
     */
    @Override
    public String getEmailAddress() {
        return g.emailAddress;
    }

    /**
     * Set where the switchover messages go
     */
    @Override
    public String setEmailAddress(String value) {
        //Initialize reg ex for email.
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = value;
        //Make the comparison case-insensitive.
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            g.emailAddress = value;
            return "Ok";
        } else {
            return "Het adres heeft een ongeldig formaat";
        }
    }

    /**
     * Get The wait time for switchover
     */
    @Override
    public long getTriggerduration() {
        return g.triggerDuration;
    }

    /**
     * Set the wait time for switchover
     */
    @Override
    public String setTriggerduration(long value) {
        if (value >= 20) {
            g.triggerDuration = value;
            return "Ok";
        } else {
            return "De duur moet minimaal 20 seconden zijn";
        }
    }

    /**
     * Get the time interval for trying to revert back to the promary ISP
     */
    @Override
    public long getRetryInterval() {
        return g.retryInterval;
    }

    /**
     * Set the time interval for trying to revert back to the promary ISP and reset
     */
    @Override
    public String setRetryInterval(long value) {
        if (value >= 60) {
            g.retryInterval = value;
            so.resetAutoSwitch();
            return "Ok";
        } else {
            return "De duur moet minimaal 60 seconden zijn";
        }
    }

    /**
     * Get the maximum number of retries to revert back to the promary ISP
     */
    @Override
    public long getMaxRetries() {
        return g.maxRetries;
    }

    /**
     * Set the maximum number of retries to revert back to the primary ISP and reset
     */
    @Override
    public String setMaxRetries(int value) {
        if (value >= 0) {
            g.maxRetries = value;
            so.resetAutoSwitch();
            return "Ok";
        } else {
            return "Het aantal pogingen moet 0 of meer zijn";
        }
    }

    /**
     * Get Path and name of the log file
     */
    @Override
    public String getLogFileName() {
        return g.logFileName;
    }

    /**
     * Set Path and name of the log file and try to log to this file
     */
    @Override
    public String setLogFileName(String value) {
        String newLogFileName = value;
        myLogger.log(Level.INFO, "De logfile wordt veranderd naar {0}", newLogFileName);
        if (myLogger.initLogger(newLogFileName)) {
            myLogger.log(Level.INFO, "De logfile is veranderd van {0} naar deze file", g.logFileName);
            g.logFileName = newLogFileName;
            return "Ok";
        } else {
            myLogger.initLogger(g.logFileName);
            return "De verandering van de log file naar " + newLogFileName + " is mislukt, deze logfile blijft in gebruik.";
        }
    }

    /**
     * Get Path and name of the script to switch to the primary ISP
     */
    @Override
    public String getPrimaryISPscript() {
        return g.primaryISPscript;
    }

    /**
     * Set Path and name of the script to switch to the primary ISP
     */
    @Override
    public String setPrimaryISPscript(String value) {
        File file = new File(value);
        if (file.exists() && file.isFile()) {
            g.primaryISPscript = value;
            return "Ok";
        } else {
            return "Het script kan niet worden gevonden";
        }
    }

    /**
     * Get Path and name of the script to switch to the backup ISP
     */
    @Override
    public String getBackupISPscript() {
        return g.backupISPscript;
    }

    /**
     * Set Path and name of the script to switch to the backup ISP
     */
    @Override
    public String setBackupISPscript(String value) {
        File file = new File(value);
        if (file.exists() && file.isFile()) {
            g.backupISPscript = value;
            return "Ok";
        } else {
            return "Het script kan niet worden gevonden";
        }
    }

    /**
     * Get SMTP server of primary ISP
     */
    @Override
    public String getPrimarySMTPserver() {
        return g.primarySMTPserver;
    }

    /**
     * Set SMTP server of primary ISP
     */
    @Override
    public String setPrimarySMTPserver(String value) {
        if (checkURL(value)) {
            g.primarySMTPserver = value;
            return "Ok";
        } else {
            return "De ingevoerde host heeft een ongeldige URL";
        }
    }

    /**
     * Get SMTP server of the backup ISP
     */
    @Override
    public String getBackupSMTPserver() {
        return g.backupSMTPserver;
    }

    /**
     * Set SMTP server of the backup ISP
     */
    @Override
    public String setBackupSMTPserver(String value) {
        if (checkURL(value)) {
            g.backupSMTPserver = value;
            return "Ok";
        } else {
            return "De ingevoerde host heeft een ongeldige URL";
        }
    }

    private int calcCurrentLogLevelIndex() {
        int currentLogLevelIndex = 0;
        for (int i = 0; i < levels.length; i++) {
            if (g.currentLogLevel == levels[i]) {
                currentLogLevelIndex = i;
            }
        }
        return currentLogLevelIndex;
    }

    /**
     * Set the log level one step finer is possible
     *
     * The Level class defines a set of standard logging levels that can be used
     * to control logging output. The logging Level objects are ordered and are
     * specified by ordered integers. Enabling logging at a given level also
     * enables logging at all higher levels. The levels in descending order are:
     * SEVERE (highest value) WARNING INFO CONFIG FINE FINER FINEST (lowest
     * value) In addition there is a level OFF that can be used to turn off
     * logging, and a level ALL that can be used to enable logging of all
     * messages.
     */
    @Override
    public String setCoarserLogLevel() {
        int currentLogLevelIndex = calcCurrentLogLevelIndex();
        if (currentLogLevelIndex < levels.length - 1) {
            g.currentLogLevel = levels[currentLogLevelIndex + 1];
            myLogger.setLevel(g.currentLogLevel);
            return "Log level verhoogd naar " + g.currentLogLevel;
        } else {
            return " Het log level is " + g.currentLogLevel + " en kan niet verder worden verhoogd";
        }
    }

    /**
     * Set the log level one step less finer if possible
     */
    @Override
    public String setFinerLogLevel() {
        int currentLogLevelIndex = calcCurrentLogLevelIndex();
        if (currentLogLevelIndex > 0) {
            g.currentLogLevel = levels[currentLogLevelIndex - 1];
            myLogger.setLevel(g.currentLogLevel);
            return "Log level verlaagd naar " + g.currentLogLevel;
        } else {
            return " Het log level is " + g.currentLogLevel + " en kan niet verder worden verlaagd";
        }
    }

    /**
     * Stop the hosts scanning
     */
    @Override
    @SuppressWarnings("static-access")
    public void stop() {
        controller.stop();
        myLogger.log(Level.INFO, "De controller wordt gestopt.");
    }

    /**
     * Start the hosts scanning
     */
    @Override
    public void start() {
        if (controller.isRunning()) {
            controller.restart();
            myLogger.log(Level.INFO, "De controller is herstart.");
        } else {
            myLogger.log(Level.INFO, "De controller is niet herstart omdat deze al liep.");
        }
    }

    /**
     * Stops if running and start hosts scanning wih current parameters
     */
    @Override
    public void restart() {
        if (controller.isRunning()) {
            controller.stop();
        }
        controller.restart();
        myLogger.log(Level.INFO, "De controller is herstart met de huidige parameterwaarden.");
    }

    /**
     * Stops if running en starts hosts scanning with saved parameters
     */
    @Override
    public void restartWithSavedParams() {
        if (controller.isRunning()) {
            controller.stop();
        }
        f.setProperties();
        String missingVars = f.setVars();
        controller.restart();
        myLogger.log(Level.INFO, "De controller is herstart met de opgeslagen parameterwaarden.");
        if (!missingVars.isEmpty()) myLogger.log(Level.WARNING, "{0} are missing!", missingVars);
    }

    /**
     * Update the properties object with the current hosts. Set the values of
     * other host keys to an empty string.
     */
    private void updatePropertiesWithHosts() {
        int i;
        for (i = 0; i < g.hosts.size(); i++) {
            g.props.setProperty("host" + i, (String) g.hosts.get(i));
        }
        while (g.props.getProperty("host" + i) != null) {
            g.props.setProperty("host" + i, "");
            i++;
        }
    }

    /**
     * Save the current parameters
     */
    @Override
    public void saveParams() {
        g.props.setProperty("currentISP", (g.currentISP == 0) ? "primaryISP" : "backupISP");
        g.props.setProperty("triggerDuration", g.triggerDuration + "");
        g.props.setProperty("retryInterval", g.retryInterval + "");
        g.props.setProperty("maxRetries", g.maxRetries + "");
        g.props.setProperty("backupISPselected", g.backupISPselected ? "true" : "false");
        g.props.setProperty("primaryISPscript", g.primaryISPscript);
        g.props.setProperty("backupISPscript", g.backupISPscript);
        g.props.setProperty("emailAddress", g.emailAddress);
        g.props.setProperty("primarySMTPserver", g.primarySMTPserver);
        g.props.setProperty("backupSMTPserver", g.backupSMTPserver);
        g.props.setProperty("logFileName", g.logFileName);
        updatePropertiesWithHosts();
        f.writeProperties();
    }

    private boolean checkURL(String uRL) {
        String expression = "^[a-z0-9-]+.([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = uRL;
        //Make the comparison case-insensitive.
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }

    /**
     * Verander een host
     *
     * @param hostNumberIndex Index of the host (0 for the most significant)
     * @param newHost The new host URL
     * @return java.lang.String
     */
    @Override
    public String changeHost(int hostNumberIndex, String newHost) {
        if (hostNumberIndex >= 0 && hostNumberIndex < g.hosts.size()) {
            if (checkURL(newHost)) {
                // test a TCP connection on port 80 with the destination host and a time-out of 2000 ms.
                if (f.testConnection(newHost, 80, 2000)) {
                    String oldHost = g.hosts.get(hostNumberIndex);
                    g.hosts.set(hostNumberIndex, newHost);
                    myLogger.log(Level.INFO, "De host {0} is vervangen door {1}", new Object[]{oldHost, newHost});
                    return "Host " + oldHost + " is vervangen door " + newHost;
                } else {
                    return "De ingevoerde host geeft geen response op poort 80";
                }
            } else {
                return "De ingevoerde host heeft een ongeldige URL";
            }
        } else {
            return "Er is geen host met index " + hostNumberIndex;
        }
    }

    /**
     * Verwijder een host
     *
     * @param hostNumberIndex Index of the host (0 for the most significant)
     * @param newHost The new host URL
     * @return java.lang.String
     */
    @Override
    public String removeHost(int hostNumberIndex) {
        if (hostNumberIndex >= 0 && hostNumberIndex < g.hosts.size()) {
            if (g.hosts.size() > 1) {
                String hostName = g.hosts.get(hostNumberIndex);
                g.hosts.remove(hostNumberIndex);
                return "Host " + hostName + " is verwijderd";
            } else {
                return "Er blijft geen host over na verwijdering; voeg eerst een nieuwe toe";
            }
        } else {
            return "Er is geen host met index " + hostNumberIndex;
        }
    }

    /**
     * Voeg een host toe om de verbinding te controleren. Test a TCP connection
     * on port 80 with the destination host and a time-out of 2000 ms.
     *
     * @param extraHost De extra host URL
     * @return java.lang.String
     */
    @Override
    public String addHost(String extraHost) {
        if (checkURL(extraHost)) {
            if (f.testConnection(extraHost, 80, 2000)) {
                g.hosts.add(extraHost);
                myLogger.log(Level.INFO, "De host {0} is toegevoegd.", extraHost);
                return "Host " + extraHost + " is toegevoegd";
            } else {
                return "De ingevoerde host geeft geen response op poort 80";
            }
        } else {
            return "De ingevoerde host heeft een ongeldige URL";
        }
    }

    /**
     * DO NOT SWITCH but change the current ISP to primary or visa versa
     *
     * @return java.lang.String
     */
    @Override
    public String changeCurrentISP() {
        if (g.currentISP == g.primaryISP) {
            g.currentISP = g.backupISP;
            return "The current ISP is nu de backup ISP";
        } else {
            g.currentISP = g.primaryISP;
            return "The current ISP is nu de primaire ISP";
        }
    }

    /**
     * Toon de hosts waarmee de verbinding wordt gecontroleerd
     *
     * @return java.lang.String
     */
    @Override
    public String showControlledHosts() {
        StringBuilder buf = new StringBuilder();
        int i = 0;
        for (String s : g.hosts) {
            buf.append(i).append(": ").append(s).append("\n");
            i++;
        }
        return buf.toString();
    }

    /**
     * Get het aantal succesvolle checks van de verbinding
     */
    @Override
    public long getSuccessfulChecks() {
        return g.successfulChecks;
    }

    /**
     * Get het aantal mislukte checks van de verbinding
     */
    @Override
    public long getFailedChecks() {
        return g.failedChecks;
    }

    /**
     * Get het aantal overschakelingen sinds de start van het programma
     */
    @Override
    public int getSwitchoverCount() {
        return g.switchoverCount;
    }

    /**
     * Revert (switch back) to the primary ISP
     *
     * @return java.lang.String
     */
    @Override
    public String revertToPrimayISP(String reason) {
        if (g.currentISP == g.backupISP) {
            myLogger.log(Level.INFO, "Manuele omschakeling naar de primaire ISP is aangevraagd om de volgende reden: {0}", reason);
            if (!so.doSwitchOver(true, true, reason)) {
                return "De omschakeling is mislukt, zie de log.";
            } else {
                g.backupISPselected = false;
                so.resetAutoSwitch();
                return "Ok";
            }
        } else {
            return "De verbinding maakt al gebruik van de primaire ISP.";
        }
    }

    /**
     * Switch over to the backup ISP
     *
     * @return java.lang.String
     */
    @Override
    public String switchToBackupISP(String reason) {
        if (g.currentISP == g.primaryISP) {
            myLogger.log(Level.INFO, "Manuele omschakeling naar de backup ISP is aangevraagd om de volgende reden: {0}", reason);

            if (!so.doSwitchOver(true, true, reason)) {
                return "De omschakeling is mislukt, zie de log.";
            } else {
                g.backupISPselected = true;
                return "Ok";
            }
        } else {
            return "De verbinding maakt al gebruik van de backup ISP.";
        }
    }

    /**
     * Stops this service. The connection will be lost.
     */
    @Override
    public String stopTheService() {
        controller.exit();
        myLogger.log(Level.INFO, "De service is gestopt door de JMX client.\n");
        return "De service is gestopt";
    }

    @Override
    public void simulatePrimaryISPIsDown() {
        g.simulatePrimaryISPIsDown = true;
    }

    @Override
    public void simulateBackupISPIsDown() {
        g.simulateBackupISPIsDown = true;
    }

    @Override
    public void stopSimulationISPIsDown() {
        g.simulatePrimaryISPIsDown = false;
        g.simulateBackupISPIsDown = false;
    }
}
