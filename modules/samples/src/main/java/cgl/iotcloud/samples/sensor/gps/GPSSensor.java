package cgl.iotcloud.samples.sensor.gps;

import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;

public class GPSSensor extends AbstractSensor {
    public GPSSensor(String type, String name) {
        super(type, name);
    }

    @Override
    public void onControlMessage(SensorMessage message) {

    }
}
