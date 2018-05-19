package probe;

import destination.Destination;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Probe a destination using different probes.
 */
public class Prober implements Runnable {
    /**
     * A looger.
     */
    private static final Logger LOG = Logger.getLogger(Prober.class);

    /**
     * A destination to work on.
     */
    private final Destination destination;
    /**
     * Indicate if this thread is running.
     */
    private boolean running = true;
    /**
     * A list of probes to do.
     */
    private final List<Probe> probeList;

    /**
     * Define a prober for a destination.
     *
     * @param destination the target destination to use the probes for
     */
    public Prober(Destination destination) {
        this.destination = destination;

        this.probeList = new ArrayList<>();

        probeList.add(new ServerPortProbe(destination));
        probeList.add(new EchoProbe(destination));
        probeList.add(new TimeServerProbe(destination));

        // Do not use ping until it is more improved
        // probeList.add(new PingProbe(destination));
    }

    /**
     * Get the destination to work on.
     *
     * @return the destination
     */
    public Destination getDestination() {
        return destination;
    }

    /**
     * Check if the thread running.
     *
     * @return true if it is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Set the running state of the thread.
     *
     * @param running true if it is running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Do the probes for a destination.
     *
     * @param destination the destination to probe
     */
    public void probe(Destination destination) {
        for (Probe probe : probeList) {

            probe.probe();

            if (destination.getProbeResult() == true) {
                // A single true probe is enough; set the probe in destination
                destination.setProbe(probe.getClass().getSimpleName());
                return;
            }
        }

        // No probe was ok; clear the probe in destination
        destination.setProbe("");
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Check if a new probe is needed
                long diffSecs = destination.getLastProbe().until(LocalDateTime.now(), ChronoUnit.SECONDS);
                LOG.trace("diffSecs=" + diffSecs);

                if (diffSecs >= destination.getInterval()) {
                    probe(destination);
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
                LOG.debug("Probe on <" + destination.getInetAddr().getHostAddress() + "> interrupted");
                running = false;
            }
        }
    }
}
