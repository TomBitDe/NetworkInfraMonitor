package probe;

import destination.Destination;

/**
 * Use this interface to define a new probe.
 */
public interface Probe {
    /**
     * Probe a destination.
     */
    public void probe();

    /**
     * Is running allowed for the probe thread.
     *
     * @return true if running is allowed, else false
     */
    public boolean isRunning();

    /**
     * Stop the probe thread by setting to false.
     *
     * @param running false to stop the probe thread
     */
    public void setRunning(boolean running);

    /**
     * Get the probes destination.
     *
     * @return the destination
     */
    public Destination getDestination();
}
