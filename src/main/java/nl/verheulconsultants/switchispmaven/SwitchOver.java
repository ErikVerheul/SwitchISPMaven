package nl.verheulconsultants.switchispmaven;

/**
 *
 * @author Erik
 */
public interface SwitchOver {

    boolean doSwitchOver(boolean sendMail, boolean manualSwitch, String reason);
    void tryToRevert();
    void resetAutoSwitch();
}
