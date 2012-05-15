package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import com.iotcloud.sensorInfo.xsd.*;
import com.iotcloud.sensorInfo.xsd.Endpoint;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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

            cgl.iotcloud.core.Endpoint epr;
            if (sensor.getType().equals(Constants.SENSOR_TYPE_BLOCK)) {
                epr = convertToEndpoint(document.getSensorInfo().getDataEndpoint(), 0);
                sensor.setDataEndpoint(epr);
            } else if (sensor.getType().equals(Constants.SENSOR_TYPE_STREAMING)) {
                epr = convertToEndpoint(document.getSensorInfo().getDataEndpoint(), 1);
                sensor.setDataEndpoint(epr);
            }

            if (document.getSensorInfo().getControlEndpoint() != null) {
                epr = convertToEndpoint(document.getSensorInfo().getControlEndpoint(), 0);
                sensor.setControlEndpoint(epr);
            }

            if (document.getSensorInfo().getUpdateEndpoint() != null) {
                epr = convertToEndpoint(document.getSensorInfo().getUpdateEndpoint(), 0);
                sensor.setControlEndpoint(epr);
            }

            return sensor;
        } catch (XmlException e) {
            handleException("Failed to convert the text to a XML", e);
        }
        return null;
    }

    private static cgl.iotcloud.core.Endpoint convertToEndpoint(Endpoint endpoint, int type) {
        cgl.iotcloud.core.Endpoint epr;

        if (type == 0) {
            epr = new JMSEndpoint();
        } else if (type == 1) {
            epr = new StreamingEndpoint();
        } else {
            handleException("");
            return null;
        }

        epr.setAddress(endpoint.getAddress());
        Properties properties = endpoint.getProperties();

        Map<String, String> props = new HashMap<String, String>();
        for (Property p : properties.getPropertyArray()) {
            props.put(p.getName(), p.getStringValue());
        }

        epr.setProperties(props);
        return epr;
    }

    private static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    private static void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }
}
