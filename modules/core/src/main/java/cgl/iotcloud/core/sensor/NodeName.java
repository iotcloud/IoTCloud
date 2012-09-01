package cgl.iotcloud.core.sensor;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeName nodeName = (NodeName) o;

        return !(group != null ? !group.equals(nodeName.group) : nodeName.group != null) &&
                !(name != null ? !name.equals(nodeName.name) : nodeName.name != null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }
}
