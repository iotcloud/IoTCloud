package cgl.iotcloud.clients;

import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;

import java.util.List;

/**
 * Uses the HTTP API to communicate with the IOT
 */
public class RegistrationHttpClient implements RegistrationClient {
    private String hostName =  "";

    public List<SCSensor> getSensors() {
        return null;
    }

    public SCSensor getSensor(String id) {
        return null;
    }

    public SCSensor getSensor(String type, FilterCriteria criteria) {
        return null;
    }

    public SCSensor registerClient(String sensorId) {
        return null;
    }
}
