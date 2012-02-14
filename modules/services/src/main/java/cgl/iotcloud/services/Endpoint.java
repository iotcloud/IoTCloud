package cgl.iotcloud.services;

public class Endpoint {
    private String address;

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
}
