package cgl.iotcloud.sensors;

import cgl.iotcloud.core.*;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.gen.services.node.xsd.Endpoint;
import cgl.iotcloud.thrift.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeThriftClient implements Client {
    private static Log log = LogFactory.getLog(NodeThriftClient.class);

    private TNodeService.Client client;

    private TTransport transport;

    public NodeThriftClient(String host, int port) {
        init(host, port);
    }

    public boolean registerNode(NodeName nodeInfo) throws IOTException {
        try {
            client.registerNode(createNodeInfo(nodeInfo));
        } catch (TException e) {
            handleException("Failed to register the node.." + nodeInfo.getName(), e);
        }
        return true;
    }

    public boolean unRegisterNode(NodeName nodeInfo) throws IOTException {
        try {
            client.unRegisterNode(createNodeInfo(nodeInfo));
        } catch (TException e) {
            handleException("Failed to unregister the node.." + nodeInfo, e);
        }
        return true;
    }

    public cgl.iotcloud.core.Endpoint registerConsumer(NodeName nodeInfo, String name,
                                                       String type, String path) throws IOTException {
        try {
            TEndpointRequest epr = new TEndpointRequest(name, type, path);
            TEndpointResponse response = client.registerConsumer(createNodeInfo(nodeInfo), epr);
            return createEndpoint(response);
        } catch (TException e) {
            handleException("Failed to un-register the consumer.." + name, e);
        }

        return null;
    }

    public cgl.iotcloud.core.Endpoint registerProducer(NodeName nodeInfo, String name,
                                     String type, String path) throws IOTException {
        try {
            TEndpointRequest epr = new TEndpointRequest(name, type, path);
            TEndpointResponse response = client.registerProducer(createNodeInfo(nodeInfo), epr);
            return createEndpoint(response);
        } catch (TException e) {
            handleException("Failed to un-register the producer.." + name, e);
        }

        return null;
    }

    public boolean unRegisterProducer(NodeName nodeInfo, String name,
                                      String type, String path) throws IOTException {
        try {
            TEndpointRequest epr = new TEndpointRequest(name, type, path);
            client.unRegisterProducer(createNodeInfo(nodeInfo), epr);
        } catch (TException e) {
            handleException("Failed to un-register the producer.." + name, e);
        }

        return true;
    }

    public boolean unRegisterConsumer(NodeName nodeInfo, String name,
                                      String type, String path) throws IOTException {
        try {
            TEndpointRequest epr = new TEndpointRequest(name, type, path);
            client.unRegisterConsumer(createNodeInfo(nodeInfo), epr);
        } catch (TException e) {
            handleException("Failed to un-register the producer.." + name, e);
        }

        return true;
    }

    public List<NodeName> getNodeList() throws IOTException {
        try {
            List<TNodeId> nodeInfo = client.getNodes();
            List<NodeName> nodes = new ArrayList<NodeName>();
            for (TNodeId n : nodeInfo) {
                nodes.add(createNodeName(n));
            }
            return nodes;
        } catch (TException e) {
            handleException("Failed to get the node list..", e);
        }
        return null;
    }

    public NodeInformation getNode(NodeName name) throws IOTException {
        try {
            TNode detail = client.getNode(createNodeInfo(name));

            return createNodeInformation(detail);
        } catch (TException e) {
            handleException("Failed to get node.. " + name, e);
        }

        return null;
    }

    private NodeInformation createNodeInformation(TNode detail) {
        NodeInformation nodeInformation = new NodeInformation(new NodeName(detail.getName(), detail.getGroup()));

        List<TEndpointResponse> consumers = detail.getConsumers();
        List<TEndpointResponse> producers = detail.getProducers();

        if (consumers != null) {
            for (TEndpointResponse e : consumers) {
                nodeInformation.addConsumer(createEndpoint(e));
            }
        }

        if (producers != null) {
            for (TEndpointResponse e : producers) {
                nodeInformation.addProducer(createEndpoint(e));
            }
        }

        return nodeInformation;
    }

    private cgl.iotcloud.core.Endpoint createEndpoint(TEndpointResponse e) {
        cgl.iotcloud.core.Endpoint endpoint = new JMSEndpoint();
        endpoint.setAddress(e.getAddress());
        endpoint.setName(e.getName());

        List<TProperty> props = e.getProperties();
        Map<String, String> propsMap = new HashMap<String, String>();
        for (TProperty p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        endpoint.setProperties(propsMap);
        return endpoint;
    }

    private TNodeId createNodeInfo(NodeName nodeName) {
        TNodeId nodeInfo = new TNodeId();

        nodeInfo.setGroup(nodeName.getGroup());
        nodeInfo.setName(nodeName.getName());

        return nodeInfo;
    }

    private NodeName createNodeName(TNodeId nodeInfo) {
        return new NodeName(nodeInfo.getName(), nodeInfo.getGroup());
    }

    private void handleException(String s, Exception e) throws IOTException {
        log.error(s, e);
        throw new IOTException(s, e);
    }

    private void handleException(String s) throws IOTException {
        log.error(s);
        throw new IOTException(s);
    }


    private void init(String host, int port) {
        try {
            transport = new TSocket(host, port);
            transport = new TFramedTransport(transport);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            client = new TNodeService.Client(protocol);

        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    private cgl.iotcloud.core.Endpoint createEndpoint(TEndpointResponse epr, String type, String name) {
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
        for (TProperty p : epr.getProperties()) {
            props.put(p.getName(), p.getValue());
        }

        endpoint.setProperties(props);

        return endpoint;
    }

    private void destroy() {
        transport.close();
    }
}
