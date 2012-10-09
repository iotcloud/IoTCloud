package cgl.iotcloud.core;

import cgl.iotcloud.core.broker.Connections;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.broker.JMSSenderFactory;
import cgl.iotcloud.core.client.SCClient;
import cgl.iotcloud.core.config.ContentRepositoryConstants;
import cgl.iotcloud.core.config.SCConfiguration;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.sensor.*;
import cgl.iotcloud.core.sensor.filter.SensorIdFilter;
import cgl.iotcloud.core.sensor.filter.SensorNameFilter;
import cgl.iotcloud.core.sensor.filter.SensorTypeFilter;
import cgl.iotcloud.core.stream.StreamingServer;

import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

/**
 * Instance of the Sensor cloud. This captures all the information about the IoTCloud.
 * Both Sensors and Clients use this.
 */
public class IoTCloud {
    private static Logger log = LoggerFactory.getLogger(IoTCloud.class);
    
    private final static String IOTCloudShutDownMssg = "IOTCloud Shutting Down"; 
    /** Configuration for the sensor cloud */
    private SCConfiguration configuration = null;
    /** Sensor catalog containing the information about the sensors */
    private SensorCatalog sensorCatalog = null;

    private ClientCatalog clientCatalog = null;
    
    private Endpoint publicEndPoint;
    private JMSSender publicSender;

    /** Updates are send through this */
    private UpdateManager updateManager = null;

    private NodeCatalog nodeCatalog = null;

    private EndpointAllocator endpointAllocator = null;
    
    private Session contentRepositorySession;
    
    private static boolean isPublicEndPointInit = false;
    
    public static boolean isContentRepositoryAvail = false;
    
    private javax.jcr.Node sensorNode;
	private javax.jcr.Node clientNode;

    public IoTCloud(SCConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Initialize the configurations and start the connections
     */
    public void init() {
        configuration.init();

        //Connections connections = configuration.getBroker().getConnections("topic");
        Connections connections = configuration.getBrokerPool().getBroker().getConnections("topic");

        if (connections == null) {
            handleException("Couldn't find the connection factor: " + "topic");
        }

        sensorCatalog = new SensorCatalog();
        clientCatalog = new ClientCatalog();

        nodeCatalog = new NodeCatalog();

        updateManager = new UpdateManager(configuration, sensorCatalog, this);
        updateManager.init();

        endpointAllocator = new EndpointAllocator(configuration, nodeCatalog);
        
        // Initialize Content Repository Session
        getContentRepositorySession();
        
        // Initialize Content Repository State
        if(isContentRepositoryAvail)
        	initContentRepositoryNodes();
        
        // Initialize Public-End-Point
        if(!isPublicEndPointInit)
        	initPublicEndpoint();
        
    }

    public SensorCatalog getSensorCatalog() {
        return sensorCatalog;
    }

    public SCConfiguration getConfiguration() {
        return configuration;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public ClientCatalog getClientCatalog() {
        return clientCatalog;
    }

    public Sensor registerSensor(String name) {
        return registerSensor(name, Constants.SENSOR_TYPE_BLOCK);
    }

    /**
     * Initializes a Public End-Point for generic messages to be dispatched to all the registered Sensors and Clients  
     */
    public void initPublicEndpoint()
    {
    	//TODO: Register the new Public End Point with in the Content Repository Node
    	
    	String uniqueId = UUID.randomUUID().toString();
    	
    	publicEndPoint = new JMSEndpoint();
    	publicEndPoint.setAddress(uniqueId + "/public");
    	
    	publicEndPoint.setProperties(configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());
    	
    	if(isContentRepositoryAvail)
    	{
			try {
				
				javax.jcr.Node publicEndPointNode = contentRepositorySession.getRootNode().addNode(ContentRepositoryConstants.PUBLIC_END_POINT);
				javax.jcr.Node keyNode = publicEndPointNode.addNode(ContentRepositoryConstants.PUBLIC_END_POINT_KEY_NODE);
				
				publicEndPointNode.setProperty(ContentRepositoryConstants.PUBLIC_END_POINT_PROPERTIES.public_end_point_address.toString(), publicEndPoint.getAddress());
				
				Map<String, String> properties = publicEndPoint.getProperties();
				Iterator<String> propKeySetIte = properties.keySet().iterator();
				
				while (propKeySetIte.hasNext())
				{
					String key = propKeySetIte.next();
					publicEndPointNode.setProperty(key, properties.get(key));
					keyNode.setProperty(key, key);
				}
				
				contentRepositorySession.save();
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				log.error(" ******** Failed to create Public End Point Repository Node ********* ");
				log.error(" ******** Shutting down all Content Repository Services ********* ");
				isContentRepositoryAvail = false;
				contentRepositorySession.logout();
			}
    	}
		
    	
    	publicSender = initPublicSender(new JMSSenderFactory().create(publicEndPoint));
    	
    	isPublicEndPointInit = true;
    }
    
    public void initPublicEndpoint(String address, Map<String, String> properties)
    {
    	
    	publicEndPoint = new JMSEndpoint();
    	publicEndPoint.setAddress(address);
    	
    	publicEndPoint.setProperties(properties);
    	
    	publicSender = initPublicSender(new JMSSenderFactory().create(publicEndPoint));
    	
    	isPublicEndPointInit = true;
    }
    
    private void getContentRepositorySession()
    {
    	Repository repository;
		try {
			repository = new URLRemoteRepository("http://localhost:9091/rmi");
			contentRepositorySession = repository.login(new SimpleCredentials("guest", new char[0]));
			isContentRepositoryAvail = true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.error(" ***** Failed to obtain Content Repository Session --> "+e.getMessage());
			log.error(" ******** Shutting down all Content Repository Services ********* ");
			isContentRepositoryAvail = false;
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			log.error(" ***** Failed to obtain Content Repository Session --> "+e.getMessage());
			log.error(" ******** Shutting down all Content Repository Services ********* ");
			isContentRepositoryAvail = false;
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			log.error(" ***** Failed to obtain Content Repository Session --> "+e.getMessage());
			log.error(" ******** Shutting down all Content Repository Services ********* ");
			isContentRepositoryAvail = false;
		}
    	
    }
    
    public void shutDownContentRepoSession()
    {
    	contentRepositorySession.logout();
    }
    
    public void clearContentRepository() throws RepositoryException
    {
    	javax.jcr.Node rootNode = contentRepositorySession.getRootNode();
		
		try{
			rootNode.getNode(ContentRepositoryConstants.PUBLIC_END_POINT).remove();
			log.debug(" ***** Removed the Public End Point Node ***** ");
			contentRepositorySession.save();
		} catch (Exception e) {
			log.debug(" ***** Failed to remove Public End Point Node --> " + e.getMessage());
		}
		
		try{
			rootNode.getNode(ContentRepositoryConstants.SENSOR_NODE).remove();
			log.debug(" ***** Removed the SENSOR Node ***** ");
			contentRepositorySession.save();
		} catch (Exception e) {
			log.debug(" ***** Failed to remove SENSOR Node --> " + e.getMessage());
		}
		
		try{
			rootNode.getNode(ContentRepositoryConstants.CLIENT_NODE).remove();
			log.debug(" ***** Removed the CLIENT Node ***** ");
			contentRepositorySession.save();
		} catch (Exception e) {
			log.debug(" ***** Failed to remove CLIENT Node --> " + e.getMessage());
		}
    }
    
    /**
     * Creates Content Repository Nodes
     */
    private void initContentRepositoryNodes()
    {
    	
		// Verify if previous Shut-Down was Legal/Valid
		Iterator<javax.jcr.Node> childNodesIterator;
		try {
			childNodesIterator = contentRepositorySession.getRootNode().getNodes();
			while(childNodesIterator.hasNext())
			{
				javax.jcr.Node eachChildNode = childNodesIterator.next();
				
				if(eachChildNode.getName().equals(ContentRepositoryConstants.CLIENT_NODE) || eachChildNode.getName().equals(ContentRepositoryConstants.SENSOR_NODE))
				{
					initiateContentRepState();
					if(isPublicEndPointInit)
						return;
					else{
						log.error(" ******** FAILED TO INIT SAVED PUBLIC END POINT ******** ");
						log.error(" ******** DROPPING THE WHOLE CONTENT TREE  ******** ");
						clearContentRepository();
					}
				}
			}
			
			sensorNode = contentRepositorySession.getRootNode().addNode(ContentRepositoryConstants.SENSOR_NODE);
			clientNode = contentRepositorySession.getRootNode().addNode(ContentRepositoryConstants.CLIENT_NODE);
			contentRepositorySession.save();
			
		} catch (RepositoryException e) {
			// Failed to create Essential Nodes
			try {
				log.error(" ******** Failed to initiate the Content Repository ******** ");
				log.error(" ******** Trying to CLEAN the Content Repositorty ******** ");
				clearContentRepository();
			} catch (RepositoryException e1) {
				// TODO Auto-generated catch block
				log.error(" ******** Failed to CLEAN the Content Repository ******** ");
			}
			
			log.error(" ******** Shutting down all Content Repository Services ********* ");
			isContentRepositoryAvail = false;
			contentRepositorySession.logout();
		}
		
    }
    
	private void initiateContentRepState() throws RepositoryException {
		// Checking if Public End point exists in the saved state, If found
		// initiate the saved state Public End Point
		javax.jcr.Node rootNode;
		try {
			rootNode = contentRepositorySession.getRootNode();

			javax.jcr.Node publicEndPointNode = rootNode.getNode(ContentRepositoryConstants.PUBLIC_END_POINT);
			javax.jcr.Node keyNode = publicEndPointNode.getNode(ContentRepositoryConstants.PUBLIC_END_POINT_KEY_NODE);
			Map<String, String> properties = new HashMap<String, String>();
			PropertyIterator keyIterator = keyNode.getProperties();
			while (keyIterator.hasNext())
			{
				String key = ((Property) keyIterator.next()).getString();
				// Avoiding the default property notifying an unstructured Node
				if(!key.equals("nt:unstructured"))
					properties.put(key, publicEndPointNode.getProperty(key).getValue().getString());
			}
			
			initPublicEndpoint(publicEndPointNode.getProperty(ContentRepositoryConstants.PUBLIC_END_POINT_PROPERTIES.public_end_point_address.toString()).getValue().getString(), properties);
		} catch (PathNotFoundException e) {
			isPublicEndPointInit = false;
			return;
		}

		// Trying to invoke Saved Sensors
		try {
			sensorNode = rootNode.getNode(ContentRepositoryConstants.SENSOR_NODE);
			Iterator<javax.jcr.Node> sensors = sensorNode.getNodes();
			
			while (sensors.hasNext()) {
				javax.jcr.Node eachSensor = sensors.next();
				SCSensor sensor = new SCSensor(eachSensor.getName());
				sensor.setId(eachSensor.getProperty(ContentRepositoryConstants.SENSOR_PROPERTIES.ID.toString()).getValue().getString());
				sensor.setType(eachSensor.getProperty(ContentRepositoryConstants.SENSOR_PROPERTIES.TYPE.toString()).getValue().getString());

				sensor.setPublicEndpoint(publicEndPoint);

				Endpoint dataEndpoint;
				if (Constants.SENSOR_TYPE_BLOCK.equalsIgnoreCase(sensor.getType())) 
				{
					dataEndpoint = new JMSEndpoint();
					dataEndpoint.setAddress(sensor.getId() + "/data");
				} else if (Constants.SENSOR_TYPE_STREAMING.equalsIgnoreCase(sensor.getType())) 
				{
					dataEndpoint = new StreamingEndpoint();
				} else {
					dataEndpoint = new JMSEndpoint();
					dataEndpoint.setAddress(sensor.getId() + "/data");
				}
				
				// Setting the Data End Point Properties from Content Repository
				javax.jcr.Node dataEndPointNode = eachSensor.getNode(ContentRepositoryConstants.SENSOR_PROPERTIES.DATA_END_POINT_NODE.toString());
				javax.jcr.Node dataEndPointKeyNode = dataEndPointNode.getNode(ContentRepositoryConstants.SENSOR_PROPERTIES.DATA_END_POINT_NODE_KEY.toString());
				Map<String, String> properties = new HashMap<String, String>();
				PropertyIterator propertyIterator = dataEndPointKeyNode.getProperties();
				while (propertyIterator.hasNext()) 
				{
					String key = ((Property) propertyIterator.next()).getString();
					// Avoiding the default property notifying an unstructured Node
					if(!key.equals("nt:unstructured"))
						properties.put(key, dataEndPointNode.getProperty(key).getValue().getString());
				}
				dataEndpoint.setProperties(properties);
				sensor.setDataEndpoint(dataEndpoint);

				Endpoint controlEndpoint;
				controlEndpoint = new JMSEndpoint();
				controlEndpoint.setAddress(sensor.getId() + "/control");
				
				// Setting the Control End Point Properties from Content Repository
				javax.jcr.Node cntrlEndPointNode = eachSensor.getNode(ContentRepositoryConstants.SENSOR_PROPERTIES.CNTRL_END_POINT_NODE.toString());
				javax.jcr.Node cntrlEndPointKeyNode = cntrlEndPointNode.getNode(ContentRepositoryConstants.SENSOR_PROPERTIES.CNTRL_END_POINT_KEY_NODE.toString());
				properties = new HashMap<String, String>();
				propertyIterator = cntrlEndPointKeyNode.getProperties();
				while (propertyIterator.hasNext()) 
				{
					String key = ((Property) propertyIterator.next()).getString();
					// Avoiding the default property notifying an unstructured Node
					if(!key.equals("nt:unstructured"))
						properties.put(key, cntrlEndPointNode.getProperty(key).getValue().getString());
				}
				controlEndpoint.setProperties(properties);
				sensor.setControlEndpoint(controlEndpoint);

				// set the update sending endpoint as the global endpoint
				Endpoint updateSendingEndpoint;
				updateSendingEndpoint = new JMSEndpoint();
				
				// Setting the Update Sending End Point Properties from Content Repository
				javax.jcr.Node updateSendingEndPointNode = eachSensor.getNode(ContentRepositoryConstants.SENSOR_PROPERTIES.UPDATE_SENDING_END_POINT_NODE.toString());
				javax.jcr.Node updateSendingEndPointKeyNode = updateSendingEndPointNode.getNode(ContentRepositoryConstants.SENSOR_PROPERTIES.UPDATE_SENDING_END_POINT_KEY_NODE.toString());
				properties = new HashMap<String, String>();
				propertyIterator = updateSendingEndPointKeyNode.getProperties();
				while (propertyIterator.hasNext()) 
				{
					String key = ((Property) propertyIterator.next()).getString();
					// Avoiding the default property notifying an unstructured Node
					if(!key.equals("nt:unstructured"))
						properties.put(key,	updateSendingEndPointNode.getProperty(key).getValue().getString());
				}
				updateSendingEndpoint.setProperties(properties);
				updateSendingEndpoint.setAddress(sensor.getId() + "/update");
				sensor.setUpdateEndpoint(updateSendingEndpoint);
				sensorCatalog.addSensor(sensor);
			}
		} catch (PathNotFoundException e) {
			rootNode.addNode(ContentRepositoryConstants.SENSOR_NODE);
			contentRepositorySession.save();
		}

		// Trying to invoke Saved Clients
		try {
			clientNode = rootNode.getNode(ContentRepositoryConstants.CLIENT_NODE);
			Iterator<javax.jcr.Node> clients = clientNode.getNodes();
			
			while (clients.hasNext()) 
			{
				javax.jcr.Node eachClient = clients.next();

				Sensor sensor = sensorCatalog.getSensor(eachClient.getProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.SENSOR_ID.toString()).getValue().getString());
				SCClient client = new SCClient(eachClient.getProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.CLIENT_ID.toString()).getValue().getString());

				client.setPublicEndpoint(publicEndPoint);
				client.setControlEndpoint(sensor.getControlEndpoint());
				client.setUpdateEndpoint(sensor.getUpdateEndpoint());
				client.setType(sensor.getType());
				
				try
				{
					javax.jcr.Node dataEndPointNode = eachClient.getNode(ContentRepositoryConstants.CLIENT_PROPERTIES.DATA_END_POINT_NODE.toString());
					Endpoint dataEndpoint = null;
					if(dataEndPointNode.getProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.END_POINT.toString()).getValue().getString().equals(ContentRepositoryConstants.CLIENT_PROPERTIES.JMS_END_POINT.toString()))
					{
						dataEndpoint = new JMSEndpoint(); 
					}else{
						dataEndpoint = new StreamingEndpoint();
					}
					
					Map<String, String> properties = new HashMap<String, String>();
					PropertyIterator propertyIterator = dataEndPointNode.getProperties();
					while (propertyIterator.hasNext()) 
					{
						String key = (String) propertyIterator.next();
						if(!key.equals(ContentRepositoryConstants.CLIENT_PROPERTIES.END_POINT.toString()))
							properties.put(key, dataEndPointNode.getProperty(key).getValue().getString());
					}
					
					dataEndpoint.setProperties(properties);
					client.setDataEndpoint(dataEndpoint);
				} catch (PathNotFoundException e) {
					client.setDataEndpoint(sensor.getDataEndpoint());
				}

				clientCatalog.addClient(client);
			}
		} catch (PathNotFoundException e) {
			rootNode.addNode(ContentRepositoryConstants.CLIENT_NODE);
			contentRepositorySession.save();
		}
		
		/*log.error(" ==== Sensors Reinvoked in to the Sensor_Catalog ==== ");
		Iterator<SCSensor> sensorIterator = sensorCatalog.getSensors().iterator();
		while (sensorIterator.hasNext())
		{
			SCSensor scSensor = sensorIterator.next();
			log.error(" ==== Sensor - " + scSensor.getId() + " ==== ");
		}
		
		log.error(" ==== Clients Reinvoked in to the Client_Catalog ==== ");
		Iterator<SCClient> clientIterator = clientCatalog.getClients().iterator();
		while (clientIterator.hasNext())
		{
			SCClient scClient = clientIterator.next();
			log.error(" ==== Client - " + scClient.getId() + " ==== ");
		}*/

	}
    
    /**
     * Registers a Sender specific to the Public End-Point
     *
     * @param publicSender this sender is used for sending information about the iotcloud to the nodes.
     */
    public JMSSender initPublicSender(JMSSender publicSender)
    {
    	publicSender.setMessageFactory(new JMSDataMessageFactory());

    	publicSender.init();
    	publicSender.start();
    	
    	return publicSender;
    }
    
    /**
     * Sends a IOT-Middle-ware Shut Down message to all the Registered Sensors and Clients (representing limited service)
     * Called prior to exiting the java-runtime  
     */
    public void sendIOTCloudShutDownMssg()
    {
    	TextDataMessage message = new TextDataMessage();
        message.setText(IOTCloudShutDownMssg);
        
        sendPublicMessage(message);
    }
    
    /**
     * Sends a Message over Public End-Point
     * @param message
     */
    public void sendPublicMessage(SensorMessage message)
    {
    	publicSender.send(message);
    }
    
    /**
     * Register a sensor with the given name and type
     * @param name name of the sensor
     * @param type type of the sensor
     * @return the sensor
     */
    public Sensor registerSensor(String name, String type) {
        SCSensor sensor = new SCSensor(name);

        String uid = UUID.randomUUID().toString();
        sensor.setId(uid);
        sensor.setType(type);

        // Setting PublicEndPoint
        sensor.setPublicEndpoint(publicEndPoint);
        
        /*sensor = generateSensorEndPoints(sensor);*/
        Endpoint dataEndpoint;
        if (Constants.SENSOR_TYPE_BLOCK.equalsIgnoreCase(type)) {
            dataEndpoint = new JMSEndpoint();
            dataEndpoint.setAddress(sensor.getId() + "/data");
            // TODO: we have to decide the connection factory to be used
            //dataEndpoint.setProperties(
            //        configuration.getBroker().getConnections("topic").getParameters());
            dataEndpoint.setProperties(
                    configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());
        } else if (Constants.SENSOR_TYPE_STREAMING.equalsIgnoreCase(type)) {
            dataEndpoint = new StreamingEndpoint();

            dataEndpoint.setProperties(configuration.getStreamingServer().getParameters());
            dataEndpoint.getProperties().put("PATH", "sensor/" + sensor.getId() + "/data");

            // add the routing to the streaming server
        } else {
            // defaulting to JMS
            dataEndpoint = new JMSEndpoint();
            dataEndpoint.setAddress(sensor.getId() + "/data");
            // TODO: we have to decide the connection factory to be used
            //dataEndpoint.setProperties(
            //        configuration.getBroker().getConnections("topic").getParameters());
            dataEndpoint.setProperties(
                    configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());
        }

        sensor.setDataEndpoint(dataEndpoint);

        Endpoint controlEndpoint;

        controlEndpoint = new JMSEndpoint();
        controlEndpoint.setAddress(sensor.getId() + "/control");
        // TODO: we have to decide the connection factory to be used
        //controlEndpoint.setProperties(
        //        configuration.getBroker().getConnections("topic").getParameters());
        controlEndpoint.setProperties(
                configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());
        sensor.setControlEndpoint(controlEndpoint);

        // set the update sending endpoint as the global endpoint
        Endpoint updateSendingEndpoint;
        updateSendingEndpoint = new JMSEndpoint();

        //updateSendingEndpoint.setProperties(
        //        configuration.getBroker().getConnections("topic").getParameters());
        updateSendingEndpoint.setProperties(
                configuration.getBrokerPool().getBroker().getConnections("topic").getParameters());
        updateSendingEndpoint.setAddress(sensor.getId() + "/update");
        sensor.setUpdateEndpoint(updateSendingEndpoint);
        
        if(isContentRepositoryAvail)
        {
        	javax.jcr.Node crSensor;
			try {
				crSensor = sensorNode.addNode(sensor.getId());
				crSensor.setProperty(ContentRepositoryConstants.SENSOR_PROPERTIES.ID.toString(), sensor.getId());
				crSensor.setProperty(ContentRepositoryConstants.SENSOR_PROPERTIES.TYPE.toString(), sensor.getType());
				
				// Setting the Data Point Properties
				javax.jcr.Node dataEndPointNode = crSensor.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.DATA_END_POINT_NODE.toString());
				javax.jcr.Node dataEndPointKeyNode = dataEndPointNode.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.DATA_END_POINT_NODE_KEY.toString());
				Map<String, String> dataEndPointProperties = sensor.getDataEndpoint().getProperties();
				Iterator<String> deppKeysetIte = dataEndPointProperties.keySet().iterator();
				while (deppKeysetIte.hasNext())
				{
					String key = deppKeysetIte.next();
					dataEndPointNode.setProperty(key, dataEndPointProperties.get(key));
					dataEndPointKeyNode.setProperty(key, key);
				}
				
				// Setting Control End Point Properties
				javax.jcr.Node cntrlEndPointNode = crSensor.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.CNTRL_END_POINT_NODE.toString());
				javax.jcr.Node cntrlEndPointKeyNode = cntrlEndPointNode.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.CNTRL_END_POINT_KEY_NODE.toString());
				Map<String, String> cntrlEndPointProperties = sensor.getControlEndpoint().getProperties();
				Iterator<String> ceppKeysetIte = cntrlEndPointProperties.keySet().iterator();
				while (ceppKeysetIte.hasNext())
				{
					String key = ceppKeysetIte.next();
					cntrlEndPointNode.setProperty(key, cntrlEndPointProperties.get(key));
					cntrlEndPointKeyNode.setProperty(key, key);
				}
				
				// Setting Update Sending End Point Properties
				javax.jcr.Node updateSendingEndPointNode = crSensor.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.UPDATE_SENDING_END_POINT_NODE.toString());
				javax.jcr.Node updateSendingEndPointKeyNode = updateSendingEndPointNode.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.UPDATE_SENDING_END_POINT_KEY_NODE.toString());
				Map<String, String> updateSendingEndPointProperties = sensor.getUpdateEndpoint().getProperties();
				Iterator<String> useppKeysetIte = updateSendingEndPointProperties.keySet().iterator();
				while (useppKeysetIte.hasNext())
				{
					String key = useppKeysetIte.next();
					updateSendingEndPointNode.setProperty(key, updateSendingEndPointProperties.get(key));
					updateSendingEndPointKeyNode.setProperty(key, key);
				}
				
				contentRepositorySession.save();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(" ********* Failed to add a sensor in to the Content Repository ********* ");
			}
        	
        }
        
        sensorCatalog.addSensor(sensor);
        updateManager.sensorChange(Constants.Updates.ADDED, sensor.getId());
        return sensor;
    }

    public SCClient registerClient(String clientId, String sensorId) {
        if (!sensorCatalog.hasSensor(sensorId)) {
            return null;
        }

        if (clientCatalog.hasClient(clientId)) {
            return clientCatalog.getClient(clientId);
        }

        Sensor sensor = sensorCatalog.getSensor(sensorId);
        SCClient client = new SCClient(clientId);
        client.setPublicEndpoint(publicEndPoint);
        client.setControlEndpoint(sensor.getControlEndpoint());
        client.setUpdateEndpoint(sensor.getUpdateEndpoint());
        client.setType(sensor.getType());
        if (sensor.getType().equals(Constants.SENSOR_TYPE_BLOCK)) {
            client.setDataEndpoint(sensor.getDataEndpoint());
        } else {
            StreamingEndpoint endpoint = new StreamingEndpoint();
            HashMap<String, String> props = new HashMap<String, String>();

            StreamingServer server = configuration.getStreamingServer();
            int port = server.getPort() + 1000 + clientCatalog.getClients().size() + 1;

            props.put("HOST", "localhost");
            props.put("PORT", "" + port);
            props.put("PATH", "*");

            endpoint.setProperties(props);
            client.setDataEndpoint(endpoint);

            // add the route to the streaming server
            server.addRoute("sensor" + sensor.getId() + "/data", "localhost", port, "*");
        }
        
        if(isContentRepositoryAvail)
        {
        	javax.jcr.Node crClient;
			try {
				crClient = clientNode.addNode(client.getId());
				crClient.setProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.CLIENT_ID.toString(), client.getId());
				crClient.setProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.SENSOR_ID.toString(), sensor.getId());
				
				if(!sensor.getType().equals(Constants.SENSOR_TYPE_BLOCK))
				{
					// Setting the Data End Point
					javax.jcr.Node dataEndPointNode = crClient.addNode(ContentRepositoryConstants.CLIENT_PROPERTIES.DATA_END_POINT_NODE.toString());
					Map<String, String> dataEndPointProperties = client.getDataEndpoint().getProperties();
					Iterator<String> deppKeysetIte = dataEndPointProperties.keySet().iterator();
					while (deppKeysetIte.hasNext())
					{
						String key = deppKeysetIte.next();
						dataEndPointNode.setProperty(key, dataEndPointProperties.get(key));
					}
					
					if(client.getDataEndpoint() instanceof JMSEndpoint)
						dataEndPointNode.setProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.END_POINT.toString(), ContentRepositoryConstants.CLIENT_PROPERTIES.JMS_END_POINT.toString());
					else if(client.getDataEndpoint() instanceof StreamingEndpoint)
						dataEndPointNode.setProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.END_POINT.toString(), ContentRepositoryConstants.CLIENT_PROPERTIES.STREAMING_END_POINT.toString());
				}
				
				contentRepositorySession.save();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(" ********* Failed to add a Client in to the Content Repository ********* ");
			}
        	
        }
        
        clientCatalog.addClient(client);

        return client;
    }

    public SCClient registerClient(String clientId, String sensorId, Endpoint dataEpr) {
        if (!sensorCatalog.hasSensor(clientId)) {
            return null;
        }

        if (clientCatalog.hasClient(clientId)) {
            return clientCatalog.getClient(clientId);
        }

        Sensor sensor = sensorCatalog.getSensor(sensorId);
        SCClient client = new SCClient(clientId);
        client.setPublicEndpoint(publicEndPoint);
        client.setControlEndpoint(sensor.getControlEndpoint());
        client.setUpdateEndpoint(sensor.getUpdateEndpoint());
        client.setDataEndpoint(dataEpr);
        
        if(isContentRepositoryAvail)
        {
        	javax.jcr.Node crClient;
			try {
				crClient = clientNode.addNode(client.getId());
				crClient.setProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.CLIENT_ID.toString(), client.getId());
				crClient.setProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.SENSOR_ID.toString(), sensor.getId());
				
				// Setting the Data End Point
				javax.jcr.Node dataEndPointNode = crClient.addNode(ContentRepositoryConstants.CLIENT_PROPERTIES.DATA_END_POINT_NODE.toString());
				Map<String, String> dataEndPointProperties = client.getDataEndpoint().getProperties();
				Iterator<String> deppKeysetIte = dataEndPointProperties.keySet().iterator();
				while (deppKeysetIte.hasNext())
				{
					String key = deppKeysetIte.next();
					dataEndPointNode.setProperty(key, dataEndPointProperties.get(key));
				}
				
				if(client.getDataEndpoint() instanceof JMSEndpoint)
					dataEndPointNode.setProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.END_POINT.toString(), ContentRepositoryConstants.CLIENT_PROPERTIES.JMS_END_POINT.toString());
				else if(client.getDataEndpoint() instanceof StreamingEndpoint)
					dataEndPointNode.setProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.END_POINT.toString(), ContentRepositoryConstants.CLIENT_PROPERTIES.STREAMING_END_POINT.toString());
				
				contentRepositorySession.save();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(" ********* Failed to add a Client in to the Content Repository ********* ");
			}
        	
        }
        
        clientCatalog.addClient(client);

        return client;
    }


    public void unRegisterClient(String id) {
        if (clientCatalog.hasClient(id)) {
            clientCatalog.removeClient(id);
            
            if(isContentRepositoryAvail)
            {
            	try {
					clientNode.getNode(id).remove();
					contentRepositorySession.save();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error(" ********* Failed to remove a Client from the Content Repository ********* ");
				} 
            }
            
        } else {
            handleException("Failed to unregister the client, non existing client");
        }
    }

    /**
     * Un-register a sensor with the given id.
     * @param id id of the sensor
     */
    public void unRegisterSensor(String id) {
        if (sensorCatalog.hasSensor(id)) {
            updateManager.sensorChange(Constants.Updates.REMOVED, id);
            sensorCatalog.removeSensor(id);
            
            if(isContentRepositoryAvail)
            {
            	try {
					sensorNode.getNode(id).remove();
					Iterator<javax.jcr.Node> clients = clientNode.getNodes();
		    		while(clients.hasNext())
		    		{
		    			javax.jcr.Node eachClientNode = clients.next();
		    			if(eachClientNode.getProperty(ContentRepositoryConstants.CLIENT_PROPERTIES.SENSOR_ID.toString()).getValue().getString().equals(id))
		    				eachClientNode.remove();
		    		}
					contentRepositorySession.save();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error(" ********* Failed to remove a Sensor from the Content Repository ********* ");
				} 
            }
            
        } else {
            handleException("Failed to unregister the sensor, non existing sensor");
        }
    }

    /**
     * Get the sensors according to a filtered criteria
     * @param type the type of the filter
     * @param props the properties used for filtering
     *
     * @return a list os sensors matching the filter
     * @throws SensorException if an error occurs in filtering
     */
    public List<SCSensor> getSensors(String type, Map<String, String> props)
            throws SensorException {
        AbstractSensorFilter sensorFilter = null;
        if (type.equals("id")) {
            sensorFilter = new SensorIdFilter(this);
        } else if (type.equals("type")) {
            sensorFilter = new SensorTypeFilter(this);
        } else if (type.equals("name")) {
            sensorFilter = new SensorNameFilter(this);
        } else {
            sensorFilter = new SensorIdFilter(this);
        }

        FilterCriteria criteria = new FilterCriteria();
        criteria.addProperties(props);

        return sensorFilter.filter(criteria);
    }

    /**
     * Register a node to the system.
     *
     * @param nodeName name of the node
     * @return created node
     */
    public NodeInformation registerNode(NodeName nodeName) throws IOTException {
        if (nodeCatalog.hasNode(nodeName)) {
            handleError("Cannot register node.. node already exists: " + nodeName);
            return null;
        }

        NodeInformation nodeInformation = new NodeInformation(nodeName);
        nodeCatalog.addNode(nodeInformation);

        return nodeInformation;
    }

    /**
     * Un-Register a node to the system.
     *
     * @param nodeName name of the node
     * @return created node
     */
    public NodeInformation unRegisterNode(NodeName nodeName) throws IOTException {
        NodeInformation nodeInformation = nodeCatalog.getNode(nodeName);
        if (nodeInformation == null) {
            handleError("Cannot find the specified node: " + nodeName);
            return null;
        }

        nodeCatalog.removeNode(nodeInformation);

        return nodeInformation;
    }

    /**
     * Register a consumer to a node
     *
     * @param nodeName node to register the consumer
     * @param name name of the endpoint
     * @param type type of the endpoint
     * @return endpoint created
     */
    public Endpoint registerConsumer(NodeName nodeName, String name,
                                     String type, String path) throws IOTException {
        NodeInformation nodeInformation = nodeCatalog.getNode(nodeName);
        if (nodeInformation == null) {
            handleError("Cannot find the specified node: " + nodeName);
            return null;
        }

        Endpoint endpoint = endpointAllocator.allocate(nodeName, name, type, path);
        nodeInformation.addConsumer(endpoint);

        return endpoint;
    }

    /**
     * Un-Register a consumer from a node
     *
     * @param nodeName name of the node
     * @param name name of the consumer
     * @throws IOTException
     */
    public void unRegisterConsumer(NodeName nodeName, String name) throws IOTException {
        NodeInformation nodeInformation = nodeCatalog.getNode(nodeName);
        if (nodeInformation == null) {
            handleError("Cannot find the specified node: " + nodeName);
            return;
        }

        Endpoint endpointToRemove = null;
        for (Endpoint e : nodeInformation.getConsumers()) {
            if (e.getName().equals(name)) {
                endpointToRemove = e;
                break;
            }
        }
        if (endpointToRemove != null) {
            nodeInformation.removeConsumer(endpointToRemove);
        } else {
            String msg = "Cannot find the specified consumer: " + name + " in node: " + nodeName;
            handleError(msg);
        }
    }

    /**
     * Register a consumer to a node
     *
     * @param nodeName node to register the consumer
     * @param name name of the endpoint
     * @param type type of the endpoint
     * @return endpoint created
     */
    public Endpoint registerProducer(NodeName nodeName, String name, String type, String path)
            throws IOTException {
        NodeInformation nodeInformation = nodeCatalog.getNode(nodeName);
        if (nodeInformation == null) {
            handleError("Cannot find the specified node: " + nodeName);
            return null;
        }

        Endpoint endpoint = endpointAllocator.allocate(nodeName, name, type, path);
        nodeInformation.addProducer(endpoint);

        return endpoint;
    }

    /**
     * Un-Register a producer from a node
     *
     * @param nodeName name of the node
     * @param name name of the producer
     * @throws IOTException
     */
    public void unRegisterProducer(NodeName nodeName, String name) throws IOTException {
        NodeInformation nodeInformation = nodeCatalog.getNode(nodeName);
        if (nodeInformation == null) {
            handleError("Cannot find the specified node: " + nodeName);
            return;
        }

        Endpoint endpointToRemove = null;
        for (Endpoint e : nodeInformation.getProducers()) {
            if (e.getName().equals(name)) {
                endpointToRemove = e;
                break;
            }
        }

        if (endpointToRemove != null) {
            nodeInformation.removeProducer(endpointToRemove);
        } else {
            String msg = "Cannot find the specified producer: " + name + " in node: " + nodeName;
            handleError(msg);
        }
    }

    public NodeCatalog getNodeCatalog() {
        return nodeCatalog;
    }

    private void handleError(String msg) throws IOTException {
        log.error(msg);
        throw new IOTException(msg);
    }

    protected void handleException(String s) {
        log.error(s);
        throw new IOTRuntimeException(s);
    }
}
