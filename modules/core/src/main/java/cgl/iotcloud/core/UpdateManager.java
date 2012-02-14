package cgl.iotcloud.core;

import cgl.iotcloud.core.broker.Listener;
import cgl.iotcloud.core.broker.ListenerFactory;
import cgl.iotcloud.core.broker.Sender;
import cgl.iotcloud.core.broker.SenderFactory;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.message.update.UpdateToMessageFactory;
import cgl.iotcloud.core.sensor.SCSensor;
import com.iotCloud.message.xsd.Sensor;
import com.iotCloud.message.xsd.UpdateDocument;
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
    private Sender sender = null;
    /** Listener for listening to sensor updates */
    private Listener listener = null;
    /** The Sensor Cloud configuration */
    private SCConfiguration configuration = null;
    /** The Sensor cloud catalog */
    private SensorCatalog catalog = null;
    /** Map of senders to send update messages */
    private Map<String, Sender> senders = new HashMap<String, Sender>();

    public UpdateManager(SCConfiguration configuration, SensorCatalog catalog) {
        this.configuration = configuration;
        this.catalog = catalog;
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
        receivingEndpoint.setProperties(configuration.getBroker().getConnections("topic").getParameters());

        sendingEndpoint = new JMSEndpoint();
        sendingEndpoint.setAddress("update/send");
        sendingEndpoint.setProperties(configuration.getBroker().getConnections("topic").getParameters());

        sender = new SenderFactory().create(sendingEndpoint);

        listener = new ListenerFactory().create(receivingEndpoint, new UpdateReceiver());

        sender.init();
        sender.start();

        listener.init();
        listener.start();
    }

    /**
     * Destroy the update manager. This will stop and clean the senders and listeners.
     */
    public void destroy() {
        sender.stop();
        sender.destroy();

        listener.stop();
        listener.destroy();
    }

    private class UpdateReceiver implements MessageHandler {
        public void onMessage(SensorMessage message) {
            sendToSensor(message);
        }
    }

    private void sendToSensor(SensorMessage message) {
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
            handleException("Sensor information should be present in the update message");
            return;
        }
        String id = update.getSensor().getId();
        Sender sender = senders.get(id);
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
        if (sensor != null) {
            Endpoint endpoint = sensor.getUpdateEndpoint();
            Sender sender = new SenderFactory().create(endpoint);
            sender.init();
            sender.start();
            senders.put(id, sender);
        } else {
            handleException("Sensor cannot be found in the catalog id: " + id);
        }
    }

    private void handleSensorRemoved(String id) {
        Sender sender = senders.get(id);
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
        UpdateToMessageFactory fac = new UpdateToMessageFactory();
        return fac.create(updateMessage);
    }

    private void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    private void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }
}
