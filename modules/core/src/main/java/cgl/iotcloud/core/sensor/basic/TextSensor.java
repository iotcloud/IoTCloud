package cgl.iotcloud.core.sensor.basic;

import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;

/**
 * This is a simple sensor developed as a base case for a text base sensor. This sensor
 * periodically sends messages to the sensor cloud.
 */
public class TextSensor extends AbstractSensor {
    public TextSensor(String type, String name) {
        super(type, name);
    }

    @Override
    public void onControlMessage(SensorMessage message) {
        if (message instanceof TextDataMessage) {
            System.out.println("Control message received: " + ((TextDataMessage) message).getText());
        }
    }
}

