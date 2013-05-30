package cgl.iotcloud.core;

import cgl.iotcloud.core.config.SCCConfigurationFactory;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.stream.StreamingServer;
import cgl.iotcloud.core.thrift.ThriftServer;
import cgl.iotcloud.core.tomcat.TomcatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starts a SGX server. This is the mail class for starting a SGX Server. This class creates
 * the necessary configurations and start the server.
 */
public class ServerManager {
    private static Logger log = LoggerFactory.getLogger(ServerManager.class);

    public static void main(String[] args) {
        log.info("Starting the SCCloud");

        SCCConfigurationFactory fac = new SCCConfigurationFactory();
        SCConfiguration config = fac.create();
        
        final IoTCloud cloud = new IoTCloud(config);
        // initialize the configurations
        try {
            cloud.init();
        } catch (Exception e) {
            log.error("Error initializing the IOTCloud...", e);
            return;
        }

        final TomcatServer server = new TomcatServer(cloud);
        final ThriftServer thriftServer = new ThriftServer(cloud);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	log.info("Shutting down IOTCloud...");
            	cloud.destroy();
                server.stop();
                thriftServer.stop();
            }
        });

        new Thread(
                new Runnable() {
                    public void run() {
                        // start the HTTP server
                        server.start();
                    }
                }).start();

        new Thread(
                new Runnable() {
                    public void run() {
                        // start the HTTP server
                        try {
                            thriftServer.start();
                        } catch (IOTException e) {
                            log.error("Failed to start the thrift server");
                        }
                    }
                }).start();

        StreamingServer streamingServer = config.getStreamingServer();
        if (streamingServer != null) {
            streamingServer.init();

            streamingServer.start();
        } else {
            log.warn("Streaming server not found......");
        }
    }
}
