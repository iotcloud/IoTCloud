package cgl.iotcloud.core.config;

import cgl.iotcloud.core.broker.Broker;
import cgl.iotcloud.core.broker.BrokerPool;
import cgl.iotcloud.core.stream.StreamingServer;

/**
 * This is the main configuration of the sensor cloud. It encapsulates all the
 * configurations including, broker, clients and sensors information.
 */
public class SCConfiguration {
	private String brokerConfigFile = "broker-config.xml";

	//private Broker broker = null;
	private BrokerPool brokerPool;

	private StreamingServer streamingServer = null;

	public void init() {
		brokerPool = BrokerPool.getInstance();
		brokerPool.init();
	}

	public void setBroker(Broker broker) {
    
    }

	/*public Broker getBroker() {
		System.out.println("=== Entering get broker ===");
		return brokerPool.getBroker();
	}*/

	public StreamingServer getStreamingServer() {
		return streamingServer;
	}

	public void setStreamingServer(StreamingServer streamingServer) {
		this.streamingServer = streamingServer;
	}

	public String getBrokerConfigFile() {
		return brokerConfigFile;
	}

	public void setBrokerConfigFile(String brokerConfigFile) {
		this.brokerConfigFile = brokerConfigFile;
	}

	public void setBrokerPool(BrokerPool pool){
		brokerPool = pool;
	}

	public BrokerPool getBrokerPool(){
		return brokerPool;
	}
}
