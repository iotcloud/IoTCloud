package cgl.iotcloud.sensors;

import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.gen.node.NodeServiceStub;
import cgl.iotcloud.gen.services.node.xsd.EndpointInfo;
import cgl.iotcloud.gen.services.node.xsd.NodeInfo;
import cgl.iotcloud.gen.services.node.xsd.RegistrationResponse;
import cgl.iotcloud.gen.services.xsd.Endpoint;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

public class NodeWSClient {
    private Logger log = LoggerFactory.getLogger(NodeWSClient.class);

    private NodeServiceStub nodeServiceStub;

    public NodeWSClient(String url) throws IOTException {
        try {
            nodeServiceStub = new NodeServiceStub(url);
        } catch (AxisFault axisFault) {
            handleException("Failed to create the client stub for service: " +
                    "SensorRegistrationService", axisFault);
        }
    }

    public RegistrationResponse registerNode(NodeInfo nodeInfo) throws IOTException {
        try {
            return nodeServiceStub.registerNode(nodeInfo);
        } catch (RemoteException e) {
            handleException("Failed to register the node.." + nodeInfo, e);
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
            nodeServiceStub.registerConsumer(nodeInfo, endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the consumer.." + endpointInfo, e);
        }

        return null;
    }

    public Endpoint registerProducer(NodeInfo nodeInfo, EndpointInfo endpointInfo) throws IOTException {
        try {
            nodeServiceStub.registerProducer(nodeInfo, endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the producer.." + endpointInfo, e);
        }

        return null;
    }

    public Endpoint unRegisterProducer(NodeInfo nodeInfo, EndpointInfo endpointInfo) throws IOTException {
        try {
            nodeServiceStub.unRegisterProducer(nodeInfo, endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the producer.." + endpointInfo, e);
        }

        return null;
    }

    public Endpoint unRegisterConsumer(NodeInfo nodeInfo, EndpointInfo endpointInfo) throws IOTException {
        try {
            nodeServiceStub.unRegisterConsumer(nodeInfo, endpointInfo);
        } catch (RemoteException e) {
            handleException("Failed to un-register the producer.." + endpointInfo, e);
        }

        return null;
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
