package cgl.iotcloud.core;

import cgl.iotcloud.core.broker.Connections;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.sensor.AbstractSensorFilter;
import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.Sensor;
import cgl.iotcloud.core.sensor.SensorException;
import cgl.iotcloud.core.sensor.filter.SensorIdFilter;
import cgl.iotcloud.core.sensor.filter.SensorNameFilter;
import cgl.iotcloud.core.sensor.filter.SensorTypeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Instance of the Sensor cloud. This captures all the information about the IoTCloud.
 * Both Sensors and Clients use this.
 */
public class IoTCloud {
    private static Logger log = LoggerFactory.getLogger(IoTCloud.class);
    /** Configuration for the sensor cloud */
    private SCConfiguration configuration = null;
    /** Sensor catalog containing the information about the sensors */
    private SensorCatalog sensorCatalog = null;
    /** Updates are send through this */
    private UpdateManager updateManager = null;

    public IoTCloud(SCConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Initialize the configurations and start the connections
     */
    public void init() {
        configuration.init();

        Connections connections = configuration.getBroker().getConnections("topic");

        if (connections == null) {
            handleException("Couldn't find the connection factor: " + "topic");
        }

        sensorCatalog = new SensorCatalog(connections);
        updateManager = new UpdateManager(configuration, sensorCatalog);
        updateManager.init();
    }

    public SensorCatalog getSensorCatalog() {
        return sensorCatalog;
    }

    public SCConfiguration getConfiguration() {
        return configuration;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public Sensor registerSensor(String name) {
        return registerSensor(name, Constants.SENSOR_TYPE_BLOCK);
    }

    /**
     * Register a sensor with the given name and type
     * @param name name of the sensor
     * @param type type of the sensor
     * @return the sensor
     */
    public Sensor registerSensor(String name, String type) {
        SCSensor sensor = new SCSensor(name);

        String uid = UUID.randomUUID().toString();
        sensor.setId(uid);
        sensor.setType(type);

        Endpoint dataEndpoint;
        if (Constants.SENSOR_TYPE_BLOCK.equals(type)) {
            dataEndpoint = new JMSEndpoint();
            dataEndpoint.setAddress(sensor.getId() + "/data");
            // TODO: we have to decide the connection factory to be used
            dataEndpoint.setProperties(
                    configuration.getBroker().getConnections("topic").getParameters());
        } else if (Constants.SENSOR_TYPE_STREAMING.equals(type)) {
            dataEndpoint = new StreamingEndpoint();

            dataEndpoint.setProperties(configuration.getStreamingServer().getParameters());
            dataEndpoint.getProperties().put("PATH", "sensor/" + sensor.getId() + "/data");

            // add the routing to the streaming server
        } else {
            // defaulting to JMS
            dataEndpoint = new JMSEndpoint();
            dataEndpoint.setAddress(sensor.getId() + "/data");
            // TODO: we have to decide the connection factory to be used
            dataEndpoint.setProperties(
                    configuration.getBroker().getConnections("topic").getParameters());
        }

        sensor.setDataEndpoint(dataEndpoint);

        Endpoint controlEndpoint;

        controlEndpoint = new JMSEndpoint();
        controlEndpoint.setAddress(sensor.getId() + "/control");
        // TODO: we have to decide the connection factory to be used
        controlEndpoint.setProperties(
                configuration.getBroker().getConnections("topic").getParameters());
        sensor.setControlEndpoint(controlEndpoint);

        // set the update sending endpoint as the global endpoint
        Endpoint updateSendingEndpoint;
        updateSendingEndpoint = new JMSEndpoint();

        updateSendingEndpoint.setProperties(
                configuration.getBroker().getConnections("topic").getParameters());
        updateSendingEndpoint.setAddress(sensor.getId() + "/update");
        sensor.setUpdateEndpoint(updateSendingEndpoint);

        sensorCatalog.addSensor(sensor);
        updateManager.sensorChange(Constants.Updates.ADDED, sensor.getId());
        return sensor;
    }

    /**
     * Un-register a sensor with the given id.
     * @param id id of the sensor
     */
    public void unRegisterSensor(String id) {
        if (sensorCatalog.hasSensor(id)) {
            updateManager.sensorChange(Constants.Updates.REMOVED, id);
            sensorCatalog.removeSensor(id);
        } else {
            String msg = "Failed to unregister the sensor, non existing sensor";
            log.error(msg);
            throw new SCException(msg);
        }
    }

    /**
     * Get the sensors according to a filtered criteria
     * @param type the type of the filter
     * @param props the properties used for filtering
     *
     * @return a list os sensors matching the filter
     * @throws SensorException if an error occurs in filtering
     */
    public List<SCSensor> getSensors(String type, Map<String, String> props)
            throws SensorException {
        AbstractSensorFilter sensorFilter = null;
        if (type.equals("id")) {
            sensorFilter = new SensorIdFilter(this);
        } else if (type.equals("type")) {
            sensorFilter = new SensorTypeFilter(this);
        } else if (type.equals("name")) {
            sensorFilter = new SensorNameFilter(this);
        } else {
            sensorFilter = new SensorIdFilter(this);
        }

        FilterCriteria criteria = new FilterCriteria();
        criteria.addProperties(props);

        return sensorFilter.filter(criteria);
    }

    protected void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }
}
