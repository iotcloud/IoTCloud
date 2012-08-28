package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.Endpoint;

/**
 * Representation of a sensor in the sensor cloud.
 */
public interface Sensor {
    /**
     * Unique name given to a sensor by the sensor grid
     *
     * @return if of the sensor
     */
    public String getId();

    /**
     * Set the id of the sensor
     *
     * @param id of the sensor
     */
    public void setId(String id);

    /**
     * Set the name of the sensor
     * @param name name of the sensor
     */
    public void setName(String name);

    /**
     * Friendly name given to the sensor
     *
     * @return name of the sensor
     */
    public String getName();

    /**
     * Get the type of the sensor
     *
     * @return type of the sensor
     */
    public String getType();

    /**
     * Set the type of the sensor
     *
     * @param type of the sensor
     */
    public void setType(String type);

    /**
     * This is the endpoint used by the sensor to send data
     *
     * @return data endpoint
     */
    public Endpoint getDataEndpoint();

    /**
     * Set the endpoint used by the sensor to send data
     *
     * @param endpoint data endpoint
     */
    public void setDataEndpoint(Endpoint endpoint);

    /**
     * This is the endpoint used by the sensor grid to send control messages to the sensor
     *
     * @return the control endpoint
     */
    public Endpoint getControlEndpoint();

    /**
     * This is the endpoint used by the sensor grid to send control messages to the sensor
     *
     * @param endpoint endpoint to send control messages
     */
    public void setControlEndpoint(Endpoint endpoint);

    /**
     * This is the endpoint used by the sensor grid to send control messages to the sensor
     *
     * @return Endpoint endpoint to send control messages
     */
    public Endpoint getUpdateEndpoint();

    /**
     * This is the endpoint used by the sensor grid to send control messages to the sensor
     *
     * @param endpoint the endpoint to be used as a update endpoint
     */
    public void setUpdateEndpoint(Endpoint endpoint);
    
    /**
     * This is the endpoint used by the IOT-Cloud to send generic messages (Example: Shut-Down Message)
     * @return
     */
    public Endpoint getPublicEndpoint();
    
    /**
     * This is the endpoint used by the IOT-Cloud to send generic messages (Example: Shut-Down Message)
     * @param publicEndpoint
     */
	public void setPublicEndpoint(Endpoint publicEndpoint);
}
