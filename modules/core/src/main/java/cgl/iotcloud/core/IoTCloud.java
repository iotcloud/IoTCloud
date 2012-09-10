package cgl.iotcloud.core;

import cgl.iotcloud.core.broker.Connections;
import cgl.iotcloud.core.broker.JMSSender;
import cgl.iotcloud.core.broker.JMSSenderFactory;
import cgl.iotcloud.core.client.SCClient;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public IoTCloud(SCConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Initialize the configurations and start the connections
     */
    public void init() {
        configuration.init();

        Connections connections = configuration.getBroker().getConnections("topic");

        if (connections == null) {
            handleException("Couldn't find the connection factor: " + "topic");
        }

        sensorCatalog = new SensorCatalog();
        clientCatalog = new ClientCatalog();

        nodeCatalog = new NodeCatalog();

        updateManager = new UpdateManager(configuration, sensorCatalog, this);
        updateManager.init();

        endpointAllocator = new EndpointAllocator(configuration, nodeCatalog);
        
        // Initialize Public-End-Point
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
    	String uniqueId = UUID.randomUUID().toString();
    	
    	publicEndPoint = new JMSEndpoint();
    	publicEndPoint.setAddress(uniqueId + "/public");
    	
    	publicEndPoint.setProperties(configuration.getBroker().getConnections("topic").getParameters());
    	
    	publicSender = initPublicSender(new JMSSenderFactory().create(publicEndPoint));
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
        
        Endpoint dataEndpoint;
        if (Constants.SENSOR_TYPE_BLOCK.equalsIgnoreCase(type)) {
            dataEndpoint = new JMSEndpoint();
            dataEndpoint.setAddress(sensor.getId() + "/data");
            // TODO: we have to decide the connection factory to be used
            dataEndpoint.setProperties(
                    configuration.getBroker().getConnections("topic").getParameters());
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
            dataEndpoint.setProperties(
                    configuration.getBroker().getConnections("topic").getParameters());
        }

        sensor.setDataEndpoint(dataEndpoint);

        Endpoint controlEndpoint;

        controlEndpoint = new JMSEndpoint();
        controlEndpoint.setAddress(sensor.getId() + "/control");
        // TODO: we have to decide the connection factory to be used
        controlEndpoint.setProperties(
                configuration.getBroker().getConnections("topic").getParameters());
        sensor.setControlEndpoint(controlEndpoint);

        // set the update sending endpoint as the global endpoint
        Endpoint updateSendingEndpoint;
        updateSendingEndpoint = new JMSEndpoint();

        updateSendingEndpoint.setProperties(
                configuration.getBroker().getConnections("topic").getParameters());
        updateSendingEndpoint.setAddress(sensor.getId() + "/update");
        sensor.setUpdateEndpoint(updateSendingEndpoint);

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
        client.setControlEndpoint(sensor.getControlEndpoint());
        client.setUpdateEndpoint(sensor.getUpdateEndpoint());
        client.setDataEndpoint(dataEpr);

        clientCatalog.addClient(client);

        return client;
    }


    public void unRegisterClient(String id) {
        if (clientCatalog.hasClient(id)) {
            clientCatalog.removeClient(id);
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

    private void handleError(String msg) throws IOTException {
        log.error(msg);
        throw new IOTException(msg);
    }

    protected void handleException(String s) {
        log.error(s);
        throw new IOTRuntimeException(s);
    }
}
