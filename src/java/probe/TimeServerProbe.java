package probe;

import destination.Destination;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * A time server probe.
 */
public class TimeServerProbe implements Probe, Runnable {
    /**
     * A logger.
     */
    private static final Logger LOG = Logger.getLogger(TimeServerProbe.class);

    /**
     * A date format for further handling of date values.
     */
    final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * The seconds value between 1900 and 1970.
     */
    private static final long SECONDS_1900_1970 = 2208988800L;

    /**
     * The destination to work on.
     */
    private final Destination destination;
    /**
     * Indicate if this time server probe is running.
     */
    private boolean running = true;

    /**
     * Create a time server probe for the given destination.
     *
     * @param destination the destination to work on
     */
    public TimeServerProbe(Destination destination) {
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
            InetSocketAddress soaddr = new InetSocketAddress(destination.getInetAddr(), 37);
            // Set the connect timeout; this is needed because the default is too long
            // Depends highly on destination.getInterval()
            so.connect(soaddr, 5000);
            in = so.getInputStream();
            for (int i = 3; i >= 0; i--) {
                time ^= (long) in.read() << i * 8;
            }
            // The Time Server returns the seconds since 1900, Java expects milliseconds since 1970
            // Calculate to get the correct format
            LOG.debug(DATEFORMAT.format(new Date((time - SECONDS_1900_1970) * 1000)));
            result = true;
        }
        catch (IOException ex) {
            LOG.error(destination.getInetAddr().getHostAddress() + " " + ex.getMessage());
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
