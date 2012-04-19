package cgl.iotcloud.core.config.xml;

import cgl.iotcloud.core.SCException;
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

    /**
     * Create a broker from the XML file in the given file path
     *
     * @param filePath the file path to the broker config xml file
     * @return a broker
     */
    public Broker create(String filePath) {
        log.info("Creating Broker Configuration using file: " + filePath);
        Broker broker = new Broker();

        File brokerFile = new File(filePath);
        BrokerConfigDocument document;
        try {
            document = BrokerConfigDocument.Factory.parse(brokerFile);
        } catch (XmlException e) {
            handleException("Error parsing the broker configuration xml file: " + filePath, e);
            return null;
        } catch (IOException e) {
            handleException("IO error reading the file: " + filePath, e);
            return null;
        }

        BrokerConfigDocument.BrokerConfig bc = document.getBrokerConfig();

        com.iotcloud.xsd.Broker b = bc.getBroker();
        ConnectionFactory fac[] = b.getConnectionFactoryArray();

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
        throw new SCException(s, e);
    }

}
