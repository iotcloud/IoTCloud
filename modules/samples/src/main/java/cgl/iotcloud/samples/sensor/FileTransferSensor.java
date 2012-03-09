package cgl.iotcloud.samples.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.StreamDataMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;
import cgl.iotcloud.core.sensor.basic.TextSensor;
import cgl.iotcloud.sensors.SensorAdaptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileTransferSensor extends AbstractSensor {
    private String directory;

    public FileTransferSensor(String type, String name) {
        super(type, name);
    }

    public void start() {
        SensorAdaptor adaptor = new SensorAdaptor("http://localhost:8081");

        TextSensor sensor = new TextSensor(Constants.SENSOR_TYPE_STREAMING, "chat-sensor");
        adaptor.registerSensor(sensor);

        adaptor.start();

        StreamDataMessage dataMessage = new StreamDataMessage();

        File file = new File("Test.txt");
        try {
            FileInputStream stream = new FileInputStream(file);
            dataMessage.setInputStream(stream);
            sensor.sendMessage(dataMessage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Quitting chat....");
        adaptor.stop();
    }

    public static void main(String[] args) {

    }

    @Override
    public void onControlMessage(SensorMessage message) {

    }
}
