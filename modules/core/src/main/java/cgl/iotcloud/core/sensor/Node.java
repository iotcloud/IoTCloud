package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;

/**
 * A application i.e Sensor or Client creates a Node to communicate.
 */
public interface Node {
    /**
     * Get the name of the node
     *
     * @return node name
     */
    public NodeName getName();

    /**
     * Start the node
     *
     * @throws IOTException if an exception occurs
     */
    public void start() throws IOTException;

    /**
     * Stop the node
     *
     * @throws IOTException if an exception occurs
     */
    public void stop() throws IOTException;

    /**
     * Create a new listener for this node
     *
     * @param name name of the listener
     * @param type type of the
     * @param path path of the node
     * @return the new listener
     * @throws IOTException if an error occurs
     */
    public Listener newListener(String name, String type, String path) throws IOTException;

    /**
     * Create a new sender for this node
     *
     * @param name name of the sender
     * @param type type of the sender
     * @param path of the sender
     * @return the new sender
     * @throws IOTException if an error occurs
     */
    public Sender newSender(String name, String type, String path) throws IOTException;
}
