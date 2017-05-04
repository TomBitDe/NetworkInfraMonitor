package destination;

import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.log4j.Logger;

/**
 * A destination to probe
 */
public class Destination implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(Destination.class);

    private static final long DEFAULT_INTERVAL = 5; // seconds
    private static final long QUALITY_UPPER_LEVEL = 10;

    private InetAddress inetAddr;
    private long interval;
    private LocalDateTime lastProbe;
    private boolean probeResult = false;
    private int quality = 0;
    private String probe = "";

    public Destination(InetAddress inetAddr) {
        LOG.trace("inetAddr=[" + inetAddr.getHostAddress() + ']');

        this.inetAddr = inetAddr;
        this.interval = DEFAULT_INTERVAL;
        this.lastProbe = LocalDateTime.now().minusSeconds(this.interval);
    }

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

        this.interval = interval;
        this.lastProbe = LocalDateTime.now().minusSeconds(this.interval);
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

    public boolean getProbeResult() {
        return probeResult;
    }

    public void setProbeResult(boolean probeResult) {
        this.probeResult = probeResult;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    private void incQuality() {
        if (this.quality < QUALITY_UPPER_LEVEL) {
            ++this.quality;
        }
    }

    private void decQuality() {
        if (this.quality > 0) {
            --this.quality;
        }
    }

    public int getQuality() {
        return this.quality;
    }

    public void setProbe(String probe) {
        this.probe = probe;

        if (this.probe.isEmpty()) {
            decQuality();
        }
        else {
            incQuality();
        }
    }

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
