package destination;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.log4j.Logger;

/**
 * A destination to probe.<br>
 * <br>
 * A destination is defined by its IP. A probe can be an echo request for example. The default probe interval time is 5
 * seconds.
 */
public class Destination implements Serializable {
    /**
     * Needed for proper serializable implementation.
     */
    private static final long serialVersionUID = 1L;
    /**
     * A logger.
     */
    private static final Logger LOG = Logger.getLogger(Destination.class);
    /**
     * The default probe interval time in seconds.
     */
    private static final long DEFAULT_INTERVAL = 5; // seconds
    /**
     * The upper level for a good connection quality. Stop increasing the quality counter if this value is reached.
     */
    private static final long QUALITY_UPPER_LEVEL = 10;
    /**
     * The IP of the destination.
     */
    private InetAddress inetAddr;
    /**
     * The interval for probing.
     */
    private long interval;
    /**
     * The time of the last probe action.
     */
    private LocalDateTime lastProbe;
    /**
     * The last prebe result.
     */
    private boolean probeResult = false;
    /**
     * The probe quality of the destination.
     */
    private int quality = 0;
    /**
     * The probes name.
     */
    private String probe = "";

    /**
     * Create a destination with a default probe interval time.
     *
     * @param inetAddr the IP of this destination
     */
    public Destination(InetAddress inetAddr) {
        LOG.trace("inetAddr=[" + inetAddr.getHostAddress() + ']');

        this.inetAddr = inetAddr;
        this.interval = DEFAULT_INTERVAL;
        this.lastProbe = LocalDateTime.now().minusSeconds(this.interval);
    }

    /**
     * Create a destination with a given probe interval time.
     *
     * @param inetAddr the IP of this destination
     * @param interval the probe interval to use; if the interval is less or equal 0 then the default probe interval is
     *                 used
     */
    public Destination(InetAddress inetAddr, int interval) {
        LOG.trace("inetAddr=[" + inetAddr.getHostAddress() + "] interval=" + interval);

        this.inetAddr = inetAddr;

        if (interval <= 0) {
            LOG.warn("interval=" + interval + " is invalid (<=0); take DEFAULT=" + DEFAULT_INTERVAL);
            this.interval = DEFAULT_INTERVAL;
        }
        else {
            this.interval = interval;
        }

        this.lastProbe = LocalDateTime.now().minusSeconds(this.interval);
    }

    /**
     * Get the IP of this destination.
     *
     * @return the destinations IP
     */
    public InetAddress getInetAddr() {
        return inetAddr;
    }

    /**
     * Set the IP of this destination.
     *
     * @param inetAddr the IP to use
     */
    public void setInetAddr(InetAddress inetAddr) {
        this.inetAddr = inetAddr;
    }

    /**
     * Get the last time when a probe was done.
     *
     * @return the probe time
     */
    public LocalDateTime getLastProbe() {
        return lastProbe;
    }

    /**
     * Set the time when a probe was done last.
     *
     * @param lastProbe the probe time
     */
    public void setLastProbe(LocalDateTime lastProbe) {
        this.lastProbe = lastProbe;
    }

    /**
     * Get the probes result.
     *
     * @return true if the probe was ok, else false
     */
    public boolean getProbeResult() {
        return probeResult;
    }

    /**
     * Set the probes result.
     *
     * @param probeResult true if the probe was successful, else false
     */
    public void setProbeResult(boolean probeResult) {
        this.probeResult = probeResult;
    }

    /**
     * Get the probes interval for this destination.
     *
     * @return the interval in seconds
     */
    public long getInterval() {
        return interval;
    }

    /**
     * Set the probes interval for this destination.
     *
     * @param interval the interval in seconds
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * Increase the quality value by one. Stop if the QUALITY_UPPER_LEVEL is reached.
     */
    private void incQuality() {
        if (this.quality < QUALITY_UPPER_LEVEL) {
            ++this.quality;
        }
    }

    /**
     * Decrease the quality value by one. Stop if one is reached.
     */
    private void decQuality() {
        if (this.quality > 0) {
            --this.quality;
        }
    }

    /**
     * Get the probe quality of this destination.
     *
     * @return the quality (0 means bad, 10 means good)
     */
    public int getQuality() {
        return this.quality;
    }

    /**
     * Set the probe done.<br>
     * If the probe is empty then probing failed. Decrease the quality in this case. Otherwise increase the quality.
     *
     * @param probe the probe done
     */
    public void setProbe(String probe) {
        this.probe = probe;

        if (this.probe.isEmpty()) {
            decQuality();
        }
        else {
            incQuality();
        }
    }

    /**
     * Get the probe.
     *
     * @return the probes name
     */
    public String getProbe() {
        return probe;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.inetAddr);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Destination other = (Destination) obj;
        return Objects.equals(this.inetAddr, other.inetAddr);
    }

    @Override
    public String toString() {
        return "Destination{" + "inetAddr=" + inetAddr + ", interval=" + interval + ", lastProbe=" + lastProbe + ", probeResult=" + probeResult + ", quality=" + quality + ", probe=" + probe + '}';
    }
}
