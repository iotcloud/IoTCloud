package cgl.iotcloud.samples.turtlebot.sensor;

import cgl.iotcloud.core.message.SensorMessage;
import geometry_msgs.Twist;
import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import sensor_msgs.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class RosTurtle extends AbstractNodeMain {
    private volatile Velocity linear = null;

    private volatile Velocity angular = null;

    private TurtleSensor sensor;

    private boolean stop = false;

    public void setLinear(Velocity linear) {
        this.linear = linear;
    }

    public void setAngular(Velocity angular) {
        this.angular = angular;
    }

    public void setSensor(TurtleSensor sensor) {
        this.sensor = sensor;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("/controller");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<Twist> publisher =
                connectedNode.newPublisher("/cmd_vel", Twist._TYPE);

        final Subscriber<Image> subscriber =
                connectedNode.newSubscriber("/camera/rgb/image_color", Image._TYPE);

        subscriber.addMessageListener(new MessageListener<Image>() {
            @Override
            public void onNewMessage(Image message) {
                Frame f = new Frame();
                f.setWidth(message.getWidth());
                f.setHeight(message.getHeight());
                f.setStep(message.getStep());

                f.setBuffer(handleMessage(message));
                System.out.println("received rgb image");
                if (!stop) {
                    sensor.getRgbSender().send(f);
                }
            }
        });

//        final Subscriber<Image> depthSubscriber =
//                connectedNode.newSubscriber("/camera/rgb/image_mono", Image._TYPE);
//        depthSubscriber.addMessageListener(new MessageListener<Image>() {
//            @Override
//            public void onNewMessage(Image message) {
//                Frame f = new Frame();
//                f.setWidth(message.getWidth());
//                f.setHeight(message.getHeight());
//                f.setStep(message.getStep());
//
//                f.setBuffer(handleMessage(message));
//                System.out.println("received depth image");
//                sensor.getDepthSender().send(f);
//            }
//        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        // This CancellableLoop will be canceled automatically when the node shuts down.
        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                Twist str = publisher.newMessage();

                if (linear != null) {
                    str.getLinear().setX(linear.getX());
                    str.getLinear().setY(linear.getY());
                    str.getLinear().setZ(linear.getZ());
                }

                if (angular != null) {
                    str.getAngular().setX(angular.getX());
                    str.getAngular().setY(angular.getY());
                    str.getAngular().setZ(angular.getZ());
                }
                if (!stop) {
                    publisher.publish(str);
                }
                Thread.sleep(100);
            }
        });
    }

    int count = 0;
    private byte[] handleMessage(Image m) {
        byte [] array = new byte[m.getHeight() * m.getWidth() * 3];

        for (int i = 0; i < m.getHeight() * m.getWidth() * 3; i++) {
            array[i] = m.getData().getByte(i);
        }

//            Image message =  m;
//            BufferedImage im = new BufferedImage(message.getWidth(), message.getHeight(), BufferedImage.TYPE_INT_RGB);
//            for (int x = 0; x < message.getWidth(); x++) {
//                for (int y = 0; y < message.getHeight(); y++) {
//                    byte red =
//                            message.getData().getByte((int) (y * message.getStep() + 3 * x));
//                    byte green =
//                            message.getData().getByte(((int) (y * message.getStep() + 3 * x + 1)));
//                    byte blue =
//                            message.getData().getByte(((int) (y * message.getStep() + 3 * x + 2)));
//                    int rgb = (red & 0xFF);
//                    rgb = (rgb << 8) + (green & 0xFF);
//                    rgb = (rgb << 8) + (blue & 0xFF);
//
//                    im.setRGB(x, y, new Color(red & 0xFF, green & 0xFF, blue & 0xFF).getRGB());
//                }
//            }
//            try {
//                File outputfile = new File("saved_" + count++ + ".png");
//                ImageIO.write(im, "png", outputfile);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        return array;
    }

    private byte[] handleMessageDepth(Image m) {
        byte [] array = new byte[m.getHeight() * m.getWidth() * 3];

        for (int i = 0; i < m.getHeight() * m.getWidth() * 3; i++) {
            array[i] = m.getData().getByte(i);
        }

//            Image message =  m;
//            BufferedImage im = new BufferedImage(message.getWidth(), message.getHeight(), BufferedImage.TYPE_INT_RGB);
//            for (int x = 0; x < message.getWidth(); x++) {
//                for (int y = 0; y < message.getHeight(); y++) {
//                    byte red =
//                            message.getData().getByte((int) (y * message.getStep() + 3 * x));
//                    byte green =
//                            message.getData().getByte(((int) (y * message.getStep() + 3 * x + 1)));
//                    byte blue =
//                            message.getData().getByte(((int) (y * message.getStep() + 3 * x + 2)));
//                    int rgb = (red & 0xFF);
//                    rgb = (rgb << 8) + (green & 0xFF);
//                    rgb = (rgb << 8) + (blue & 0xFF);
//
//                    im.setRGB(x, y, new Color(red & 0xFF, green & 0xFF, blue & 0xFF).getRGB());
//                }
//            }
//            try {
//                File outputfile = new File("saved_" + count++ + ".png");
//                ImageIO.write(im, "png", outputfile);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        return array;
    }

    @Override
    public void onShutdown(Node node) {
        node.shutdown();
    }

    public void stop() {
        stop = true;
    }
}
