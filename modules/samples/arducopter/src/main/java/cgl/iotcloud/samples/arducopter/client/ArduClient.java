package cgl.iotcloud.samples.arducopter.client;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.samples.arducopter.mssg.*;
import cgl.iotcloud.sensors.NodeClient;

public class ArduClient {

    private Updater updater;

    private NodeClient nodeClient = null;

    private Listener attitudeListener;
    private Listener stateListener;
    private Listener mriListener;
    private Listener vhListener;
    private Listener rcListener;
    private Listener controlListener;

    private Sender controlsSender;

    public ArduClient() {
        try {
            nodeClient = new NodeClient("http://localhost:8080/");
        } catch (IOTException e) {
            e.printStackTrace();
        }
    }

    public void setUpdater(Updater updater) {
        this.updater = updater;
    }

    public void start() {
        try {
            NodeInformation nodeInformation = nodeClient.getNode(new NodeName("ardusensor"));

            Endpoint endpoint = nodeInformation.getProducer("attitudeData");
            attitudeListener = nodeClient.newListener(endpoint);
            if (attitudeListener instanceof JMSListener) {
                ((JMSListener) attitudeListener).setMessageFactory(new JMSDataMessageFactory());
            }

            attitudeListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    handleAttitudeMessage(message);
                }
            });

            attitudeListener.init();
            attitudeListener.start();

            endpoint = nodeInformation.getProducer("stateData");
            stateListener = nodeClient.newListener(endpoint);
            if (stateListener instanceof JMSListener) {
                ((JMSListener) stateListener).setMessageFactory(new JMSDataMessageFactory());
            }

            stateListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    handleStateMessage(message);
                }
            });

            stateListener.init();
            stateListener.start();

            endpoint = nodeInformation.getProducer("mriData");
            mriListener = nodeClient.newListener(endpoint);
            if (mriListener instanceof JMSListener) {
                ((JMSListener) mriListener).setMessageFactory(new JMSDataMessageFactory());
            }

            mriListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    handleMRIMessage(message);
                }
            });

            mriListener.init();
            mriListener.start();

            endpoint = nodeInformation.getProducer("vhData");
            vhListener = nodeClient.newListener(endpoint);
            if (vhListener instanceof JMSListener) {
                ((JMSListener) vhListener).setMessageFactory(new JMSDataMessageFactory());
            }

            vhListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    handleVHMessage(message);
                }
            });

            vhListener.init();
            vhListener.start();

            endpoint = nodeInformation.getProducer("rcData");
            rcListener = nodeClient.newListener(endpoint);
            if (rcListener instanceof JMSListener) {
                ((JMSListener) rcListener).setMessageFactory(new JMSDataMessageFactory());
            }

            rcListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    handleRCMessage(message);
                }
            });

            rcListener.init();
            rcListener.start();

            endpoint = nodeInformation.getProducer("controlData");
            controlListener = nodeClient.newListener(endpoint);
            if (controlListener instanceof JMSListener) {
                ((JMSListener) controlListener).setMessageFactory(new JMSDataMessageFactory());
            }

            controlListener.setMessageHandler(new MessageHandler() {
                @Override
                public void onMessage(SensorMessage message) {
                    handleControlMessage(message);
                }
            });

            controlListener.init();
            controlListener.start();

            endpoint = nodeInformation.getConsumer("controls");
            controlsSender = nodeClient.newSender(endpoint);
            if (controlsSender instanceof JMSSender) {
                ((JMSSender) controlsSender).setMessageFactory(new JMSDataMessageFactory());
            }

            controlsSender.init();
            controlsSender.start();
        } catch (IOTException e) {
            e.printStackTrace();
        }
    }

    private void handleAttitudeMessage(SensorMessage message) {
        if (message instanceof AttitudeMessage) {
            AttitudeMessage attitudeMessage = (AttitudeMessage) message;
            updater.updateAttitudeData(attitudeMessage.getPitch(),
                    attitudeMessage.getPitchSpeed(),
                    attitudeMessage.getRoll(),
                    attitudeMessage.getRollSpeed(),
                    attitudeMessage.getYaw(),
                    attitudeMessage.getYawSpeed());
        }
    }

    private void handleStateMessage(SensorMessage message) {
        if (message instanceof StateMessage) {
            StateMessage stateMessage = (StateMessage) message;
            updater.updateStateData(stateMessage.getMode(),
                    stateMessage.isArmed(),
                    stateMessage.isGuided());
        }
    }

    private void handleMRIMessage(SensorMessage message) {
        if (message instanceof MRIMessage) {
            MRIMessage mriMessage = (MRIMessage) message;
            updater.updateMRIData(mriMessage.getTimeUsec(),
                    mriMessage.getXacc(),
                    mriMessage.getXgyro(),
                    mriMessage.getXmag(),
                    mriMessage.getYacc(),
                    mriMessage.getYgyro(),
                    mriMessage.getYmag(),
                    mriMessage.getZacc(),
                    mriMessage.getZgyro(),
                    mriMessage.getZmag());
        }
    }

    private void handleVHMessage(SensorMessage message) {
        if (message instanceof VHMessage) {
            VHMessage vhMessage = (VHMessage) message;
            updater.updateVHData(vhMessage.getAirSpeed(),
                    vhMessage.getAlt(),
                    vhMessage.getClimb(),
                    vhMessage.getGroundSpeed(),
                    vhMessage.getHeading(),
                    vhMessage.getThrottle());
        }
    }

    private void handleRCMessage(SensorMessage message) {
        if (message instanceof RCMessage) {
        }
    }

    private void handleControlMessage(SensorMessage message) {
        if (message instanceof ControlMessage) {
            ControlMessage controlMessage = (ControlMessage) message;
            updater.updateControlData(controlMessage.getPitch(),
                    controlMessage.getRoll(),
                    controlMessage.getThrust(),
                    controlMessage.getYaw());
        }
    }

    public Sender getControlsSender() {
        return controlsSender;
    }
}
