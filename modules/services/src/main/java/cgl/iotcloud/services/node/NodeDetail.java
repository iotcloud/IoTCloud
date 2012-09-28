package cgl.iotcloud.services.node;

public class NodeDetail {
    private NodeInfo name;

    private EndpointInfo[] endpoints;

    public NodeInfo getName() {
        return name;
    }

    public EndpointInfo[] getEndpoints() {
        return endpoints;
    }

    public void setName(NodeInfo name) {
        this.name = name;
    }

    public void setEndpoints(EndpointInfo[] endpoints) {
        this.endpoints = endpoints;
    }
}
