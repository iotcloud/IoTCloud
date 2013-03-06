package cgl.iotcloud.samples.arducopter.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.samples.arducopter.mssg.StateControlMessage;
import cgl.iotcloud.samples.arducopter.mssg.ControllerMessage;
import cgl.iotcloud.sensors.Node;

import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import com.google.common.collect.Lists;

public class ArduSensor {

    private RosArducopter rosArducopter;
    private Node node;

    private Sender attitudeSender;
    private Sender stateSender;
    private Sender mriSender;
    private Sender vhSender;
    private Sender rcSender;
    private Sender controlSender;

    private Listener controlListener;
    private Listener stateListener;

    private ArduCopterController controller;

    public ArduSensor() {
        try {
            node = new Node(new NodeName("ardusensor"), "http://localhost:8080");
        } catch (IOTException e) {
            e.printStackTrace();
        }
        rosArducopter = new RosArducopter();
        controller = rosArducopter.getController();

    }

    public Sender getAttitudeSender() {
        return attitudeSender;
    }

    public Sender getStateSender() {
        return stateSender;
    }

    public Sender getMriSender() {
        return mriSender;
    }

    public Sender getVhSender() {
        return vhSender;
    }

    public Sender getRcSender() {
        return rcSender;
    }

    public Sender getControlSender() {
        return controlSender;
    }

    public void start(NodeConfiguration nodeConfiguration) throws IOTException {
        node.start();

        attitudeSender = node.newSender("attitudeData", Constants.MESSAGE_TYPE_BLOCK, "attitudeData");
        if (attitudeSender instanceof JMSSender) {
            ((JMSSender) attitudeSender).setMessageFactory(new JMSDataMessageFactory());
        }

        stateSender = node.newSender("stateData", Constants.MESSAGE_TYPE_BLOCK, "stateData");
        if (stateSender instanceof JMSSender) {
            ((JMSSender) stateSender).setMessageFactory(new JMSDataMessageFactory());
        }

        mriSender = node.newSender("mriData", Constants.MESSAGE_TYPE_BLOCK, "mriData");
        if (mriSender instanceof JMSSender) {
            ((JMSSender) mriSender).setMessageFactory(new JMSDataMessageFactory());
        }

        vhSender = node.newSender("vhData", Constants.MESSAGE_TYPE_BLOCK, "vhData");
        if (vhSender instanceof JMSSender) {
            ((JMSSender) vhSender).setMessageFactory(new JMSDataMessageFactory());
        }

        rcSender = node.newSender("rcData", Constants.MESSAGE_TYPE_BLOCK, "rcData");
        if (rcSender instanceof JMSSender) {
            ((JMSSender) rcSender).setMessageFactory(new JMSDataMessageFactory());
        }

        controlSender = node.newSender("controlData", Constants.MESSAGE_TYPE_BLOCK, "controlData");
        if (controlSender instanceof JMSSender) {
            ((JMSSender) controlSender).setMessageFactory(new JMSDataMessageFactory());
        }

        controlListener = node.newListener("controls", Constants.MESSAGE_TYPE_BLOCK, "controls");
        if (controlListener instanceof JMSListener) {
            ((JMSListener) controlListener).setMessageFactory(new JMSDataMessageFactory());
        }

        controlListener.setMessageHandler(new MessageHandler() {
            @Override
            public void onMessage(SensorMessage message) {
                if (message instanceof ControllerMessage) {
                    ControllerMessage m = (ControllerMessage) message;
                    System.out.println("received control message..........");
                    System.out.println("active: " + m.getActive());
                    System.out.println("yaw: " + m.getYaw());
                    System.out.println("thrust: " + m.getThrust());
                    System.out.println("roll: " + m.getRoll());
                    System.out.println("pitch: " + m.getPitch());

                    controller.setActive(m.getActive());
                    controller.setYaw(m.getYaw());
                    controller.setThrust(m.getThrust());
                    controller.setPitch(m.getPitch());
                    controller.setRoll(m.getRoll());

                    controller.setR5(m.getR5());
                    controller.setR6(m.getR6());
                    controller.setR7(m.getR7());
                    controller.setR8(m.getR8());

                    controller.setNewData(true);
                }
            }
        });

        stateListener = node.newListener("stateControls", Constants.MESSAGE_TYPE_BLOCK, "stateControls");
        if (stateListener instanceof JMSListener) {
            ((JMSListener) stateListener).setMessageFactory(new JMSDataMessageFactory());
        }

        stateListener.setMessageHandler(new MessageHandler() {
            @Override
            public void onMessage(SensorMessage message) {
                if (message instanceof StateControlMessage) {
                    StateControlMessage stateControlMessage = (StateControlMessage) message;

                    controller.setActive(stateControlMessage.isActive());
                    //controller.setNewData(true);
                }
            }
        });

        //Preconditions.checkState(turtle != null);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(rosArducopter, nodeConfiguration);

        rosArducopter.setSensor(this);
    }

    public static void main(String[] argv) throws Exception {
        // register with ros_java
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(argv));
        NodeConfiguration nodeConfiguration = loader.build();
        final ArduSensor sensor = new ArduSensor();

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
}
