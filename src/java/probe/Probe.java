package probe;

import destination.Destination;

/**
 */
public interface Probe {
    /**
     * Probe a destination
     */
    public void probe();

    /**
     * Is running allowed for the probe thread
     *
     * @return true if running is allowed, else false
     */
    public boolean isRunning();

    /**
     * Stop the probe thread by setting to false
     *
     * @param running
     */
    public void setRunning(boolean running);

    /**
     * Get the probes destination
     *
     * @return the destination
     */
    public Destination getDestination();
}