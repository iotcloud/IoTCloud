package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.Listener;
import cgl.iotcloud.core.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A application i.e Sensor or Client creates a Node to communicate.
 */
public class Node {
    private Logger log = LoggerFactory.getLogger(Node.class);

    private List<Listener> listeners = new ArrayList<Listener>();

    private List<Sender> senders = new ArrayList<Sender>();

    public NodeName getName() {
        return null;
    }

    public void start() {

    }

    public void stop() {

    }

    public Listener getSubscriber(String name) {
        return null;
    }

    public Sender getSender(String name) {
        return null;
    }
}
