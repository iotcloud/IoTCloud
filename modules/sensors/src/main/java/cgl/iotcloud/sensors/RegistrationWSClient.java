package cgl.iotcloud.sensors;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.gen.clients.SensorRegistrationServiceStub;
import cgl.iotcloud.gen.services.sensor.xsd.SensorRegistrationInformation;
import cgl.iotcloud.gen.services.xsd.SensorInformation;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * A wrapper class for getting sensor information.
 */
public class RegistrationWSClient implements RegistrationClient {
    private static Logger log = LoggerFactory.getLogger(RegistrationWSClient.class);

    private SensorRegistrationServiceStub stub;

    public RegistrationWSClient(String url) {
        try {
            stub = new SensorRegistrationServiceStub(url);
        } catch (AxisFault axisFault) {
            handleException("Failed to create the client stub for service: " +
                    "SensorRegistrationService", axisFault);
        }
    }

    /**
     * Register the sensor to the grid
     *
     * @param name name of the sensor
     * @param type type of the sensor
     * @return creates a sensor adaptor
     */
    public SensorInformation registerSensor(String name, String type) {
        try {
            return stub.registerSensor(createSensorInformation(name, type));
        } catch (RemoteException e) {
            handleException("Failed to invoke the method registerSensor in service: " +
                    "SensorRegistrationService", e);
        }
        return null;
    }

    public void unRegisterSensor(String id) {
        try {
            stub.unregisterSensor(id);
        } catch (RemoteException e) {
            handleException("Failed to invoke the method to un-register in " +
                    "service: SensorRegistrationService", e);
        }
    }

    private SensorRegistrationInformation createSensorInformation(String name, String type) {
        SensorRegistrationInformation info = new SensorRegistrationInformation();
        info.setType(type);
        info.setName(name);

        return info;
    }

    protected static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }
}
