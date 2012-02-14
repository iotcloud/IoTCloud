package cgl.iotcloud.core.broker;

import java.util.Map;
import java.util.Set;

/**
 * Creates a Connections from the given settings
 */
public class ConnectionsFactory {
    /**
     * Create connection from the given params
     *
     * @param name name of the connections
     * @param params parameters
     * @return connections
     */
    public Connections create(String name, Map<String, String> params) {
        Connections connections = new Connections(name);

        Set<Map.Entry<String, String>> valueSet = params.entrySet();
        for (Map.Entry<String, String> v : valueSet) {
            connections.addParameter(v.getKey(), v.getValue());
        }

        return connections;
    }
}
