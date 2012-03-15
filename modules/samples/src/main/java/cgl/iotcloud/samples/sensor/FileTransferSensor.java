package cgl.iotcloud.samples.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.StreamDataMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;
import cgl.iotcloud.sensors.SensorAdaptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileTransferSensor extends AbstractSensor {
    private boolean send = false;

    private boolean run = true;

    public FileTransferSensor(String type, String name) {
        super(type, name);
    }

    public void start() {
        SensorAdaptor adaptor = new SensorAdaptor("http://localhost:8081");
        adaptor.registerSensor(this);
        adaptor.start();

        while (run) {
            if (send) {
                StreamDataMessage dataMessage = new StreamDataMessage();
                File file = new File("Test.txt");
                try {
                    FileInputStream stream = new FileInputStream(file);
                    dataMessage.setInputStream(stream);
                    sendMessage(dataMessage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Quitting chat....");
        adaptor.stop();
    }

    public static void main(String[] args) {
        FileTransferSensor sensor = new FileTransferSensor(Constants.SENSOR_TYPE_STREAMING, "file-sensor");
        sensor.start();
    }

    @Override
    public void onControlMessage(SensorMessage message) {
        if (message instanceof TextDataMessage) {
            String command = ((TextDataMessage) message).getText();
            if (command != null && command.equals("send")) {
                send = true;
            } else if (command != null && command.equals("stop")) {
                run = false;
            }
        }
    }
}
