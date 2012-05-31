package cgl.iotcloud.streaming.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingHttpServer {
    private static Logger log = LoggerFactory.getLogger(StreamingHttpServer.class);

    private ServerConfiguration configuration;

    public StreamingHttpServer(ServerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        log.info("Starting the Streaming HTTP Server.........");

        // start the listening port
        configuration.getServerEndpoint().start();
    }

    public void stop() {
        configuration.getServerEndpoint().stop();
    }
}
