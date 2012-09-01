package cgl.iotcloud.services.node;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.services.Endpoint;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeService {
    private Logger log = LoggerFactory.getLogger(NodeService.class);

    public RegistrationResponse registerNode(NodeInfo nodeInfo) throws AxisFault {
        IoTCloud ioTCloud = retrieveIoTCloud();

        if (nodeInfo.getName() == null) {
            handleException("Invalid parameter for registering node.. " +
                    "a valid node name must be present");
        }

        NodeName nodeName = new NodeName(nodeInfo.getName(), nodeInfo.getGroup());
        ioTCloud.registerNode(nodeName);

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

        ioTCloud.unRegisterNode(nodeName);

        RegistrationResponse response = new RegistrationResponse();
        response.setStatus("Success");

        return response;
    }

    public Endpoint registerProducer(NodeInfo nodeInfo,
                                     EndpointRequestInfo endpoint) throws AxisFault {
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

    public Endpoint registerConsumer(NodeInfo nodeInfo,
                                     EndpointRequestInfo endpoint) throws AxisFault {
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
