package cgl.iotcloud.core;

/**
 * Constants used in the project.
 */
public class Constants {
    public static final String BROKER_CONFIG_FILE = "repository/conf/broker-config.xml";
    public static final String DEFAULT_BROKER_NAME = "default";

    public static final String JETTY_CONFIG_FILE = "repository/conf/jetty.xml";

    public static final String SENSOR_TYPE_BLOCK = "block";

    public static final String SENSOR_TYPE_STREAMING = "stream";

    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String MESSAGE_TYPE_BYTE = "byte";

    /** Do not cache any JMS resources between tasks (when sending) or JMS CF's (when sending) */
	public static final int CACHE_NONE = 0;
	/** Cache only the JMS connection between tasks (when receiving), or JMS CF's (when sending)*/
	public static final int CACHE_CONNECTION = 1;
	/** Cache only the JMS connection and Session between tasks (receiving), or JMS CF's (sending) */
	public static final int CACHE_SESSION = 2;
	/** Cache the JMS connection, Session and Consumer between tasks when receiving*/
	public static final int CACHE_CONSUMER = 3;
	/** Cache the JMS connection, Session and Producer within a JMSConnectionFactory when sending */
	public static final int CACHE_PRODUCER = 4;
    /** automatic choice of an appropriate caching level (depending on the transaction strategy) */
	public static final int CACHE_AUTO = 5;

    /** The web services context path */
    public static final String CONTEXT_PATH = "/soap";
    /** The services context path */
    public static final String SERVICES_PATH = "/services/*";
    /** Location of axis2 repository */
    public static final String REPOSITORY_AXIS2 = "repository/axis2";
    /** Location of axis2.xml */
    public static final String REPOSITORY_AXIS2_CONF_AXIS2_XML = "repository/axis2/conf/axis2.xml";

    public static final String SENSOR_CLOUD_AXIS2_PROPERTY = "SC_PROPERTY";

    public static final String IOT_CLOUD_SERVLET_PROPERTY = "IOT_SERVLET_PROPERTY";

    public class Updates {
        public static final String ADDED = "ADDED";
        public static final String REMOVED = "REMOVED";
        public static final String ALIVE = "ALIVE";
        public static final String CUSTOM = "CUSTOM";
        public static final String STATUS = "STATUS";
    }

    public static final String MESSAGE_TYPE_BLOCK  = "block";
    public static final String MESSAGE_TYPE_STREAM = "stream";
}
