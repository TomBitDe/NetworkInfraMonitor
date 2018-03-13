package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import monitor.Monitor;
import org.apache.log4j.Logger;
import util.IpUtils;
import util.MsgUtils;

/**
 * Handle the monitoring configuration view.
 */
@WebListener
@ManagedBean(name = "MonitorConfigurationBean")
@ApplicationScoped
public class MonitorConfigurationBean implements Serializable, ServletContextListener {
    /**
     * Needed for proper serializable implementation.
     */
    private static final long serialVersionUID = 1L;
    /**
     * A logger.
     */
    private static final Logger LOG = Logger.getLogger(MonitorConfigurationBean.class);
    /**
     * The path to put monitor configuration.
     */
    private static final String MONITORS_CFG = "WEB-INF/monitors.properties";
    /**
     * Key prefix value.
     */
    private static final String KEY_PREFIX = "row";
    /**
     * The default interval value.
     */
    private static final String DEFAULT_INTERVAL = "30";
    /**
     * The minimum interval entry value to test for.
     */
    private static final int INTERVAL_MIN_VALUE = 9;
    /**
     * The maximum interval entry value to test for.
     */
    private static final int INTERVAL_MAX_VALUE = 300;

    /**
     * Start IP entry.
     */
    private String startIp;
    /**
     * End IP entry.
     */
    private String endIp;
    /**
     * Interval entry.
     */
    private String interval;
    /**
     * Comment entry.
     */
    private String comment;

    /**
     * A list of configured monitors in the datatable view.
     */
    private List<MonitorView> configuredMonitors = new ArrayList<>();
    /**
     * A list of selected monitors in the datatable view.
     */
    private List<MonitorView> selectedMonitors;

    /**
     * A synchronized list of running monitors
     */
    private static final List<Monitor> RUNNING_MONITORS = Collections.synchronizedList(new ArrayList<>());

    /**
     * Is add button disabled.
     */
    private boolean addDisabled = false;
    /**
     * Is delete button disabled.
     */
    private boolean deleteDisabled = true;
    /**
     * Is start button disabled.
     */
    private boolean startDisabled = true;
    /**
     * Is stop button disabled.
     */
    private boolean stopDisabled = true;
    /**
     * Is result button disabled.
     */
    private boolean resultsDisabled = true;
    /**
     * Is load button disabled.
     */
    private boolean loadDisabled = false;

    /**
     * Creates a new instance of MonitorConfigurationBean.
     */
    public MonitorConfigurationBean() {
        super();
    }

    /**
     * Load the last configuration from properties file.
     */
    public void loadConfiguration() {
        Properties props = new Properties();
        FileInputStream fis = null;

        File cfgPropertiesFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath(MONITORS_CFG));
        LOG.info(cfgPropertiesFile.getPath());

        try {
            fis = new FileInputStream(cfgPropertiesFile);
            props.load(fis);
            Set<String> keySet = props.stringPropertyNames();
            configuredMonitors.clear();

            for (int idx = 0; idx < keySet.size(); ++idx) {
                String value = props.getProperty(KEY_PREFIX + idx);
                String[] parts = value.split("#");

                setStartIp(parts[0]);
                setEndIp(parts[1]);
                setInterval(parts[2]);
                setComment(parts[3]);

                addMonitor();
            }
            fis.close();
        }
        catch (IOException ioex) {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException ex) {
                    LOG.error(ex.getMessage());
                }
            }
            LOG.info(ioex.getMessage());
            configuredMonitors.clear();
        }
    }

    /**
     * Save the current configuration in properties file.
     */
    private void saveConfiguration() {
        Properties props = new Properties();

        int idx = 0;
        for (MonitorView monitor : configuredMonitors) {
            String key = KEY_PREFIX + idx;
            String value = monitor.getStartIp() + "#" + monitor.getEndIp() + "#"
                    + monitor.getInterval() + "#" + monitor.getComment();

            props.setProperty(key, value);

            ++idx;
        }

        OutputStream oStream = null;

        try {
            File cfgPropertiesFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath(MONITORS_CFG));
            LOG.info(cfgPropertiesFile.getPath());

            oStream = new FileOutputStream(cfgPropertiesFile);
            props.store(oStream, "");

            oStream.close();
        }
        catch (IOException ioex) {
            if (oStream != null) {
                try {
                    oStream.close();
                }
                catch (IOException ex) {
                    LOG.error(ex.getMessage());
                }
            }
            LOG.info(ioex.getMessage());
        }
    }

    /**
     * Get the start IP entry value.
     *
     * @return the start IP
     */
    public String getStartIp() {
        return startIp;
    }

    /**
     * Set the start IP entry value.
     *
     * @param startIp the start IP
     */
    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    /**
     * Get the end IP entry value.
     *
     * @return the end IP
     */
    public String getEndIp() {
        return endIp;
    }

    /**
     * Set the end IP entry value.
     *
     * @param endIp the end IP
     */
    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    /**
     * Get the interval entry value.
     *
     * @return the interval
     */
    public String getInterval() {
        return interval;
    }

    /**
     * Set the interval entry value.
     *
     * @param interval the interval
     */
    public void setInterval(String interval) {
        this.interval = interval;
    }

    /**
     * Get the comment text entry value.
     *
     * @return the comment text
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment text entry value.
     *
     * @param comment the comment text
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Get all the configured monitors.
     *
     * @return the monitors as MonitorView list
     */
    public List<MonitorView> getConfiguredMonitors() {
        return configuredMonitors;
    }

    /**
     * Set all monitors as the current configuration.
     *
     * @param configuredMonitors the MonitorView list
     */
    public void setConfiguredMonitors(List<MonitorView> configuredMonitors) {
        this.configuredMonitors = configuredMonitors;
    }

    /**
     * Get the selected monitors.
     *
     * @return the selected monitors as MonitorView list
     */
    public List<MonitorView> getSelectedMonitors() {
        return selectedMonitors;
    }

    /**
     * Set the selected monitors.
     *
     * @param selectedMonitors the MonitorView list
     */
    public void setSelectedMonitors(List<MonitorView> selectedMonitors) {
        this.selectedMonitors = selectedMonitors;
    }

    /**
     * Get the currently running monitors.
     *
     * @return the monitors
     */
    public List<Monitor> getRunningMonitors() {
        return RUNNING_MONITORS;
    }

    /**
     * Check if the add button is disabled.
     *
     * @return true if disabled
     */
    public boolean isAddDisabled() {
        return addDisabled;
    }

    /**
     * Set the add button disabled.
     *
     * @param addDisabled true if button needs to be diaabled, else false
     */
    public void setAddDisabled(boolean addDisabled) {
        this.addDisabled = addDisabled;
    }

    /**
     * Check if the delete button is disabled.
     *
     * @return true if disabled
     */
    public boolean isDeleteDisabled() {
        return deleteDisabled;
    }

    /**
     * Set the delete button disabled.
     *
     * @param deleteDisabled true if button needs to be diaabled, else false
     */
    public void setDeleteDisabled(boolean deleteDisabled) {
        this.deleteDisabled = deleteDisabled;
    }

    /**
     * Check if the start button is disabled.
     *
     * @return true if disabled
     */
    public boolean isStartDisabled() {
        return startDisabled;
    }

    /**
     * Set the start button disabled.
     *
     * @param startDisabled true if button needs to be diaabled, else false
     */
    public void setStartDisabled(boolean startDisabled) {
        this.startDisabled = startDisabled;
    }

    /**
     * Check if the stop button is disabled.
     *
     * @return true if disabled
     */
    public boolean isStopDisabled() {
        return stopDisabled;
    }

    /**
     * Set the stop button disabled.
     *
     * @param stopDisabled true if button needs to be diaabled, else false
     */
    public void setStopDisabled(boolean stopDisabled) {
        this.stopDisabled = stopDisabled;
    }

    /**
     * Check if the results button is disabled.
     *
     * @return true if disabled
     */
    public boolean isResultsDisabled() {
        return resultsDisabled;
    }

    /**
     * Set the results button disabled.
     *
     * @param resultsDisabled true if button needs to be diaabled, else false
     */
    public void setResultsDisabled(boolean resultsDisabled) {
        this.resultsDisabled = resultsDisabled;
    }

    /**
     * Check if the load button is disabled.
     *
     * @return true if disabled
     */
    public boolean isLoadDisabled() {
        return loadDisabled;
    }

    /**
     * Set the load button disabled.
     *
     * @param loadDisabled true if button needs to be diaabled, else false
     */
    public void setLoadDisabled(boolean loadDisabled) {
        this.loadDisabled = loadDisabled;
    }

    /**
     * Add a monitor to the list of configured monitors.
     */
    public void addMonitor() {
        // Check the start IP
        if (!IpUtils.validIp(startIp)) {
            MsgUtils.showErrorMessage("Start IP is invalid !");
            return;
        }

        if (endIp.isEmpty()) {
            // Take the start IP as end IP to create a single IP range
            endIp = startIp;
            MsgUtils.showMessage("Single IP range created.");
        }
        else if (!IpUtils.validIp(endIp)) {
            // Check the end IP
            MsgUtils.showErrorMessage("End IP is invalid !");
            return;
        }

        if (interval.isEmpty()) {
            // Take a default if no input
            interval = DEFAULT_INTERVAL;
        }
        else {
            try {
                int num = Integer.parseInt(interval);
                if (num <= INTERVAL_MIN_VALUE || num > INTERVAL_MAX_VALUE) {
                    MsgUtils.showErrorMessage("Interval is out of range " + INTERVAL_MIN_VALUE + "-" + INTERVAL_MAX_VALUE
                            + ". Enter an interval between " + INTERVAL_MIN_VALUE + " and " + INTERVAL_MAX_VALUE + " !");
                    return;
                }
            }
            catch (NumberFormatException nfex) {
                MsgUtils.showErrorMessage("Interval is invalid. Enter a number !");
                return;
            }
        }

        LOG.debug("Configured monitors:");
        getConfiguredMonitors().forEach((item) -> {
            LOG.debug(item.toString());
        });

        MonitorView monitorView = new MonitorView(startIp, endIp, interval, comment);
        LOG.debug("Try adding " + monitorView.toString());

        if (!getConfiguredMonitors().contains(monitorView)) {
            if (monitorView.isValidAgainst(getConfiguredMonitors())) {
                configuredMonitors.add(monitorView);
                LOG.debug("Added " + monitorView.toString());
            }
            else {
                LOG.debug(monitorView.toString() + " invalid");
                MsgUtils.showErrorMessage("Monitor is invalid. Check the range entered !");
                return;
            }
        }
        else {
            LOG.debug(monitorView.toString() + " already exists");
            MsgUtils.showErrorMessage("Monitor already exists. Enter a different monitor !");
            return;
        }

        if (isDeleteDisabled()) {
            setDeleteDisabled(false);
        }

        if (!getConfiguredMonitors().isEmpty()) {
            setStartDisabled(false);
        }

        LOG.info("Save configuration");
        saveConfiguration();
    }

    /**
     * Delete the selected monitors from the list of configured monitors.
     */
    public void deleteSelection() {
        LOG.debug("Selected monitors:");
        getSelectedMonitors().forEach((item) -> {
            LOG.debug(item.toString());
        });

        LOG.debug("Configured monitors:");
        getConfiguredMonitors().forEach((item) -> {
            LOG.debug(item.toString());
        });

        // CAUTION: Do not use the following
        //          for (MonitorView monitorView : getConfiguredMonitors())
        //          It will result in an exception
        for (Iterator<MonitorView> iterator = getConfiguredMonitors().iterator(); iterator.hasNext();) {
            MonitorView monitorView = iterator.next();
            if (getSelectedMonitors().contains(monitorView)) {
                iterator.remove();
                LOG.debug("Removed " + monitorView);
            }
        }

        if (getConfiguredMonitors().isEmpty()) {
            setDeleteDisabled(true);
            setStartDisabled(true);
        }

        LOG.info("Save configuration");
        saveConfiguration();
    }

    /**
     * Start the monitors of the configured monitors list. Set buttons accordingly.
     *
     * @return the next faces page to go
     */
    public String startMonitors() {
        if (!isStartDisabled()) {
            LOG.info("Start monitors");

            synchronized (RUNNING_MONITORS) {
                getConfiguredMonitors().forEach((monitorView) -> {
                    try {
                        Monitor monitor = new Monitor(monitorView.getStartIp(), monitorView.getEndIp(), Integer.parseInt(monitorView.getInterval()), monitorView.getComment());
                        monitor.start();
                        RUNNING_MONITORS.add(monitor);
                    }
                    catch (InterruptedException iex) {
                        LOG.error("Could NOT start " + monitorView + " because:" + iex.getMessage());
                        MsgUtils.showMessage("Could not start a monitor");
                    }
                });
            }

            setAddDisabled(true);
            setDeleteDisabled(true);
            setStartDisabled(true);
            setLoadDisabled(true);
            setStopDisabled(false);
            setResultsDisabled(false);

            MsgUtils.showMessage("Monitors started");
            LOG.info("Finished starting monitors");

        }
        else {
            LOG.info("Monitors already started");
        }

        return ("go_results");
    }

    /**
     * Stop the currently running monitors. Set buttons accordingly.
     */
    public void stopMonitors() {
        if (!isStopDisabled()) {
            LOG.info("Stop monitors");

            synchronized (RUNNING_MONITORS) {
                // CAUTION: Do not use the following
                //          for (Monitor monitor : RUNNING_MONITORS)
                //          It will result in an exception
                for (Iterator<Monitor> iterator = RUNNING_MONITORS.iterator(); iterator.hasNext();) {
                    Monitor monitor = iterator.next();
                    monitor.stop();
                    iterator.remove();
                }
            }

            setAddDisabled(false);
            setDeleteDisabled(false);
            setLoadDisabled(false);
            setStartDisabled(false);
            setStopDisabled(true);
            setResultsDisabled(true);

            MsgUtils.showMessage("Stopping monitors");
            LOG.info("Finished stopping monitors");
        }
        else {
            LOG.info("Monitors already stopped");
        }
    }

    /**
     * Nothing to implement here.
     *
     * @param sce the servlet context event
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Nothing to do here
    }

    /**
     * Stop the running moitors if the web server stops.
     *
     * @param sce the event
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.info("Context destroyed; stop all monitors...");
        synchronized (RUNNING_MONITORS) {
            // CAUTION: Do not use the following
            //          for (Monitor monitor : RUNNING_MONITORS)
            //          It will result in an exception
            for (Iterator<Monitor> iterator = RUNNING_MONITORS.iterator(); iterator.hasNext();) {
                Monitor monitor = iterator.next();
                monitor.stop();
                iterator.remove();
            }
        }
        LOG.info("All monitors stopped...");
    }
}
