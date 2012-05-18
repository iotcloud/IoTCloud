package cgl.iotcloud.sensors;

import cgl.iotcloud.gen.services.xsd.SensorInformation;

public interface RegistrationClient {
    /**
     * Register the sensor to the grid
     *
     * @param name name of the sensor
     * @param type type of the sensor
     * @return creates a sensor adaptor
     */
    public SensorInformation registerSensor(String name, String type);

    public void unRegisterSensor(String id);
}
