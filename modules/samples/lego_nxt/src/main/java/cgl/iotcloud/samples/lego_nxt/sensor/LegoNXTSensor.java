package cgl.iotcloud.samples.lego_nxt.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.message.data.MapDataMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.sensor.AbstractSensor;
import cgl.iotcloud.samples.lego_nxt.common.LegoNXTSensorTypes;
import cgl.iotcloud.sensors.SensorAdaptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nxt_msgs.Contact;
import nxt_msgs.Gyro;
import nxt_msgs.Range;

public class LegoNXTSensor extends AbstractSensor implements RosLegoNXTListener {
	private RosLegoNXT legoNXT ;
	private static LegoNXTSensor sensor ;
	private boolean isSensorStarted;

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
		isSensorStarted = true;
	}

	public static void main(String[] argv) throws Exception {
		// register with ros_java
		List<String> args = new ArrayList();
		args.add("lego_nxt_sensor");
		// CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(argv));
		CommandLineLoader loader = new CommandLineLoader(args);

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
	public void onRosSensorMessage(Object msg) {
		//System.out.println("== Enter onRosMessage ==");

		if(msg instanceof Contact){
			//System.out.println("==Enter handle contact message ==");

			TextDataMessage txtDataMsg = new TextDataMessage();
			txtDataMsg.setText(LegoNXTSensorTypes.TOUCH_SENSOR+":"+((Contact)msg).getContact());

			if(sensor != null && isSensorStarted)
				sensor.sendMessage(txtDataMsg);
		}
		
		if(msg instanceof Range){
			//System.out.println("==Enter handle range message ==");

			TextDataMessage txtDataMsg = new TextDataMessage();
			
			Range rangeMsg = (Range)msg;
			double range = rangeMsg.getRange();
			double rangeMin = rangeMsg.getRangeMin();
			double rangeMax = rangeMsg.getRangeMax();
			
			String sensorMsg =  "range : "+range+" ,range max : "+rangeMax+" ,range min : "+rangeMin;
			txtDataMsg.setText(LegoNXTSensorTypes.ULTRASONIC_SENSOR+":"+sensorMsg);

			if(sensor != null && isSensorStarted)
				sensor.sendMessage(txtDataMsg);
		}
		
		if(msg instanceof Gyro){
			//System.out.println("==Enter handle gyro message ==");

			TextDataMessage txtDataMsg = new TextDataMessage();
			
			double calibX = ((Gyro) msg).getCalibrationOffset().getX();
			double calibY = ((Gyro) msg).getCalibrationOffset().getY();
			double calibZ = ((Gyro) msg).getCalibrationOffset().getZ();
			
			String calibration = "[ claibration_offset : (x : "+calibX+" ,y : "+calibY+" ,z : "+calibZ+")";
			double angVelocityX = ((Gyro) msg).getAngularVelocity().getX();
			double angVelocityY = ((Gyro) msg).getAngularVelocity().getY();
			double angVelocityZ = ((Gyro) msg).getAngularVelocity().getZ();
			
			String angular_velocity = ", angular_velocity :(x : "+angVelocityX+" ,y : "+angVelocityY+" ,z : "+angVelocityZ+")";
			
			double[] covarianceVals = ((Gyro) msg).getAngularVelocityCovariance();
			String covariance = " ,covariance : (";
			for(double covarianceVal:covarianceVals){
				covariance += ", "+covarianceVal;
			}
			covariance+=" ) ]";
			
			String sensorMsg =  calibration+angular_velocity+covariance;
			txtDataMsg.setText(LegoNXTSensorTypes.GYRO_SENSOR+":"+sensorMsg);

			if(sensor != null && isSensorStarted)
				sensor.sendMessage(txtDataMsg);
		}
	}

}