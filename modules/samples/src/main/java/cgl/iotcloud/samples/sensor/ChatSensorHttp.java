package cgl.iotcloud.samples.sensor;

import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;

public class ChatSensorHttp extends AbstractSensor {
    public ChatSensorHttp(String type, String name) {
        super(type, name);
    }

    @Override
    public void onControlMessage(SensorMessage message) {

    }
}
