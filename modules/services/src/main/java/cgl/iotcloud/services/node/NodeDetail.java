package cgl.iotcloud.services.node;

import cgl.iotcloud.services.Endpoint;

public class NodeDetail {
    private NodeInfo nodeInfo;

    private Endpoint[] consumers;

    private Endpoint[] producers;

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public Endpoint[] getConsumers() {
        return consumers;
    }

    public Endpoint[] getProducers() {
        return producers;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public void setConsumers(Endpoint[] consumers) {
        this.consumers = consumers;
    }

    public void setProducers(Endpoint[] producers) {
        this.producers = producers;
    }
}
