package cgl.iotcloud.sensors;


import cgl.iotcloud.core.*;
import cgl.iotcloud.core.broker.JMSListenerFactory;
import cgl.iotcloud.core.broker.JMSSenderFactory;
import cgl.iotcloud.core.sensor.NodeName;
import cgl.iotcloud.core.stream.StreamingListenerFactory;
import cgl.iotcloud.core.stream.StreamingSenderFactory;

import java.util.HashMap;
import java.util.Map;

public class Node implements cgl.iotcloud.core.sensor.Node {
    private NodeName name;

    private Map<String, Listener> listeners = new HashMap<String, Listener>();

    private Map<String, Sender> senders = new HashMap<String, Sender>();

    private NodeAdaptor adaptor = null;

    protected Node(NodeName name, String url) throws IOTException {
        this.name = name;
        this.adaptor = new NodeAdaptor(this, url);
    }

    @Override
    public Sender newSender(String name, String type, String path) throws IOTException {
        if (senders.containsKey(name)) {
            return senders.get(name);
        }

        Endpoint endpoint = adaptor.registerProducer(name, type, path);

        Sender sender = null;
        if (type.equals(Constants.MESSAGE_TYPE_BLOCK)) {
            JMSSenderFactory factory = new JMSSenderFactory();
            sender = factory.create(endpoint);
        } else if (type.equals(Constants.MESSAGE_TYPE_STREAM)) {
            StreamingSenderFactory factory = new StreamingSenderFactory();
            sender = factory.create(endpoint);
        }

        if (sender != null) {
            senders.put(name, sender);
            sender.init();
            sender.start();
        }

        return sender;
    }

    @Override
    public Listener newListener(String name, String type, String path) throws IOTException {
        if (listeners.containsKey(name)) {
            return listeners.get(name);
        }
        Endpoint endpoint = adaptor.registerConsumer(name, type, path);

        Listener listener = null;
        if (type.equals(Constants.MESSAGE_TYPE_BLOCK)) {
            JMSListenerFactory factory = new JMSListenerFactory();
            listener = factory.create(endpoint);
        } else if (type.equals(Constants.MESSAGE_TYPE_STREAM)) {
            StreamingListenerFactory factory = new StreamingListenerFactory();
            listener = factory.create(endpoint);
        }

        if (listener != null) {
            listeners.put(name, listener);

            listener.init();
            listener.start();
        }

        return listener;
    }

    @Override
    public void stop() throws IOTException {
        for (Sender sender : senders.values()) {
            sender.stop();
            sender.destroy();
        }

        for (Listener listener : listeners.values()) {
            listener.stop();
            listener.destroy();
        }

        senders.clear();
        listeners.clear();

        adaptor.unRegisterNode();
    }

    @Override
    public void start() throws IOTException {
        adaptor.registerNode();
    }

    @Override
    public NodeName getName() {
        return name;
    }
}
