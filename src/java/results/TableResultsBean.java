package results;

import config.MonitorConfigurationBean;
import destination.Destination;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import monitor.Monitor;
import org.apache.log4j.Logger;

/**
 * Handle the monitoring results as table
 */
@ManagedBean(name = "TableResultsBean")
@ViewScoped
public class TableResultsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TableResultsBean.class);

    private final List<Destination> destinations = new ArrayList<>();

    @ManagedProperty("#{MonitorConfigurationBean}")
    private MonitorConfigurationBean configuration;

    /**
     * Creates a new instance of MonitoringResultsBean
     */
    public TableResultsBean() {
    }

    /**
     * Get the current configuration; needed because configuration is a @ManagedProperty
     *
     * @return the bean
     */
    public MonitorConfigurationBean getConfiguration() {
        return configuration;
    }

    /**
     * Set the current configuration; needed because configuration is a @ManagedProperty
     *
     * @param configuration the bean
     */
    public void setConfiguration(MonitorConfigurationBean configuration) {
        this.configuration = configuration;
    }

    /**
     * Get all the destinations of the current configuration
     *
     * @return the destinations
     */
    public List<Destination> getDestinations() {
        destinations.clear();
        for (Monitor monitor : configuration.getRunningMonitors()) {
            for (Destination destination : monitor.getDestinations()) {
                destinations.add(destination);
            }
        }
        return destinations;
    }
}
