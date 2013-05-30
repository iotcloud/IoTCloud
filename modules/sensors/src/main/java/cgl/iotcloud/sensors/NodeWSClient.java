package cgl.iotcloud.sensors;

import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.gen.node.NodeServiceStub;
import cgl.iotcloud.gen.services.node.xsd.EndpointInfo;
import cgl.iotcloud.gen.services.node.xsd.NodeDetail;
import cgl.iotcloud.gen.services.node.xsd.NodeInfo;
import cgl.iotcloud.gen.services.node.xsd.RegistrationResponse;
import cgl.iotcloud.gen.services.node.xsd.Endpoint;
import cgl.iotcloud.gen.services.node.xsd.Property;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeWSClient implements Client {
    private Logger log = LoggerFactory.getLogger(NodeWSClient.class);

    private NodeServiceStub nodeServiceStub;

    public NodeWSClient(String url) throws IOTException {
        try {
            nodeServiceStub = new NodeServiceStub(url + "/soap/services/NodeService");
        } catch (AxisFault axisFault) {
            handleException("Failed to create the client stub for service: " +
                    "SensorRegistrationService", axisFault);
        }
    }

    public boolean registerNode(NodeName nodeInfo) throws IOTException {
        try {
            nodeServiceStub.registerNode(createNodeInfo(nodeInfo));
        } catch (RemoteException e) {
            handleException("Failed to register the node.." + nodeInfo.getName(), e);
        }
        return true;
    }

    public boolean unRegisterNode(NodeName nodeInfo) throws IOTException {
        try {
            nodeServiceStub.unRegisterNode(createNodeInfo(nodeInfo));
        } catch (RemoteException e) {
            handleException("Failed to unregister the node.." + nodeInfo, e);
        }
        return true;
    }

    public cgl.iotcloud.core.Endpoint registerConsumer(NodeName nodeInfo, String name,
                                     String type, String path) throws IOTException {
        try {
            EndpointInfo endpointInfo = createEndpointInfo(name, type, path);
            Endpoint e = nodeServiceStub.registerConsumer(createNodeInfo(nodeInfo), endpointInfo);
            return createEndpoint(e);
        } catch (RemoteException e) {
            handleException("Failed to un-register the consumer.." + name, e);
        }

        return null;
    }

    public cgl.iotcloud.core.Endpoint registerProducer(NodeName nodeInfo, String name,
                                     String type, String path) throws IOTException {
        try {
            EndpointInfo endpointInfo = createEndpointInfo(name, type, path);
            Endpoint e = nodeServiceStub.registerProducer(createNodeInfo(nodeInfo), endpointInfo);
            return createEndpoint(e);
        } catch (RemoteException e) {
            handleException("Failed to un-register the producer.." + name, e);
        }

        return null;
    }

    public boolean unRegisterProducer(NodeName nodeInfo, String name,
                                      String type, String path) throws IOTException {
        try {
            EndpointInfo endpointInfo = createEndpointInfo(name, type, path);
            RegistrationResponse response = nodeServiceStub.unRegisterProducer(createNodeInfo(nodeInfo), endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the producer.." + name, e);
        }

        return true;
    }

    public boolean unRegisterConsumer(NodeName nodeInfo, String name,
                                      String type, String path) throws IOTException {
        EndpointInfo endpointInfo = createEndpointInfo(name, type, path);
        try {
            nodeServiceStub.unRegisterConsumer(createNodeInfo(nodeInfo), endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the producer.." + endpointInfo, e);
        }

        return true;
    }

    public List<NodeName> getNodeList() throws IOTException {
        try {
            NodeInfo nodeInfo[] = nodeServiceStub.getRegisteredNodes();
            List<NodeName> nodes = new ArrayList<NodeName>();
            for (NodeInfo n : nodeInfo) {
                nodes.add(createNodeName(n));
            }
            return nodes;
        } catch (RemoteException e) {
            handleException("Failed to get the node list..", e);
        }
        return null;
    }

    public NodeInformation getNode(NodeName name) throws IOTException {
        try {
            NodeDetail detail = nodeServiceStub.getNode(createNodeInfo(name));

            return createNodeInformation(detail);
        } catch (RemoteException e) {
            handleException("Failed to get node.. " + name, e);
        }

        return null;
    }

    private NodeInformation createNodeInformation(NodeDetail detail) {
        NodeInfo info = detail.getName();

        NodeInformation nodeInformation = new NodeInformation(createNodeName(info));

        Endpoint consumers[] = detail.getConsumers();
        Endpoint producers[] = detail.getProducers();

        if (consumers != null) {
            for (Endpoint e : consumers) {
                nodeInformation.addConsumer(createEndpoint(e));
            }
        }

        if (producers != null) {
            for (Endpoint e : producers) {
                nodeInformation.addProducer(createEndpoint(e));
            }
        }

        return nodeInformation;
    }

    private cgl.iotcloud.core.Endpoint createEndpoint(Endpoint e) {
        cgl.iotcloud.core.Endpoint endpoint = new JMSEndpoint();
        endpoint.setAddress(e.getAddress());
        endpoint.setName(e.getName());

        Property []props = e.getProperties();
        Map<String, String> propsMap = new HashMap<String, String>();
        for (Property p : props) {
            propsMap.put(p.getName(), p.getValue());
        }
        endpoint.setProperties(propsMap);
        return endpoint;
    }

    private NodeInfo createNodeInfo(NodeName nodeName) {
        NodeInfo nodeInfo = new NodeInfo();

        nodeInfo.setGroup(nodeName.getGroup());
        nodeInfo.setName(nodeName.getName());

        return nodeInfo;
    }

    private EndpointInfo createEndpointInfo(String name, String type, String path) {
        EndpointInfo info = new EndpointInfo();
        info.setName(name);
        info.setType(type);
        info.setPath(path);
        return info;
    }

    private NodeName createNodeName(NodeInfo nodeInfo) {
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
}
