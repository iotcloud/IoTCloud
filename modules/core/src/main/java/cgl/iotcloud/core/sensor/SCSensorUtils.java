package cgl.iotcloud.core.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.endpoint.JMSEndpoint;
import cgl.iotcloud.core.endpoint.StreamingEndpoint;
import com.iotcloud.sensorInfo.xsd.*;
import com.iotcloud.sensorInfo.xsd.Endpoint;
import com.iotcloud.sensorInfo.xsd.Sensor;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for converting sensor
 */
public class SCSensorUtils {
    private static Logger log = LoggerFactory.getLogger(SCSensorUtils.class);

    public static String convertToString(List<SCSensor> sensors) {
        AllSensorsDocument document = AllSensorsDocument.Factory.newInstance();
        AllSensorsDocument.AllSensors allSensors = document.addNewAllSensors();

        Sensor []sensorArray = new Sensor[sensors.size()];
        for (int i = 0; i < sensors.size(); i++) {
            sensorArray[i] = convertToXML(sensors.get(i));
        }

        if (sensors.size() > 0) {
            allSensors.setSensorArray(sensorArray);
        }
        return document.toString();
    }

    public static String convertToString(SCSensor sensor) {
        SensorInfoDocument document = SensorInfoDocument.Factory.newInstance();
        Sensor s = convertToXML(sensor);
        document.setSensorInfo(s);

        return document.toString();
    }

    private static Sensor convertToXML(SCSensor sensor) {
        Sensor sensorInfo = Sensor.Factory.newInstance();

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
        return sensorInfo;
    }

    public static List<SCSensor> convertToSensors(InputStream in) {
        try {
            AllSensorsDocument document = AllSensorsDocument.Factory.parse(in);
            List<SCSensor> ss = new ArrayList<SCSensor>();

            Sensor sensors[] = document.getAllSensors().getSensorArray();
            for (Sensor s : sensors) {
                ss.add(convertToSensor(s));
            }
            return ss;
        } catch (XmlException e) {
            handleException("Invalid XML returned", e);
        } catch (IOException e) {
            handleException("Error reading the XML from the input stream", e);
        }
        return null;
    }

    public static List<SCSensor> convertToSensors(String string) {
        try {
            AllSensorsDocument document = AllSensorsDocument.Factory.parse(string);
            List<SCSensor> ss = new ArrayList<SCSensor>();

            Sensor sensors[] = document.getAllSensors().getSensorArray();
            for (Sensor s : sensors) {
                ss.add(convertToSensor(s));
            }
            return ss;
        } catch (XmlException e) {
            handleException("Invalid XML returned", e);
        }
        return null;
    }

    public static SCSensor convertToSensor(InputStream in) {
        try {
            SensorInfoDocument document = SensorInfoDocument.Factory.parse(in);

            return convertToSensor(document.getSensorInfo());
        } catch (XmlException e) {
            handleException("Failed to convert the text to a XML", e);
        } catch (IOException e) {
            handleException("Error reading the XML from the input stream", e);
        }
        return null;
    }

    public static SCSensor convertToSensor(String string) {
        try {
            SensorInfoDocument document = SensorInfoDocument.Factory.parse(string);

            return convertToSensor(document.getSensorInfo());
        } catch (XmlException e) {
            handleException("Failed to convert the text to a XML", e);
        }
        return null;
    }

    public static SCSensor convertToSensor(Sensor xmlSensor) {
        SCSensor sensor = new SCSensor(xmlSensor.getName());

        sensor.setId(xmlSensor.getId());
        sensor.setType(xmlSensor.getType());

        cgl.iotcloud.core.Endpoint epr;
        if (sensor.getType().equals(Constants.SENSOR_TYPE_BLOCK)) {
            epr = convertToEndpoint(xmlSensor.getDataEndpoint(), 0);
            sensor.setDataEndpoint(epr);
        } else if (sensor.getType().equals(Constants.SENSOR_TYPE_STREAMING)) {
            epr = convertToEndpoint(xmlSensor.getDataEndpoint(), 1);
            sensor.setDataEndpoint(epr);
        }

        if (xmlSensor.getControlEndpoint() != null) {
            epr = convertToEndpoint(xmlSensor.getControlEndpoint(), 0);
            sensor.setControlEndpoint(epr);
        }

        if (xmlSensor.getUpdateEndpoint() != null) {
            epr = convertToEndpoint(xmlSensor.getUpdateEndpoint(), 0);
            sensor.setUpdateEndpoint(epr);
        }

        return sensor;
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
