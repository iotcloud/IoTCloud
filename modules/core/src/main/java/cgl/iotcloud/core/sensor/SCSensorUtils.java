package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.SCException;
import com.iotcloud.sensorInfo.xsd.*;
import com.iotcloud.sensorInfo.xsd.Endpoint;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Utility class for converting sensor
 */
public class SCSensorUtils {
    private static Logger log = LoggerFactory.getLogger(SCSensorUtils.class);

    public static String convertToString(SCSensor sensor) {
        SensorInfoDocument document = SensorInfoDocument.Factory.newInstance();
        SensorInfoDocument.SensorInfo sensorInfo = document.addNewSensorInfo();

        sensorInfo.setName(sensor.getName());
        sensorInfo.setType(sensor.getType());
        sensorInfo.setId(sensor.getId());

        Endpoint controlEndpoint = sensorInfo.addNewControlEndpoint();
        Endpoint dataEndpoint = sensorInfo.addNewDataEndpoint();
        Endpoint updateEndpoint = sensorInfo.addNewUpdateEndpoint();

        cgl.iotcloud.core.Endpoint epr = sensor.getControlEndpoint();
        controlEndpoint.setAddress(epr.getAddress());
        Properties properties = controlEndpoint.addNewProperties();

        for (Map.Entry<String, String> e : epr.getProperties().entrySet()) {
            Property property = properties.addNewProperty();
            property.setName(e.getKey());
            property.setStringValue(e.getValue());
        }

        epr = sensor.getDataEndpoint();
        dataEndpoint.setAddress(epr.getAddress());
        properties = dataEndpoint.addNewProperties();

        for (Map.Entry<String, String> e : epr.getProperties().entrySet()) {
            Property property = properties.addNewProperty();
            property.setName(e.getKey());
            property.setStringValue(e.getValue());
        }

        epr = sensor.getUpdateEndpoint();
        dataEndpoint.setAddress(epr.getAddress());
        properties = updateEndpoint.addNewProperties();

        for (Map.Entry<String, String> e : epr.getProperties().entrySet()) {
            Property property = properties.addNewProperty();
            property.setName(e.getKey());
            property.setStringValue(e.getValue());
        }

        return document.toString();
    }

    public static SCSensor convertToSensor(String string) {
        try {
            SensorInfoDocument document = SensorInfoDocument.Factory.parse(string);

            SCSensor sensor = new SCSensor(document.getSensorInfo().getName());

            sensor.setId(document.getSensorInfo().getId());
            sensor.setType(document.getSensorInfo().getType());

            document.getSensorInfo().getDataEndpoint();
        } catch (XmlException e) {
            handleException("Failed to convert the text to a XML", e);
        }
        return null;
    }

    private static cgl.iotcloud.core.Endpoint convertToEndpoint(Endpoint endpoint) {
        return null;
    }

    private static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

}
