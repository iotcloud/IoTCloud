package cgl.iotcloud.core.message.update;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.jms.JMSMessageFactory;
import com.iotcloud.message.xsd.Param;
import com.iotcloud.message.xsd.Sensor;
import com.iotcloud.message.xsd.UpdateDocument;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Create a update message from the text.
 */
public class MessageToUpdateFactory implements JMSMessageFactory {
    private static Logger log = LoggerFactory.getLogger(MessageToUpdateFactory.class);

    public UpdateMessage createFromMessage(String text) {
        try {

            UpdateDocument document = UpdateDocument.Factory.parse(text);

            UpdateDocument.Update update = document.getUpdate();
            String id = update.getSensor().getId();
            UpdateMessage message = new UpdateMessage(id);

            Sensor sensor = update.getSensor();
            for (Param p : sensor.getParamArray()) {
                message.addUpdate(p.getName(), p.getValue());
            }

            return message;
        } catch (XmlException e) {
            handleException("Invalid update document: " + text, e);
        }

        return null;
    }

    private void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    @Override
    public SensorMessage create(Message message) {
        try {
            if (message instanceof TextMessage) {
                UpdateDocument document = UpdateDocument.Factory.parse(((TextMessage) message).getText());

                UpdateDocument.Update update = document.getUpdate();
                String id = update.getSensor().getId();
                UpdateMessage updateMessage = new UpdateMessage(id);

                Sensor sensor = update.getSensor();
                for (Param p : sensor.getParamArray()) {
                    updateMessage.addUpdate(p.getName(), p.getValue());
                }

                return updateMessage;
            }
        } catch (XmlException e) {
            handleException("Invalid update document: " + message, e);
        } catch (JMSException e) {
            handleException("Error retrieving text from the message", e);
        }

        return null;
    }

    @Override
    public Message create(SensorMessage message, Session session) {
        return null;
    }
}
