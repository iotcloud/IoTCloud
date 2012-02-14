package cgl.iotcloud.core.broker;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.config.ConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import java.util.Hashtable;
import java.util.Map;

/**
 * This manages the connections to a JMS Topic or a Queue.
 */
public class Connections {
    private static Logger log = LoggerFactory.getLogger(Connections.class);

    /** The name used for the connection factory definition within Axis2 */
    private String name = "default";
    /** The list of parameters from the broker-config.xml definition */
    private Map<String, String> parameters = new Hashtable<String, String>();

    /** The JMS ConnectionFactory this definition refers to */
    private ConnectionFactory conFactory = null;
    /** The shared JMS Connection for this JMS connection factory */
    private Connection sharedConnection = null;
    /** The shared JMS Session for this JMS connection factory */
    private Session sharedSession = null;
    /** The shared JMS MessageProducer for this JMS connection factory */
    private MessageProducer sharedProducer = null;
    /** The shared message consumer */
    private MessageConsumer sharedConsumer = null;
    /** The Shared Destination */
    private Destination sharedDestination = null;
    /** Initial context */
    private Context context = null;
    /** The shared JMS connection for this JMS connection factory */
    private int cacheLevel = Constants.CACHE_NONE;

    public Connections(String name) {
        this.name = name;
    }

    /**
     * Initialize the connection factory
     * @throws cgl.iotcloud.core.SCException if an error occurs
     */
    public void init() throws SCException {
        String key = ConfigConstants.PARAM_CACHE_LEVEL;
        String val = parameters.get(key);

        if ("none".equalsIgnoreCase(val)) {
            this.cacheLevel = Constants.CACHE_NONE;
        } else if ("connection".equalsIgnoreCase(val)) {
            this.cacheLevel = Constants.CACHE_CONNECTION;
        } else if ("session".equals(val)){
            this.cacheLevel = Constants.CACHE_SESSION;
        } else if ("producer".equals(val)) {
            this.cacheLevel = Constants.CACHE_PRODUCER;
        } else if (val != null) {
            throw new SCException("Invalid cache level : " + val + " for JMS CF : " + name);
        }

        try {
            context = new InitialContext((Hashtable<String, String>) parameters);
            conFactory = lookup(context, javax.jms.ConnectionFactory.class,
                    parameters.get(ConfigConstants.PARAM_CONFAC_JNDI_NAME));
            log.debug("JMS ConnectionFactory : {} initialized", name != null ? name : "");

        } catch (NamingException e) {
            throw new SCException("Cannot acquire JNDI context, JMS Connection factory : " +
                parameters.get(ConfigConstants.PARAM_CONFAC_JNDI_NAME) +
                " for JMS CF : " + name + " using : " + parameters, e);
        }
    }

    /**
     * Get the destination specified by the path
     * @param destination the destination to be looked up
     * @return the JMS destination
     */
    public Destination getDestination(String destination) {
        try {
            return lookup(context, Destination.class, destination);
        } catch (NamingException e) {
            handleException("Error while looking up the destination: " + destination, e);
        }
        return null;
    }

    /**
     * Get the destination specified by the path. If the destination cannot be found a
     * new one is created
     * @param destination the destination to be looked up
     * @param session jms session
     * @return the JMS destination
     */
    public Destination getDestination(String destination, Session session) {
        Destination dest = null;
        try {
            dest = lookup(context, Destination.class, destination);
        } catch (NamingException e) {
            try {
                dest = session.createTopic(destination);
            } catch (JMSException je) {
                handleException("Error while creating the destination topic: " + destination, je);
            }
        }
        return dest;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Get a new Connection or shared Connection from this JMS CF
     * @return new or shared Connection from this JMS CF
     */
    public Connection getConnection() {
        if (cacheLevel > Constants.CACHE_NONE) {
            return getSharedConnection();
        } else {
            return createConnection();
        }
    }

    /**
     * Get a new Session or shared Session from this JMS CF
     * @param connection the Connection to be used
     * @return new or shared Session from this JMS CF
     */
    public Session getSession(Connection connection) {
        if (cacheLevel > Constants.CACHE_CONNECTION) {
            return getSharedSession();
        } else {
            return createSession((connection == null ? getConnection() : connection));
        }
    }

    /**
     * Get a new MessageProducer or shared MessageProducer from this JMS CF
     * @param connection the Connection to be used
     * @param session the Session to be used
     * @param destination the Destination to bind MessageProducer to
     * @return new or shared MessageProducer from this JMS CF
     */
    public MessageProducer getMessageProducer(
        Connection connection, Session session, Destination destination) {
        if (cacheLevel > Constants.CACHE_SESSION) {
            return getSharedProducer();
        } else {
            return createProducer((session == null ? getSession(connection) : session), destination);
        }
    }

    /**
     * Get a new MessageConsumer or shared MessageConsume from this JMS CF
     * @param connection the Connection to be used
     * @param session the Session to be used
     * @param destination the Destination to bind MessageConsumer to
     * @param listener the message listener
     * @return new or shared MessageProducer from this JMS CF
     * @throws javax.jms.JMSException if an exception occurs
     */
    public MessageConsumer getMessageConsumer(
        Connection connection, Session session,
        Destination destination, MessageListener listener) throws JMSException {
        if (cacheLevel > Constants.CACHE_SESSION) {
            return sharedConsumer;
        } else {
            return createConsumer(
                    (session == null ? getSession(connection) : session), destination, listener);
        }
    }

    /**
     * This is a JMS spec independent method to create a MessageProducer. Please be cautious when
     * making any changes
     *
     * @param session JMS session
     * @param destination the Destination
     * @param isQueue is the Destination a queue?
     * @param jmsSpec11 should we use JMS 1.1 API ?
     * @return a MessageProducer to send messages to the given Destination
     * @throws JMSException on errors, to be handled and logged by the caller
     */
    public static MessageProducer createProducer(
        Session session, Destination destination, Boolean isQueue, boolean jmsSpec11) throws JMSException {

        if (jmsSpec11 || isQueue == null) {
            return session.createProducer(destination);
        } else {
            if (isQueue) {
                return ((QueueSession) session).createSender((Queue) destination);
            } else {
                return ((TopicSession) session).createPublisher((Topic) destination);
            }
        }
    }

    public MessageConsumer createConsumer(
        Session session, Destination destination, MessageListener listener) throws JMSException {

        MessageConsumer consumer = session.createConsumer(destination);
        if (listener != null) {
            consumer.setMessageListener(listener);
        }
        return consumer;
    }

    private static <T> T lookup(Context context, Class<T> clazz, String name)
        throws NamingException {

        Object object = context.lookup(name);
        try {
            return clazz.cast(object);
        } catch (ClassCastException ex) {
            // Instead of a ClassCastException, throw an exception with some
            // more information.
            if (object instanceof Reference) {
                Reference ref = (Reference)object;
                handleException("JNDI failed to de-reference Reference with name " +
                        name + "; is the factory " + ref.getFactoryClassName() +
                        " in your classpath?");
                return null;
            } else {
                handleException("JNDI lookup of name " + name + " returned a " +
                        object.getClass().getName() + " while a " + clazz + " was expected");
                return null;
            }
        }
    }

    public synchronized void stop() {
        if (sharedConnection != null) {
            try {
            	sharedConnection.close();
            } catch (JMSException e) {
                log.warn("Error shutting down connection factory : {}", name, e);
            }
        }
    }

    /**
     * Get a new Connection or shared Connection from this JMS CF
     * @return new or shared Connection from this JMS CF
     */
    private synchronized Connection getSharedConnection() {
        if (sharedConnection == null) {
            sharedConnection = createConnection();
            log.debug("Created shared JMS Connection for JMS CF : {}", name);
        }
        return sharedConnection;
    }

    /**
     * Get a shared Session from this JMS CF
     * @return shared Session from this JMS CF
     */
    private synchronized Session getSharedSession() {
        if (sharedSession == null) {
            sharedSession = createSession(getSharedConnection());
            log.debug("Created shared JMS Session for JMS CF : {}", name);
        }
        return sharedSession;
    }

    /**
     * Get a shared MessageProducer from this JMS CF
     * @return shared MessageProducer from this JMS CF
     */
    private synchronized MessageProducer getSharedProducer() {
        if (sharedProducer == null) {
            sharedProducer = createProducer(getSharedSession(), sharedDestination);
            log.debug("Created shared JMS MessageConsumer for JMS CF : {}", name);
        }
        return sharedProducer;
    }

    /**
     * Create a new Connection
     * @return a new Connection
     */
    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = createConnection(
                    conFactory,
                    parameters.get(ConfigConstants.PARAM_JMS_USERNAME),
                    parameters.get(ConfigConstants.PARAM_JMS_PASSWORD),
                    isJmsSpec11(), isQueue());

            log.debug("New JMS Connection from JMS CF : {} created", name);
        } catch (JMSException e) {
            handleException("Error acquiring a Connection from the JMS CF : " + name +
                " using properties : " + parameters, e);
        }
        return connection;
    }

    /**
     * Create a new MessageProducer
     * @param session Session to be used
     * @param destination Destination to be used
     * @return a new MessageProducer
     */
    private MessageProducer createProducer(Session session, Destination destination) {
        try {
            log.debug("Creating a new JMS MessageProducer from JMS CF : {}", name);

            return createProducer(
                session, destination, isQueue(), isJmsSpec11());

        } catch (JMSException e) {
            handleException("Error creating JMS producer from JMS CF : " + name,e);
        }
        return null;
    }

    /**
     * Create a new Session
     * @param connection Connection to use
     * @return A new Session
     */
    private Session createSession(Connection connection) {
        try {
            log.debug("Creating a new JMS Session from JMS CF : {}", name);

            return createSession(
                connection, false, Session.AUTO_ACKNOWLEDGE, isJmsSpec11(), isQueue());

        } catch (JMSException e) {
            handleException("Error creating JMS session from JMS CF : " + name, e);
        }
        return null;
    }

    /**
     * This is a JMS spec independent method to create a Session. Please be cautious when
     * making any changes
     *
     * @param connection the JMS Connection
     * @param transacted should the session be transacted?
     * @param ackMode the ACK mode for the session
     * @param jmsSpec11 should we use the JMS 1.1 API?
     * @param isQueue is this Session to deal with a Queue?
     * @return a Session created for the given information
     * @throws JMSException on errors, to be handled and logged by the caller
     */
    public static Session createSession(Connection connection, boolean transacted, int ackMode,
        boolean jmsSpec11, Boolean isQueue) throws JMSException {

        if (jmsSpec11 || isQueue == null) {
            return connection.createSession(transacted, ackMode);

        } else {
            if (isQueue) {
                return ((QueueConnection) connection).createQueueSession(transacted, ackMode);
            } else {
                return ((TopicConnection) connection).createTopicSession(transacted, ackMode);
            }
        }
    }

    /**
     * This is a JMS spec independent method to create a Connection. Please be cautious when
     * making any changes
     *
     * @param conFac the ConnectionFactory to use
     * @param user optional user name
     * @param pass optional password
     * @param jmsSpec11 should we use JMS 1.1 API ?
     * @param isQueue is this to deal with a Queue?
     * @return a JMS Connection as requested
     * @throws JMSException on errors, to be handled and logged by the caller
     */
    public static Connection createConnection(ConnectionFactory conFac,
        String user, String pass, boolean jmsSpec11, Boolean isQueue) throws JMSException {

        Connection connection = null;
        if (log.isDebugEnabled()) {
            log.debug("Creating a " + (isQueue == null ? "Generic" : isQueue ? "Queue" : "Topic") +
                "Connection using credentials : (" + user + "/" + pass + ")");
        }

        TopicConnectionFactory tConFac = null;
        tConFac = (TopicConnectionFactory) conFac;
        connection = tConFac.createTopicConnection();

//        if (jmsSpec11 || isQueue == null) {
//            if (user != null && pass != null) {
//                connection = conFac.createConnection(user, pass);
//            } else {
//                connection = conFac.createConnection();
//            }
//        } else {
//            QueueConnectionFactory qConFac = null;
//            TopicConnectionFactory tConFac = null;
//            if (isQueue) {
//                qConFac = (QueueConnectionFactory) conFac;
//            } else {
//                tConFac = (TopicConnectionFactory) conFac;
//            }
//
//            if (user != null && pass != null) {
//                if (qConFac != null) {
//                    connection = qConFac.createQueueConnection(user, pass);
//                } else if (tConFac != null) {
//                    connection = tConFac.createTopicConnection(user, pass);
//                }
//            } else {
//                if (qConFac != null) {
//                    connection = qConFac.createQueueConnection();
//                } else if (tConFac != null) {
//                    connection = tConFac.createTopicConnection();
//                }
//            }
//        }
        return connection;
    }

    /**
     * Return the type of the JMS CF Destination
     * @return TRUE if a Queue, FALSE for a Topic and NULL for a JMS 1.1 Generic Destination
     */
    public Boolean isQueue() {
        if (parameters.get(ConfigConstants.PARAM_CONFAC_TYPE) == null &&
                parameters.get(ConfigConstants.PARAM_DEST_TYPE) == null) {
            return false;
        }

        if (parameters.get(ConfigConstants.PARAM_CONFAC_TYPE) != null) {
            if ("queue".equalsIgnoreCase(parameters.get(ConfigConstants.PARAM_CONFAC_TYPE))) {
                return true;
            } else if ("topic".equalsIgnoreCase(parameters.get(ConfigConstants.PARAM_CONFAC_TYPE))) {
                return false;
            } else {
                throw new SCException("Invalid " + ConfigConstants.PARAM_CONFAC_TYPE + " : " +
                    parameters.get(ConfigConstants.PARAM_CONFAC_TYPE) + " for JMS CF : " + name);
            }
        } else {
            if ("queue".equalsIgnoreCase(parameters.get(ConfigConstants.PARAM_DEST_TYPE))) {
                return true;
            } else if ("topic".equalsIgnoreCase(parameters.get(ConfigConstants.PARAM_DEST_TYPE))) {
                return false;
            } else {
                throw new SCException("Invalid " + ConfigConstants.PARAM_DEST_TYPE + " : " +
                    parameters.get(ConfigConstants.PARAM_DEST_TYPE) + " for JMS CF : " + name);
            }
        }
    }

    /**
     * Should the JMS 1.1 API be used? - defaults to yes
     * @return true, if JMS 1.1 api should  be used
     */
    public boolean isJmsSpec11() {
        return parameters.get(ConfigConstants.PARAM_JMS_SPEC_VER) == null ||
            "1.1".equals(parameters.get(ConfigConstants.PARAM_JMS_SPEC_VER));
    }

    public String getName() {
        return name;
    }

    public void addParameter(String name, String value) {
        parameters.put(name, value);
    }

    protected static void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }

    protected static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }
}
