package cgl.iotcloud.samples.arducopter.client;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.jms.JMSControlMessageFactory;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.samples.arducopter.mssg.*;
import cgl.iotcloud.sensors.NodeClient;

public class ArduClient {

    private ArduUI arduUI;

    private NodeClient nodeClient = null;

    private Listener attitudeListener;
    private Listener stateListener;
    private Listener mriListener;
    private Listener vhListener;
    private Listener rcListener;
    private Listener controlListener;

    public ArduClient(ArduUI arduUI){
        this.arduUI = arduUI;
        try {
            nodeClient = new NodeClient("http://localhost:8080/");
        } catch (IOTException e) {
            e.printStackTrace();
        }
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

        } catch (IOTException e) {
            e.printStackTrace();
        }
    }

    private void handleAttitudeMessage(SensorMessage message)
    {
        if (message instanceof AttitudeMessage)
        {
            arduUI.updateAttitudeMessage(message);
        }
    }

    private void handleStateMessage(SensorMessage message)
    {
        if (message instanceof StateMessage)
        {
            arduUI.updateStateMessage(message);
        }
    }

    private void handleMRIMessage(SensorMessage message)
    {
        if (message instanceof MRIMessage)
        {
            arduUI.updateMRIMessage(message);
        }
    }

    private void handleVHMessage(SensorMessage message)
    {
        if (message instanceof VHMessage)
        {
            arduUI.updateVHMessage(message);
        }
    }

    private void handleRCMessage(SensorMessage message)
    {
        if (message instanceof RCMessage)
        {
            // TODO: Push the Message to the UI/Client
        }
    }

    private void handleControlMessage(SensorMessage message)
    {
        if (message instanceof ControlMessage)
        {
            arduUI.updateControlMessage(message);
        }
    }
}
