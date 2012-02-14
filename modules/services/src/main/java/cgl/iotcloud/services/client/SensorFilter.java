package cgl.iotcloud.services.client;

import cgl.iotcloud.services.Property;

public class SensorFilter {
    private String type = null;

    private Property[] properties = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Property[] getProperties() {
        return properties;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
    }
}
