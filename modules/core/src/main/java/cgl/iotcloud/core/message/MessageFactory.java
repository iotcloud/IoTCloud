package cgl.iotcloud.core.message;

import cgl.iotcloud.core.message.SensorMessage;

import javax.jms.Message;
import javax.jms.Session;

public interface MessageFactory {
    /**
     * Create a sensor message with a JMS message
     * @param message the JMS message
     *
     * @return sensor message
     */
    public SensorMessage create(Message message);

    /**
     * Create a JMS message with a sensor message
     * @param message the sensor message
     * @param session jms session
     *
     * @return jms message
     */
    public Message create(SensorMessage message, Session session);
}
