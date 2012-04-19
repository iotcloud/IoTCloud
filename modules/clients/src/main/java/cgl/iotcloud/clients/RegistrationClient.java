package cgl.iotcloud.clients;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.gen.clients.ClientRegistrationServiceStub;
import cgl.iotcloud.gen.services.client.xsd.SensorFilter;
import cgl.iotcloud.gen.services.xsd.ClientInformation;
import cgl.iotcloud.gen.services.xsd.Endpoint;
import cgl.iotcloud.gen.services.xsd.Property;
import cgl.iotcloud.gen.services.xsd.SensorInformation;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper class to the WebService client.
 */
public class RegistrationClient {
    private static Logger log = LoggerFactory.getLogger(RegistrationClient.class);

    private ClientRegistrationServiceStub stub;

    public RegistrationClient(String url) {
        try {
            stub = new ClientRegistrationServiceStub(url);
        } catch (AxisFault axisFault) {
            handleException("Failed to create the client stub for service: " +
                    "ClientRegistrationService", axisFault);
        }
    }

    /**
     * Get all the sensors
     *
     * @return get all the sensors
     */
    public List<SCSensor> getSensors() {
        try {
            SensorInformation info[] = stub.getAllSensors();
            List<SCSensor> sensors = new ArrayList<SCSensor>();
            for (SensorInformation i : info) {
                SCSensor sensor = infoToSensor(i);

                sensors.add(sensor);
            }

            return sensors;
        } catch (RemoteException e) {
            handleException("Failed to invoke the getAllSenors operation", e);
        }

        return null;
    }

    /**
     * Get the information about the particular sensor
     *
     * @param id if of the sensor
     * @return a Sensor
     */
    public SCSensor getSensor(String id) {
        try {
            SensorInformation info = stub.getSensorInformation(id);
            if (info != null) {
                return infoToSensor(info);
            }
        } catch (RemoteException e) {
            handleException("Failed to invoke the getSensorInformation operation", e);
        }

        return null;
    }

    /**
     * Get the information about the particular sensor
     *
     * @param type type of the sensor
     * @param criteria filter information
     * @return a Sensor
     */
    public SCSensor getSensor(String type, FilterCriteria criteria) {
        try {
            SensorFilter filter = new SensorFilter();
            filter.setType(type);
            Set<Map.Entry<String, String>> propertySet = criteria.getProperties().entrySet();
            Property props[] = new Property[propertySet.size()];
            int i = 0;
            for (Map.Entry<String, String> e : propertySet) {
                Property property = new Property();
                property.setName(e.getKey());
                property.setValue(e.getValue());
                props[i++] = property;
            }
            filter.setProperties(props);

            SensorInformation info[] = stub.getSensors(filter);
            if (info != null && info.length > 0) {
                return infoToSensor(info[0]);
            }
        } catch (RemoteException e) {
            handleException("Failed to invoke the getSensorInformation operation", e);
        }

        return null;
    }

    public SCSensor registerClient(String sensorId) {
        try {
            ClientInformation info = stub.registerClient(sensorId);
            if (info != null) {
                return infoToSensor(info);
            }
        } catch (RemoteException e) {
            handleException("Failed to invoke the getSensorInformation operation", e);
        }
        return null;
    }

    private SCSensor infoToSensor(SensorInformation i) {
        SCSensor sensor = new SCSensor(i.getName());
        sensor.setId(i.getId());
        sensor.setType(i.getType());

        Endpoint e = i.getControlEndpoint();
        cgl.iotcloud.core.Endpoint controlEpr = new JMSEndpoint();
        controlEpr.setAddress(e.getAddress());

        Property props[] = e.getProperties();
        Map<String, String> propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        controlEpr.setProperties(propsMap);
        sensor.setControlEndpoint(controlEpr);

        e = i.getDataEndpoint();
        cgl.iotcloud.core.Endpoint dataEpr;
        if (Constants.SENSOR_TYPE_BLOCK.equals(sensor.getType())) {
            dataEpr = new JMSEndpoint();
        } else {
            dataEpr = new StreamingEndpoint();
        }
        dataEpr.setAddress(e.getAddress());


        props = e.getProperties();
        propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        dataEpr.setProperties(propsMap);
        sensor.setDataEndpoint(dataEpr);

        e = i.getUpdateEndpoint();
        cgl.iotcloud.core.Endpoint updateEpr = new JMSEndpoint();
        updateEpr.setAddress(e.getAddress());

        props = e.getProperties();
        propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        updateEpr.setProperties(propsMap);
        sensor.setUpdateEndpoint(updateEpr);

        return sensor;
    }


    private SCSensor infoToSensor(ClientInformation i) {
        SCSensor sensor = new SCSensor(i.getName());
        sensor.setId(i.getId());
        sensor.setType(i.getType());

        Endpoint e = i.getControlEndpoint();
        cgl.iotcloud.core.Endpoint controlEpr = new JMSEndpoint();
        controlEpr.setAddress(e.getAddress());

        Property props[] = e.getProperties();
        Map<String, String> propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        controlEpr.setProperties(propsMap);
        sensor.setControlEndpoint(controlEpr);

        e = i.getDataEndpoint();
        cgl.iotcloud.core.Endpoint dataEpr;
        if (Constants.SENSOR_TYPE_BLOCK.equals(sensor.getType())) {
            dataEpr = new JMSEndpoint();
        } else {
            dataEpr = new StreamingEndpoint();
        }
        dataEpr.setAddress(e.getAddress());


        props = e.getProperties();
        propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        dataEpr.setProperties(propsMap);
        sensor.setDataEndpoint(dataEpr);

        e = i.getUpdateEndpoint();
        cgl.iotcloud.core.Endpoint updateEpr = new JMSEndpoint();
        updateEpr.setAddress(e.getAddress());

        props = e.getProperties();
        propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        updateEpr.setProperties(propsMap);
        sensor.setUpdateEndpoint(updateEpr);

        return sensor;
    }

    /**
     * Get the sensors according to the given filter criteria.
     *
     * @param criteria the filter criteria
     * @return the filtered sensors
     */
    public List<SCSensor> getSensors(FilterCriteria criteria) {
        try {
            SensorFilter sf = new SensorFilter();
            List<SCSensor> sensors = new ArrayList<SCSensor>();

            sf.setType(criteria.get("type"));
            Map<String, String> props = criteria.getProperties();
            Set<Map.Entry<String, String>> sets = props.entrySet();
            for (Map.Entry<String, String> entry : sets) {
                Property p = new Property();
                p.setName(entry.getKey());
                p.setValue(entry.getValue());
                sf.addProperties(p);
            }

            SensorInformation info[] = stub.getSensors(sf);
            for (SensorInformation i : info) {
                SCSensor sensor = infoToSensor(i);

                sensors.add(sensor);
            }

            return sensors;
        } catch (RemoteException e) {
            handleException("Failed to invoke the getSensors operations", e);
        }

        return null;
    }

    private static void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }

    private static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }
}
