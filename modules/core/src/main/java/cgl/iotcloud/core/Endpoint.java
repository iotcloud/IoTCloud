package cgl.iotcloud.core;

import java.util.Map;

/**
 * Represent an endpoint viewed by client, sensor or the broker. All three
 * entities uses the same interface to represent the endpoints.
 */
public interface Endpoint {
    /**
     * Get the name of the endpoint
     *
     * @return name of the endpoint
     */
    public String getName();

    /**
     * Set the name of the endpoint
     *
     * @param name name of the endpoint
     */
    public void setName(String name);

    /**
     * Get the address of the endpoint
     *
     * @return address of the endpoint
     */
    public String getAddress();

    /**
     * Get Properties related to this endpoint
     *
     * @return properties related to this endpoint as a Map
     */
    public Map<String, String> getProperties();

    /**
     * Set the properties related to this endpoint
     *
     * @param properties set the properties
     */
    public void setProperties(Map<String, String> properties);

    /**
     * Set the address of the endpoint
     *
     * @param address address of the endpoint
     */
    public void setAddress(String address);
}
