package cgl.iotcloud.core.client;

import java.util.UUID;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSListenerFactory;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.broker.JMSSenderFactory;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.update.MessageToUpdateFactory;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.stream.StreamingListener;
import cgl.iotcloud.core.stream.StreamingListenerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a class used for receiving messages and controlling the sensors.
 */
public class Client implements ManagedLifeCycle {
    private static Logger log = LoggerFactory.getLogger(Client.class);

    /** Message listener for sensor data */
    private Listener messageListener = null;
    /** Message listener for sensor data */
    private Listener publicMessageListener = null;
    /** Message sender for control data */
    private JMSSender controlSender = null;
    /** Message Listener for update data */
    private JMSListener updateLister;
    /** Message handler specified by the user */
    private MessageHandler handler = null;
    /** Message handler specified by the user */
    private MessageHandler publicMessagehandler = null;
    /** Update handler specified by the user */
    private MessageHandler updateHandler = null;
    /** Sensor used by the client */
    private SCSensor sensor = null;
    private String clientId ;

    public Client(SCSensor sensor) {
        this.sensor = sensor;
        this.clientId = UUID.randomUUID().toString();
    }

    public void setMessageHandler(MessageHandler handler, MessageHandler publicMessagehandler) {
        this.handler = handler;
        this.publicMessagehandler = publicMessagehandler;
    }

    public void setUpdateMessageHandler(MessageHandler handler) {
        this.updateHandler = handler;
    }

    public void init() {
        JMSSenderFactory senderFactory = new JMSSenderFactory();
        //controlSender = senderFactory.createControlSender(sensor.getControlEndpoint());
        controlSender = senderFactory.createControlSender(clientId,sensor.getControlEndpoint());
        if (log.isDebugEnabled()) {
            log.debug("Initializing the Sender..");
        }
        controlSender.init();
        if (log.isDebugEnabled()) {
            log.debug("Starting the Sender..");
        }
        controlSender.start();

        JMSListenerFactory listenerFactory = new JMSListenerFactory();
        // Creating a Public Listener
        //publicMessageListener = listenerFactory.create(sensor.getPublicEndpoint(), publicMessagehandler);
        publicMessageListener = listenerFactory.create(clientId,sensor.getPublicEndpoint(), publicMessagehandler);
        
        if (sensor.getType().equals(Constants.SENSOR_TYPE_BLOCK)) {
            //messageListener = listenerFactory.create(sensor.getDataEndpoint(), handler);
            messageListener = listenerFactory.create(clientId,sensor.getDataEndpoint(), handler);
        } else {
            messageListener = new StreamingListenerFactory().create(sensor.getDataEndpoint());
            if (messageListener instanceof StreamingListener) {
                ((StreamingListener) messageListener).setMessageHandler(handler);
            }
        }

        if (sensor.getUpdateEndpoint() != null && updateHandler != null) {
            //updateLister = listenerFactory.create(sensor.getUpdateEndpoint(), updateHandler);
            updateLister = listenerFactory.create(clientId,sensor.getUpdateEndpoint(), updateHandler);
            updateLister.setMessageFactory(new MessageToUpdateFactory());

            updateLister.init();
            updateLister.start();
        }

        if (log.isDebugEnabled()) {
            log.debug("Initializing the Listener..");
        }
        messageListener.init();
        if (log.isDebugEnabled()) {
            log.debug("Starting the Listener..");
        }
        messageListener.start();
        
        publicMessageListener.init();
        publicMessageListener.start();
    }

    public void destroy() {
        controlSender.stop();
        controlSender.destroy();

        messageListener.stop();
        messageListener.destroy();
        
        publicMessageListener.stop();
        publicMessageListener.destroy();

        updateLister.stop();
        updateLister.destroy();
    }

    public void sendMessage(SensorMessage message) {
        controlSender.send(message);
    }
}
