package cgl.iotcloud.sensors;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.sensor.Node;
import cgl.iotcloud.gen.services.node.xsd.EndpointInfo;
import cgl.iotcloud.gen.services.node.xsd.NodeInfo;
import cgl.iotcloud.gen.services.node.xsd.Property;
import cgl.iotcloud.core.Endpoint;

import java.util.HashMap;
import java.util.Map;

public class NodeAdaptor {
    private NodeWSClient nodeWSClient = null;

    private boolean rest = false;

    private Node node;

    public NodeAdaptor(Node node, String url) throws IOTException {
        this.node = node;
        nodeWSClient = new NodeWSClient(url);
    }

    public void registerNode() throws IOTException {
        NodeInfo nodeInfo = createNodeInfo(node);
        if (!rest) {
            nodeWSClient.registerNode(nodeInfo);
        }
    }

    public void unRegisterNode() throws IOTException {
        NodeInfo nodeInfo = createNodeInfo(node);
        if (!rest) {
            nodeWSClient.unRegisterNode(nodeInfo);
        }
    }

    public Endpoint registerConsumer(String name,
                                     String type, String path) throws IOTException {
        NodeInfo nodeInfo = createNodeInfo(node);
        EndpointInfo endpointInfo = createEndpointInfo(name, type, path);
        if (!rest) {
            cgl.iotcloud.gen.services.node.xsd.Endpoint epr =
                    nodeWSClient.registerConsumer(nodeInfo, endpointInfo);
            return createEndpoint(epr, type, name);
        }

        return null;
    }

    public Endpoint registerProducer(String name,
                                     String type, String path) throws IOTException {
        NodeInfo nodeInfo = createNodeInfo(node);
        EndpointInfo endpointInfo = createEndpointInfo(name, type, path);

        if (!rest) {
            cgl.iotcloud.gen.services.node.xsd.Endpoint epr =
                    nodeWSClient.registerProducer(nodeInfo, endpointInfo);
            return createEndpoint(epr, type, name);
        }

        return null;
    }

    public boolean unRegisterProducer(String name, String type, String path) throws IOTException {
        NodeInfo nodeInfo = createNodeInfo(node);
        EndpointInfo endpointInfo = createEndpointInfo(name, type, path);

        if (!rest) {
            return nodeWSClient.unRegisterProducer(nodeInfo, endpointInfo);
        } else {
            return false;
        }
    }

    public boolean unRegisterConsumer(String name, String type, String path) throws IOTException {
        NodeInfo nodeInfo = createNodeInfo(node);
        EndpointInfo endpointInfo = createEndpointInfo(name, type, path);

        if (!rest) {
            return nodeWSClient.unRegisterConsumer(nodeInfo, endpointInfo);
        } else {
            return false;
        }
    }


    private NodeInfo createNodeInfo(Node node) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setGroup(node.getName().getGroup());
        nodeInfo.setName(node.getName().getName());
        return nodeInfo;
    }

    private EndpointInfo createEndpointInfo(String name, String type, String path) {
        EndpointInfo info = new EndpointInfo();
        info.setName(name);
        info.setType(type);
        info.setPath(path);
        return info;
    }

    private Endpoint createEndpoint(cgl.iotcloud.gen.services.node.xsd.Endpoint epr, String type, String name) {
        cgl.iotcloud.core.Endpoint endpoint = null;
        if (type.equals(Constants.MESSAGE_TYPE_BLOCK)) {
            endpoint = new JMSEndpoint();
        } else if (type.equals(Constants.MESSAGE_TYPE_STREAM)) {
            endpoint = new StreamingEndpoint();
        } else {
            endpoint = new JMSEndpoint();
        }

        endpoint.setAddress(epr.getAddress());
        endpoint.setName(name);

        Map<String, String> props = new HashMap<String, String>();
        for (Property p : epr.getProperties()) {
            props.put(p.getName(), p.getValue());
        }

        endpoint.setProperties(props);

        return endpoint;
    }
}
