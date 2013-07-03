package nl.verheulconsultants.switchispmaven;

import java.util.logging.LogRecord;

/**
 *
 * @author Erik
 */
public interface LoggerInt {

    OutputQueue getOutputQueue();

    boolean initLogger(String logFileN);

    void log(LogRecord record);
}
