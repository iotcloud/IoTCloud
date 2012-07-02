package cgl.iotcloud.sensors;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.sensor.Sensor;
import cgl.iotcloud.gen.clients.SensorRegistrationServiceStub;
import cgl.iotcloud.gen.services.sensor.xsd.SensorRegistrationInformation;
import cgl.iotcloud.gen.services.xsd.Endpoint;
import cgl.iotcloud.gen.services.xsd.Property;
import cgl.iotcloud.gen.services.xsd.SensorInformation;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 * A wrapper class for getting sensor information.
 */
public class RegistrationWSClient implements RegistrationClient {
    private static Logger log = LoggerFactory.getLogger(RegistrationWSClient.class);

    private SensorRegistrationServiceStub stub;

    public RegistrationWSClient(String url) {
        try {
            stub = new SensorRegistrationServiceStub(url);
        } catch (AxisFault axisFault) {
            handleException("Failed to create the client stub for service: " +
                    "SensorRegistrationService", axisFault);
        }
    }

    /**
     * Register the sensor to the grid
     *
     * @param name name of the sensor
     * @param type type of the sensor
     * @return creates a sensor adaptor
     */
    public SensorInformation registerSensor(String name, String type) {
        try {
            return stub.registerSensor(createSensorInformation(name, type));
        } catch (RemoteException e) {
            handleException("Failed to invoke the method registerSensor in service: " +
                    "SensorRegistrationService", e);
        }
        return null;
    }

    /**
     * Un-register the sensor from the grid.
     *
     * @param id id of the sensor to be un-registered
     */
    public void unRegisterSensor(String id) {
        try {
            stub.unregisterSensor(id);
        } catch (RemoteException e) {
            handleException("Failed to invoke the method to un-register in " +
                    "service: SensorRegistrationService", e);
        }
    }

    private SensorRegistrationInformation createSensorInformation(String name, String type) {
        SensorRegistrationInformation info = new SensorRegistrationInformation();
        info.setType(type);
        info.setName(name);

        return info;
    }

    private void populateSensor(Sensor sensor, SensorInformation i) {
        sensor.setId(i.getId());
        sensor.setType(i.getType());

        Endpoint e = i.getControlEndpoint();
        if (e == null) {
            handleException("Required endpoint control endpoint cannot be found");
            return;
        }
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
        if (e == null) {
            handleException("Required endpoint data endpoint cannot be found");
            return;
        }
        cgl.iotcloud.core.Endpoint dataEpr = new JMSEndpoint();
        dataEpr.setAddress(e.getAddress());

        props = e.getProperties();
        propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        dataEpr.setProperties(propsMap);
        sensor.setDataEndpoint(dataEpr);

        e = i.getUpdateEndpoint();
        if (e == null) {
            handleException("Required endpoint update endpoint cannot be found");
            return;
        }
        cgl.iotcloud.core.Endpoint updateEpr = new JMSEndpoint();
        updateEpr.setAddress(e.getAddress());

        props = e.getProperties();
        propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        updateEpr.setProperties(propsMap);
        sensor.setUpdateEndpoint(updateEpr);
    }

    protected static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    protected static void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }

    @Override
    public void registerSensor(Sensor sensor) {
        try {
            SensorInformation info = stub.registerSensor(
                    createSensorInformation(sensor.getName(), sensor.getType()));

            if (info != null) {
                populateSensor(sensor, info);
            } else {
                handleException("Failed to invoke the method registerSensor in service: " +
                        "SensorRegistrationService");
            }
        } catch (RemoteException e) {
            handleException("Failed to invoke the method registerSensor in service: " +
                    "SensorRegistrationService", e);
        }
    }

    @Override
    public void unRegisterSensor(Sensor sensor) {
        try {
            stub.unregisterSensor(sensor.getId());
        } catch (RemoteException e) {
            handleException("Failed to un-register the sensor with the id: " +
                    sensor.getId() + " and name: " + sensor.getName());
        }
    }
}
