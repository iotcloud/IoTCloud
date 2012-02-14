package cgl.iotcloud.core.config;

public class ConfigConstants {
    public static final String PARAM_CACHE_LEVEL = "jms.cacheLevel";
    public static final String PARAM_CONFAC_JNDI_NAME = "jms.ConnectionFactoryJNDIName";

    /** The username to use when obtaining a JMS Connection */
    public static final String PARAM_JMS_USERNAME = "jms.UserName";
    /** The password to use when obtaining a JMS Connection */
    public static final String PARAM_JMS_PASSWORD = "jms.Password";

    /**
     * Connection factory type if using JMS 1.0, either DESTINATION_TYPE_QUEUE or DESTINATION_TYPE_TOPIC
     */
    public static final String PARAM_CONFAC_TYPE = "jms.ConnectionFactoryType";

    /**
     * The Connection factory Parameter name indicating the destination type for requests.
     */
    public static final String PARAM_DEST_TYPE = "jms.DestinationType";

     /** The parameter indicating the JMS API specification to be used - if this is "1.1" the JMS
     * 1.1 API would be used, else the JMS 1.0.2B
     */
    public static final String PARAM_JMS_SPEC_VER = "jms.JMSSpecVersion";
}
