package cgl.iotcloud.core;

import cgl.iotcloud.core.broker.BrokerPool;
import cgl.iotcloud.core.config.SCCConfigurationFactory;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.jetty.SGCHTTPServer;
import cgl.iotcloud.core.stream.StreamingServer;
import cgl.iotcloud.core.tomcat.TomcatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import javax.jcr.RepositoryException;

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

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	log.info("Shutting down IOTCloud...");
            	cloud.sendIOTCloudShutDownMssg();
            	
            	if(cloud.isContentRepositoryAvail)
            	{
	            	try {
						cloud.clearContentRepository();
						cloud.shutDownContentRepoSession();
					} catch (RepositoryException e) {
						// TODO Auto-generated catch block
						log.error(" ********** Failed to CLEAN the Content Repository ********** ");
					}
            	}
            	
                server.stop();
            }
        });

        new Thread(
                new Runnable() {
                    public void run() {
                        // start the HTTP server
                        server.start();
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
