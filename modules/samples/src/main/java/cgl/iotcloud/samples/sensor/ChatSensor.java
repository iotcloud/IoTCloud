package cgl.iotcloud.samples.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.sensor.basic.TextSensor;
import cgl.iotcloud.sensors.SensorAdaptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatSensor {
    public static void main(String[] args) {
        SensorAdaptor adaptor = new SensorAdaptor("http://localhost:8081");

        TextSensor sensor = new TextSensor(Constants.SENSOR_TYPE_BLOCK, "chat-sensor");
        adaptor.registerSensor(sensor);

        adaptor.start();

        InputStreamReader cin = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(cin);
        String chat;
        try {
            chat = reader.readLine();
            while (!"quit".equals(chat)) {
                TextDataMessage message = new TextDataMessage();
                message.setText(chat);
                sensor.sendMessage(message);

                chat = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Quitting chat....");
        adaptor.stop();
    }
}
