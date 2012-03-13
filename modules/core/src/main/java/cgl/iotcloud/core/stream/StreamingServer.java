package cgl.iotcloud.core.stream;

import cgl.iotcloud.streaming.http.server.HttpClientEndpoint;
import cgl.iotcloud.streaming.http.server.HttpServerEndpoint;
import cgl.iotcloud.streaming.http.server.RoutingRule;
import cgl.iotcloud.streaming.http.server.ServerConfiguration;
import cgl.iotcloud.streaming.http.server.StreamingHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamingServer {
    private Logger log = LoggerFactory.getLogger(StreamingServer.class);

    private Map<String, String> parameters = new HashMap<String, String>();

    private int port;

    private String path = "*";

    private StreamingHttpServer server;

    private ServerConfiguration configuration;

    public StreamingServer(String path, int port, Map<String, String> parameters) {
        this.path = path;
        this.port = port;
        this.parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public void init() {
        configuration = new ServerConfiguration();

        HttpServerEndpoint serverEndpoint = new HttpServerEndpoint(configuration, ".*", 7888);
        HttpClientEndpoint clientEndpoint = new HttpClientEndpoint(configuration, "aa", "localhost", 6888);

        List<HttpClientEndpoint> ruleClientEndpoints = new ArrayList<HttpClientEndpoint>();
        ruleClientEndpoints.add(clientEndpoint);
        RoutingRule rule = new RoutingRule(".*", ruleClientEndpoints);
        configuration.addRoutingRule(rule);


        configuration.setServerEndpoint(serverEndpoint);
        configuration.addClientEndpoint(clientEndpoint);

        server = new StreamingHttpServer(configuration);
    }

    public void start() {
        server.start();
    }

    public void addRoute(String path, String host, int port) {

    }
}
