package cgl.iotcloud.samples.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.jms.JMSControlMessageFactory;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.sensors.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NodeChatSensor {
    private Node node;

    private Listener controlListener;

    private Sender messageSender;

    public NodeChatSensor() throws IOTException {
        node = new Node(new NodeName("NodeChatSensor"), "http://localhost:8080");
    }

    public void start() throws IOTException {
        node.start();

        controlListener = node.newListener("control", Constants.MESSAGE_TYPE_BLOCK, "control");
        controlListener.setMessageHandler(new MessageHandler() {
            @Override
            public void onMessage(SensorMessage message) {
                System.out.println("Control message received" + message);
            }
        });

        if (controlListener instanceof JMSListener) {
            ((JMSListener)controlListener).setMessageFactory(new JMSControlMessageFactory());
        }

        messageSender = node.newSender("data", Constants.MESSAGE_TYPE_BLOCK, "data");
        if (messageSender instanceof JMSSender) {
            ((JMSSender) messageSender).setMessageFactory(new JMSDataMessageFactory());
        }
    }

    public void stop() throws IOTException {
        node.stop();
    }

    public void readChat() {
        InputStreamReader cin = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(cin);
        String chat;
        try {
            chat = reader.readLine();
            while (!"quit".equals(chat)) {
                TextDataMessage message = new TextDataMessage();
                message.setText(chat);
                messageSender.send(message);

                chat = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        try {
            NodeChatSensor chatSensor = new NodeChatSensor();
            chatSensor.start();
            chatSensor.readChat();
            chatSensor.stop();
        } catch (IOTException e) {
            e.printStackTrace();
        }
    }
}
