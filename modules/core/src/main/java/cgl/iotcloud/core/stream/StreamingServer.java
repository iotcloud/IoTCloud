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

/**
 * Start a streaming message server programmatically. Consider using a third
 * party library for doing this.
 */
public class StreamingServer {
    private Logger log = LoggerFactory.getLogger(StreamingServer.class);

    private Map<String, String> parameters = new HashMap<String, String>();
    /** port to listen to */
    private int port;
    /** path to listen to */
    private String path = "*";
    /** the actual http server */
    private StreamingHttpServer server;
    /** configuration of the server */
    private ServerConfiguration configuration;

    /**
     * Create a streaming server
     *
     * @param path path to listen
     * @param port port to listen
     * @param parameters parameters
     */
    public StreamingServer(String path, int port, Map<String, String> parameters) {
        this.path = path;
        this.port = port;
        this.parameters = parameters;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * get the port of the server
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * get the path of the server
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Initialize the server
     */
    public void init() {
        configuration = new ServerConfiguration();

        HttpServerEndpoint serverEndpoint = new HttpServerEndpoint(configuration, ".*", 7888);
        HttpClientEndpoint clientEndpoint =
                new HttpClientEndpoint(configuration, "aa", "localhost", 6888);

        List<HttpClientEndpoint> ruleClientEndpoints = new ArrayList<HttpClientEndpoint>();
        ruleClientEndpoints.add(clientEndpoint);
        RoutingRule rule = new RoutingRule(".*", ruleClientEndpoints);
        configuration.addRoutingRule(rule);


        configuration.setServerEndpoint(serverEndpoint);
        configuration.addClientEndpoint(clientEndpoint);

        server = new StreamingHttpServer(configuration);
    }

    /**
     * start the server
     */
    public void start() {
        log.info("Starting the streaming server...");
        server.start();
    }

    /**
     * stop the server
     */
    public void stop() {
        log.info("Shutting down the streaming server...");
        server.stop();
    }

    /**
     * add a route to the server
     * @param serverPath path
     * @param host host
     * @param port port
     * @param clientPath client sending path
     */
    public void addRoute(String serverPath, String host, int port, String clientPath) {
        HttpClientEndpoint clientEndpoint =
                new HttpClientEndpoint(configuration, clientPath, host, port);

        RoutingRule existingRule = configuration.getRoutingRule(serverPath);
        if (existingRule == null) {
            List<HttpClientEndpoint> ruleClientEndpoints = new ArrayList<HttpClientEndpoint>();
            ruleClientEndpoints.add(clientEndpoint);
            RoutingRule rule = new RoutingRule(".*", ruleClientEndpoints);
            configuration.addRoutingRule(rule);
        } else {
            existingRule.addTargetEndpoint(clientEndpoint);
        }
    }

    public void removeRoute(String serverPath, String host, int port, String clientPath) {

    }
}
