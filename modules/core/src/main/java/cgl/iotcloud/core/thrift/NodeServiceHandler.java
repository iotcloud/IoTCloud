package cgl.iotcloud.core.thrift;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.NodeCatalog;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.thrift.*;
import org.apache.thrift.TException;

import java.util.*;

public class NodeServiceHandler implements TNodeService.Iface {
    private IoTCloud ioTCloud;

    public NodeServiceHandler(IoTCloud ioTCloud) {
        this.ioTCloud = ioTCloud;
    }

    @Override
    public TResponse registerNode(TNodeId nodeId) throws TException {
        if (nodeId.getName() == null) {
            handleException("Invalid parameter for registering node.. " +
                    "a valid node name must be present");
        }

        NodeName nodeName = new NodeName(nodeId.getName(), nodeId.getGroup());
        try {
            ioTCloud.registerNode(nodeName);
        } catch (IOTException e) {
            handleException("Failed to register node..", e);
        }

        TResponse response = new TResponse();
        response.setStatus(1);
        return response;
    }

    private void handleException(String s, IOTException e) {

    }

    private void handleException(String s) {

    }

    @Override
    public TResponse unRegisterNode(TNodeId nodeId) throws TException {
        if (nodeId.getName() == null) {
            handleException("Invalid parameter for registering node.. " +
                    "a valid node name must be present");
        }

        NodeName nodeName = new NodeName(nodeId.getName(), nodeId.getGroup());

        try {
            ioTCloud.unRegisterNode(nodeName);
        } catch (IOTException e) {
            handleException("Failed to register node..", e);
        }

        TResponse response = new TResponse();
        response.setStatus(1);

        return response;
    }

    @Override
    public TEndpointResponse registerProducer(TNodeId nodeId, TEndpointRequest endpoint) throws TException {
        if (nodeId.getName() == null) {
            handleException("Invalid parameter for registering producer.. " +
                    "a valid node name must be present");
        }

        if (endpoint.getName() == null) {
            handleException("Invalid parameter for registering producer.. " +
                    "a valid name must be present");
        }
        if (endpoint.getPath() == null) {
            handleException("Invalid parameter for registering producer.. " +
                    "a valid path must be present");
        }

        NodeName nodeName = new NodeName(nodeId.getName(), nodeId.getGroup());

        try {
            cgl.iotcloud.core.Endpoint epr = ioTCloud.registerProducer(nodeName, endpoint.getName(),
                    endpoint.getType(), endpoint.getPath());
            return createEndpoint(epr);
        } catch (IOTException e) {
            handleException("Failed to register the producer..", e);
        }

        return null;
    }

    @Override
    public TResponse unRegisterProducer(TNodeId nodeId, TEndpointRequest endpoint) throws TException {
        if (nodeId.getName() == null) {
            handleException("Invalid parameter for registering producer.. " +
                    "a valid node name must be present");
        }

        if (endpoint.getName() == null) {
            handleException("Invalid parameter for registering producer.. " +
                    "a valid name must be present");
        }

        NodeName nodeName = new NodeName(nodeId.getName(), nodeId.getGroup());

        try {
            ioTCloud.unRegisterProducer(nodeName, endpoint.getName());
        } catch (IOTException e) {
            handleException("Failed to register the producer..", e);
        }

        return null;
        //return new Response().setStatus(1);
    }

    @Override
    public TEndpointResponse registerConsumer(TNodeId nodeId, TEndpointRequest endpoint) throws TException {
        if (nodeId.getName() == null) {
            handleException("Invalid parameter for registering consumer.. " +
                    "a valid node name must be present");
        }

        if (endpoint.getName() == null) {
            handleException("Invalid parameter for registering consumer.. " +
                    "a valid name must be present");
        }
        if (endpoint.getPath() == null) {
            handleException("Invalid parameter for registering consumer.. " +
                    "a valid path must be present");
        }

        NodeName nodeName = new NodeName(nodeId.getName(), nodeId.getGroup());

        try {
            cgl.iotcloud.core.Endpoint epr = ioTCloud.registerConsumer(nodeName, endpoint.getName(),
                    endpoint.getType(), endpoint.getPath());
            return createEndpoint(epr);
        } catch (IOTException e) {
            handleException("Failed to register the producer..", e);
        }

        return null;
    }

    @Override
    public TResponse unRegisterConsumer(TNodeId nodeId, TEndpointRequest endpoint) throws TException {
        if (nodeId.getName() == null) {
            handleException("Invalid parameter for registering consumer.. " +
                    "a valid node name must be present");
        }

        if (endpoint.getName() == null) {
            handleException("Invalid parameter for registering consumer.. " +
                    "a valid name must be present");
        }

        NodeName nodeName = new NodeName(nodeId.getName(), nodeId.getGroup());

        try {
            ioTCloud.unRegisterConsumer(nodeName, endpoint.getName());
        } catch (IOTException e) {
            handleException("Failed to register the producer..", e);
        }

        return new TResponse().setStatus(1);
    }

    @Override
    public List<TNodeId> getNodes() throws TException {
        NodeCatalog nodeCatalog = ioTCloud.getNodeCatalog();

        List<TNodeId> infos = new ArrayList<TNodeId>();
        int count = 0;
        for (NodeInformation n : nodeCatalog.getNodes()) {
            infos.add(createNodeInfo(n));
        }
        return infos;
    }

    @Override
    public TNode getNode(TNodeId id) throws TException {
        NodeCatalog catalog = ioTCloud.getNodeCatalog();

        NodeName nodeName = new NodeName(id.getName(), id.getGroup());

        NodeInformation nodeInformation = catalog.getNode(nodeName);

        TEndpointResponse consumers[] = new TEndpointResponse[nodeInformation.getConsumers().size()];
        TEndpointResponse producers[] = new TEndpointResponse[nodeInformation.getProducers().size()];

        int count = 0;
        for (cgl.iotcloud.core.Endpoint e : nodeInformation.getConsumers()) {
            consumers[count++] = createEndpoint(e);
        }

        count = 0;
        for (cgl.iotcloud.core.Endpoint e : nodeInformation.getProducers()) {
            producers[count++] = createEndpoint(e);
        }

        TNode nodeDetail = new TNode();
        nodeDetail.setConsumers(Arrays.asList(consumers));
        nodeDetail.setProducers(Arrays.asList(producers));

        nodeDetail.setName(id.getName());
        nodeDetail.setGroup(id.getGroup());


       return nodeDetail;
    }

    private TEndpointResponse createEndpoint(Endpoint epr) {
        TEndpointResponse endpoint = new TEndpointResponse();

        endpoint.setAddress(epr.getAddress());
        endpoint.setName(epr.getName());

        Set<Map.Entry<String, String>> props = epr.getProperties().entrySet();
        int i = 0;
        for (Map.Entry<String, String> e : props) {
            TProperty p = new TProperty();
            p.setName(e.getKey());
            p.setValue(e.getValue());
            endpoint.addToProperties(p);
        }

        return endpoint;
    }

    private TNodeId createNodeInfo(NodeInformation nodeInformation) {
        TNodeId nodeInfo = new TNodeId();

        nodeInfo.setGroup(nodeInformation.getName().getGroup());
        nodeInfo.setName(nodeInformation.getName().getName());

        return nodeInfo;
    }
}
