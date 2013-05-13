package cgl.iotcloud.core;

import cgl.iotcloud.core.message.SensorMessage;

/**
 * A sender is responsible for sending a message out. This is used by both nodes
 */
public interface Sender extends ManagedLifeCycle, Control {
    public void send(SensorMessage message);
}
