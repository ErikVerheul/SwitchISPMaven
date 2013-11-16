/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author erik
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/spring.xml")
public class MyLoggerTest {

    @Autowired
    private Globals g;
    @Autowired
    private OutputQueue queue;
    @Autowired
    private MyLogger instance;

    public MyLoggerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getOutputQueue method, of class MyLogger.
     */
    @Test
    public void testGetOutputQueue() {
        System.out.println("getOutputQueue");
        Object result = instance.getOutputQueue();
        assertNotNull(result);
    }

    /**
     * Test of initLogger method, of class MyLogger.
     */
    @Test
    public void testInitLogger() {
        System.out.println("initLogger");
        String logFileN = g.logFileName;
        boolean expResult = true;
        boolean result = instance.initLogger(logFileN);
        assertEquals(expResult, result);
    }

    /**
     * Get the last line from the log
     * @return the last line of the log file or null when not found
     */
    String getLastLogLine() {
        String logFileN = g.logFileName;
        BufferedReader reader;        
        try {
            reader = new BufferedReader(new FileReader(logFileN));
            String line;
            String lastLine = null;
            try {
                while ((line = reader.readLine()) != null) {
                    lastLine = line;
                }
                return lastLine;
            } catch (IOException ex) {
                System.err.println("IOException: " + ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    System.err.println("IOException: " + ex);
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("FileNotFoundException: " + ex);
        }
        return null;
    }

    /**
     * Test of log method, of class MyLogger.
     */
    @Test
    public void testLog() {
        System.out.println("log");
        String expResult = "test";
        instance.log(Level.INFO, expResult);
        assertTrue("log message not found at end of log file", getLastLogLine().endsWith(expResult));
        assertTrue("log message not found at end of log queue", queue.getAll().endsWith(expResult+"\n"));
    }
}