package cgl.iotcloud.core.stream;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.Sender;

/**
 * Creates a Streaming sender from the endpoint
 */
public class StreamingSenderFactory {
    public Sender create(Endpoint endpoint) {
        String portStr = endpoint.getProperties().get("PORT");
        int port = Integer.valueOf(portStr);

        String path = endpoint.getProperties().get("PATH");

        String host = endpoint.getProperties().get("HOST");

        return new StreamingSender(path, host, port);
    }
}
