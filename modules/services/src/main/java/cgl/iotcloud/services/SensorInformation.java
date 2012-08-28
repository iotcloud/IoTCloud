package cgl.iotcloud.services;

/**
 * Bean for holding information about sensors
 */
public class SensorInformation {
    /** Id of the sensor */
    private String id;
    /** type of the sensor */
    private String type;
    /** name of the sensor */
    private String name;
    /** control endpoint */
    private Endpoint controlEndpoint;
    /** data endpoint */
    private Endpoint dataEndpoint;
    /** public endpoint */
    private Endpoint publicEndpoint;
    /** update endpoint */
    private Endpoint updateEndpoint;

    /**
     * name of the sensor
     *
      * @return name of the sensor
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the sensor
     *
     * @param name name of the sensor
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the id of the sensor
     *
     * @return get the id of the sensor
     */
    public String getId() {
        return id;
    }

    /**
     * Get the type of the sensor
     *
     * @return type of the sensor
     */
    public String getType() {
        return type;
    }

    /**
     * Set the id of the sensor
     *
     * @param id id of the sensor
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the type of the sensor
     *
     * @param type type of the sensor
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Get the control endpoint of the sensor
     *
     * @return the control endpoint
     */
    public Endpoint getControlEndpoint() {
        return controlEndpoint;
    }

    /**
     * Get the data endpoint
     *
     * @return data endpoint
     */
    public Endpoint getDataEndpoint() {
        return dataEndpoint;
    }

    /**
     * Set the control endpoint
     *
     * @param controlEndpoint control endpoint
     */
    public void setControlEndpoint(Endpoint controlEndpoint) {
        this.controlEndpoint = controlEndpoint;
    }

    /**
     * Set the data endpoint of the sensor
     *
     * @param dataEndpoint set the data endpoint of the sensor
     */
    public void setDataEndpoint(Endpoint dataEndpoint) {
        this.dataEndpoint = dataEndpoint;
    }

    /**
     * Get the update endpoint
     *
     * @return update endpoint
     */
    public Endpoint getUpdateEndpoint() {
        return updateEndpoint;
    }

    /**
     * Set the update endpoint
     *
     * @param updateEndpoint update endpoint
     */
    public void setUpdateEndpoint(Endpoint updateEndpoint) {
        this.updateEndpoint = updateEndpoint;
    }
    
    /**
     * Get the Public End-Point
     * @return
     */
    public Endpoint getPublicEndpoint() {
		return publicEndpoint;
	}

    /**
     * Set the Public End-Point
     * @param publicEndpoint
     */
	public void setPublicEndpoint(Endpoint publicEndpoint) {
		this.publicEndpoint = publicEndpoint;
	}
}
