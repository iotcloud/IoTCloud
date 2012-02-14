package cgl.iotcloud.core.endpoint;

import cgl.iotcloud.core.Endpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Captures information about a JMS endpoint
 */
public class JMSEndpoint implements Endpoint {
    /** Name of the endpoint */
    private String name;
    /** JMS topic name */
    private String address;
    /** set of properties defining the endpoint */
    private Map<String, String> properties = new HashMap<String, String>();

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}



