package cgl.iotcloud.core.message.jms;

import cgl.iotcloud.core.IOTRuntimeException;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.ObjectDataMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.Serializable;

/**
 *
 */
public class JMSDataMessageFactory implements JMSMessageFactory {
    private static Logger log = LoggerFactory.getLogger(JMSDataMessageFactory.class);

    public SensorMessage create(Message message) {
        if (message instanceof TextMessage) {
            TextDataMessage txtMessage = new TextDataMessage();
            try {
                txtMessage.setText(((javax.jms.TextMessage) message).getText());
            } catch (JMSException e) {
                handleException("Failed to retrieve text from the message " + message, e);
            }

            return txtMessage;
        } else if (message instanceof ObjectMessage) {
            try {
                Serializable o = ((ObjectMessage) message).getObject();
                if (o instanceof ObjectDataMessage) {
                    return (ObjectDataMessage) o;
                } else {
                    handleException("The message in not a ObjectDataMessage");
                }
            } catch (JMSException e) {
                handleException("Error retrieving the message", e);
            }
        }
        return null;
    }

    public Message create(SensorMessage message, Session session) {
        if (message instanceof TextDataMessage) {
            TextMessage txtMessage = null;
            try {
                txtMessage = session.createTextMessage();
                txtMessage.setText(((TextDataMessage) message).getText());

                return txtMessage;
            } catch (JMSException e) {
                handleException("Failed to create text message from the session " + session, e);
            }
        } else if (message instanceof ObjectDataMessage) {
            try {
                ObjectMessage objectMessage = session.createObjectMessage();
                Serializable s = (Serializable) message;
                objectMessage.setObject(s);

                return objectMessage;
            } catch (JMSException e) {
                handleException("Failed to create the object message with the JMS session: " + session, e);
            }
        }
        return null;
    }

    private static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new IOTRuntimeException(s, e);
    }

    private static void handleException(String s) {
        log.error(s);
        throw new IOTRuntimeException(s);
    }
}
