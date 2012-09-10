package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;

/**
 * A application i.e Sensor or Client creates a Node to communicate.
 */
public interface Node {
    public NodeName getName();

    public void start() throws IOTException;

    public void stop() throws IOTException;

    public Listener newListener(String name, String type, String path) throws IOTException;

    public Sender newSender(String name, String type, String path) throws IOTException;
}
