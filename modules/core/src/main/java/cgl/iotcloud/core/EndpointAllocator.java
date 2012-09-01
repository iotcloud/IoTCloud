package cgl.iotcloud.core;

import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;

/**
 * This class is responsible for picking endpoint for consumers and producers.
 */
public class EndpointAllocator {
    private SCConfiguration configuration;

    private NodeCatalog catalog;

    public EndpointAllocator(SCConfiguration configuration, NodeCatalog catalog) {
        this.configuration = configuration;
        this.catalog = catalog;
    }

    public Endpoint allocate(NodeName nodeName, String name, String type, String path) {
        Endpoint endpoint;

        NodeInformation nodeInfo = catalog.getNode(nodeName);
        for (Endpoint e : nodeInfo.getConsumers()) {
            if (e.getName().equals(name)) {
                return null;
            }
        }

        for (Endpoint e : nodeInfo.getProducers()) {
            if (e.getName().equals(name)) {
                return null;
            }
        }

        if (type.equals(Constants.MESSAGE_TYPE_BLOCK)) {
            endpoint = new JMSEndpoint();
            endpoint.setAddress(nodeName.getGroup() + "/" + nodeName.getName() + "/" + path);
            // TODO: we have to decide the connection factory to be used
            endpoint.setProperties(
                    configuration.getBroker().getConnections("topic").getParameters());
        } else {
            endpoint = new StreamingEndpoint();
            endpoint.setProperties(configuration.getStreamingServer().getParameters());
            endpoint.getProperties().put("PATH", "sensor/" +
                    nodeInfo.getName().getName() + "/" + path);
        }

        return endpoint;
    }
}
