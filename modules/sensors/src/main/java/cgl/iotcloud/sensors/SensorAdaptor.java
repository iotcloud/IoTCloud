package cgl.iotcloud.sensors;

import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.sensor.Sensor;
import cgl.iotcloud.gen.services.xsd.Endpoint;
import cgl.iotcloud.gen.services.xsd.Property;
import cgl.iotcloud.gen.services.xsd.SensorInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SensorAdaptor {
    private static Logger log = LoggerFactory.getLogger(SensorRegistrationClient.class);

    private Sensor sensor = null;

    private SensorRegistrationClient client = null;

    public SensorAdaptor(String url) {
        client = new SensorRegistrationClient(url + "/soap/services/SensorRegistrationService");
    }

    public void registerSensor(String name, String type, String sensorClass) {
        SensorInformation info = client.registerSensor(name, type);
        if (info == null) {
            handleException("Failed to register sensor: " + sensor.getName() + " " + sensor.getType());
        }

        try {
            Class c = Class.forName(sensorClass);
            Object o = c.newInstance();
            if (o instanceof Sensor) {
                populateSensor((Sensor) o, info);
            }
        } catch (ClassNotFoundException e) {
            handleException("Failed to locate the class: " + sensorClass, e);
        } catch (InstantiationException e) {
            handleException("Failed to intialize the class: " + sensorClass, e);
        } catch (IllegalAccessException e) {
            handleException("Access restricted for the class: " + sensorClass, e);
        }
    }

    public void registerSensor(Sensor sensor) {
        SensorInformation info = client.registerSensor(sensor.getName(), sensor.getType());
        if (info == null) {
            handleException("Failed to register sensor: " + sensor.getName() + " " + sensor.getType());
        }
        populateSensor(sensor, info);

        this.sensor = sensor;
    }

    private void populateSensor(Sensor sensor, SensorInformation i) {
        sensor.setId(i.getId());
        sensor.setType(i.getType());

        Endpoint e = i.getControlEndpoint();
        if (e == null) {
            handleException("Required endpoint control endpoint cannot be found");
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

    public void start() {
        if (sensor instanceof ManagedLifeCycle)  {
            ((ManagedLifeCycle) sensor).init();
        }
    }

    public void stop() {
        client.unRegisterSensor(sensor.getId());

        if (sensor instanceof ManagedLifeCycle) {
            ((ManagedLifeCycle) sensor).destroy();
        }
    }

    protected static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    protected static void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }
}
