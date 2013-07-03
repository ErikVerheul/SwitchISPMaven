package nl.verheulconsultants.switchispmaven;

/**
 *
 * @author Erik
 */
public interface SwitchOver {

    boolean doSwitchOver(String command, boolean sendMail, boolean manualSwitch, String reason);
}
