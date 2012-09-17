package cgl.iotcloud.core.config.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cgl.iotcloud.core.IOTRuntimeException;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcloud.xsd.BrokerConfigDocument;

import cgl.iotcloud.core.broker.Broker;

/**
 * Creates list of brokers by reading broker-config.xml
 *
 */
public class BrokersFactory {
	private static Logger log = LoggerFactory.getLogger(BrokersFactory.class);
<<<<<<< HEAD
	
	
=======

>>>>>>> d5d5663c9c6c25873eb4965b66df9330622dc48f
	public List<Broker> create(String filePath){
		File brokerFile = new File(filePath);
		List<Broker> brokers= new ArrayList<Broker>();

		BrokerConfigDocument document = null;
		try {
			document = BrokerConfigDocument.Factory.parse(brokerFile);

		} catch (XmlException e) {
			handleException("Error parsing the broker configuration xml file: " + filePath, e);
		} catch (IOException e) {
			handleException("IO error reading the file: " + filePath, e);
		}

		BrokerConfigDocument.BrokerConfig brokerConfig = document.getBrokerConfig();
		com.iotcloud.xsd.BrokerPool brokerPool =brokerConfig.getBrokerPool();

		com.iotcloud.xsd.Broker[] xmlBrokers = brokerPool.getBrokerArray();
		BrokerFactory bFac = new BrokerFactory();
		for(com.iotcloud.xsd.Broker xmlBroker:xmlBrokers){
			Broker broker =bFac.createBroker(xmlBroker);
			brokers.add(broker);
		}
		return brokers;
	}

	private void handleException(String s, Exception e) {
		log.error(s, e);
		throw new IOTRuntimeException(s, e);
	}
}
