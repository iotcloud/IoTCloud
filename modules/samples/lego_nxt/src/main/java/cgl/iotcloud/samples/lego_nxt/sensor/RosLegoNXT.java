package cgl.iotcloud.samples.lego_nxt.sensor;

import geometry_msgs.Twist;
import nxt_msgs.Contact;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

public class RosLegoNXT extends AbstractNodeMain {
    private volatile Velocity linear = null;
    private volatile Velocity angular = null;
    private RosLegoNXTListener rosListener;

    public void setLinear(Velocity linear) {
        this.linear = linear;
    }

    public void setAngular(Velocity angular) {
        this.angular = angular;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("/LegoNXT");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final Publisher<Twist> publisher =
                connectedNode.newPublisher("cmd_vel", Twist._TYPE);
        
        final Subscriber<Contact> contactSubscriber  = connectedNode.newSubscriber("m_touch_sensor",Contact._TYPE);
        
        contactSubscriber.addMessageListener(new MessageListener<Contact>(){
        	
        	public void onNewMessage(Contact msg){
        		if(rosListener != null)
        			rosListener.onRosSensorMessage(msg);
        	}
        });
        
    }

    public static void main(String[] args) {
        RosLegoNXT legoNXT = new RosLegoNXT();

        while (true) {
        	legoNXT.setLinear(new Velocity(.1, 0, 0));
        }
    }

	public void registerListener(RosLegoNXTListener listener) {
		rosListener = listener;
	}
} 
