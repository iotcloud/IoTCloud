package cgl.iotcloud.core;

import cgl.iotcloud.core.sensor.SCSensor;
import com.iotcloud.sensorInfo.xsd.*;
import com.iotcloud.sensorInfo.xsd.Endpoint;

public class SCSensorUtils {

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
//        Pro
//        controlEndpoint.setProperties();

        return null;
    }

    public static SCSensor convertToSensor(String string) {
        return null;
    }

}
