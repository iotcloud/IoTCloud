package cgl.iotcloud.sensors;

import cgl.iotcloud.core.sensor.Sensor;

public interface RegistrationClient {
    /**
     * Register the sensor to the grid
     *
     * @param sensor the sensor to register
     */
    public void registerSensor(Sensor sensor);

    /**
     * Un-register the sensor from the grid.
     *
     * @param sensor sensor to un-register
     */
    public void unRegisterSensor(Sensor sensor);
}
