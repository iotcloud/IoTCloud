package cgl.iotcloud.samples.client;

import cgl.iotcloud.clients.SensorClient;
import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.message.update.UpdateMessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatClientHttp {
    public static void main(String[] args) {
        SensorClient sensorClient = new SensorClient("localhost", 8080, true);
        InputStreamReader cin = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(cin);

        final boolean[] quit = {false};
        sensorClient.fixOnSensorWithName("chat-sensor");

        sensorClient.setUpdateListener(new UpdateMessageHandler() {
            public void onUpdate(UpdateMessage message) {
                if (message.getUpdate(Constants.Updates.STATUS) != null &&
                        message.getUpdate(Constants.Updates.STATUS).equals(Constants.Updates.REMOVED)) {
                    quit[0] = true;
                    try {
                        reader.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        });

        sensorClient.listen(new MessageHandler() {
            public void onMessage(SensorMessage message) {
                if (message instanceof TextDataMessage) {
                    System.out.println("Message Received: " + ((TextDataMessage) message).getText());
                }
            }
        });

        String chat;
        try {
            chat = reader.readLine();
            while (!"quit".equals(chat) && !quit[0]) {
                TextDataMessage message = new TextDataMessage();
                message.setText(chat);
                sensorClient.sendControlMessage(message);

                chat = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("quit chat");
    }
}
