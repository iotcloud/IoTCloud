package cgl.iotcloud.samples.arducopter.sensor;

import cgl.iotcloud.samples.arducopter.mssg.*;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import roscopter.Control;
import roscopter.RC;

public class RosArducopter extends AbstractNodeMain {

    private ArduSensor sensor;

    private ArduCopterController controller;

    private boolean active;

    public void setSensor(ArduSensor sensor) {
        this.sensor = sensor;
        this.controller = new ArduCopterController();
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("/ardu_controller_node");
    }

    public ArduCopterController getController() {
        return controller;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        final Subscriber<roscopter.Attitude> rosCopterAttitudeSubscriber = connectedNode.newSubscriber("/attitude", roscopter.Attitude._TYPE);
        final Subscriber<roscopter.State> rosCopterStateSubscriber = connectedNode.newSubscriber("/state", roscopter.State._TYPE);
        final Subscriber<roscopter.Mavlink_RAW_IMU> rosCopterMRISubscriber = connectedNode.newSubscriber("/raw_imu", roscopter.Mavlink_RAW_IMU._TYPE);
        final Subscriber<roscopter.VFR_HUD> rosCopterVHSubscriber = connectedNode.newSubscriber("/vfr_hud", roscopter.VFR_HUD._TYPE);
        final Subscriber<roscopter.RC> rosCopterRCSubscriber = connectedNode.newSubscriber("/rc", roscopter.RC._TYPE);
        final Subscriber<roscopter.Control> rosCopterControlSubscriber = connectedNode.newSubscriber("/roscopter/Control", roscopter.Control._TYPE);

        final Publisher<RC> rcPublisher =
                connectedNode.newPublisher("/send_rc", RC._TYPE);
        final Publisher<Control> controlPublisher =
                connectedNode.newPublisher("/control", Control._TYPE);

        rosCopterAttitudeSubscriber.addMessageListener(new MessageListener<roscopter.Attitude>() {
            @Override
            public void onNewMessage(roscopter.Attitude message) {
                // TEST
                System.out.println("Attitude Message --> " + "Pitch=" + message.getPitch() + ",Pitch_Speed=" + message.getPitchspeed() + ",Roll=" + message.getRoll()
                        + ",Roll_Speed=" + message.getRollspeed() + ",Yaw=" + message.getYaw() + ",Yaw_Speed=" + message.getYawspeed());
                // TODO: Publish Attitude Message to Native(IoT) Network
                AttitudeMessage attitudeMessage = new AttitudeMessage();
                attitudeMessage.setPitch(message.getPitch());
                attitudeMessage.setPitchSpeed(message.getPitchspeed());
                attitudeMessage.setRoll(message.getRoll());
                attitudeMessage.setRollSpeed(message.getRollspeed());
                attitudeMessage.setYaw(message.getYaw());
                attitudeMessage.setYawSpeed(message.getYawspeed());

                sensor.getAttitudeSender().send(attitudeMessage);
            }
        });

        rosCopterStateSubscriber.addMessageListener(new MessageListener<roscopter.State>() {
            @Override
            public void onNewMessage(roscopter.State message) {
                // TEST
                System.out.println("State Message --> " + "Mode=" + message.getMode() + ",Armed=" + message.getArmed() + ",Guided=" + message.getGuided());
                // TODO: Publish State Message to Native(IoT) Network
                StateMessage stateMessage = new StateMessage();
                stateMessage.setMode(message.getMode());
                stateMessage.setArmed(message.getArmed());
                stateMessage.setGuided(message.getGuided());

                sensor.getStateSender().send(stateMessage);
            }
        });

        rosCopterMRISubscriber.addMessageListener(new MessageListener<roscopter.Mavlink_RAW_IMU>() {
            @Override
            public void onNewMessage(roscopter.Mavlink_RAW_IMU message) {
                // TEST
                System.out.println("Mavlink_RAW_IMU Message --> " + "Time=" + message.getTimeUsec() + ",Xacc=" + message.getXacc() + ",Xgyro=" + message.getXgyro()
                        + ",Xmag=" + message.getXmag() + ",etc");
                // TODO: Publish Mavlink_RAW_IMU Message to Native(IoT) Network
                MRIMessage mriMessage = new MRIMessage();
                mriMessage.setTimeUsec(message.getTimeUsec());
                mriMessage.setXacc(message.getXacc());
                mriMessage.setXgyro(message.getXgyro());
                mriMessage.setXmag(message.getXmag());
                mriMessage.setYacc(message.getYacc());
                mriMessage.setYgyro(message.getYgyro());
                mriMessage.setYmag(message.getYmag());
                mriMessage.setZacc(message.getZacc());
                mriMessage.setZgyro(message.getZgyro());
                mriMessage.setZmag(message.getZmag());

                sensor.getMriSender().send(mriMessage);
            }
        });

        rosCopterVHSubscriber.addMessageListener(new MessageListener<roscopter.VFR_HUD>() {
            @Override
            public void onNewMessage(roscopter.VFR_HUD message) {
                // TEST
                System.out.println("VFR_HUD Message --> " + "AirSpeed=" + message.getAirspeed() + ",Altitude=" + message.getAlt() + ",Climb=" + message.getClimb()
                        + ",GroundSpeed=" + message.getGroundspeed() + ",Heading=" + message.getHeading() + ",Throttle=" + message.getThrottle());
                // TODO: Publish VFR_HUD Message to Native(IoT) Network
                VHMessage vhMessage = new VHMessage();
                vhMessage.setAirSpeed(message.getAirspeed());
                vhMessage.setAlt(message.getAlt());
                vhMessage.setClimb(message.getClimb());
                vhMessage.setGroundSpeed(message.getGroundspeed());
                vhMessage.setHeading(message.getHeading());
                vhMessage.setThrottle(message.getThrottle());

                sensor.getVhSender().send(vhMessage);
            }
        });

        rosCopterRCSubscriber.addMessageListener(new MessageListener<roscopter.RC>() {
            @Override
            public void onNewMessage(roscopter.RC message) {
                // TEST
                System.out.println("RC Message --> " + "Channel=" + message.getChannel());
                // TODO: Publish RC Message to Native(IoT) Network
                RCMessage rcMessage = new RCMessage();
                rcMessage.setChannel(message.getChannel());

                sensor.getRcSender().send(rcMessage);
            }
        });

        rosCopterControlSubscriber.addMessageListener(new MessageListener<roscopter.Control>() {
            @Override
            public void onNewMessage(roscopter.Control message) {
                // TEST
                System.out.println("Control Message --> " + "Pitch=" + message.getPitch() + ",Roll=" + message.getRoll() + ",Thrust=" + message.getThrust()
                        + ",Yaw=" + message.getYaw());
                // TODO: Publish Control Message to Native(IoT) Network
                ControlMessage controlMessage = new ControlMessage();
                controlMessage.setPitch(message.getPitch());
                controlMessage.setRoll(message.getRoll());
                controlMessage.setThrust(message.getThrust());
                controlMessage.setYaw(message.getYaw());

                sensor.getControlSender().send(controlMessage);
            }
        });

        connectedNode.executeCancellableLoop(new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                RC rc = rcPublisher.newMessage();
                rc.setChannel(new int[]{1001, 1001, 1001, 1001, 1001, 1001, 1001, 1001});
                rcPublisher.publish(rc);

                Control control = controlPublisher.newMessage();

                control.setPitch((float) controller.getLeftX());
                control.setRoll((float) controller.getLeftX());
                control.setPitch((float) controller.getLeftX());
                control.setRoll((float) controller.getLeftX());

                controlPublisher.publish(control);

                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {
                }
            }
        });
    }
}
