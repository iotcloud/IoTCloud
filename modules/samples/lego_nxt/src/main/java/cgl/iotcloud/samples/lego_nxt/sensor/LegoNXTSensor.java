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
import nxt_msgs.Contact;

public class LegoNXTSensor extends AbstractSensor implements RosLegoNXTListener {
    private RosLegoNXT legoNXT ;
    private static LegoNXTSensor sensor ;

    public LegoNXTSensor(String type, String name) {
        super(type, name);

        legoNXT = new RosLegoNXT();
        legoNXT.registerListener(this);
    }

    public void start(NodeConfiguration nodeConfiguration) {
        Preconditions.checkState(legoNXT != null);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(legoNXT, nodeConfiguration);

        // register the sensor itself
        SensorAdaptor adaptor = new SensorAdaptor("http://localhost:8080");
        adaptor.registerSensor(sensor);

        adaptor.start();
    }

    public static void main(String[] argv) throws Exception {
        // register with ros_java
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(argv));

        NodeConfiguration nodeConfiguration = loader.build();

        sensor = new LegoNXTSensor(Constants.SENSOR_TYPE_BLOCK, "lego-nxt-sensor");
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
    
    @Override
    public void onRosMessage(Object obj){
    	
    	if(obj instanceof Contact){
    		MapDataMessage msg = new MapDataMessage();
    		msg.put("contact", ((Contact)obj).getContact());
    		
    		if(msg == null)
    			System.out.println("msg is null ===");
    		else
    			System.out.println("msg is not null ===");
    		
    		if(sensor == null)
    			System.out.println("sensor is null");
    		sensor.sendMessage(msg);
    	}
    }
}