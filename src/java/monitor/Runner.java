package monitor;

/**
 * Test runner for a monitor.
 */
public class Runner {
    public static void main(String[] args) throws InterruptedException {
        Monitor monitor = new Monitor("192.168.1.1", "192.168.1.10", 5, "Test");
        monitor.start();
    }
}
