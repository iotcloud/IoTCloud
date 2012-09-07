package cgl.iotcloud.core.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The class holds the information about the broker and its configurations.
 * Both clients, servers and sensor grid uses this information to talk to the broker.
 */
public class Broker {
    private Logger log = LoggerFactory.getLogger(Broker.class);
    private String brokerName;
    private Map<String, Connections> connections = new HashMap<String, Connections>();

    /**
     * Initialize the broker
     */
    public void init() {
        // initialize all the jms connection factories
        Collection<Connections> c = connections.values();
        for (Connections connections : c) {
            connections.init();
        }
    }

    /**
     * Add connection information to the broker
     *
     * @param connections te connections to be added
     */
    public void addConnections(Connections connections) {
        this.connections.put(connections.getName(), connections);
    }

    /**
     * Get a connection information by the name
     *
     * @param name name of the connection information
     * @return connection information
     */
    public Connections getConnections(String name) {
        return connections.get(name);
    }
    
    public void setName(String name){
    	this.brokerName = name;
    }
    
    public String getName(){
    	return brokerName;
    }
}
