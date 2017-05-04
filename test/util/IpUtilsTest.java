package util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test IpUtils.
 */
public class IpUtilsTest {

    public IpUtilsTest() {
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
     * Test of validIp method, of class IpUtils.
     */
    @Test
    public void testValidIp() {
        System.out.println("validIp");
        Assert.assertEquals(true, IpUtils.validIp("192.168.0.1"));
        Assert.assertEquals(true, IpUtils.validIp("0.0.0.0"));
        Assert.assertEquals(true, IpUtils.validIp("255.255.255.255"));
        Assert.assertEquals(false, IpUtils.validIp("a.b.c.d"));
        Assert.assertEquals(false, IpUtils.validIp("1.1.1"));
        Assert.assertEquals(false, IpUtils.validIp("1.1.1."));
        Assert.assertEquals(false, IpUtils.validIp("1.256.1.10"));
    }

    /**
     * Test of validIpRange method, of class IpUtils.
     */
    @Test
    public void testValidIpRange() {
        System.out.println("validIpRange");
        Assert.assertEquals(true, IpUtils.validIpRange("0.0.0.0", "0.0.0.0"));
        Assert.assertEquals(true, IpUtils.validIpRange("192.168.1.5", "192.168.1.255"));
        Assert.assertEquals(false, IpUtils.validIpRange("192.168.2.5", "192.168.1.255"));
    }

    /**
     * Test of createIpRange method, of class IpUtils.
     */
    @Test
    public void testCreateIpRange() {
        System.out.println("createIpRange");
        Assert.assertEquals(1, IpUtils.createIpRange("192.168.1.5", "192.168.1.5").size());
        Assert.assertEquals(9, IpUtils.createIpRange("192.168.1.5", "192.168.1.13").size());
        Assert.assertEquals(256, IpUtils.createIpRange("192.168.1.0", "192.168.1.255").size());
    }

    /**
     * Test of toHex method, of class IpUtils.
     */
    @Test
    public void testToHex() {
        System.out.println("toHex");
        Assert.assertEquals("ff", IpUtils.toHex("0.0.0.255"));
    }

    /**
     * Test of ipToLong method, of class IpUtils.
     */
    @Test
    public void testIpToLong() {
        System.out.println("ipToLong");
        Assert.assertEquals(0, IpUtils.ipToLong("0.0.0.0"));
        Assert.assertEquals(4, IpUtils.ipToLong("0.0.0.4"));
    }

    /**
     * Test of longToIp method, of class IpUtils.
     */
    @Test
    public void testLongToIp() {
        System.out.println("longToIp");
        Assert.assertEquals("0.0.0.255", IpUtils.longToIp(255));
    }
}
