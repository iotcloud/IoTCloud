package cgl.iotcloud.streaming.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StreamingHttpServer {
    private static Logger log = LoggerFactory.getLogger(StreamingHttpServer.class);

    public void start() {
        log.info("Starting the Streaming HTTP Server.........");

        ServerConfiguration configuration = new ServerConfiguration();

        HttpServerEndpoint serverEndpoint = new HttpServerEndpoint(configuration, ".*", 7888);
        HttpClientEndpoint clientEndpoint = new HttpClientEndpoint(configuration, "aa", "localhost", 6888);

        List<HttpClientEndpoint> ruleClientEndpoints = new ArrayList<HttpClientEndpoint>();
        ruleClientEndpoints.add(clientEndpoint);
        RoutingRule rule = new RoutingRule(".*", ruleClientEndpoints);
        configuration.addRoutingRule(rule);


        configuration.addServerEndpoint(serverEndpoint);
        configuration.addClientEndpoint(clientEndpoint);

        // start the listening port
        serverEndpoint.start();
    }
}
