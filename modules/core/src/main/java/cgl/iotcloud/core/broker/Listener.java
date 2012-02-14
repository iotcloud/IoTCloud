package cgl.iotcloud.core.broker;

import cgl.iotcloud.core.Control;
import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.State;
import cgl.iotcloud.core.message.MessageHandler;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.jms.JMSDataMessageFactory;
import cgl.iotcloud.core.message.jms.MessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

/**
 * This will be used by the Sensors to listen to the control messages by the SC.
 */
public class Listener implements Control, ManagedLifeCycle {
    private static Logger log = LoggerFactory.getLogger(Listener.class);

    private Connections connections = null;

    private MessageHandler messageHandler = null;

    private MessageConsumer consumer = null;

    private Connection connection = null;

    private Destination destination = null;

    private Session session = null;

    private State state = State.DEFAULT;

    private String destinationPath = null;

    private MessageFactory messageFactory = new JMSDataMessageFactory();

    public Listener(Connections connections, MessageHandler messageHandler,
                    String destinationPath) {
        this.connections = connections;
        this.messageHandler = messageHandler;
        this.destinationPath = destinationPath;
    }

    /**
     * Initialize the Listener. Establish the connection to the JMS broker.
     */
    public void init() {
        connections.init();

        connection = connections.getConnection();
        session = connections.getSession(connection);
        destination = connections.getDestination(destinationPath, session);

        try {
            consumer = connections.getMessageConsumer(connection, session, destination, new Receiver());
        } catch (JMSException e) {
            handleException("Failed to create a consumer for the destination: " + destination, e);
        }

        state = State.INITIALIZED;
    }

    /**
     * Destroy the listener. Close the connection to the JMS broker.
     */
    public void destroy() {
        try {
            if (state != State.STOPPED) {
                connection.stop();
            }
            consumer.close();
            connection.close();

            state = State.DESTROYED;
        } catch (JMSException e) {
            handleException("Failed to close the JMS connection for the destination: "
                    + destination, e);
        }
    }

    public void start() {
        if (state != State.INITIALIZED) {
            throw new IllegalStateException("State should be: " + State.INITIALIZED.getState());
        }
        try {
            connection.start();

            state = State.STARTED;
        } catch (JMSException e) {
            handleException("Failed to start the JMS connection for the destination: "
                    + destination, e);
        }
    }

    public void stop() {
        if (state != State.STARTED) {
            throw new IllegalStateException("State should be: " + State.STARTED.getState());
        }
        try {
            session.close();
            connection.stop();

            state = State.STOPPED;
        } catch (JMSException e) {
            handleException("Failed to stop the JMS connection for the destination: "
                    + destination, e);
        }
    }

    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public String getState() {
        return state.getState();
    }

    private class Receiver implements MessageListener {
        public void onMessage(Message message) {
            // construct a sensor message
            SensorMessage sm = messageFactory.create(message);

            // call the message handler
            messageHandler.onMessage(sm);
        }
    }

    protected void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

}
