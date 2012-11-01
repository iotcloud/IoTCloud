package cgl.iotcloud.core.broker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	private static Map<String,Broker> assignedBrokerMap;
	private static LinkedList<Broker> brokerQueue;

	private BrokerPool(){
		init();
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
		
		assignedBrokerMap = new HashMap<String,Broker>();
		
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
	
	public synchronized Broker getBroker(String id){
		Broker retVal = null;
		if(assignedBrokerMap.containsKey(id))
			retVal = assignedBrokerMap.get(id);
		else{
			retVal =  getBroker();
			assignedBrokerMap.put(id, retVal);
		}
		return retVal;
	}
	

	/**
	 * Adds broker back to pool
	 * @param broker
	 */
	public synchronized void releaseBroker(Broker broker){
		brokerQueue.add(broker);
	}
}
