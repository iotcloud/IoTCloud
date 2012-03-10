package cgl.iotcloud.core.stream;

import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.Listener;

public class StreamingListenerFactory {
    public Listener create(Endpoint endpoint) {
        String portStr = endpoint.getProperties().get("PORT");
        int port = Integer.valueOf(portStr);

        String path = endpoint.getProperties().get("PATH");

        return new StreamingListener(port, path);
    }
}
