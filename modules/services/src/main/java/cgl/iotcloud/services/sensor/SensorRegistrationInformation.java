package cgl.iotcloud.services.sensor;

/**
 * A bean class for setting sensor registration information
 */
public class SensorRegistrationInformation {
    private String name;

    private String type;

    private String id;

    private String group;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
