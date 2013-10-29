package nl.verheulconsultants.switchispmaven;

/*
 * This service continiously checks if the Internet connection is up. If not it runs a script to switch to a backup ISP or
 * visa versa. When a switch occurs an email is send to notify the administrator.
 * This program runs as a daemon or service. Use JConsole to administer the JMX enabled functions.
 * Usage: Select hosts which respond to TCP connection on port 80.
 * Option: When the switchISPservice.properties file is not located in the directory where the program is started,
 *         use the file/path as the first argument.
 *
 *
 * copyright Erik Verheul
 * Verheul Consultants 2011-2013
 */

import java.io.File;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class SwitchISPService {

    public static void main(String[] args) {
        System.out.println("Files are read from " + new File(".").getAbsolutePath());
        BeanFactory factory = new XmlBeanFactory(new ClassPathResource("spring.xml"));
        TheService service = (TheService) factory.getBean("theService");
        service.runTheService(args);       
    }
}
