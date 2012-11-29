package cgl.iotcloud.samples.turtlebot.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.message.jms.JMSControlMessageFactory;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.sensors.Node;
import com.google.common.collect.Lists;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class TurtleSensor {
    private RosTurtle turtle = null;

    private Listener controlListener;

    private Sender rgbSender;

    private Sender depthSender;

    private Node node;

    public TurtleSensor() {
        try {
            node = new Node(new NodeName("turtle-sensor"), "http://localhost:8080");
        } catch (IOTException e) {
            e.printStackTrace();
        }
        turtle = new RosTurtle();
    }

    public Sender getRgbSender() {
        return rgbSender;
    }

    public Sender getDepthSender() {
        return depthSender;
    }

    public void start(NodeConfiguration nodeConfiguration) throws IOTException {
        node.start();

        controlListener = node.newListener("control", Constants.MESSAGE_TYPE_BLOCK, "control");
        if (controlListener instanceof JMSListener) {
            ((JMSListener) controlListener).setMessageFactory(new JMSControlMessageFactory());
        }
        controlListener.setMessageHandler(new MessageHandler() {
            @Override
            public void onMessage(SensorMessage message) {
                onControlMessage(message);
            }
        });

        rgbSender = node.newSender("rgbData", Constants.MESSAGE_TYPE_BLOCK, "rgdData");
        if (rgbSender instanceof JMSSender) {
            ((JMSSender) rgbSender).setMessageFactory(new JMSDataMessageFactory());
        }

        depthSender = node.newSender("depthData", Constants.MESSAGE_TYPE_BLOCK, "depthData");
        if (depthSender instanceof JMSSender) {
            ((JMSSender) depthSender).setMessageFactory(new JMSDataMessageFactory());
        }

        //Preconditions.checkState(turtle != null);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(turtle, nodeConfiguration);

        turtle.setSensor(this);
    }

    public static void main(String[] argv) throws Exception {
        // register with ros_java
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(argv));
        NodeConfiguration nodeConfiguration = loader.build();
        final TurtleSensor sensor = new TurtleSensor();

        sensor.start(nodeConfiguration);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                sensor.stop();
            }
        });
    }

    private void stop() {
        try {
            node.stop();
        } catch (IOTException e) {
            e.printStackTrace();
        }
    }

    public void onControlMessage(SensorMessage message) {
        if (message instanceof DefaultControlMessage) {
            DefaultControlMessage controlMessage = (DefaultControlMessage) message;

            for (String keys : controlMessage.getControls().keySet()) {
                System.out.println("Received control message: " + keys + " :" + controlMessage.getControls().get(keys));
            }

            Velocity vl = new Velocity();
            Object o = controlMessage.getControl("l.x");
            if (o instanceof Double) {
                vl.setX((Double) o);
            }

            o = controlMessage.getControl("l.y");
            if (o instanceof Double) {
                vl.setY((Double) o);
            }

            o = controlMessage.getControl("l.z");
            if (o instanceof Double) {
                vl.setZ((Double) o);
            }

            Velocity va = new Velocity();
            o = controlMessage.getControl("a.x");
            if (o instanceof Double) {
                va.setX((Double) o);
            }

            o = controlMessage.getControl("a.y");
            if (o instanceof Double) {
                va.setY((Double) o);
            }

            o = controlMessage.getControl("a.z");
            if (o instanceof Double) {
                va.setZ((Double) o);
            }
            turtle.setLinear(vl);
            turtle.setAngular(va);
        }
    }
}
