package cgl.iotcloud.core;

import cgl.iotcloud.core.message.SensorMessage;

public interface Sender extends ManagedLifeCycle, Control {
    public void send(SensorMessage message);
}
