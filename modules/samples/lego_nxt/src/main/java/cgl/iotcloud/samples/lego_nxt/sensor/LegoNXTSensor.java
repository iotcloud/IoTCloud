package cgl.iotcloud.samples.lego_nxt.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.message.data.MapDataMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;
import cgl.iotcloud.sensors.SensorAdaptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.util.Set;

public class LegoNXTSensor extends AbstractSensor implements RosListener {
    private RosLegoNXT legoNXT = null;

    public TurtleSensor(String type, String name) {
        super(type, name);

        legoNXT = new RosLegoNXTMover();
    }

    public void start(NodeConfiguration nodeConfiguration) {
        Preconditions.checkState(legoNXT != null);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(legoNXT, nodeConfiguration);

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
            legoNXT.setLinear(vl);
            legoNXT.setAngular(va);
        }
    }
    
    public void onRosMessage(Object obj){
    	if(msg instanceof Contact){
    		MapDataMessage msg = new MapDataMessage();
    		msg.put("contact", ((Contact)obj).getContact());
    		
    		sendMessage(msg);
    	}
    }
}