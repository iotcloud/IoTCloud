package cgl.iotcloud.sensors;

import cgl.iotcloud.gen.services.xsd.SensorInformation;

/**
 * HTTP based implementation for retrieving information
 */
public class RegistrationHttpClient implements RegistrationClient {
    public SensorInformation registerSensor(String name, String type) {
        return null;
    }

    public void unRegisterSensor(String id) {

    }
}
