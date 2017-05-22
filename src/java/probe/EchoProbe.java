package probe;

import destination.Destination;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.apache.log4j.Logger;

/**
 * An echo probe
 */
public class EchoProbe implements Probe, Runnable {
    private static final Logger LOG = Logger.getLogger(EchoProbe.class);

    private final Destination destination;
    private boolean running = true;

    public EchoProbe(Destination destination) {
        this.destination = destination;
    }

    @Override
    public Destination getDestination() {
        return destination;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void probe() {
        boolean result;

        destination.setLastProbe(LocalDateTime.now());

        try {
            InetAddress inet = InetAddress.getByAddress(destination.getInetAddr().getAddress());
            result = inet.isReachable(5000);
        }
        catch (IOException ex) {
            LOG.error(ex.getMessage());
            result = false;
        }

        destination.setProbeResult(result);

        LOG.debug("Echo <" + destination.getInetAddr().getHostAddress() + "> quality=" + destination.getQuality());
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
