package cgl.iotcloud.services.node;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.NodeCatalog;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.services.Endpoint;
import cgl.iotcloud.services.Property;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class NodeService {
    private Logger log = LoggerFactory.getLogger(NodeService.class);

    public RegistrationResponse registerNode(NodeInfo nodeInfo) throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        if (nodeInfo.getName() == null) {
            handleException("Invalid parameter for registering node.. " +
                    "a valid node name must be present");
        }

        NodeName nodeName = new NodeName(nodeInfo.getName(), nodeInfo.getGroup());
        try {
            ioTCloud.registerNode(nodeName);
        } catch (IOTException e) {
            handleException("Failed to register node..", e);
        }

        RegistrationResponse response = new RegistrationResponse();
        response.setStatus("Success");

        return response;
    }

    public RegistrationResponse unRegisterNode(NodeInfo nodeInfo) throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        if (nodeInfo.getName() == null) {
            handleException("Invalid parameter for registering node.. " +
                    "a valid node name must be present");
        }

        NodeName nodeName = new NodeName(nodeInfo.getName(), nodeInfo.getGroup());

        try {
            ioTCloud.unRegisterNode(nodeName);
        } catch (IOTException e) {
            handleException("Failed to register node..", e);
        }

        RegistrationResponse response = new RegistrationResponse();
        response.setStatus("Success");

        return response;
    }

    public Endpoint registerProducer(NodeInfo nodeInfo,
                                     EndpointInfo endpoint) throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        if (nodeInfo.getName() == null) {
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

        NodeName nodeName = new NodeName(nodeInfo.getName(), nodeInfo.getGroup());

        try {
            ioTCloud.registerProducer(nodeName, endpoint.getName(),
                    endpoint.getType(), endpoint.getPath());
        } catch (IOTException e) {
            handleException("Failed to register the producer..", e);
        }

        return null;
    }

    public RegistrationResponse unRegisterProducer(NodeInfo nodeInfo,
                                     EndpointInfo endpoint) throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        if (nodeInfo.getName() == null) {
            handleException("Invalid parameter for registering producer.. " +
                    "a valid node name must be present");
        }

        if (endpoint.getName() == null) {
            handleException("Invalid parameter for registering producer.. " +
                    "a valid name must be present");
        }

        NodeName nodeName = new NodeName(nodeInfo.getName(), nodeInfo.getGroup());

        try {
            ioTCloud.unRegisterProducer(nodeName, endpoint.getName());
        } catch (IOTException e) {
            handleException("Failed to register the producer..", e);
        }

        return new RegistrationResponse("success");
    }

    public Endpoint registerConsumer(NodeInfo nodeInfo,
                                     EndpointInfo endpoint) throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        if (nodeInfo.getName() == null) {
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

        NodeName nodeName = new NodeName(nodeInfo.getName(), nodeInfo.getGroup());

        try {
            ioTCloud.registerConsumer(nodeName, endpoint.getName(),
                    endpoint.getType(), endpoint.getPath());
        } catch (IOTException e) {
            handleException("Failed to register the producer..", e);
        }

        return null;
    }

    public RegistrationResponse unRegisterConsumer(NodeInfo nodeInfo,
                                     EndpointInfo endpoint) throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        if (nodeInfo.getName() == null) {
            handleException("Invalid parameter for registering consumer.. " +
                    "a valid node name must be present");
        }

        if (endpoint.getName() == null) {
            handleException("Invalid parameter for registering consumer.. " +
                    "a valid name must be present");
        }

        NodeName nodeName = new NodeName(nodeInfo.getName(), nodeInfo.getGroup());

        try {
            ioTCloud.unRegisterConsumer(nodeName, endpoint.getName());
        } catch (IOTException e) {
            handleException("Failed to register the producer..", e);
        }

        return new RegistrationResponse("success");
    }

    public NodeInfo[] getRegisteredNodes() throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        NodeCatalog nodeCatalog = ioTCloud.getNodeCatalog();

        NodeInfo []infos = new NodeInfo[nodeCatalog.getNodes().size()];
        int count = 0;
        for (NodeInformation n : nodeCatalog.getNodes()) {
            infos[count++] = createNodeInfo(n);
        }
        return infos;
    }

    public NodeDetail getNode(NodeInfo nodeInfo) throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        NodeCatalog catalog = ioTCloud.getNodeCatalog();

        NodeName nodeName = createNodeName(nodeInfo);

        NodeInformation nodeInformation = catalog.getNode(nodeName);

        Endpoint consumers[] = new Endpoint[nodeInformation.getConsumers().size()];
        Endpoint producers[] = new Endpoint[nodeInformation.getProducers().size()];

        int count = 0;
        for (cgl.iotcloud.core.Endpoint e : nodeInformation.getConsumers()) {
            consumers[count++] = createEndpoint(e);
        }

        count = 0;
        for (cgl.iotcloud.core.Endpoint e : nodeInformation.getProducers()) {
            producers[count++] = createEndpoint(e);
        }

        NodeDetail nodeDetail = new NodeDetail();
        nodeDetail.setConsumers(consumers);
        nodeDetail.setProducers(producers);

        nodeDetail.setName(nodeInfo);

        return nodeDetail;
    }

    private Endpoint createEndpoint(cgl.iotcloud.core.Endpoint epr) throws AxisFault {
        Endpoint endpoint = new Endpoint();

        endpoint.setAddress(epr.getAddress());

        Set<Map.Entry<String, String>> props = epr.getProperties().entrySet();
        Property properties[] = new Property[props.size()];
        int i = 0;
        for (Map.Entry<String, String> e : props) {
            Property p = new Property();
            p.setName(e.getKey());
            p.setValue(e.getValue());
            properties[i++] = p;
        }
        endpoint.setProperties(properties);
        return endpoint;
    }

    private NodeName createNodeName(NodeInfo nodeInfo) {
        return new NodeName(nodeInfo.getName(), nodeInfo.getGroup());
    }

    private NodeInfo createNodeInfo(NodeInformation nodeInformation) {
        NodeInfo nodeInfo = new NodeInfo();

        nodeInfo.setGroup(nodeInformation.getName().getGroup());
        nodeInfo.setName(nodeInformation.getName().getName());

        return nodeInfo;
    }

    private IoTCloud retrieveIoTCloud() throws AxisFault {
        MessageContext msgCtx = MessageContext.getCurrentMessageContext();

        IoTCloud cloud = (IoTCloud) msgCtx.getProperty(
                Constants.SENSOR_CLOUD_AXIS2_PROPERTY);
        if (cloud == null) {
            handleException("Error at the server... Error retrieving IOTCloud instance..");
        }
        return cloud;
    }

    private void handleException(String msg) throws AxisFault {
        log.error(msg);
        throw new AxisFault(msg);
    }

    private void handleException(String msg, Exception e) throws AxisFault {
        log.error(msg);
        throw new AxisFault(msg);
    }
}
