package probe;

import destination.Destination;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.apache.log4j.Logger;

/**
 * A server port probe.<br>
 * <br>
 * Open a TCPIP server on a given port and just close it to find out if this destination is alive.
 */
public class ServerPortProbe implements Probe, Runnable {
    /**
     * A logger.
     */
    private static final Logger LOG = Logger.getLogger(ServerPortProbe.class);

    /**
     * A server port.
     */
    private static final int PORT = 8001;

    /**
     * The destination to work on.
     */
    private final Destination destination;
    /**
     * Indicate if this time server probe is running.
     */
    private boolean running = true;

    /**
     * Create a server port probe for the given destination.
     *
     * @param destination the destination to work on
     */
    public ServerPortProbe(Destination destination) {
        this.destination = destination;
    }

    @Override
    public void probe() {
        Socket so = null;
        InputStream in = null;
        long time = 0;
        boolean result;

        destination.setLastProbe(LocalDateTime.now());

        try {
            so = new Socket();
            InetSocketAddress soaddr = new InetSocketAddress(destination.getInetAddr(), PORT);
            // Set the connect timeout; this is needed because the default is too long
            // Depends highly on destination.getInterval()
            so.connect(soaddr, 5000);
            in = so.getInputStream();
            result = true;
        }
        catch (IOException ex) {
            LOG.info(destination.getInetAddr().getHostAddress() + " " + ex.getMessage());
            result = false;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ex) {
                    LOG.debug("Closing input stream failed, but that is ok...");
                }
            }
            if (so != null) {
                try {
                    so.close();
                }
                catch (IOException ex) {
                    LOG.debug("Closing socket failed, but that is ok...");
                }
            }
        }

        // Set the probe result; set it for further display
        destination.setProbeResult(result);

        LOG.debug("TimeServer <" + destination.getInetAddr().getHostAddress() + "> quality=" + destination.getQuality());
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public Destination getDestination() {
        return this.destination;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Check if a new probe is needed
                long diffSecs = destination.getLastProbe().until(LocalDateTime.now(), ChronoUnit.SECONDS);
                LOG.trace("diffSecs=" + diffSecs);

                if (diffSecs >= destination.getInterval()) {
                    probe();
                }
                else {
                    if (destination.getInterval() - diffSecs > 0) {
                        long sleepTime = destination.getInterval() - diffSecs;
                        LOG.trace("sleepTime=" + sleepTime);
                        Thread.sleep(sleepTime * 1000);
                    }
                }
            }
            catch (InterruptedException iex) {
                LOG.warn("Probe on <" + destination.getInetAddr().getHostAddress() + "> interrupted");
                running = false;
            }
        }
    }
}
