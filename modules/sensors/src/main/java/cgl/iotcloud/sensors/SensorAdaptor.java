package cgl.iotcloud.sensors;

import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.sensor.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SensorAdaptor {
    private static Logger log = LoggerFactory.getLogger(RegistrationWSClient.class);

    private Sensor sensor = null;

    private RegistrationClient client = null;

    private boolean isRest = false;
    
    public SensorAdaptor(String url) {
        client = new RegistrationWSClient(url + "/soap/services/SensorRegistrationService");
    }

    public SensorAdaptor(String host, int port, boolean rest) {
        isRest = rest;
        if (isRest) {
            client = new RegistrationHttpClient(host, port, false);
        } else {
            client = new RegistrationWSClient("http://" + host + ":" + port);
        }
    }

    public void registerSensor(String name, String type, String sensorClass) {                
        try {
            Class c = Class.forName(sensorClass);            
            Object o = c.newInstance();
            if (o instanceof Sensor) {
                Sensor s = (Sensor) o;
                s.setName(name);
                s.setType(type);
                client.registerSensor(s);                
                
                sensor = s;
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
        client.registerSensor(sensor);               

        this.sensor = sensor;
    }

    public void start() {
        if (sensor instanceof ManagedLifeCycle)  {
            ((ManagedLifeCycle) sensor).init();
        }
    }

    public void stop() {
        client.unRegisterSensor(sensor);

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
