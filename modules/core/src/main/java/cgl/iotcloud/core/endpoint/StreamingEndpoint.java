package cgl.iotcloud.core.endpoint;

import cgl.iotcloud.core.Endpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Streaming endpoint.
 */
public class StreamingEndpoint implements Endpoint {
    private String address;

    private String name;

    private Map<String, String> properties = new HashMap<String, String>();

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

    public void setProperties(Map<String, String> properties) {
        this.properties.putAll(properties);
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
