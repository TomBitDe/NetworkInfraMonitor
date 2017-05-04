package probe;

import destination.Destination;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 */
public class Prober implements Runnable {
    private static final Logger LOG = Logger.getLogger(Prober.class);

    private final Destination destination;
    private boolean running = true;
    private final List<Probe> probeList;

    public Prober(Destination destination) {
        this.destination = destination;

        this.probeList = new ArrayList<>();

        probeList.add(new EchoProbe(destination));
        probeList.add(new PingProbe(destination));
    }

    public Destination getDestination() {
        return destination;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

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
