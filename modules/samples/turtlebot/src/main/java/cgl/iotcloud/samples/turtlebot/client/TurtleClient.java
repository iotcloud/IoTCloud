package cgl.iotcloud.samples.turtlebot.client;

import cgl.iotcloud.client.robot.RootFrame;
import cgl.iotcloud.clients.SensorClient;
import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.ControlMessage;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.samples.turtlebot.sensor.Frame;
import cgl.iotcloud.samples.turtlebot.sensor.Velocity;

import java.awt.image.BufferedImage;

public class TurtleClient {
    private SensorClient sensorClient;

    public void start() {
        sensorClient = new SensorClient("http://localhost:8080/");

        sensorClient.fixOnSensorWithName("turtle-sensor");

        sensorClient.setUpdateHandler(new MessageHandler() {
            public void onMessage(SensorMessage message) {
                if (message instanceof UpdateMessage) {
                    UpdateMessage updateMessage = (UpdateMessage) message;
                    if (updateMessage.getUpdate(Constants.Updates.STATUS) != null &&
                            updateMessage.getUpdate(Constants.Updates.STATUS).equals(Constants.Updates.REMOVED)) {

                    }
                }
            }
        });

        sensorClient.listen(new MessageHandler() {
            public void onMessage(SensorMessage message) {
                if (message instanceof TextDataMessage) {
                    System.out.println("Message Received: " + ((TextDataMessage) message).getText());
                }
                System.out.println(message);

                handleMessage(message);
            }
            }, new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    if (message instanceof Frame) {
                        System.out.println(message);
                    }
                    System.out.println(message);
                }
            }
        );
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
                    im.setRGB(x, y, rgb);
                }
            }
            try {
				RootFrame.getInstance().setImage(im);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            RootFrame.getInstance().getDataContainer().repaint();
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

        sensorClient.sendControlMessage(controlMessage);
    }
}
