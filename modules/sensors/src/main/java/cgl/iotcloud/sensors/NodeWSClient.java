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

public class NodeWSClient {
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

    public RegistrationResponse registerNode(NodeInfo nodeInfo) throws IOTException {
        try {
            return nodeServiceStub.registerNode(nodeInfo);
        } catch (RemoteException e) {
            handleException("Failed to register the node.." + nodeInfo.getName(), e);
        }
        return null;
    }

    public RegistrationResponse unRegisterNode(NodeInfo nodeInfo) throws IOTException {
        try {
            return nodeServiceStub.unRegisterNode(nodeInfo);
        } catch (RemoteException e) {
            handleException("Failed to unregister the node.." + nodeInfo, e);
        }
        return null;
    }

    public Endpoint registerConsumer(NodeInfo nodeInfo, EndpointInfo endpointInfo) throws IOTException {
        try {
            return nodeServiceStub.registerConsumer(nodeInfo, endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the consumer.." + endpointInfo, e);
        }

        return null;
    }

    public Endpoint registerProducer(NodeInfo nodeInfo, EndpointInfo endpointInfo) throws IOTException {
        try {
            return nodeServiceStub.registerProducer(nodeInfo, endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the producer.." + endpointInfo, e);
        }

        return null;
    }

    public boolean unRegisterProducer(NodeInfo nodeInfo, EndpointInfo endpointInfo) throws IOTException {
        try {
            RegistrationResponse response = nodeServiceStub.unRegisterProducer(nodeInfo, endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the producer.." + endpointInfo, e);
        }

        return true;
    }

    public boolean unRegisterConsumer(NodeInfo nodeInfo, EndpointInfo endpointInfo) throws IOTException {
        try {
            nodeServiceStub.unRegisterConsumer(nodeInfo, endpointInfo);
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

        for (Endpoint e : consumers) {
            nodeInformation.addConsumer(createEndpoint(e));
        }

        for (Endpoint e : producers) {
            nodeInformation.addProducer(createEndpoint(e));
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
