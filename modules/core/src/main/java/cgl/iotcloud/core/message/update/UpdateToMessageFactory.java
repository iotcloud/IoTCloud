package cgl.iotcloud.core.message.update;

import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.TextDataMessage;
import com.iotCloud.message.xsd.Sensor;
import com.iotCloud.message.xsd.UpdateDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class UpdateToMessageFactory {
    private static Logger log = LoggerFactory.getLogger(UpdateToMessageFactory.class);
    public SensorMessage create(UpdateMessage message) {
        TextDataMessage txtMsg = new TextDataMessage();
        UpdateDocument document = UpdateDocument.Factory.newInstance();
        UpdateDocument.Update update = document.addNewUpdate();
        Sensor sensor = update.addNewSensor();
        sensor.setId(message.getSensorId());

        for (Map.Entry<String, String> e : message.getAllUpdates().entrySet()) {
            com.iotCloud.message.xsd.Param param = sensor.addNewParam();
            param.setName(e.getKey());
            param.setValue(e.getValue());
        }

        String text = document.toString();
        if (log.isDebugEnabled()) {
            log.debug("Creating sensor update message for sensor ID: " + message.getSensorId() + " " + text);
        }
        txtMsg.setText(text);

        return txtMsg;
    }
}
