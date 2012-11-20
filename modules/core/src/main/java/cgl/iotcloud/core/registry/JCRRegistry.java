package cgl.iotcloud.core.registry;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.config.ContentRepositoryConstants;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import cgl.iotcloud.core.sensor.SCSensor;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class JCRRegistry {
    private Logger log = LoggerFactory.getLogger(JCRRegistry.class);

    private Session contentRepositorySession;
    private boolean isContentRepositoryAvail = false;
    private String url = "http://localhost:9091/rmi";
    private javax.jcr.Node sensorNode;
    private javax.jcr.Node clientNode;
    private static boolean isPublicEndPointInit = false;

    private IoTCloud ioTCloud = null;

    public JCRRegistry(IoTCloud ioTCloud) {
        Properties prop = new Properties();
        InputStream in = getClass().getResourceAsStream("repository.properties");
        if (in != null) {
            try {
                prop.load(in);
                in.close();
            } catch (IOException e) {
                log.error("Failed to load properties from repository.properties");
            }

            if (prop.containsKey("jcr.url")) {
                url = (String) prop.get("jcr.url");
            }
        }
        this.ioTCloud = ioTCloud;
    }

    public void init() {
        getContentRepositorySession();

        // Initialize Content Repository State
        if (isContentRepositoryAvail) {
            initContentRepositoryNodes();
        }
    }

    public void shutDownContentRepoSession() {
        contentRepositorySession.logout();
    }

    public boolean isContentRepositoryAvail() {
        return isContentRepositoryAvail;
    }

    public static boolean isPublicEndPointInit() {
        return isPublicEndPointInit;
    }

    private void getContentRepositorySession() {
        Repository repository;
        try {
            repository = new URLRemoteRepository(url);
            contentRepositorySession = repository.login(new SimpleCredentials("guest", new char[0]));
            isContentRepositoryAvail = true;
        } catch (MalformedURLException e) {
            log.error("Failed to obtain Content Repository Session" + e.getMessage());
            log.error("Shutting down all Content Repository Services");
            isContentRepositoryAvail = false;
        } catch (LoginException e) {
            log.error("Failed to obtain Content Repository Session " + e.getMessage());
            log.error("Shutting down all Content Repository Services");
            isContentRepositoryAvail = false;
        } catch (RepositoryException e) {
            log.error("Failed to obtain Content Repository Session " + e.getMessage());
            log.error("Shutting down all Content Repository Services");
            isContentRepositoryAvail = false;
        }
    }

    public void clearContentRepository() throws RepositoryException {
        javax.jcr.Node rootNode = contentRepositorySession.getRootNode();

        try {
            rootNode.getNode(ContentRepositoryConstants.PUBLIC_END_POINT).remove();
            log.debug("Removed the Public End Point Node");
            contentRepositorySession.save();
        } catch (Exception e) {
            log.debug("Failed to remove Public End Point Node" + e.getMessage());
        }

        try {
            rootNode.getNode(ContentRepositoryConstants.SENSOR_NODE).remove();
            log.debug("Removed the SENSOR Node");
            contentRepositorySession.save();
        } catch (Exception e) {
            log.debug("Failed to remove SENSOR Node" + e.getMessage());
        }

        try {
            rootNode.getNode(ContentRepositoryConstants.CLIENT_NODE).remove();
            log.debug("Removed the CLIENT Node");
            contentRepositorySession.save();
        } catch (Exception e) {
            log.debug("Failed to remove CLIENT Node" + e.getMessage());
        }
    }

    public void createPublicEndpoint() {
        if (!isContentRepositoryAvail) {
            return;
        }
        try {

            javax.jcr.Node publicEndPointNode = contentRepositorySession.getRootNode().addNode(ContentRepositoryConstants.PUBLIC_END_POINT);
            javax.jcr.Node keyNode = publicEndPointNode.addNode(ContentRepositoryConstants.PUBLIC_END_POINT_KEY_NODE);

            publicEndPointNode.setProperty(
                    ContentRepositoryConstants.PUBLIC_END_POINT_PROPERTIES.public_end_point_address.toString(),
                    ioTCloud.getPublicEndPoint().getAddress());

            Map<String, String> properties = ioTCloud.getPublicEndPoint().getProperties();

            for (String key : properties.keySet()) {
                publicEndPointNode.setProperty(key, properties.get(key));
                keyNode.setProperty(key, key);
            }

            contentRepositorySession.save();
        } catch (RepositoryException e) {
            // TODO Auto-generated catch block
            log.error("Failed to create Public End Point Repository Node");
            log.error("Shutting down all Content Repository Services");
            isContentRepositoryAvail = false;
            contentRepositorySession.logout();
        }
    }

    /**
     * Creates Content Repository Nodes
     */
    private void initContentRepositoryNodes() {
        // Verify if previous Shut-Down was Legal/Valid
        Iterator<Node> childNodesIterator;
        try {
            childNodesIterator = contentRepositorySession.getRootNode().getNodes();
            while (childNodesIterator.hasNext()) {
                javax.jcr.Node eachChildNode = childNodesIterator.next();

                if (eachChildNode.getName().equals(ContentRepositoryConstants.CLIENT_NODE) ||
                        eachChildNode.getName().equals(ContentRepositoryConstants.SENSOR_NODE)) {
                    initiateContentRepState();

                    if (isPublicEndPointInit) {
                        return;
                    } else {
                        log.error("FAILED TO INIT SAVED PUBLIC END POINT");
                        log.error("DROPPING THE WHOLE CONTENT TREE");
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
                log.error("Failed to initiate the Content Repository");
                log.error("Trying to CLEAN the Content Repository");
                clearContentRepository();
            } catch (RepositoryException e1) {
                log.error("Failed to CLEAN the Content Repository");
            }

            log.error("Shutting down all Content Repository Services");
            isContentRepositoryAvail = false;
            contentRepositorySession.logout();
        }
    }

    private void initiateContentRepState() throws RepositoryException {
        if (!isContentRepositoryAvail) return;
        // Checking if Public End point exists in the saved state, If found
        // initiate the saved state Public End Point
        javax.jcr.Node rootNode;
        try {
            rootNode = contentRepositorySession.getRootNode();

            javax.jcr.Node publicEndPointNode = rootNode.getNode(ContentRepositoryConstants.PUBLIC_END_POINT);
            javax.jcr.Node keyNode = publicEndPointNode.getNode(ContentRepositoryConstants.PUBLIC_END_POINT_KEY_NODE);
            Map<String, String> properties = new HashMap<String, String>();
            PropertyIterator keyIterator = keyNode.getProperties();
            while (keyIterator.hasNext()) {
                String key = ((Property) keyIterator.next()).getString();
                // Avoiding the default property notifying an unstructured Node
                if (!key.equals("nt:unstructured"))
                    properties.put(key, publicEndPointNode.getProperty(key).getValue().getString());
            }

            ioTCloud.initPublicEndpoint(publicEndPointNode.getProperty(
                    ContentRepositoryConstants.PUBLIC_END_POINT_PROPERTIES.public_end_point_address.toString()).
                    getValue().getString(), properties);
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

                sensor.setPublicEndpoint(ioTCloud.getPublicEndPoint());

                Endpoint dataEndpoint;
                if (Constants.SENSOR_TYPE_BLOCK.equalsIgnoreCase(sensor.getType())) {
                    dataEndpoint = new JMSEndpoint();
                    dataEndpoint.setAddress(sensor.getId() + "/data");
                } else if (Constants.SENSOR_TYPE_STREAMING.equalsIgnoreCase(sensor.getType())) {
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
                while (propertyIterator.hasNext()) {
                    String key = ((Property) propertyIterator.next()).getString();
                    // Avoiding the default property notifying an unstructured Node
                    if (!key.equals("nt:unstructured"))
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
                while (propertyIterator.hasNext()) {
                    String key = ((Property) propertyIterator.next()).getString();
                    // Avoiding the default property notifying an unstructured Node
                    if (!key.equals("nt:unstructured"))
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
                while (propertyIterator.hasNext()) {
                    String key = ((Property) propertyIterator.next()).getString();
                    // Avoiding the default property notifying an unstructured Node
                    if (!key.equals("nt:unstructured"))
                        properties.put(key, updateSendingEndPointNode.getProperty(key).getValue().getString());
                }
                updateSendingEndpoint.setProperties(properties);
                updateSendingEndpoint.setAddress(sensor.getId() + "/update");
                sensor.setUpdateEndpoint(updateSendingEndpoint);

                ioTCloud.getSensorCatalog().addSensor(sensor);
            }
        } catch (PathNotFoundException e) {
            rootNode.addNode(ContentRepositoryConstants.SENSOR_NODE);
            contentRepositorySession.save();
        }


    }

    public void addSensor(SCSensor sensor) {
        if (!isContentRepositoryAvail) {
            return;
        }
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
            while (deppKeysetIte.hasNext()) {
                String key = deppKeysetIte.next();
                dataEndPointNode.setProperty(key, dataEndPointProperties.get(key));
                dataEndPointKeyNode.setProperty(key, key);
            }

            // Setting Control End Point Properties
            javax.jcr.Node cntrlEndPointNode = crSensor.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.CNTRL_END_POINT_NODE.toString());
            javax.jcr.Node cntrlEndPointKeyNode = cntrlEndPointNode.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.CNTRL_END_POINT_KEY_NODE.toString());
            Map<String, String> cntrlEndPointProperties = sensor.getControlEndpoint().getProperties();
            Iterator<String> ceppKeysetIte = cntrlEndPointProperties.keySet().iterator();
            while (ceppKeysetIte.hasNext()) {
                String key = ceppKeysetIte.next();
                cntrlEndPointNode.setProperty(key, cntrlEndPointProperties.get(key));
                cntrlEndPointKeyNode.setProperty(key, key);
            }

            // Setting Update Sending End Point Properties
            javax.jcr.Node updateSendingEndPointNode = crSensor.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.UPDATE_SENDING_END_POINT_NODE.toString());
            javax.jcr.Node updateSendingEndPointKeyNode = updateSendingEndPointNode.addNode(ContentRepositoryConstants.SENSOR_PROPERTIES.UPDATE_SENDING_END_POINT_KEY_NODE.toString());
            Map<String, String> updateSendingEndPointProperties = sensor.getUpdateEndpoint().getProperties();
            Iterator<String> useppKeysetIte = updateSendingEndPointProperties.keySet().iterator();
            while (useppKeysetIte.hasNext()) {
                String key = useppKeysetIte.next();
                updateSendingEndPointNode.setProperty(key, updateSendingEndPointProperties.get(key));
                updateSendingEndPointKeyNode.setProperty(key, key);
            }

            contentRepositorySession.save();

        } catch (Exception e) {
            log.error("Failed to add a sensor in to the Content Repository");
        }

    }

    public void unRegisterSensor(String id) {
        if (!isContentRepositoryAvail) return;

        try {
            sensorNode.getNode(id).remove();
            Iterator<javax.jcr.Node> clients = clientNode.getNodes();
            while (clients.hasNext()) {
                javax.jcr.Node eachClientNode = clients.next();
                if (eachClientNode.getProperty(
                        ContentRepositoryConstants.CLIENT_PROPERTIES.SENSOR_ID.toString()).
                        getValue().getString().equals(id)) {
                    eachClientNode.remove();
                }
            }
            contentRepositorySession.save();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error(" ********* Failed to remove a Sensor from the Content Repository ********* ");
        }
    }
}
