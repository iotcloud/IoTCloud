package cgl.iotcloud.core;

import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Store for sensors. This store should be populated from the configuration
 * as well as dynamically.
 */
public class SensorCatalog {
    private List<SCSensor> sensors = new ArrayList<SCSensor>();


    public List<SCSensor> getSensors() {
        return sensors;
    }

    public SCSensor getSensor(String id) {
        for (SCSensor s : sensors) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }
    
    public SCSensor getSensorWithName(String name) {
        for (SCSensor s : sensors) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    public void addSensor(SCSensor sensor) {
        if (sensor.getId() == null) {
            throw new IllegalArgumentException("The sensor should have an ID");
        }

        sensors.add(sensor);
    }

    public boolean removeSensor(SCSensor sensor) {
        return sensors.remove(sensor);
    }

    public boolean removeSensor(String id) {
        for (Sensor s : sensors) {
            if (s.getId().equals(id)) {
                return sensors.remove(s);
            }
        }
        return false;
    }

    public boolean hasSensor(String id) {
        for (Sensor s : sensors) {
            if (s.getId().endsWith(id)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasSensorWithName(String name) {
        for (Sensor s : sensors) {
            if (s.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
