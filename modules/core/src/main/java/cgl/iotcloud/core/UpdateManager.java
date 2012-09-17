package cgl.iotcloud.core;

import cgl.iotcloud.core.broker.JMSListener;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.broker.JMSListenerFactory;
import cgl.iotcloud.core.broker.JMSSenderFactory;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.update.MessageToUpdateFactory;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.sensor.SCSensor;
import com.iotcloud.message.xsd.Sensor;
import com.iotcloud.message.xsd.UpdateDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for delivering sensor grid updates to the clients. It will listen on a
 * global channel for sensor updates. Then forwards these updates to the relevant topics.
 */
public class UpdateManager implements ManagedLifeCycle {
    private static Logger log = LoggerFactory.getLogger(UpdateManager.class);
    /** Endpoints where updates are received from the sensors */
    private Endpoint receivingEndpoint = null;
    /** Global updates are sent to this endpoint */
    private Endpoint sendingEndpoint = null;
    /** Actual sender for sending global updates */
    private JMSSender sender = null;
    /** Listener for listening to sensor updates */
    private JMSListener listener = null;
    /** The Sensor Cloud configuration */
    private SCConfiguration configuration = null;
    /** The Sensor cloud catalog */
    private SensorCatalog catalog = null;
    /** Map of senders to send update messages */
    private Map<String, JMSSender> senders = new HashMap<String, JMSSender>();

    private HeartBeatListener heartBeatListener;

    public UpdateManager(SCConfiguration configuration, SensorCatalog catalog, IoTCloud ioTCloud) {
        this.configuration = configuration;
        this.catalog = catalog;

        this.heartBeatListener = new HeartBeatListener(catalog, ioTCloud);
    }

    public Endpoint getReceivingEndpoint() {
        return receivingEndpoint;
    }

    public Endpoint getSendingEndpoint() {
        return sendingEndpoint;
    }

    /**
     * Initialize the update manager. This will create message senders and listeners.
     */
    public void init() {
        receivingEndpoint = new JMSEndpoint();
        receivingEndpoint.setAddress("update/receive");
        //receivingEndpoint.setProperties(configuration.getBroker().getConnections("topic").getParameters());
        receivingEndpoint.setProperties(configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());
        
        sendingEndpoint = new JMSEndpoint();
        sendingEndpoint.setAddress("update/send");
        //sendingEndpoint.setProperties(configuration.getBroker().getConnections("topic").getParameters());
        sendingEndpoint.setProperties(configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());

        sender = new JMSSenderFactory().create(sendingEndpoint);

        listener = new JMSListenerFactory().create(receivingEndpoint, new UpdateReceiver());
        listener.setMessageFactory(new MessageToUpdateFactory());

        sender.setMessageFactory(new MessageToUpdateFactory());
        sender.init();
        sender.start();

        listener.init();
        listener.start();

        heartBeatListener.init();
    }
    
    /**
     * Destroy the update manager. This will stop and clean the senders and listeners.
     */
    public void destroy() {
        sender.stop();
        sender.destroy();

        listener.stop();
        listener.destroy();

        heartBeatListener.destroy();
    }

    private class UpdateReceiver implements MessageHandler {
        public void onMessage(SensorMessage message) {
            // check weather it is a heartbeat message
            if (!heartBeatListener.onUpdateMessage(message)) {
                sendToSensor(message);
            }
        }
    }

    private void sendToSensor(SensorMessage message) {
        String id;
        if (message instanceof UpdateMessage) {
            id = ((UpdateMessage) message).getSensorId();
        } else {
            UpdateDocument document;
            try {
                if (message instanceof TextDataMessage) {
                    document = UpdateDocument.Factory.parse(((TextDataMessage) message).getText());
                } else {
                    handleException("Un-expected message type received");
                    return;
                }
            } catch (Exception e) {
                handleException("Error parsing the update message", e);
                return;
            }

            UpdateDocument.Update update = document.getUpdate();
            Sensor sensor = update.getSensor();
            if (sensor == null) {
                handleException("Sensor infoUpdateToMessageFactoryrmation should be present in the update message");
                return;
            }
            id = update.getSensor().getId();
        }

        JMSSender sender = senders.get(id);
        if (sender == null) {
            handleException("Received update for unregistered sensor: " + id);
        } else {
            // just forward the message as it is
            sender.send(message);
        }
    }

    public void sensorChange(String change, String id) {
        if (Constants.Updates.ADDED.equals(change)) {
            handleSensorAdded(id);
        } else if (Constants.Updates.REMOVED.equals(change)) {
            handleSensorRemoved(id);
        } else {
            handleException("Unknown change: " + change);
        }
    }

    private void handleSensorAdded(String id) {
        SCSensor sensor = catalog.getSensor(id);
        heartBeatListener.addSensor(id);
        if (sensor != null) {
            Endpoint endpoint = sensor.getUpdateEndpoint();
            JMSSender sender = new JMSSenderFactory().create(endpoint);
            sender.setMessageFactory(new MessageToUpdateFactory());

            sender.init();
            sender.start();
            senders.put(id, sender);
        } else {
            handleException("Sensor cannot be found in the catalog id: " + id);
        }
    }

    private void handleSensorRemoved(String id) {
        heartBeatListener.removeSensor(id);

        JMSSender sender = senders.get(id);
        if (sender != null) {
            // send the update message to the listeners
            sender.send(createUpdateMessage(Constants.Updates.REMOVED, id));
            // stop the senders
            sender.stop();
            sender.destroy();

            // remove the sensor from the map
            senders.remove(id);
        } else {
            handleException("Sensor with the id: " + id + " cannot be found in " +
                    "the system to be removed");
        }
    }

    private SensorMessage createUpdateMessage(String change, String id) {
        UpdateMessage updateMessage = new UpdateMessage(id);
        updateMessage.addUpdate(Constants.Updates.STATUS, change);
        return updateMessage;
    }

    private void handleException(String s, Exception e) {
        log.error(s, e);
        throw new IOTRuntimeException(s, e);
    }

    private void handleException(String s) {
        log.error(s);
        throw new IOTRuntimeException(s);
    }
}
