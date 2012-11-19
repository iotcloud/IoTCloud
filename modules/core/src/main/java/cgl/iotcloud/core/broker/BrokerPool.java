package cgl.iotcloud.core.broker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BrokerPool implements pool of available brokers in FIFO
 * order.
 */
public class BrokerPool {
    private Logger log = LoggerFactory.getLogger(BrokerPool.class);
    private Map<String, Broker> assignedBrokerMap;
    private LinkedList<Broker> brokerQueue;

    public BrokerPool() {
        brokerQueue = new LinkedList<Broker>();
        assignedBrokerMap = new HashMap<String, Broker>();
    }

    /**
     * Initializes the broker pool
     * by reading broker.config
     */
    public void init() {

    }

    /**
     * clears broker pool
     */
    public void destroy() {
        brokerQueue.clear();
    }

    /**
     * Returns next available broker to connect.
     *
     * @return Broker
     */
    public synchronized Broker getBroker() {
        Broker broker = brokerQueue.remove();
        brokerQueue.add(broker);
        return broker;
    }

    public synchronized Broker getBroker(String id) {
        Broker retVal;
        if (assignedBrokerMap != null && assignedBrokerMap.containsKey(id)) {
            retVal = assignedBrokerMap.get(id);
        } else {
            retVal = getBroker();
            assignedBrokerMap.put(id, retVal);
        }
        return retVal;
    }


    /**
     * Adds broker back to pool
     *
     * @param broker
     */
    public synchronized void releaseBroker(Broker broker) {
        brokerQueue.add(broker);
    }

    public void addBrokers(List<Broker> brokers) {
        brokerQueue.addAll(brokers);
    }
}
