package monitor;

import destination.Destination;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import probe.Prober;
import util.IpUtils;

/**
 * A monitor for an IP address range.
 */
public class Monitor implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(Monitor.class);

    private final String start;
    private final String end;
    private int interval;
    private final String comment;

    private transient List<Destination> destinations = new ArrayList<>();
    private ExecutorService service;
    private transient List<Future> futureList;

    public Monitor() {
        this.start = "";
        this.end = "";
        this.interval = 0;
        this.comment = "";
    }

    public Monitor(String ip, int interval, String comment) {
        this.start = ip;
        this.end = ip;
        this.interval = interval;
        this.comment = comment;

        destinations = buildDestinations(ip, ip);
    }

    public Monitor(String start, String end, int interval, String comment) {
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.comment = comment;

        destinations = buildDestinations(start, end);
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;

        destinations.forEach((target) -> {
            target.setInterval(interval);
        });
    }

    public String getComment() {
        return comment;
    }

    /**
     * Get all the destinations the monitor handles.
     *
     * @return the destinations
     */
    public List<Destination> getDestinations() {
        return destinations;
    }

    /**
     * Build the destinations that are in the range to monitor.
     *
     * @param start the start of the range
     * @param end   the end of the range
     *
     * @return the destinations in the range
     */
    private List<Destination> buildDestinations(String start, String end) {
        destinations.clear();

        try {
            List<String> addrList = IpUtils.createIpRange(start, end);

            for (String addr : addrList) {
                InetAddress address = InetAddress.getByName(addr);
                destinations.add(new Destination(address, interval));
            }
        }
        catch (UnknownHostException uhex) {
            LOG.error(uhex.getMessage());
        }

        return destinations;
    }

    /**
     * Start the monitoring for the range.
     *
     * @throws InterruptedException in case the thread is interrupted
     */
    public void start() throws InterruptedException {
        LOG.debug("Start");

        if (destinations == null) {
            LOG.info("Destinations not initialized...");
            return;
        }

        if (destinations.isEmpty()) {
            LOG.info("No destinations defined...");
            return;
        }

        service = Executors.newFixedThreadPool(destinations.size());
        futureList = new ArrayList<>();

        for (Destination target : destinations) {
            Future future = service.submit(new Prober(target));
            futureList.add(future);
        }

        LOG.debug("Finished start");
    }

    /**
     * Stop the monitoring for the range.
     */
    public void stop() {
        LOG.debug("Stop");

        if (futureList != null) {
            for (Future future : futureList) {
                future.cancel(true);
            }
            futureList.clear();
        }

        if (service != null) {
            service.shutdownNow();
        }

        LOG.debug("Stop finished");
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.start);
        hash = 41 * hash + Objects.hashCode(this.end);
        hash = 41 * hash + this.interval;
        hash = 41 * hash + Objects.hashCode(this.comment);
        hash = 41 * hash + Objects.hashCode(this.service);
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
        final Monitor other = (Monitor) obj;
        if (this.interval != other.interval) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.end, other.end)) {
            return false;
        }
        if (!Objects.equals(this.comment, other.comment)) {
            return false;
        }
        if (!Objects.equals(this.service, other.service)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Monitor{" + "start=" + start + ", end=" + end + ", interval=" + interval + ", comment=" + comment + ", service=" + service + '}';
    }

}
