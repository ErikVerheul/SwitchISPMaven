package nl.verheulconsultants.switchispmaven;

import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;

/**
 *
 * @author Erik
 */
public class EMailClientImp implements EMailClient {
    private MyLogger myLogger;
    private Functions f;

    EMailClientImp(Functions f, MyLogger myLogger) {
        this.f = f;
        this.myLogger = myLogger;
    }

    private void showErrorMessage(String msg, int replyCode, String errMsg) {
        myLogger.log(Level.SEVERE,"{0}" + "\n"
                + "De errorcode = " + "{1}\nDe error message is: {2}", new Object[]{msg, replyCode, errMsg});
    }

    @Override
    public boolean sendEMail(String sender, String recipient, String message, String subject) {
        SMTPClient client = new SMTPClient();
        String usedSMTPserver = f.getCurrentSMTPserver();
        try {
            int replyCode;

            client.connect(usedSMTPserver);
            // After connection attempt, check the reply code to verify success.
            replyCode = client.getReplyCode();
            if (!SMTPReply.isPositiveCompletion(replyCode)) {
                client.disconnect();
                showErrorMessage("De server " + usedSMTPserver + " weigert de verbindingsaanvraag.", replyCode, client.getReplyString());
                return false;
            }

            // Do useful stuff here.
            if (!client.login()) {
                myLogger.log(Level.SEVERE,"Inloggen met HELO commando is mislukt.");
                return false;
            }
            //(specify who's sending mail)
            replyCode = client.mail(recipient); 
            if (!SMTPReply.isPositiveCompletion(replyCode)) {
                showErrorMessage("Fout bij het verzenden van het MAIL bericht.", replyCode, client.getReplyString());
                return false;
            }
            //(specify who's getting it)
            replyCode = client.rcpt(recipient);
            if (!SMTPReply.isPositiveCompletion(replyCode)) {
                showErrorMessage("Fout bij het verzenden van het RCPT bericht.", replyCode, client.getReplyString());
                return false;
            }

            java.io.Writer writer = client.sendMessageData();
            if (writer == null) {
                showErrorMessage("Fout bij het verzenden van het sendMessageData bericht.", client.getReplyCode(), client.getReplyString());
                return false;
            }

            SimpleSMTPHeader header = new SimpleSMTPHeader(sender, recipient, subject);
            writer.write(header.toString());
            writer.write(message);
            writer.close();
            if (!client.completePendingCommand()) {
                showErrorMessage("Het bericht kon niet worden verzonden.", client.getReplyCode(), client.getReplyString());
                return false;
            }

            return true;
        } catch (IOException e) {
            myLogger.log(Level.SEVERE, "Kan geen verbinding maken met de server {0}.\nDe oorzaak is: {1}", new Object[]{usedSMTPserver, e});
            return false;
        } finally {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (IOException ignore) {
                    // do nothing
                }
            }
        }
    }
}
