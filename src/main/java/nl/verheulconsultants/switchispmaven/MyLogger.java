/*
 * This class extends the Java logger with a copy of the last entries
 * in a queue which can be viewed with JMX.
 */
package nl.verheulconsultants.switchispmaven;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Erik
 */
public class MyLogger extends Logger {

    private OutputQueue outputQueue;
    private FileHandler loggerFileHandler;
    static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("switchispservice");

    MyLogger(Globals g, String resourceBundleName, OutputQueue outputQueue) {
        super(g.logFileName, resourceBundleName);
        this.outputQueue = outputQueue;
    }

    public OutputQueue getOutputQueue() {
        return outputQueue;
    }

    /**
     * Open or create a log file with file name
     *
     * @param logger the Logger object
     * @param logFileName the Logger File name
     */
    private void createFileHandler(java.util.logging.Logger logger, String logFileName) throws IOException  {
        loggerFileHandler = new FileHandler(logFileName, true);
        loggerFileHandler.setFormatter(new SimpleFormatter());
        // Send myLogger output to our FileHandler.
        logger.addHandler(loggerFileHandler);
        logger.setLevel(Level.INFO);
    }

    public boolean initLogger(String logFileN) {
        try {
            // close the loggerFileHandler if open
            if (loggerFileHandler != null) {
                loggerFileHandler.close();
            }
            //open or create a log file
            createFileHandler(logger, logFileN);
            return true;
        } catch (IOException e) {
            System.err.println("Fout bij het initieren van de logger, de oorzaak is: " + e);
            return false;
        }
    }

    @Override
    public void log(LogRecord record) {
        MessageFormat mf = new MessageFormat(record.getMessage());
        try {
            outputQueue.add(new Date(record.getMillis()) + ": " + record.getLevel() + ": " + mf.format(record.getParameters()));
        } catch (InterruptedException ex) {
            System.err.println("Kan log record niet toevoegen, de oorzaak is: " + ex);
        }
        logger.log(record);
    }
}
