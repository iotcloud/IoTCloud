package cgl.iotcloud.clients;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.client.Client;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.update.MessageToUpdateFactory;
import cgl.iotcloud.core.message.update.UpdateMessage;
import cgl.iotcloud.core.message.update.UpdateMessageHandler;
import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used by the Client programmers for sensors.
 */
public class SensorClient {
    private Logger log = LoggerFactory.getLogger(SensorClient.class);
    /** Sensor grid url */
    private String sensorUrl;
    /** Client to get information about the sensors */
    private RegistrationClient client;
    /** Use to send messages and receive messages */
    private Client sensorClient;

    public SensorClient(String sensorUrl) {
        this.sensorUrl = sensorUrl;

        init();
    }

    /**
     * Initialize the sensor client. This will create the client to get
     * the information from the sensor grid
     */
    private void init() {
        log.debug("Initializing the Sensor Client for the URL: " + sensorUrl);

        client = new RegistrationClient(sensorUrl + "soap/services/ClientRegistrationService");
    }

    public void fixOnSensorWithName(String name) {
        FilterCriteria criteria = new FilterCriteria();
        criteria.addProperty("type", "name");
        criteria.addProperty("name", name);
        SCSensor sensor = client.getSensor("name", criteria);
        if (sensor == null) {
            handleException("Sensor with the name: " + name + " cannot be fount");
        }

        sensorClient = new Client(sensor);
    }

    public void register(String sensorId) {
        SCSensor sensor = client.registerClient(sensorId);
        if (sensor == null) {
            handleException("Sensor with the id: " + sensorId + " cannot be fount");
        }

        sensorClient = new Client(sensor);
    }

    public void registerWithName(String name) {
        FilterCriteria criteria = new FilterCriteria();
        criteria.addProperty("type", "name");
        criteria.addProperty("name", name);
        SCSensor sensor = client.getSensor("name", criteria);
        if (sensor == null) {
            handleException("Sensor with the name: " + name + " cannot be fount");
            return;
        }
        register(sensor.getId());
    }



    public void setUpdateListener(UpdateMessageHandler handler) {
        sensorClient.setUpdateMessageHandler(new UpdateMessageReceiver(handler));
    }

    public void listen(MessageHandler handler) {
        sensorClient.setMessageHandler(handler);
        sensorClient.init();
    }

    public void sendControlMessage(SensorMessage message) {
        sensorClient.sendMessage(message);
    }

    public RegistrationClient getClient() {
        return client;
    }

    private void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }

    /**
     * This is a MessageHandler for receiving update messages about the sensor grid.
     */
    private class UpdateMessageReceiver implements MessageHandler {
        private UpdateMessageHandler handler;

        private UpdateMessageReceiver(UpdateMessageHandler handler) {
            this.handler = handler;
        }

        public void onMessage(SensorMessage message) {
            if (message instanceof TextDataMessage) {
                MessageToUpdateFactory fac = new MessageToUpdateFactory();
                UpdateMessage update = fac.createFromMessage(((TextDataMessage) message).getText());
                handler.onUpdate(update);
            }
        }
    }
}
