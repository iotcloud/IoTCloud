package cgl.iotcloud.core.config;

import cgl.iotcloud.core.broker.Broker;

/**
 * This is the main configuration of the sensor cloud. It encapsulates all the
 * configurations including, broker, clients and sensors information.
 */
public class SCConfiguration {
    private String brokerConfigFile = "broker-config.xml";

    private Broker broker = null;

    public void init() {
        broker.init();
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public Broker getBroker() {
        return broker;
    }

    public String getBrokerConfigFile() {
        return brokerConfigFile;
    }

    public void setBrokerConfigFile(String brokerConfigFile) {
        this.brokerConfigFile = brokerConfigFile;
    }
}
