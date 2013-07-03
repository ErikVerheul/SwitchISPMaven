package nl.verheulconsultants.switchispmaven;

/**
 * Interface MBeanFromMainMBean
 *
 * @author erik
 */
public interface MBeanFromMainMBean {

    /**
     * Get the current ISP connection
     */
    public String getCurrentISP();

    /**
     * Get where the switchover messages go
     */
    public String getEmailAddress();

    /**
     * Set where the switchover messages go
     */
    public String setEmailAddress(String value);

    /**
     * Get the wait time for switchover
     */
    public long getTriggerduration();

    /**
     * Set the wait time for switchover
     */
    public String setTriggerduration(long value);

    /**
     * Get the time interval for trying to revert back to the promary ISP
     */
    public long getRetryInterval();

    /**
     * Set the time interval for trying to revert back to the promary ISP
     */
    public String setRetryInterval(long value);

    /**
     * Get the maximum number of retries to revert back to the promary ISP
     */
    public long getMaxRetries();

    /**
     * Set the maximum number of retries to revert back to the promary ISP
     */
    public String setMaxRetries(int value);

    /**
     * Get path and name of the log file
     */
    public String getLogFileName();

    /**
     * Set path and name of the log file
     */
    public String setLogFileName(String value);

    /**
     * Get path and name of the script to switch to the primary ISP
     */
    public String getPrimaryISPscript();

    /**
     * Set path and name of the script to switch to the primary ISP
     */
    public String setPrimaryISPscript(String value);

    /**
     * Get path and name of the script to switch to the backup ISP
     */
    public String getBackupISPscript();

    /**
     * Set path and name of the script to switch to the backup ISP
     */
    public String setBackupISPscript(String value);

    /**
     * Get SMTP server of primary ISP
     */
    public String getPrimarySMTPserver();

    /**
     * Set SMTP server of primary ISP
     */
    public String setPrimarySMTPserver(String value);

    /**
     * Get SMTP server of the backup ISP
     */
    public String getBackupSMTPserver();

    /**
     * Set SMTP server of the backup ISP
     */
    public String setBackupSMTPserver(String value);

    /**
     * Set the loglevel one step finer is posible
     */
    public String setCoarserLogLevel();

    /**
     * Set the log level one step less finer if possible
     */
    public String setFinerLogLevel();

    /**
     * Simulate primary ISP is not reachable
     */
    public void simulatePrimaryISPIsDown();

    /**
     * Simulate backup ISP is not reachable
     */
    public void simulateBackupISPIsDown();

    /**
     * Stop to simulate that any ISP is not reachable
     */
    public void stopSimulationISPIsDown();

    /**
     * Stop the hosts scanning
     */
    public void stop();

    /**
     * Start the hosts scanning
     */
    public void start();

    /**
     * Stops if running and start hosts scanning wih current parameters
     */
    public void restart();

    /**
     * Stops if running en starts hosts scanning with saved parameters
     */
    public void restartWithSavedParams();

    /**
     * Save the current parameters
     */
    public void saveParams();

    /**
     * Voeg een host toe om de verbinding te controleren
     *
     * @param extraHost De extra host URL
     * @return java.lang.String
     */
    public String addHost(String extraHost);

    /**
     * Verander een host
     *
     * @param hostNumberIndex Index of the host (0 for the most significant)
     * @param newHost The new host URL
     * @return java.lang.String
     */
    public String changeHost(int hostNumberIndex, String newHost);

    /**
     * Verwijder een host
     *
     * @param hostNumberIndex Index of the host (0 for the most significant)
     * @return java.lang.String
     */
    public String removeHost(int hostNumberIndex);

    /**
     * Change the current ISP to primary or backup
     *
     * @return java.lang.String
     */
    public String changeCurrentISP();

    /**
     * Toon de hosts waarmee de verbinding wordt gecontroleerd
     *
     * @return java.lang.String
     */
    public String showControlledHosts();

    /**
     * Get Het aantal succevolle checks van de verbinding
     */
    public long getSuccessfulChecks();

    /**
     * Get Het aantal mislukte checks van de verbinding
     */
    public long getFailedChecks();

    /**
     * Get Het aantal overschakelingen sinds de start van het programma
     */
    public int getSwitchoverCount();

    /**
     * Revert (switch back) to the primary ISP
     *
     * @return java.lang.String
     */
    public String revertToPrimayISP(String reason);

    /**
     * Switch over to the backup ISP
     *
     * @return java.lang.String
     */
    public String switchToBackupISP(String reason);

    /**
     * Stops this service. The connection will be lost.
     */
    public String stopTheService();
}
