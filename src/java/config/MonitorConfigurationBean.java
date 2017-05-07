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
 * Handle the monitoring configuration
 */
@WebListener
@ManagedBean(name = "MonitorConfigurationBean")
@ApplicationScoped
public class MonitorConfigurationBean implements Serializable, ServletContextListener {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(MonitorConfigurationBean.class);
    private static final String MONITORS_CFG = "WEB-INF/monitors.properties";
    private static final String KEY_PREFIX = "row";
    private static final String DEFAULT_INTERVAL = "30";

    private String startIp;
    private String endIp;
    private String interval;
    private String comment;

    private List<MonitorView> configuredMonitors = new ArrayList<>();
    private List<MonitorView> selectedMonitors;

    private static final List<Monitor> RUNNING_MONITORS = Collections.synchronizedList(new ArrayList<>());

    private boolean addDisabled = false;
    private boolean deleteDisabled = true;
    private boolean startDisabled = true;
    private boolean stopDisabled = true;
    private boolean resultsDisabled = true;
    private boolean loadDisabled = false;

    /**
     * Creates a new instance of MonitorConfigurationBean
     */
    public MonitorConfigurationBean() {
    }

    public void loadConfiguration() {
        Properties props = new Properties();

        File cfgPropertiesFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath(MONITORS_CFG));
        LOG.info(cfgPropertiesFile.getPath());

        try {
            props.load(new FileInputStream(cfgPropertiesFile));
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
        }
        catch (IOException ioex) {
            LOG.info(ioex.getMessage());
            configuredMonitors.clear();
        }
    }

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

        try {
            File cfgPropertiesFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath(MONITORS_CFG));
            LOG.info(cfgPropertiesFile.getPath());

            OutputStream oStream = new FileOutputStream(cfgPropertiesFile);
            props.store(oStream, "");
        }
        catch (IOException ioex) {
            LOG.info(ioex.getMessage());
        }
    }

    public String getStartIp() {
        return startIp;
    }

    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    public String getEndIp() {
        return endIp;
    }

    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Get all the configured monitors
     *
     * @return the monitors as MonitorView list
     */
    public List<MonitorView> getConfiguredMonitors() {
        return configuredMonitors;
    }

    /**
     * Set all monitors as the current configuration
     *
     * @param configuredMonitors the MonitorView list
     */
    public void setConfiguredMonitors(List<MonitorView> configuredMonitors) {
        this.configuredMonitors = configuredMonitors;
    }

    /**
     * Get the selected monitors
     *
     * @return the selected monitors as MonitorView list
     */
    public List<MonitorView> getSelectedMonitors() {
        return selectedMonitors;
    }

    /**
     * Set the selected monitors
     *
     * @param selectedMonitors the MonitorView list
     */
    public void setSelectedMonitors(List<MonitorView> selectedMonitors) {
        this.selectedMonitors = selectedMonitors;
    }

    /**
     * Get the currently running monitors
     *
     * @return the monitors
     */
    public List<Monitor> getRunningMonitors() {
        return RUNNING_MONITORS;
    }

    public boolean isAddDisabled() {
        return addDisabled;
    }

    public void setAddDisabled(boolean addDisabled) {
        this.addDisabled = addDisabled;
    }

    public boolean isDeleteDisabled() {
        return deleteDisabled;
    }

    public void setDeleteDisabled(boolean deleteDisabled) {
        this.deleteDisabled = deleteDisabled;
    }

    public boolean isStartDisabled() {
        return startDisabled;
    }

    public void setStartDisabled(boolean startDisabled) {
        this.startDisabled = startDisabled;
    }

    public boolean isStopDisabled() {
        return stopDisabled;
    }

    public void setStopDisabled(boolean stopDisabled) {
        this.stopDisabled = stopDisabled;
    }

    public boolean isResultsDisabled() {
        return resultsDisabled;
    }

    public void setResultsDisabled(boolean resultsDisabled) {
        this.resultsDisabled = resultsDisabled;
    }

    public boolean isLoadDisabled() {
        return loadDisabled;
    }

    public void setLoadDisabled(boolean loadDisabled) {
        this.loadDisabled = loadDisabled;
    }

    /**
     * Add a monitor to the list of configured monitors
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
                if (num <= 9 || num > 300) {
                    MsgUtils.showErrorMessage("Interval is out of range 10-300. Enter an interval between 10 and 300 !");
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
     * Delete the selected monitors from the list of configured monitors
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
     * Start the monitors of the configured monitors list. Set buttons accordingly
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
     * Stop the currently running monitors. Set buttons accordingly
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

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Nothing to do here
    }

    /**
     * Stop the running moitors if the web server stopps
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
