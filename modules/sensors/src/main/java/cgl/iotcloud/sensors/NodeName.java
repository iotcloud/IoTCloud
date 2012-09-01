package cgl.iotcloud.sensors;

/**
 * Represent how this Sensor appears
 */
public class NodeName {
    private String name = null;

    private String group = "default";

    public NodeName(String name) {
        this(name, null);
    }

    public NodeName(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
