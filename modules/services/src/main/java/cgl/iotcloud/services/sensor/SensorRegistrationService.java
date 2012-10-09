package cgl.iotcloud.services.sensor;

import cgl.iotcloud.core.Constants;
import cgl.iotcloud.core.IoTCloud;
import cgl.iotcloud.core.sensor.Sensor;
import cgl.iotcloud.services.Endpoint;
import cgl.iotcloud.services.Property;
import cgl.iotcloud.services.SensorInformation;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * The service is used by Sensors to register them selves to the grid.
 */
public class SensorRegistrationService {
    private static Logger log = LoggerFactory.getLogger(SensorRegistrationService.class);

    /**
     * Register the sensor to the sensor grid.
     *
     * @param information the information about the sensor
     * @return sensor information
     * @throws AxisFault if an error occurs
     */
    public SensorInformation registerSensor(SensorRegistrationInformation information) throws AxisFault {
        IoTCloud cloud =  retrieveIoTCloud();
        Sensor sensor;
        if (information.getType() != null) {
            sensor = cloud.registerSensor(information.getName(), information.getType());
        } else {
            sensor = cloud.registerSensor(information.getName());
        }

        SensorInformation info = new SensorInformation();
        info.setType(sensor.getType());
        info.setId(sensor.getId());
        info.setName(sensor.getName());

        if (sensor.getPublicEndpoint() == null) {
            handleException("Failed to create a PublicEndpoint for the Sensor");
        }
        Endpoint publicEpr = createEndpoint(sensor.getPublicEndpoint());
        info.setPublicEndpoint(publicEpr);
        
        if (sensor.getDataEndpoint() == null) {
            handleException("Failed to create a DataEndpoint for the Sensor");
        }
        Endpoint dataEpr = createEndpoint(sensor.getDataEndpoint());
        info.setDataEndpoint(dataEpr);

        if (sensor.getControlEndpoint() == null) {
            handleException("Failed to create a ControlEndpoint for the Sensor");
        }
        Endpoint controlEpr = createEndpoint(sensor.getControlEndpoint());
        info.setControlEndpoint(controlEpr);


        Endpoint updateEpr = createEndpoint(sensor.getUpdateEndpoint());
        if (sensor.getUpdateEndpoint() == null) {
            handleException("Failed to create a UpdateEndpoint for the Sensor");
        }
        info.setUpdateEndpoint(updateEpr);
        return info;
    }

    public void unregisterSensor(String id) throws AxisFault {
        IoTCloud cloud =  retrieveIoTCloud();

        cloud.unRegisterSensor(id);
    }

    private Endpoint createEndpoint(cgl.iotcloud.core.Endpoint epr) throws AxisFault {
        Endpoint endpoint = new Endpoint();

        endpoint.setAddress(epr.getAddress());

        Set<Map.Entry<String, String>> props = epr.getProperties().entrySet();
        Property properties[] = new Property[props.size()];
        int i = 0;
        for (Map.Entry<String, String> e : props) {
            Property p = new Property();
            p.setName(e.getKey());
            p.setValue(e.getValue());
            properties[i++] = p;
        }
        endpoint.setProperties(properties);
        return endpoint;
    }

    private IoTCloud retrieveIoTCloud() throws AxisFault {
        MessageContext msgCtx = MessageContext.getCurrentMessageContext();

        IoTCloud cloud = (IoTCloud) msgCtx.getProperty(
                Constants.SENSOR_CLOUD_AXIS2_PROPERTY);
        if (cloud == null) {
            handleException("Sensor Cloud Configuration Cannot be found");
        }
        return cloud;
    }

    private void handleException(String msg) throws AxisFault {
        log.error(msg);
        throw new AxisFault(msg);
    }

    private void handleException(String msg, Exception e) throws AxisFault {
        log.error(msg);
        throw new AxisFault(msg);
    }
}
