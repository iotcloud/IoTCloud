package cgl.iotcloud.samples.client;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.sensors.NodeClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class  NodeChatClient {
    private NodeClient nodeClient = null;

    private Sender controlSender;

    private Listener dataListener;

    public NodeChatClient() {
        try {
            nodeClient = new NodeClient("http://localhost:8080/");
        } catch (IOTException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            NodeInformation nodeInformation = nodeClient.getNode(new NodeName("NodeChatSensor"));

            Endpoint endpoint = nodeInformation.getConsumer("control");

            controlSender = nodeClient.newSender(endpoint);
            if (controlSender instanceof JMSSender) {
                ((JMSSender) controlSender).setMessageFactory(new JMSDataMessageFactory());
            }

            controlSender.init();
            controlSender.start();

            endpoint = nodeInformation.getProducer("data");
            dataListener = nodeClient.newListener(endpoint);
            dataListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    if (message instanceof TextDataMessage) {
                        System.out.println("Char received: " + ((TextDataMessage) message).getText());
                    }
                }
            });

            if (dataListener instanceof JMSListener) {
                ((JMSListener) dataListener).setMessageFactory(new JMSDataMessageFactory());
            }

            dataListener.init();
            dataListener.start();
        } catch (IOTException e) {
            e.printStackTrace();
        }
    }

    public void read() {
        String chat;
        InputStreamReader cin = new InputStreamReader(System.in);
        final BufferedReader reader = new BufferedReader(cin);
        try {
            chat = reader.readLine();
            while (!"quit".equals(chat)) {
                TextDataMessage message = new TextDataMessage();
                message.setText(chat);
                controlSender.send(message);

                chat = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        NodeChatClient chatClient = new NodeChatClient();
        chatClient.init();
        chatClient.read();
    }
}
