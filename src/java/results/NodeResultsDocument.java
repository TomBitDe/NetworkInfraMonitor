package results;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.log4j.Logger;

/**
 * The document to use for the node result view.
 */
public class NodeResultsDocument implements Serializable, Comparable<NodeResultsDocument> {
    /**
     * Needed for proper serializable implementation.
     */
    private static final long serialVersionUID = 1L;
    /**
     * A logger.
     */
    private static final Logger LOG = Logger.getLogger(NodeResultsDocument.class);

    // Monitor items to display
    private String monitorId;
    private int summary;
    private String interval;

    // Destination items to display
    private InetAddress inetAddr;
    private LocalDateTime lastProbe;
    private int probeResult = -1;
    private int quality = -1;
    private String probe = "";

    /**
     * Create a Node Results Document for display.
     *
     * @param monitorId the monitor id
     * @param summary   a summary text
     * @param interval  the interval to run the monitor
     */
    public NodeResultsDocument(String monitorId, int summary, String interval) {
        this.monitorId = monitorId;
        this.summary = summary;
        this.interval = interval;
        this.probeResult = -1;
        this.quality = -1;
        this.probe = "";
    }

    /**
     * Create a Node Results Document for display.
     *
     * @param inetAddr    the IP
     * @param lastProbe   the last probe time
     * @param probeResult the last probe result
     * @param quality     the probe quality
     * @param probe       the probe that was used on success
     */
    public NodeResultsDocument(InetAddress inetAddr, LocalDateTime lastProbe, int probeResult, int quality, String probe) {
        this.summary = -1;
        this.inetAddr = inetAddr;
        this.lastProbe = lastProbe;
        this.probeResult = probeResult;
        this.quality = quality;
        this.probe = probe;
    }

    /**
     * Get the monitors id.
     *
     * @return the monitors id
     */
    public String getMonitorId() {
        return monitorId;
    }

    /**
     * Set the monitors id.
     *
     * @param monitorId the monitors id
     */
    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    /**
     * Get the monitors summary text.
     *
     * @return the summary text
     */
    public int getSummary() {
        return summary;
    }

    /**
     * Set the monitors summary text.
     *
     * @param summary the text
     */
    public void setSummary(int summary) {
        this.summary = summary;
    }

    /**
     * Get the monitors probe interval.
     *
     * @return the interval value
     */
    public String getInterval() {
        return interval;
    }

    /**
     * Set the monitors probe interval.
     *
     * @param interval the interval value
     */
    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     * Get the IP the monitor is responsible for.
     *
     * @return the IP
     */
    public InetAddress getInetAddr() {
        return inetAddr;
    }

    /**
     * Set the IP the monitor is responsible for.
     *
     * @param inetAddr the IP
     */
    public void setInetAddr(InetAddress inetAddr) {
        this.inetAddr = inetAddr;
    }

    /**
     * Get the last probe time.
     *
     * @return the time value
     */
    public LocalDateTime getLastProbe() {
        return lastProbe;
    }

    /**
     * Set the last probe time.
     *
     * @param lastProbe the time
     */
    public void setLastProbe(LocalDateTime lastProbe) {
        this.lastProbe = lastProbe;
    }

    /**
     * Get the last probe result.
     *
     * @return the result value
     */
    public int getProbeResult() {
        return probeResult;
    }

    /**
     * Set the last probe result.
     *
     * @param probeResult the result
     */
    public void setProbeResult(int probeResult) {
        this.probeResult = probeResult;
    }

    /**
     * Get the probe quality.
     *
     * @return the quality value
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Set the probe quality.
     *
     * @param quality the quality value
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    /**
     * Get the probes name.
     *
     * @return the probes name on success
     */
    public String getProbe() {
        return probe;
    }

    /**
     * Set the probes name.
     *
     * @param probe the probes name
     */
    public void setProbe(String probe) {
        this.probe = probe;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.monitorId);
        hash = 11 * hash + this.summary;
        hash = 11 * hash + Objects.hashCode(this.interval);
        hash = 11 * hash + Objects.hashCode(this.inetAddr);
        hash = 11 * hash + Objects.hashCode(this.lastProbe);
        hash = 11 * hash + this.probeResult;
        hash = 11 * hash + this.quality;
        hash = 11 * hash + Objects.hashCode(this.probe);
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
        final NodeResultsDocument other = (NodeResultsDocument) obj;
        if (this.summary != other.summary) {
            return false;
        }
        if (this.probeResult != other.probeResult) {
            return false;
        }
        if (this.quality != other.quality) {
            return false;
        }
        if (!Objects.equals(this.monitorId, other.monitorId)) {
            return false;
        }
        if (!Objects.equals(this.interval, other.interval)) {
            return false;
        }
        if (!Objects.equals(this.probe, other.probe)) {
            return false;
        }
        if (!Objects.equals(this.inetAddr, other.inetAddr)) {
            return false;
        }
        if (!Objects.equals(this.lastProbe, other.lastProbe)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NodeResultsDocument{" + "monitorId=" + monitorId + ", summary=" + summary + ", interval=" + interval + ", inetAddr=" + inetAddr + ", lastProbe=" + lastProbe + ", probeResult=" + probeResult + ", quality=" + quality + ", probe=" + probe + '}';
    }

    @Override
    public int compareTo(NodeResultsDocument o) {
        if (this.getMonitorId().compareTo(o.getMonitorId()) == 0
                && this.getSummary() == o.getSummary()
                && this.getInterval().compareTo(o.getInterval()) == 0) {
            return 0;
        }

        if (this.getInetAddr().equals(o.getInetAddr())
                && this.getProbeResult() == o.getProbeResult()
                && this.getQuality() == o.getQuality()
                && this.getProbe().compareTo(o.getProbe()) == 0
                && this.getLastProbe().compareTo(o.getLastProbe()) == 0) {
            return 0;
        }

        return 1;
    }
}
