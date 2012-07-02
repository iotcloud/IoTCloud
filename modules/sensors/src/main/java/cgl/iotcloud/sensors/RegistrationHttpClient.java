package cgl.iotcloud.sensors;

import cgl.iotcloud.core.sensor.Sensor;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP based implementation for retrieving information
 */
public class RegistrationHttpClient implements RegistrationClient {

    private Logger log = LoggerFactory.getLogger(RegistrationHttpClient.class);

    private DefaultHttpClient httpClient = new DefaultHttpClient();

    private HttpHost target = null;

    @Override
    public void registerSensor(Sensor sensor) {

    }

    @Override
    public void unRegisterSensor(Sensor sensor) {

    }
}
