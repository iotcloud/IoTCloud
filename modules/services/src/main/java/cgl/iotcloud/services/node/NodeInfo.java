package cgl.iotcloud.services.node;

public class NodeInfo {
    private String name;

    private String group;

    public NodeInfo() {
    }

    public NodeInfo(String name, String group) {
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
