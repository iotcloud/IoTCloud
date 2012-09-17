package cgl.iotcloud.core.broker;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.broker.Broker;
import cgl.iotcloud.core.config.xml.BrokersFactory;

/**
 * BrokerPool implements pool of available brokers in FIFO 
 * order.
 */
public class BrokerPool {
	private Logger log = LoggerFactory.getLogger(BrokerPool.class);
	private static BrokerPool brokerPool;
	private static LinkedList<Broker> brokerQueue;

	private BrokerPool(){

	}
	
	/**
	 * Returns a instance of BrokerPool
	 * 
	 * @return BrokerPool 
	 */
	public static BrokerPool getInstance(){
		if(brokerQueue == null)
			brokerQueue = new LinkedList<Broker>();
		
		if(brokerPool == null)
			brokerPool = new BrokerPool();
		
		return brokerPool;
	}
	
	/**
	 *	Initializes the broker pool 
	 *	by reading broker.config
	 */
	public void init(){
		BrokersFactory bf = new BrokersFactory();
		List<Broker> brokers = bf.create(Constants.BROKER_CONFIG_FILE);
		brokerQueue.addAll(brokers);
	}

	/**
	 * clears broker pool
	 */
	public void destroy(){
		brokerQueue.clear();
	}
	
	/**
	 * Returns next available broker to connect. 
	 * @return Broker
	 */
	public synchronized Broker getBroker(){
		Broker broker = brokerQueue.remove();
		brokerQueue.add(broker);
		return broker;
	}

	/**
	 * Adds broker back to pool
	 * @param broker
	 */
	public synchronized void releaseBroker(Broker broker){
		brokerQueue.add(broker);
	}
}
