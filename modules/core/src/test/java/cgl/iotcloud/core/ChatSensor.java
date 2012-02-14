package cgl.iotcloud.core;

import cgl.iotcloud.core.config.SCCConfigurationFactory;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.sensor.basic.TextSensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatSensor {
    private IoTCloud cloud = null;

    public void setUp() {
        SCCConfigurationFactory fac = new SCCConfigurationFactory();
        SCConfiguration config = fac.create("repository/conf/broker-config.xml");
        cloud = new IoTCloud(config);
        cloud.init();
    }

    public void runSensor() {
        TextSensor sensor = new TextSensor(Constants.SENSOR_TYPE_BLOCK, "chat-sensor");
        sensor.init();

        InputStreamReader cin = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(cin);
        String chat = null;
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

        sensor.destroy();
    }

    public static void main(String[] args) {
        ChatSensor chatSensor = new ChatSensor();
        chatSensor.setUp();
        chatSensor.runSensor();
    }
}
