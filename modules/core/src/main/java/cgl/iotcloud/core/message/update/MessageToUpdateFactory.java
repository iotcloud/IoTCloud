package cgl.iotcloud.core.message.update;

import cgl.iotcloud.core.SCException;
import com.iotCloud.message.xsd.Param;
import com.iotCloud.message.xsd.Sensor;
import com.iotCloud.message.xsd.UpdateDocument;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageToUpdateFactory {
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
}
