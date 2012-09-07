package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This holds information about a connected node in the middle ware.
 */
public class NodeInformation {
    private Logger log = LoggerFactory.getLogger(NodeInformation.class);

    private List<Endpoint> producers = new ArrayList<Endpoint>();

    private List<Endpoint> consumers = new ArrayList<Endpoint>();

    private NodeName nodeName = null;

    public NodeInformation(NodeName nodeName) {
        this.nodeName = nodeName;
    }

    public NodeName getName() {
        return nodeName;
    }

    public List<Endpoint> getProducers() {
        return producers;
    }

    public List<Endpoint> getConsumers() {
        return consumers;
    }

    public boolean removeConsumer(Endpoint endpoint) {
        return consumers.remove(endpoint);
    }

    public boolean removeProducer(Endpoint endpoint) {
        return producers.remove(endpoint);
    }

    public void addConsumer(Endpoint endpoint) {
        consumers.add(endpoint);
    }

    public void addProducer(Endpoint endpoint) {
        producers.add(endpoint);
    }
}
