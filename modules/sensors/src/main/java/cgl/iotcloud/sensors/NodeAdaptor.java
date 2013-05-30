package cgl.iotcloud.sensors;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.sensor.Node;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.gen.services.node.xsd.NodeInfo;
import cgl.iotcloud.gen.services.node.xsd.Property;
import cgl.iotcloud.core.Endpoint;

import java.util.HashMap;
import java.util.Map;

public class NodeAdaptor {
    public static final int MODE_REST = 1;
    public static final int MODE_WS = 2;
    public static final int MODE_THRIFT = 3;

    private Client nodeClient = null;

    private int mode = 2;

    private Node node;

    public NodeAdaptor(Node node, String url) throws IOTException {
        this.node = node;
        if (mode == MODE_WS) {
            nodeClient = new NodeWSClient(url);
        } else if (mode == MODE_THRIFT) {
            nodeClient = new NodeThriftClient("localhost", 9090);
        }
    }

    public NodeAdaptor(Node node, String host, int port, int mode) throws IOTException {
        this.node = node;
        if (mode == MODE_WS) {
            nodeClient = new NodeWSClient("http://" + host + ":"  + port);
        } else if (mode == MODE_THRIFT) {
            nodeClient = new NodeThriftClient(host, port);
        }
    }

    public NodeAdaptor(Node node, String url, int mode) throws IOTException {
        this.node = node;
        this.mode = mode;
        nodeClient = new NodeWSClient(url);
    }

    public void registerNode() throws IOTException {
        NodeName nodeInfo = createNodeName(node);
        nodeClient.registerNode(nodeInfo);
    }

    public void unRegisterNode() throws IOTException {
        NodeName nodeInfo = createNodeName(node);
        nodeClient.unRegisterNode(nodeInfo);
    }

    public Endpoint registerConsumer(String name,
                                     String type, String path) throws IOTException {
        NodeName nodeInfo = createNodeName(node);

        return nodeClient.registerConsumer(nodeInfo, name, type, path);
    }

    public Endpoint registerProducer(String name,
                                     String type, String path) throws IOTException {
        NodeName nodeInfo = createNodeName(node);
        return nodeClient.registerProducer(nodeInfo, name, type, path);
    }

    public boolean unRegisterProducer(String name, String type, String path) throws IOTException {
        NodeName nodeInfo = createNodeName(node);

        return nodeClient.unRegisterProducer(nodeInfo, name, type, path);

    }

    public boolean unRegisterConsumer(String name, String type, String path) throws IOTException {
        NodeName nodeInfo = createNodeName(node);
        return nodeClient.unRegisterConsumer(nodeInfo, name, type, path);
    }

    private NodeName createNodeName(Node node) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setGroup(node.getName().getGroup());
        nodeInfo.setName(node.getName().getName());
        return node.getName();
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
