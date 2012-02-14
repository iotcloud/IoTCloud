package cgl.iotcloud.core.client;

import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.broker.Listener;
import cgl.iotcloud.core.broker.ListenerFactory;
import cgl.iotcloud.core.broker.Sender;
import cgl.iotcloud.core.broker.SenderFactory;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.sensor.SCSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a class used for receiving messages and controlling the sensors.
 */
public class Client implements ManagedLifeCycle {
    private static Logger log = LoggerFactory.getLogger(Client.class);

    /** Message listener for sensor data */
    private Listener messageListener = null;
    /** Message sender for control data */
    private Sender controlSender = null;
    /** Message Listener for update data */
    private Listener updateLister;
    /** Message handler specified by the user */
    private MessageHandler handler = null;
    /** Update handler specified by the user */
    private MessageHandler updateHandler = null;
    /** Sensor used by the client */
    private SCSensor sensor = null;

    public Client(SCSensor sensor) {
        this.sensor = sensor;
    }

    public void setMessageHandler(MessageHandler handler) {
        this.handler = handler;
    }

    public void setUpdateMessageHandler(MessageHandler handler) {
        this.updateHandler = handler;
    }

    public void init() {
        SenderFactory senderFactory = new SenderFactory();
        controlSender = senderFactory.createControlSender(sensor.getControlEndpoint());

        if (log.isDebugEnabled()) {
            log.debug("Initializing the Sender..");
        }
        controlSender.init();
        if (log.isDebugEnabled()) {
            log.debug("Starting the Sender..");
        }
        controlSender.start();

        ListenerFactory listenerFactory = new ListenerFactory();
        messageListener = listenerFactory.create(sensor.getDataEndpoint(), handler);

        if (sensor.getUpdateEndpoint() != null && updateHandler != null) {
            updateLister = listenerFactory.create(sensor.getUpdateEndpoint(), updateHandler);
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
    }

    public void destroy() {
        controlSender.stop();
        controlSender.destroy();

        messageListener.stop();
        messageListener.destroy();

        updateLister.stop();
        updateLister.destroy();
    }

    public void sendMessage(SensorMessage message) {
        controlSender.send(message);
    }
}
