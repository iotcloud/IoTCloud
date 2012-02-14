package cgl.iotcloud.core;

import cgl.iotcloud.core.client.Client;
import cgl.iotcloud.core.config.SCCConfigurationFactory;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.sensor.SCSensor;

public class ChatClient {
    private IoTCloud cloud = null;

    public void init() {
        SCCConfigurationFactory fac = new SCCConfigurationFactory();
        SCConfiguration config = fac.create("repository/conf/broker-config.xml");
        cloud = new IoTCloud(config);
        cloud.init();
    }

    public void runClient() {
        Client client = new Client(new SCSensor("chat-sensor"));
        client.setMessageHandler(new MessageReceiver());
        client.init();

        TextDataMessage message = new TextDataMessage();
        message.setText("Hello world");
        //client.sendMessage(message);

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }

    private class MessageReceiver implements MessageHandler {
        public void onMessage(SensorMessage message) {
            if (message instanceof TextDataMessage) {
                System.out.println("Message recieved: " + ((TextDataMessage) message).getText());
            }
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();

        client.init();
        client.runClient();
    }
}
