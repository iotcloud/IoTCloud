package cgl.iotcloud.core.broker;

import cgl.iotcloud.core.Control;
import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.State;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.message.jms.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
* Every sensor will have a message sender to send messages to the broker
*/
public class Sender implements Control, ManagedLifeCycle {
    private static Logger log = LoggerFactory.getLogger(Sender.class);
    /** The connections used by this sender */
    private Connections connections = null;
    /** The JMS message producer */
    private MessageProducer producer = null;
    /** JMS connection used by the listener */
    private Connection connection = null;
    /** JMS destination */
    private Destination destination = null;
    /** Session used by the listener */
    private Session session = null;
    /** State of the listener */
    private State state = State.INITIALIZED;
    /** The topic to listen to */
    private String destinationPath = null;

    private MessageFactory messageFactory = new JMSDataMessageFactory();

    public Sender(Connections connections, String destinationPath) {
        this.connections = connections;
        this.destinationPath = destinationPath;
    }

    public void init() {
        connections.init();

        connection = connections.getConnection();
        session = connections.getSession(connection);
        destination = connections.getDestination(destinationPath, session);

        producer = connections.getMessageProducer(connection, session, destination);

        state = State.INITIALIZED;
    }

    public void destroy() {

        state = State.DESTROYED;
    }

    public void start() {
        if (state != State.INITIALIZED) {
            throw new IllegalStateException("The sender should be initialized");
        }
        try {
            connection.start();

            state = State.STARTED;
        } catch (JMSException e) {
            handleException("Connection to the destination cannot be started " + destination, e);
        }
    }

    public void stop() {
        if (state != State.STARTED) {
            throw new IllegalStateException("The sender should be stared");
        }
        try {
            connection.stop();

            state = State.STOPPED;
        } catch (JMSException e) {
            handleException("Connection to the destination cannot be stopped " + destination, e);
        }
    }

    public String getState() {
        return state.getState();
    }

    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public void send(SensorMessage sm) {
        try {
            // construct a JMS message from the sensor message
            Message message = messageFactory.create(sm, session);

            producer.send(message);
        } catch (JMSException e) {
            handleException("Failed to create a text message using the session: " + session, e);
        }
    }

    protected void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }
}
