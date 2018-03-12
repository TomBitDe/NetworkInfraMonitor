package monitor;

/**
 * Test runner for a monitor.
 */
public class Runner {
    /**
     * The main to start the test runner.
     *
     * @param args no arguments needed
     *
     * @throws InterruptedException in case the test runner is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        Monitor monitor = new Monitor("192.168.1.1", "192.168.1.10", 5, "Test");
        monitor.start();
    }
}
