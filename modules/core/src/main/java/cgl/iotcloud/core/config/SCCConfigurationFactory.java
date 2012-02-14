package cgl.iotcloud.core.config;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.broker.Broker;
import cgl.iotcloud.core.config.xml.BrokerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SCCConfigurationFactory {
    private static Logger log = LoggerFactory.getLogger(SCCConfigurationFactory.class);

    public SCConfiguration create(String file) {
        log.info("Creating Sensor Cloud Configuration...");
        SCConfiguration configuration = new SCConfiguration();

        configuration.setBrokerConfigFile(file);

        BrokerFactory fac = new BrokerFactory();
        Broker broker = fac.create(file);

        configuration.setBroker(broker);
        return configuration;
    }

    public SCConfiguration create() {
        return create(Constants.BROKER_CONFIG_FILE);
    }
}
