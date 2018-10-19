package nl.verheulconsultants.switchispmaven;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

/**
 *
 * @author Erik
 */
class Globals {

  static final long ONE_SECOND = 1000L;
  static final long FIVE_SECONDS = 5000L;
  static final long DEFAULT_TRIGGERDURATION_IN_SEC = 30L;
  static final long DEFAULT_RETRYINTERVAL_IN_SEC = 300L;
  static final int DEFAULT_MAXRETRIES = 5;
  static final int PRIMARY_ISP = 0;
  static final int BACKUP_ISP = 1;
  static final int HTTP_PORT = 80;
  static final int TIME_OUT = 2000;

  private List<String> hosts;
  private long lastContactWithAnyHost;
  private String propsFileName;
  private Properties props;
  private boolean propertiesSetTemporarely;
  private long successfulChecks;
  private long failedChecks;
  private int switchoverCount;
  // these values must be set to real values using JConsole
  private int currentISP;
  private long triggerDuration;
  private long retryInterval;
  private int maxRetries;
  private boolean backupISPselected;
  private String emailAddress;
  private String logFileName;
  private String primaryISPscript;
  private String backupISPscript;
  private String primarySMTPserver;
  private String backupSMTPserver;
  private Level currentLogLevel;
  // end of members which can be changed by the user
  private boolean simulatePrimaryISPIsDown;
  private boolean simulateBackupISPIsDown;
  private boolean mockCheckISPisOK = false;

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
    triggerDuration = DEFAULT_TRIGGERDURATION_IN_SEC;
    retryInterval = DEFAULT_RETRYINTERVAL_IN_SEC;
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

  /**
   * @return the triggerDuration
   */
  long getTriggerDuration() {
    return triggerDuration;
  }

  /**
   * @param triggerDuration the triggerDuration to set
   */
  void setTriggerDuration(long triggerDuration) {
    this.triggerDuration = triggerDuration;
  }

  /**
   * @return the currentLogLevel
   */
  Level getCurrentLogLevel() {
    return currentLogLevel;
  }

  /**
   * @param currentLogLevel the currentLogLevel to set
   */
  void setCurrentLogLevel(Level currentLogLevel) {
    this.currentLogLevel = currentLogLevel;
  }

  /**
   * @return the propertiesSetTemporarely
   */
  boolean isPropertiesSetTemporarely() {
    return propertiesSetTemporarely;
  }

  /**
   * @return the successfulChecks
   */
  long getSuccessfulChecks() {
    return successfulChecks;
  }

  /**
   * Increase successful checks with one
   */
  void increaseSuccessfulChecks() {
    successfulChecks++;
  }

  /**
   * @return the failedChecks
   */
  long getFailedChecks() {
    return failedChecks;
  }

  /**
   * Increase failed checks with one
   */
  void increaseFailedChecks() {
    failedChecks++;
  }

  /**
   * @return the switchoverCount
   */
  int getSwitchoverCount() {
    return switchoverCount;
  }

  /**
   * Increase the switch-over count with one
   */
  void increaseSwitchoverCount() {
    switchoverCount++;
  }

  /**
   * @return the currentISP
   */
  int getCurrentISP() {
    return currentISP;
  }

  /**
   * @param currentISP the currentISP to set
   */
  void setCurrentISP(int currentISP) {
    this.currentISP = currentISP;
  }

  /**
   * @return the retryInterval
   */
  long getRetryInterval() {
    return retryInterval;
  }

  /**
   * @param retryInterval the retryInterval to set
   */
  void setRetryInterval(long retryInterval) {
    this.retryInterval = retryInterval;
  }

  /**
   * @return the maxRetries
   */
  int getMaxRetries() {
    return maxRetries;
  }

  /**
   * @param maxRetries the maxRetries to set
   */
  void setMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
  }

  /**
   * @return the backupISPselected
   */
  boolean isBackupISPselected() {
    return backupISPselected;
  }

  /**
   * @param backupISPselected the backupISPselected to set
   */
  void setBackupISPselected(boolean backupISPselected) {
    this.backupISPselected = backupISPselected;
  }

  /**
   * @return the emailAddress
   */
  String getEmailAddress() {
    return emailAddress;
  }

  /**
   * @param emailAddress the emailAddress to set
   */
  void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  /**
   * @return the logFileName
   */
  String getLogFileName() {
    return logFileName;
  }

  /**
   * @param logFileName the logFileName to set
   */
  void setLogFileName(String logFileName) {
    this.logFileName = logFileName;
  }

  /**
   * @return the primaryISPscript
   */
  String getPrimaryISPscript() {
    return primaryISPscript;
  }

  /**
   * @param primaryISPscript the primaryISPscript to set
   */
  void setPrimaryISPscript(String primaryISPscript) {
    this.primaryISPscript = primaryISPscript;
  }

  /**
   * @return the backupISPscript
   */
  String getBackupISPscript() {
    return backupISPscript;
  }

  /**
   * @param backupISPscript the backupISPscript to set
   */
  void setBackupISPscript(String backupISPscript) {
    this.backupISPscript = backupISPscript;
  }

  /**
   * @return the primarySMTPserver
   */
  String getPrimarySMTPserver() {
    return primarySMTPserver;
  }

  /**
   * @param primarySMTPserver the primarySMTPserver to set
   */
  void setPrimarySMTPserver(String primarySMTPserver) {
    this.primarySMTPserver = primarySMTPserver;
  }

  /**
   * @return the backupSMTPserver
   */
  String getBackupSMTPserver() {
    return backupSMTPserver;
  }

  /**
   * @param backupSMTPserver the backupSMTPserver to set
   */
  void setBackupSMTPserver(String backupSMTPserver) {
    this.backupSMTPserver = backupSMTPserver;
  }

  /**
   * @return the simulatePrimaryISPIsDown
   */
  boolean isSimulatePrimaryISPIsDown() {
    return simulatePrimaryISPIsDown;
  }

  /**
   * @return the simulateBackupISPIsDown
   */
  boolean isSimulateBackupISPIsDown() {
    return simulateBackupISPIsDown;
  }

  /**
   * @return the mockCheckISPisOK
   */
  boolean isMockCheckISPisOK() {
    return mockCheckISPisOK;
  }

  /**
   * @return the lastContactWithAnyHost
   */
  long getLastContactWithAnyHost() {
    return lastContactWithAnyHost;
  }

  /**
   * @param lastContactWithAnyHost the lastContactWithAnyHost to set
   */
  void setLastContactWithAnyHost(long lastContactWithAnyHost) {
    this.lastContactWithAnyHost = lastContactWithAnyHost;
  }

  /**
   * @return the propsFileName
   */
  String getPropsFileName() {
    return propsFileName;
  }

  /**
   * @param propsFileName the propsFileName to set
   */
  void setPropsFileName(String propsFileName) {
    this.propsFileName = propsFileName;
  }

  /**
   * @return the props
   */
  Properties getProps() {
    return props;
  }

  /**
   * @param props the props to set
   */
  void setProps(Properties props) {
    this.props = props;
  }

  /**
   * @param propertiesSetTemporarely the propertiesSetTemporarely to set
   */
  void setPropertiesSetTemporarely(boolean propertiesSetTemporarely) {
    this.propertiesSetTemporarely = propertiesSetTemporarely;
  }

  /**
   * @return the hosts
   */
  List<String> getHosts() {
    return hosts;
  }

  /**
   * @param hosts the hosts to set
   */
  void setHosts(List<String> hosts) {
    this.hosts = hosts;
  }

  /**
   * @param simulatePrimaryISPIsDown the simulatePrimaryISPIsDown to set
   */
  void setSimulatePrimaryISPIsDown(boolean simulatePrimaryISPIsDown) {
    this.simulatePrimaryISPIsDown = simulatePrimaryISPIsDown;
  }

  /**
   * @param simulateBackupISPIsDown the simulateBackupISPIsDown to set
   */
  void setSimulateBackupISPIsDown(boolean simulateBackupISPIsDown) {
    this.simulateBackupISPIsDown = simulateBackupISPIsDown;
  }

  /**
   * @param mockCheckISPisOK the mockCheckISPisOK to set
   */
  void setMockCheckISPisOK(boolean mockCheckISPisOK) {
    this.mockCheckISPisOK = mockCheckISPisOK;
  }

}
