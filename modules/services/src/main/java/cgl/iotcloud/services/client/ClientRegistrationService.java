package cgl.iotcloud.services.client;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.client.SCClient;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.Sensor;
import cgl.iotcloud.core.sensor.SensorException;
import cgl.iotcloud.services.ClientInformation;
import cgl.iotcloud.services.Endpoint;
import cgl.iotcloud.services.Property;
import cgl.iotcloud.services.SensorInformation;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * This is a web service used by the clients to get information about the Sensors
 * deployed in the sensor grid.
 */
public class ClientRegistrationService {
    private static Logger log = LoggerFactory.getLogger(ClientRegistrationService.class);

    /**
     * Get all the sensors deployed in the sensor grid
     *
     * @return the information about all the sensors
     * @throws AxisFault if an error occurs
     */
    public SensorInformation[] getAllSensors() throws AxisFault {
        IoTCloud iotCloud = retrieveIoTCloud();
        List<SCSensor> sensors = iotCloud.getSensorCatalog().getSensors();
        SensorInformation si[] = new SensorInformation[sensors.size()];
        for (int i = 0; i < sensors.size(); i++) {
            si[i] = createSensorInformation(sensors.get(i));
        }

        return si;
    }

    /**
     * Get the sensors filtered by the filter criteria
     *
     * @param filter filter criteria
     * @return information about the matching sensors
     * @throws AxisFault if an error occurs
     */
    public SensorInformation[] getSensors(SensorFilter filter) throws AxisFault {
        IoTCloud cloud = retrieveIoTCloud();

        String type = filter.getType();

        Property properties[] = filter.getProperties();
        Map<String, String> props = new HashMap<String, String>();
        for (Property p : properties) {
            props.put(p.getName(), p.getValue());
        }

        try {
            List<SCSensor> sensors = cloud.getSensors(type, props);
            SensorInformation si[] = new SensorInformation[sensors.size()];
            for (int i = 0; i < sensors.size(); i++) {
                si[i] = createSensorInformation(sensors.get(i));
            }

            return si;
        } catch (SensorException e) {
            handleException("Couldn't filter the sensors", e);
        }

        return null;
    }

    /**
     * Give the client information about the sensor
     * @param sensorId id of the sensor
     * @return Information about the sensor
     * @throws AxisFault if an error occurs
     */
    public SensorInformation getSensorInformation(String sensorId) throws AxisFault {
        IoTCloud cloud = retrieveIoTCloud();

        Sensor sensor = cloud.getSensorCatalog().getSensor(sensorId);

        if (sensor != null) {
            return createSensorInformation(sensor);
        }

        return null;
    }

    public ClientInformation registerClient(String sensorId) throws AxisFault {
        IoTCloud cloud = retrieveIoTCloud();
        String id = UUID.randomUUID().toString();

        SCClient sensor = cloud.registerClient(id, sensorId);

        if (sensor != null) {
            return createClientInformation(sensor);
        }

        return null;
    }

    public ClientInformation registerClientWithDataEpr(
            String sensorId, Endpoint dataEndpoint) throws AxisFault {
        IoTCloud cloud = retrieveIoTCloud();
        String id = UUID.randomUUID().toString();

        SCClient sensor = cloud.registerClient(id, sensorId, createEprFromMessage(dataEndpoint));

        if (sensor != null) {
            return createClientInformation(sensor);
        }

        return null;
    }

    /**
     * Return the global update endpoint
     *
     * @return global update endpoint
     * @throws AxisFault id an error occurs
     */
    public Endpoint getUpdateEndpoint() throws AxisFault {
        IoTCloud cloud = retrieveIoTCloud();
        return createEpr(cloud.getUpdateManager().getSendingEndpoint());
    }

    private SensorInformation createSensorInformation(Sensor sensor) {
        SensorInformation info = new SensorInformation();
        info.setId(sensor.getId());
        info.setType(sensor.getType());
        info.setName(sensor.getName());

        cgl.iotcloud.core.Endpoint epr = sensor.getControlEndpoint();
        Endpoint endpoint = createEpr(epr);
        info.setControlEndpoint(endpoint);

        epr = sensor.getDataEndpoint();
        endpoint = createEpr(epr);
        info.setDataEndpoint(endpoint);

        epr = sensor.getUpdateEndpoint();
        endpoint = createEpr(epr);
        info.setUpdateEndpoint(endpoint);

        return info;
    }

    private ClientInformation createClientInformation(SCClient sensor) {
        ClientInformation info = new ClientInformation();
        info.setId(sensor.getId());

        info.setType(sensor.getType());

        cgl.iotcloud.core.Endpoint epr = sensor.getControlEndpoint();
        Endpoint endpoint = createEpr(epr);
        info.setControlEndpoint(endpoint);

        epr = sensor.getDataEndpoint();
        endpoint = createEpr(epr);
        info.setDataEndpoint(endpoint);

        epr = sensor.getUpdateEndpoint();
        endpoint = createEpr(epr);
        info.setUpdateEndpoint(endpoint);

        return info;
    }

    private Endpoint createEpr(cgl.iotcloud.core.Endpoint epr) {
        Endpoint endpoint = new Endpoint();
        endpoint.setAddress(epr.getAddress());
        Set<Map.Entry<String, String>> propSet = epr.getProperties().entrySet();
        Property props[] = new Property[propSet.size()];
        int i = 0;
        for (Map.Entry<String, String> e : propSet) {
            Property p = new Property();
            p.setName(e.getKey());
            p.setValue(e.getValue());
            props[i++] = p;
        }
        endpoint.setProperties(props);
        return endpoint;
    }

    private cgl.iotcloud.core.Endpoint createEprFromMessage(Endpoint epr) {
        cgl.iotcloud.core.Endpoint endpoint = new JMSEndpoint();
        endpoint.setAddress(epr.getAddress());
//        Set<Map.Entry<String, String>> propSet = epr.getProperties().entrySet();
//        Property props[] = new Property[propSet.size()];
//        int i = 0;
//        for (Map.Entry<String, String> e : propSet) {
//            Property p = new Property();
//            p.setName(e.getKey());
//            p.setValue(e.getValue());
//            props[i++] = p;
//        }
//        endpoint.setProperties(props);
//        return endpoint;

        return null;
    }

    private IoTCloud retrieveIoTCloud() throws AxisFault {
        MessageContext msgCtx = MessageContext.getCurrentMessageContext();

        IoTCloud cloud = (IoTCloud) msgCtx.getProperty(
                Constants.SENSOR_CLOUD_AXIS2_PROPERTY);
        if (cloud == null) {
            handleException("Sensor Cloud Configuration Cannot be found");
        }
        return cloud;
    }

    private void handleException(String msg) throws AxisFault {
        log.error(msg);
        throw new AxisFault(msg);
    }

    private void handleException(String msg, Exception e) throws AxisFault {
        log.error(msg);
        throw new AxisFault(msg);
    }

}
