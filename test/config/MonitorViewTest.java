package config;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test the class MonitorView.
 */
public class MonitorViewTest {

    public MonitorViewTest() {
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
     * Test of equals method, of class MonitorView.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object obj = null;
        MonitorView instance = new MonitorView("10.10.10.21", "10.10.10.30", "30", "Test");
        Assert.assertEquals(true, instance.equals(new MonitorView("10.10.10.21", "10.10.10.30", "30", "Test")));
        Assert.assertEquals(false, instance.equals(new MonitorView("10.10.10.20", "10.10.10.30", "30", "Test")));
        Assert.assertEquals(false, instance.equals(new MonitorView("10.10.10.21", "10.10.10.31", "30", "Test")));
        Assert.assertEquals(false, instance.equals(new MonitorView("10.10.10.21", "10.10.10.30", "12", "Test")));
    }

    /**
     * Test of isValidAgainst method, of class MonitorView.
     */
    @Test
    public void testIsValidAgainst() {
        System.out.println("isValidAgainst");
        List<MonitorView> monitorList = new ArrayList<>();
        monitorList.add(new MonitorView("10.10.10.1", "10.10.10.22", "30", "Test"));
        monitorList.add(new MonitorView("10.10.10.47", "10.10.10.47", "30", "Test"));
        monitorList.add(new MonitorView("10.10.10.50", "10.10.10.55", "30", "Test"));
        monitorList.add(new MonitorView("10.15.10.1", "10.16.10.55", "30", "Test"));

        Assert.assertEquals(true, new MonitorView("10.10.10.23", "10.10.10.40", "5", "Test").isValidAgainst(monitorList));
        Assert.assertEquals(false, new MonitorView("10.10.10.22", "10.10.10.40", "5", "Test").isValidAgainst(monitorList));
        Assert.assertEquals(false, new MonitorView("10.10.10.23", "10.10.10.47", "5", "Test").isValidAgainst(monitorList));
        Assert.assertEquals(false, new MonitorView("10.10.10.48", "10.10.10.60", "5", "Test").isValidAgainst(monitorList));
    }

}
