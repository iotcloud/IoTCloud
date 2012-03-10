package cgl.iotcloud.core.stream;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.Sender;

public class StreamingSenderFactory {
    public Sender create(Endpoint endpoint) {
        String portStr = endpoint.getProperties().get("PORT");
        int port = Integer.valueOf(portStr);

        String path = endpoint.getProperties().get("PATH");

        String host = endpoint.getProperties().get("HOST");

        return new StreamingSender(path, host, port);
    }
}
