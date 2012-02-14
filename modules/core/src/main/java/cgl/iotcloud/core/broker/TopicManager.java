package cgl.iotcloud.core.broker;

import cgl.iotcloud.core.SCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Creates and deletes topics on the broker for sensors and clients to listen and send data to.
 */
public class TopicManager {
    private Logger log = LoggerFactory.getLogger(TopicManager.class);

    private Connections connections = null;

    private Connection connection = null;

    private Session session = null;

    public TopicManager(Connections connections) {
        this.connections = connections;
    }

    public void init() {
        connection = connections.getConnection();
        try {
            session = connections.getSession(connection);

            connection.start();
        } catch (JMSException e) {
            handleException("Error creating a JMS session", e);
        }
    }

    public void destroy() {
        try {
            session.close();
            connection.stop();
        } catch (JMSException e) {
            handleException("Error destroying the Topics Manager", e);
        }
    }

    public void createTopic(String topic) {
        try {
            session.createTopic(topic);
        } catch (JMSException e) {
            handleException("Error creating a JMS topic: " + topic, e);
        }
    }

    protected void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }
}
