package cgl.iotcloud.samples.turtlebot.sensor;

import geometry_msgs.Twist;
import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import sensor_msgs.Image;

import java.util.concurrent.atomic.AtomicInteger;

public class RosTurtle extends AbstractNodeMain {
    private volatile Velocity linear = null;

    private volatile Velocity angular = null;

    private AtomicInteger count = new AtomicInteger(0);

    private TurtleSensor sensor;

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
                ChannelBuffer buffer = message.getData();

                if (buffer.hasArray()) {
                    f.setBuffer(buffer.array());
                }
                System.out.println("recevived image");
                sensor.sendMessage(f);
            }
        });

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

                publisher.publish(str);
                Thread.sleep(500);
            }
        });
    }
}
