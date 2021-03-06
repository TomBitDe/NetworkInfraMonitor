package probe;

import destination.Destination;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * A ping probe.<br>
 * <br>
 * Contact the ping port to find out if this destination is alive.
 */
public class PingProbe implements Probe, Runnable {
    /**
     * A logger.
     */
    private static final Logger LOG = Logger.getLogger(PingProbe.class);
    /**
     * Keywords for checking if a ping request failed.
     */
    private static final String[] KEYWORDS = {"nicht erreichbar", "not reachable", "berschreitung", "timeout"};

    /**
     * The destination to work on.
     */
    private final Destination destination;
    /**
     * Indicate if this echo probe is running.
     */
    private boolean running = true;

    /**
     * Create a ping probe for the given destination.
     *
     * @param destination the destination to work on
     */
    public PingProbe(Destination destination) {
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
        BufferedReader bufferedReader = null;
        boolean result = true;

        destination.setLastProbe(LocalDateTime.now());

        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase(Locale.getDefault()).contains("win");

            ProcessBuilder processBuilder = new ProcessBuilder("ping", isWindows ? "-n" : "-c", "1", destination.getInetAddr().getHostAddress());
            Process proc = processBuilder.start();

            bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (containsFailKeywords(line)) {
                    result = false;
                    break;
                }
            }
            bufferedReader.close();
        }
        catch (IOException ex) {
            LOG.error(ex.getMessage());
            result = false;
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException ex1) {
                    LOG.error(ex1.getMessage());
                }
            }
        }

        destination.setProbeResult(result);

        LOG.debug("Ping <" + destination.getInetAddr().getHostAddress() + "> quality=" + destination.getQuality());
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

    /**
     * Check if the response text line contains a keyword that indicated a probe failure.
     *
     * @param line the response text line
     *
     * @return true if line contains a fail keyword
     */
    private static boolean containsFailKeywords(String line) {
        LOG.trace("line=[" + line + ']');
        boolean result = false;

        for (String failKeyword : KEYWORDS) {
            if (line.toUpperCase(Locale.getDefault()).contains(failKeyword.toUpperCase(Locale.getDefault()))) {
                result = true;
                break;
            }
        }

        LOG.trace("result=" + result);
        return result;
    }
}
