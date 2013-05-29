package cgl.iotcloud.sensors;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.IOTException;
import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.broker.JMSListenerFactory;
import cgl.iotcloud.core.broker.JMSSenderFactory;
import cgl.iotcloud.core.sensor.NodeInformation;
import cgl.iotcloud.core.sensor.NodeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NodeClient {
    private Logger log = LoggerFactory.getLogger(NodeClient.class);

    private NodeWSClient wsClient = null;

    public NodeClient(String url) throws IOTException {
        try {
            wsClient = new NodeWSClient(url);
        } catch (IOTException e) {
            handleException("Failed to create client.", e);
        }
    }

    public List<NodeName> getNodeList() throws IOTException {
        return wsClient.getNodeList();
    }

    public NodeInformation getNode(NodeName name) throws IOTException {
        return wsClient.getNode(name);
    }

    public Listener newListener(cgl.iotcloud.core.Endpoint endpoint) {
        JMSListenerFactory factory = new JMSListenerFactory();
        return factory.create(endpoint);
    }

    public Sender newSender(cgl.iotcloud.core.Endpoint endpoint) {
        JMSSenderFactory factory = new JMSSenderFactory();
        return factory.create(endpoint);
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
