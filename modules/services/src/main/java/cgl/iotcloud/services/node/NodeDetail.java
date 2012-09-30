package cgl.iotcloud.services.node;

import cgl.iotcloud.services.Endpoint;

public class NodeDetail {
    private NodeInfo name;

    private Endpoint[] consumers;

    private Endpoint[] producers;

    public NodeInfo getName() {
        return name;
    }

    public void setName(NodeInfo name) {
        this.name = name;
    }

    public Endpoint[] getConsumers() {
        return consumers;
    }

    public Endpoint[] getProducers() {
        return producers;
    }

    public void setConsumers(Endpoint[] consumers) {
        this.consumers = consumers;
    }

    public void setProducers(Endpoint[] producers) {
        this.producers = producers;
    }
}
