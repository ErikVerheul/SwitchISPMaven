package nl.verheulconsultants.switchispmaven;

/**
 * Interface MBeanFromMainMBean
 *
 * @author erik
 */
public interface MBeanFromMainMBean {

    /**
     * Get the program version
     */
     String getProgramVersion();
    
    /**
     * Get the current ISP connection
     */
     String getCurrentISP();

    /**
     * Get where the switchover messages go
     */
     String getEmailAddress();

    /**
     * Set where the switchover messages go
     */
     String setEmailAddress(String value);

    /**
     * Get the wait time for switchover
     */
     long getTriggerduration();

    /**
     * Set the wait time for switchover
     */
     String setTriggerduration(long value);

    /**
     * Get the time interval for trying to revert back to the promary ISP
     */
     long getRetryInterval();

    /**
     * Set the time interval for trying to revert back to the promary ISP
     */
     String setRetryInterval(long value);

    /**
     * Get the maximum number of retries to revert back to the promary ISP
     */
     long getMaxRetries();

    /**
     * Set the maximum number of retries to revert back to the promary ISP
     */
     String setMaxRetries(int value);

    /**
     * Get path and name of the log file
     */
     String getLogFileName();

    /**
     * Set path and name of the log file
     */
     String setLogFileName(String value);

    /**
     * Get path and name of the script to switch to the primary ISP
     */
     String getPrimaryISPscript();

    /**
     * Set path and name of the script to switch to the primary ISP
     */
     String setPrimaryISPscript(String value);

    /**
     * Get path and name of the script to switch to the backup ISP
     */
     String getBackupISPscript();

    /**
     * Set path and name of the script to switch to the backup ISP
     */
     String setBackupISPscript(String value);

    /**
     * Get SMTP server of primary ISP
     */
     String getPrimarySMTPserver();

    /**
     * Set SMTP server of primary ISP
     */
     String setPrimarySMTPserver(String value);

    /**
     * Get SMTP server of the backup ISP
     */
     String getBackupSMTPserver();

    /**
     * Set SMTP server of the backup ISP
     */
     String setBackupSMTPserver(String value);

    /**
     * Set the loglevel one step finer is posible
     */
     String setCoarserLogLevel();

    /**
     * Set the log level one step less finer if possible
     */
     String setFinerLogLevel();

    /**
     * Simulate primary ISP is not reachable
     */
     void simulatePrimaryISPIsDown();

    /**
     * Simulate backup ISP is not reachable
     */
     void simulateBackupISPIsDown();

    /**
     * Stop to simulate that any ISP is not reachable
     */
     void stopSimulationISPIsDown();

    /**
     * Stop the hosts scanning
     */
     void stop();

    /**
     * Start the hosts scanning
     */
     void start();

    /**
     * Stops if running and start hosts scanning wih current parameters
     */
     void restart();

    /**
     * Stops if running en starts hosts scanning with saved parameters
     */
     void restartWithSavedParams();

    /**
     * Save the current parameters
     */
     void saveParams();

    /**
     * Voeg een host toe om de verbinding te controleren
     *
     * @param extraHost De extra host URL
     * @return java.lang.String
     */
     String addHost(String extraHost);

    /**
     * Verander een host
     *
     * @param hostNumberIndex Index of the host (0 for the most significant)
     * @param newHost The new host URL
     * @return java.lang.String
     */
     String changeHost(int hostNumberIndex, String newHost);

    /**
     * Verwijder een host
     *
     * @param hostNumberIndex Index of the host (0 for the most significant)
     * @return java.lang.String
     */
     String removeHost(int hostNumberIndex);

    /**
     * Change the current ISP to primary or backup
     *
     * @return java.lang.String
     */
     String changeCurrentISP();

    /**
     * Toon de hosts waarmee de verbinding wordt gecontroleerd
     *
     * @return java.lang.String
     */
     String showControlledHosts();

    /**
     * Get Het aantal succevolle checks van de verbinding
     */
     long getSuccessfulChecks();

    /**
     * Get Het aantal mislukte checks van de verbinding
     */
     long getFailedChecks();

    /**
     * Get Het aantal overschakelingen sinds de start van het programma
     */
     int getSwitchoverCount();

    /**
     * Revert (switch back) to the primary ISP
     *
     * @return java.lang.String
     */
     String revertToPrimayISP(String reason);

    /**
     * Switch over to the backup ISP
     *
     * @return java.lang.String
     */
     String switchToBackupISP(String reason);

    /**
     * Stops this service. The connection will be lost.
     */
     String stopTheService();
}
