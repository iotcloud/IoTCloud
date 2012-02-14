package cgl.iotcloud.core;

/**
 * This interface should be implemented by the entities requiring some
 * control. It has the basic control operations defined. For example sensors
 * that accepting some control instructions can implement this interface.
 */
public interface Control {
    /**
     * Start the entity
     */
    public void start();

    /**
     * Stop the entity
     */
    public void stop();

    /**
     * Get the current state of the entity
     * @return state as a String
     */
    public String getState();
}
