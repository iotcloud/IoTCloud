package cgl.iotcloud.clients;

import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;

import java.util.List;

public interface RegistrationClient {
    public List<SCSensor> getSensors();

    /**
     * Get the information about the particular sensor
     *
     * @param id if of the sensor
     * @return a Sensor
     */
    public SCSensor getSensor(String id);

    /**
     * Get the information about the particular sensor
     *
     * @param type     type of the sensor
     * @param criteria filter information
     * @return a Sensor
     */
    public SCSensor getSensor(String type, FilterCriteria criteria);

    public SCSensor registerClient(String sensorId);
}
