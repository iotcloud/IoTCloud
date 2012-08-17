package cgl.iotcloud.samples.turtlebot.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;
import cgl.iotcloud.sensors.SensorAdaptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class TurtleSensor extends AbstractSensor {
    private RosTurtle turtle = null;

    public TurtleSensor(String type, String name) {
        super(type, name);

        turtle = new RosTurtle();
    }

    public void start(NodeConfiguration nodeConfiguration) {
        Preconditions.checkState(turtle != null);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(turtle, nodeConfiguration);

        // register the sensor itself
        SensorAdaptor adaptor = new SensorAdaptor("http://localhost:8080");
        adaptor.registerSensor(this);

        adaptor.start();
    }

    public static void main(String[] argv) throws Exception {
        // register with ros_java
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(argv));

        NodeConfiguration nodeConfiguration = loader.build();

        TurtleSensor sensor = new TurtleSensor(Constants.SENSOR_TYPE_BLOCK, "turtle-sensor");
        sensor.start(nodeConfiguration);
    }

    @Override
    public void onControlMessage(SensorMessage message) {
        if (message instanceof DefaultControlMessage) {
            DefaultControlMessage controlMessage = (DefaultControlMessage) message;

            Velocity vl = new Velocity();
            Object o = controlMessage.getControl("l.x");
            if (o instanceof Integer) {
                vl.setX((Integer) o);
            }

            o = controlMessage.getControl("l.y");
            if (o instanceof Integer) {
                vl.setY((Integer) o);
            }

            o = controlMessage.getControl("l.z");
            if (o instanceof Integer) {
                vl.setZ((Integer) o);
            }

            Velocity va = new Velocity();
            o = controlMessage.getControl("a.x");
            if (o instanceof Integer) {
                va.setX((Integer) o);
            }

            o = controlMessage.getControl("a.y");
            if (o instanceof Integer) {
                va.setY((Integer) o);
            }

            o = controlMessage.getControl("a.z");
            if (o instanceof Integer) {
                va.setZ((Integer) o);
            }
            turtle.setLinear(vl);
            turtle.setAngular(va);
        }
    }
}
