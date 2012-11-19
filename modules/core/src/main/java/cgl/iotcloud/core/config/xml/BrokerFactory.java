package cgl.iotcloud.core.config.xml;

import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.core.broker.Broker;
import cgl.iotcloud.core.broker.Connections;
import com.iotcloud.xsd.BrokerConfigDocument;
import com.iotcloud.xsd.ConnectionFactory;
import com.iotcloud.xsd.Param;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Creates a broker configuration using the broker configuration file.
 */
public class BrokerFactory {
    private static Logger log = LoggerFactory.getLogger(BrokerFactory.class);

    public Broker createBroker(com.iotcloud.xsd.Broker xmlBroker) {
        Broker broker = new Broker();
        broker.setName(xmlBroker.getName());

        ConnectionFactory fac[] = xmlBroker.getConnectionFactoryArray();

        for (ConnectionFactory f : fac) {
            broker.addConnections(createConnections(f));
        }
    	return broker;
    }
    
    private Connections createConnections(ConnectionFactory fac) {
        Connections connections = new Connections(fac.getName());
        Param params[] = fac.getParamArray();

        for (Param p : params) {
            connections.addParameter(p.getName(), p.getStringValue());
        }

        return connections;
    }

    private void handleException(String s, Exception e) {
        log.error(s, e);
        throw new IOTRuntimeException(s, e);
    }
}
