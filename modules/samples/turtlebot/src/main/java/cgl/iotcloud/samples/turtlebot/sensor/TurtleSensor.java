package cgl.iotcloud.samples.turtlebot.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;
import cgl.iotcloud.sensors.SensorAdaptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.ros.exception.RosRuntimeException;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

public class TurtleSensor extends AbstractSensor {
    public TurtleSensor(String type, String name) {
        super(type, name);
    }

    public static void main(String[] argv) throws Exception {
        // register with ros_java
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(argv));

        final String nodeClassName = "cgl.iotcloud.samples.turtlebot.sensor.RosTurtle";
        NodeConfiguration nodeConfiguration = loader.build();

        NodeMain nodeMain = null;
        try {
            nodeMain = loader.loadClass(nodeClassName);
        } catch (ClassNotFoundException e) {
            throw new RosRuntimeException("Unable to locate node: " + nodeClassName, e);
        } catch (InstantiationException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        } catch (IllegalAccessException e) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, e);
        }

        Preconditions.checkState(nodeMain != null);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(nodeMain, nodeConfiguration);

        // register the sensor itself
        SensorAdaptor adaptor = new SensorAdaptor("http://localhost:8080");

        TurtleSensor sensor = new TurtleSensor(Constants.SENSOR_TYPE_BLOCK, "turtle-sensor");
        adaptor.registerSensor(sensor);

        adaptor.start();
    }

    @Override
    public void onControlMessage(SensorMessage message) {
        if (message instanceof DefaultControlMessage) {
            DefaultControlMessage controlMessage = (DefaultControlMessage) message;


        }
    }
}
