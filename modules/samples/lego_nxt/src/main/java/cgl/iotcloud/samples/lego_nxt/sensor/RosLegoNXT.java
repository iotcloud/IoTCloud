package cgl.iotcloud.samples.lego_nxt.sensor;

import geometry_msgs.Twist;
import nxt_msgs.Contact;
import nxt_msgs.Range;
import nxt_msgs.Gyro;
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

		// touch sensor
		final Subscriber<Contact> contactSubscriber  = connectedNode.newSubscriber("touch_sensor",Contact._TYPE);
		contactSubscriber.addMessageListener(new MessageListener<Contact>(){

			public void onNewMessage(Contact msg){
				if(rosListener != null)
					rosListener.onRosSensorMessage(msg);
			}
		});

		//ultrasonic sensor
		final Subscriber<Range> ultrasonicSubscriber  = connectedNode.newSubscriber("ultrasonic_sensor",Range._TYPE);
		ultrasonicSubscriber.addMessageListener(new MessageListener<Range>(){

			public void onNewMessage(Range msg){
				if(rosListener != null)
					rosListener.onRosSensorMessage(msg);
			}
		});

		//gyro sensor
		final Subscriber<Gyro> gyroSubscriber  = connectedNode.newSubscriber("gyro_sensor",Gyro._TYPE);
		gyroSubscriber.addMessageListener(new MessageListener<Gyro>(){

			public void onNewMessage(Gyro msg){
				if(rosListener != null)
					rosListener.onRosSensorMessage(msg);
			}
		});

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
