package cgl.iotcloud.core;

import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.core.sensor.SCSensor;

/**
 * This class is responsible for picking endpoint for consumers and producers.
 */
public class EndpointAllocator {
    private SCConfiguration configuration;

    private NodeCatalog catalog;

    private SensorCatalog sensorCatalog;

    public EndpointAllocator(SCConfiguration configuration, NodeCatalog catalog) {
        this.configuration = configuration;
        this.catalog = catalog;
    }

    public Endpoint allocate(NodeName nodeName, String name, String type, String path) {
        Endpoint endpoint;

        NodeInformation nodeInfo = catalog.getNode(nodeName);
        for (Endpoint e : nodeInfo.getConsumers()) {
            if (e.getName() != null && e.getName().equals(name)) {
                return null;
            }
        }

        for (Endpoint e : nodeInfo.getProducers()) {
            if (e.getName() != null && e.getName().equals(name)) {
                return null;
            }
        }

        if (type == null) {
            type = Constants.MESSAGE_TYPE_BLOCK;
        }

        if (type.equals(Constants.MESSAGE_TYPE_BLOCK)) {
            endpoint = new JMSEndpoint();
            endpoint.setName(name);
            endpoint.setAddress(nodeName.getGroup() + "/" + nodeName.getName() + "/" + path);
            // TODO: we have to decide the connection factory to be used
            endpoint.setProperties(
                    configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());
        } else {
            endpoint = new StreamingEndpoint();
            endpoint.setName(name);
            endpoint.setProperties(configuration.getStreamingServer().getParameters());
            endpoint.getProperties().put("PATH", "sensor/" +
                    nodeInfo.getName().getName() + "/" + path);
        }

        return endpoint;
    }

    public Endpoint allocate(String id, String name, String type, String path) {
        Endpoint endpoint;

        SCSensor nodeInfo = sensorCatalog.getSensor(id);

        if (type == null) {
            type = Constants.MESSAGE_TYPE_BLOCK;
        }

        if (type.equals(Constants.MESSAGE_TYPE_BLOCK)) {
            endpoint = new JMSEndpoint();
            endpoint.setName(name);
            endpoint.setAddress(name + "/" + path);
            // TODO: we have to decide the connection factory to be used
            endpoint.setProperties(
                    configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());
        } else {
            endpoint = new StreamingEndpoint();
            endpoint.setName(name);
            endpoint.setProperties(configuration.getStreamingServer().getParameters());
            endpoint.getProperties().put("PATH", "sensor/" +
                    name + "/" + path);
        }

        return endpoint;
    }
}
