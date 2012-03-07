package cgl.iotcloud.core.message.jms;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

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
        }
        return null;
    }

    public Message create(SensorMessage message, Session session) {
        if (message instanceof TextDataMessage) {
            TextMessage txtMessage = null;
            try {
                txtMessage = session.createTextMessage();
                txtMessage.setText(((TextDataMessage) message).getText());
            } catch (JMSException e) {
                handleException("Failed to create text message from the session " + session, e);
            }
            return txtMessage;
        }
        return null;
    }

    private static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }
}
