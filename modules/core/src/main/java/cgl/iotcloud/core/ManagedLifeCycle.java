package cgl.iotcloud.core;

/**
 * This interface is implemented by the entities with a life cycle.
 * Init method is called at the start and destroy is called at the end.
 */
public interface ManagedLifeCycle {
    /**
     * Initializes the object
     */
    public void init();

    /**
     * Destroy the object
     */
    public void destroy();
}
