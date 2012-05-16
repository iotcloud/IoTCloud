package cgl.iotcloud.sensors;

import cgl.iotcloud.gen.services.xsd.SensorInformation;

public interface RegistrationClient {
    public SensorInformation registerSensor(String name, String type);

    public void unRegisterSensor(String id);
}
