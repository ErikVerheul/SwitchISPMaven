package nl.verheulconsultants.switchispmaven;

/**
 *
 * @author Erik
 */
public interface EMailClient {

    boolean sendEMail(String sender, String recipient, String message, String subject);
}
