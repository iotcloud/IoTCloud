package cgl.iotcloud.services;

/**
 * Represent an endpoint as a bean
 */
public class Endpoint {
    private String type;

    private String name;

    /** address */
    private String address;

    /** array of properties */
    private Property[] properties;

    public String getAddress() {
        return address;
    }

    public Property[] getProperties() {
        return properties;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }
}
