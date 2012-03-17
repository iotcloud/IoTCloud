package cgl.iotcloud.core.message.jms;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.control.DefaultControlMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

public class JMSControlMessageFactory implements JMSMessageFactory {
    private static Logger log = LoggerFactory.getLogger(JMSControlMessageFactory.class);

    public SensorMessage create(Message message) {
        if (message instanceof TextMessage) {
            TextDataMessage txtMessage = new TextDataMessage();
            try {
                txtMessage.setText(((javax.jms.TextMessage) message).getText());
            } catch (JMSException e) {
                handleException("Failed to retrieve text from the message " + message, e);
            }

            return txtMessage;
        } else if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;
            DefaultControlMessage controlMessage = new DefaultControlMessage();
            try {
                Enumeration e = mapMessage.getMapNames();
                while (e.hasMoreElements()) {
                    Object o = e.nextElement();
                    if (o instanceof String) {
                        controlMessage.addControl((String)o, mapMessage.getObject((String) o));
                    }
                }
            } catch (JMSException e) {
                handleException("Failed to retrieve map message from the message " + message, e);
            }
            return controlMessage;
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
        } else if (message instanceof DefaultControlMessage) {
            try {
                MapMessage mapMessage = session.createMapMessage();
                Set<Map.Entry<String, Object>> entries = ((DefaultControlMessage) message).
                        getControls().entrySet();
                for (Map.Entry e : entries) {
                    mapMessage.setObject((String) e.getKey(), e.getValue());
                }
                return  mapMessage;
            } catch (JMSException e) {
                handleException("Failed to create map message from the session " + session, e);
            }
        }
        return null;
    }

    protected static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }
}
