package results;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.log4j.Logger;

/**
 */
public class NodeResultsDocument implements Serializable, Comparable<NodeResultsDocument> {
    private static final long serialVersionUID = 1L;
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

    public NodeResultsDocument(String monitorId, int summary, String interval) {
        this.monitorId = monitorId;
        this.summary = summary;
        this.interval = interval;
        this.probeResult = -1;
        this.quality = -1;
        this.probe = "";
    }

    public NodeResultsDocument(InetAddress inetAddr, LocalDateTime lastProbe, int probeResult, int quality, String probe) {
        this.summary = -1;
        this.inetAddr = inetAddr;
        this.lastProbe = lastProbe;
        this.probeResult = probeResult;
        this.quality = quality;
        this.probe = probe;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public int getSummary() {
        return summary;
    }

    public void setSummary(int summary) {
        this.summary = summary;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public InetAddress getInetAddr() {
        return inetAddr;
    }

    public void setInetAddr(InetAddress inetAddr) {
        this.inetAddr = inetAddr;
    }

    public LocalDateTime getLastProbe() {
        return lastProbe;
    }

    public void setLastProbe(LocalDateTime lastProbe) {
        this.lastProbe = lastProbe;
    }

    public int getProbeResult() {
        return probeResult;
    }

    public void setProbeResult(int probeResult) {
        this.probeResult = probeResult;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getProbe() {
        return probe;
    }

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
