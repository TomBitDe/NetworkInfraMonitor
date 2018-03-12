package config;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.apache.commons.lang.math.LongRange;
import org.apache.log4j.Logger;
import util.IpUtils;

/**
 * A view on a monitors data.<br>
 * <br>
 * A monitor covers a range of IP addresses to handle. An interval defines when to do the monitoring action in sequence.
 * A comment defines a brief description for the monitor.
 */
@ManagedBean(name = "MonitorView")
@ViewScoped
public class MonitorView implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(MonitorView.class);

    private String startIp;
    private String endIp;
    private String interval;
    private String comment;

    /**
     * Creates a new instance of MonitorView.
     *
     * @param startIp  the start IP of the monitoring range
     * @param endIp    the end IP of the monitoring range
     * @param interval the monitoring interval
     * @param comment  the comment for the monitor
     */
    public MonitorView(String startIp, String endIp, String interval, String comment) {
        this.startIp = startIp;
        this.endIp = endIp;
        this.interval = interval;
        this.comment = comment;
    }

    /**
     * Get the start IP of the monitor range.
     *
     * @return the start IP
     */
    public String getStartIp() {
        return startIp;
    }

    /**
     * Set the start IP of the monitor range.
     *
     * @param startIp the IP to start with
     */
    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    /**
     * Get the end IP of the monitor range.
     *
     * @return the end IP
     */
    public String getEndIp() {
        return endIp;
    }

    /**
     * Set the end IP of the monitor range.
     *
     * @param endIp the IP to end with
     */
    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    /**
     * Get the monitoring interval used by this monitor.
     *
     * @return the interval
     */
    public String getInterval() {
        return interval;
    }

    /**
     * Set the monitoring interval used by this monitor.
     *
     * @param interval interval to use
     */
    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     * Get the comment assigned to this monitor.
     *
     * @return the comment text
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment text for this monitor.
     *
     * @param comment the comment to assign
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Check if this monitor view is valid against all others from the monitor list.<br>
     * <br>
     * Valid means no includes, overlaps with monitors in the list of other monitors.
     *
     * @param monitorList the monitor list to test against
     *
     * @return true if valid, else false
     */
    public boolean isValidAgainst(List<MonitorView> monitorList) {
        for (MonitorView monitor : monitorList) {
            LongRange monitorRange = new LongRange(IpUtils.ipToLong(monitor.startIp), IpUtils.ipToLong(monitor.endIp));
            LongRange currentRange = new LongRange(IpUtils.ipToLong(startIp), IpUtils.ipToLong(endIp));

            if (currentRange.overlapsRange(monitorRange)) {
                LOG.error("Overlap: " + startIp + " " + endIp + " " + monitor + " ");
                return false;
            }

            if (monitorRange.containsRange(currentRange)) {
                LOG.error("Contain: " + monitor + " " + startIp + " " + endIp);
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.startIp);
        hash = 29 * hash + Objects.hashCode(this.endIp);
        hash = 29 * hash + Objects.hashCode(this.interval);
        hash = 29 * hash + Objects.hashCode(this.comment);
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
        final MonitorView other = (MonitorView) obj;
        if (!Objects.equals(this.startIp, other.startIp)) {
            return false;
        }
        if (!Objects.equals(this.endIp, other.endIp)) {
            return false;
        }
        if (!Objects.equals(this.interval, other.interval)) {
            return false;
        }
        if (!Objects.equals(this.comment, other.comment)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MonitorView{" + "startIp=" + startIp + ", endIp=" + endIp + ", interval=" + interval + ", comment=" + comment + '}';
    }
}
