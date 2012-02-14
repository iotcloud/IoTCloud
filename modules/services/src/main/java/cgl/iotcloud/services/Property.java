package cgl.iotcloud.services;

/**
 * A bean for holding a key-value pair
 */
public class Property {
    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
