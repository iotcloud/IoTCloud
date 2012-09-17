package cgl.iotcloud.core.config;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.broker.Broker;
import cgl.iotcloud.core.config.xml.BrokerFactory;
import cgl.iotcloud.core.config.xml.BrokersFactory;
import cgl.iotcloud.core.config.xml.StreamingServerFactory;
import cgl.iotcloud.core.stream.StreamingSenderFactory;
import cgl.iotcloud.core.stream.StreamingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class SCCConfigurationFactory {
    private static Logger log = LoggerFactory.getLogger(SCCConfigurationFactory.class);
    
    public SCConfiguration create(String file,String brokerName) {
        log.info("Creating Sensor Cloud Configuration...");
        SCConfiguration configuration = new SCConfiguration();

        configuration.setBrokerConfigFile(file);

       /* BrokersFactory fac = new BrokersFactory();
        List<Broker> brokers =fac.create(file);

        Broker broker = null;
        for(Broker br:brokers)
        	if(br.getName().equalsIgnoreCase(brokerName))
        		broker = br;*/
        
        //configuration.setBroker(broker);
        
        StreamingServerFactory sSF = new StreamingServerFactory();
        StreamingServer streamingHttpServer = sSF.create(file);

        configuration.setStreamingServer(streamingHttpServer);

        return configuration;
    }
    
    public SCConfiguration create(String file){
    	SCConfiguration configuration =create(file,Constants.DEFAULT_BROKER_NAME);
    	return configuration;
    }
    public SCConfiguration create() {
        return create(Constants.BROKER_CONFIG_FILE);
    }
}
