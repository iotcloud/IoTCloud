package cgl.iotcloud.samples.turtlebot.client;

import cgl.iotcloud.client.robot.RootFrame;
import cgl.iotcloud.clients.SensorClient;
import cgl.iotcloud.core.*;
import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.message.ControlMessage;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.jms.JMSControlMessageFactory;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.samples.turtlebot.sensor.Frame;
import cgl.iotcloud.samples.turtlebot.sensor.Velocity;
import cgl.iotcloud.sensors.NodeClient;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TurtleClient {
    private SensorClient sensorClient;
    private TurtleUI turtleUI;

    private NodeClient nodeClient = null;

    private Sender controlSender;

    private Listener rgbListener;

    private Listener depthListener;

    public TurtleClient(TurtleUI turtleUI){
    	this.turtleUI = turtleUI;
        try {
            nodeClient = new NodeClient("http://localhost:8080/");
        } catch (IOTException e) {
            e.printStackTrace();
        }
    }
    
    public void start() {
        try {
            NodeInformation nodeInformation = nodeClient.getNode(new NodeName("turtle-sensor"));

            Endpoint endpoint = nodeInformation.getConsumer("control");
            controlSender = nodeClient.newSender(endpoint);
            if (controlSender instanceof JMSSender) {
                ((JMSSender) controlSender).setMessageFactory(new JMSControlMessageFactory());
            }

            controlSender.init();
            controlSender.start();

            endpoint = nodeInformation.getProducer("rgbData");
            rgbListener = nodeClient.newListener(endpoint);
            if (rgbListener instanceof JMSListener) {
                ((JMSListener) rgbListener).setMessageFactory(new JMSDataMessageFactory());
            }

            rgbListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    handleMessage(message);
                }
            });

            rgbListener.init();
            rgbListener.start();

            endpoint = nodeInformation.getProducer("depthData");
            depthListener = nodeClient.newListener(endpoint);
            if (depthListener instanceof JMSListener) {
                ((JMSListener) depthListener).setMessageFactory(new JMSDataMessageFactory());
            }

            depthListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    //handleMessage(message);
                }
            });
            depthListener.init();
            depthListener.start();

        } catch (IOTException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(SensorMessage m) {
        if (m instanceof Frame) {
            Frame message = (Frame) m;
            BufferedImage im = new BufferedImage(message.getWidth(), message.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < message.getWidth(); x++) {
                for (int y = 0; y < message.getHeight(); y++) {
                    byte red =
                            message.getBuffer()[(int) (y * message.getStep() + 3 * x)];
                    byte green =
                            message.getBuffer()[((int) (y * message.getStep() + 3 * x + 1))];
                    byte blue =
                            message.getBuffer()[((int) (y * message.getStep() + 3 * x + 2))];
                    int rgb = (red & 0xFF);
                    rgb = (rgb << 8) + (green & 0xFF);
                    rgb = (rgb << 8) + (blue & 0xFF);

                    im.setRGB(x, y, new Color(red & 0xFF, green & 0xFF, blue & 0xFF).getRGB());
                }
            }
            try {
                turtleUI.update(im);
			} catch (Exception e) {
				e.printStackTrace();
			}
            
        }
    }

    private void handleMonoMessage(SensorMessage m) {
        if (m instanceof Frame) {
            Frame message = (Frame) m;
            BufferedImage im = new BufferedImage(message.getWidth(), message.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            for (int x = 0; x < message.getWidth(); x++) {
                for (int y = 0; y < message.getHeight(); y++) {
                    byte red =
                            message.getBuffer()[(int) (y * message.getStep() + 4 * x)];
                    byte green =
                            message.getBuffer()[((int) (y * message.getStep() + 4 * x + 1))];
                    byte blue =
                            message.getBuffer()[((int) (y * message.getStep() + 4 * x + 2))];
                    int rgb = (red & 0xFF);
                    rgb = (rgb << 8) + (green & 0xFF);
                    rgb = (rgb << 8) + (blue & 0xFF);

                    im.setRGB(x, y, new Color(red & 0xFF, green & 0xFF, blue & 0xFF).getRGB());
                }
            }
            try {
                turtleUI.update(im);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setVelocity(Velocity linear, Velocity angular) {
        DefaultControlMessage controlMessage = new DefaultControlMessage();

        controlMessage.addControl("l.x", linear.getX());
        controlMessage.addControl("l.y", linear.getY());
        controlMessage.addControl("l.z", linear.getZ());

        controlMessage.addControl("a.x", angular.getX());
        controlMessage.addControl("a.y", angular.getY());
        controlMessage.addControl("a.z", angular.getZ());
        System.out.println("Sending message");
        controlSender.send(controlMessage);
    }
}
