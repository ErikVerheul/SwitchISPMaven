/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.verheulconsultants.switchispmaven;

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
public class TryToRevertToPrimaryISPTest {

    @Autowired
    Globals g;
    @Autowired
    TryToRevertToPrimaryISP instance;

    public TryToRevertToPrimaryISPTest() {
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
     * Test of reset method, of class TryToRevertToPrimaryISP.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        instance.reset();
        assertTrue("retries not reset properly.", g.maxRetries == instance.getRetries());
        assertTrue("interval not reset properly.", g.retryInterval == instance.getInterval());
    }

    /**
     * Test of tryToRevert method, of class TryToRevertToPrimaryISP.
     */
    @Test
    public void testTryToRevert() {
        System.out.println("tryToRevert1");
        g.currentISP = Globals.primaryISP;
        g.backupISPselected = false;
        instance.tryToRevert();
        assertTrue("Should still be on primary ISP.", g.currentISP == Globals.primaryISP);
   
        System.out.println("tryToRevert2");
        g.currentISP = Globals.backupISP;
        g.backupISPselected = true;
        instance.tryToRevert();
        assertTrue("Should not switch when debiberately on backup", g.currentISP == Globals.backupISP);


        System.out.println("tryToRevert3");
        g.currentISP = Globals.backupISP;
        g.backupISPselected = false;
        g.mockCheckISPisOK = true;
        instance.setDoNotTryBefore(System.currentTimeMillis() + instance.getInterval() * 1000);
        instance.tryToRevert();
        assertTrue("Should not switch before interval has passed", g.currentISP == Globals.backupISP);
        
        System.out.println("tryToRevert4");
        g.currentISP = Globals.backupISP;
        g.backupISPselected = false;
        g.mockCheckISPisOK = false; //test should fail 
        instance.setDoNotTryBefore(System.currentTimeMillis());
        instance.tryToRevert();
        assertTrue("Should not have switched to primary ISP.", g.currentISP != Globals.primaryISP);
        assertTrue("retries not subtracted by 1", instance.getRetries() == g.maxRetries - 1);
        
   
        System.out.println("tryToRevert5");
        g.currentISP = Globals.backupISP;
        g.backupISPselected = false;
        g.mockCheckISPisOK = true;
        instance.setDoNotTryBefore(System.currentTimeMillis());
        instance.tryToRevert();
        assertTrue("Not switched to primary ISP.", g.currentISP == Globals.primaryISP);
    }
}