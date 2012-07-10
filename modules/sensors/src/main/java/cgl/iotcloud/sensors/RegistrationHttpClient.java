package cgl.iotcloud.sensors;

import cgl.iotcloud.api.http.HttpAPIConstants;
import cgl.iotcloud.core.Endpoint;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.sensor.SCSensor;
import cgl.iotcloud.core.sensor.SCSensorUtils;
import cgl.iotcloud.core.sensor.Sensor;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP based implementation for retrieving information
 */
public class RegistrationHttpClient implements RegistrationClient {
    private static Logger log = LoggerFactory.getLogger(RegistrationHttpClient.class);

    private DefaultHttpClient httpClient = new DefaultHttpClient();

    private HttpHost target = null;

    public RegistrationHttpClient(String hostName, int port, boolean ssl) {
        target = new HttpHost(hostName, port, ssl ? "https" : "http");
    }

    public RegistrationHttpClient(int port, String hostName) {
        this(hostName, port, false);
    }

    @Override
    public void registerSensor(Sensor sensor) {
        String url = HttpAPIConstants.REST_APT + HttpAPIConstants.SENSOR_API + HttpAPIConstants.REGISTER +
                "?name=" + sensor.getName() + "&type=" + sensor.getType() ;

        InputStream in = getContent(url);

        SCSensor scSensor = SCSensorUtils.convertToSensor(in);   
        populateSensor(sensor, scSensor);
    }

    @Override
    public void unRegisterSensor(Sensor sensor) {
        String url = HttpAPIConstants.REST_APT + HttpAPIConstants.SENSOR_API + HttpAPIConstants.UNREGISTER +
                "?" + HttpAPIConstants.ID + "=" + sensor.getId();

        HttpPost req = new HttpPost(url);

        try {
            httpClient.execute(target, req);
        } catch (IOException e) {
            handleException("Error in http connection", e);
        }

    }

    private InputStream getContent(String url) {
        try {
            HttpPost req = new HttpPost(url);

            HttpResponse rsp = httpClient.execute(target, req);
            HttpEntity entity = rsp.getEntity();

            if (rsp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return entity.getContent();
            } else {
                return null;
            }
        } catch (ClientProtocolException e) {
            handleException("Error in http connection", e);
        } catch (IOException e) {
            handleException("IO error occurred", e);
        }
        return null;
    }

    private void populateSensor(Sensor sensor, SCSensor scSensor) {
        sensor.setId(scSensor.getId());
        sensor.setType(scSensor.getType());

        Endpoint e = scSensor.getControlEndpoint(); 
        sensor.setControlEndpoint(e);

        e = scSensor.getDataEndpoint();
        if (e == null) {
            handleException("Required endpoint data endpoint cannot be found");
            return;
        }
        sensor.setDataEndpoint(e);

        e = scSensor.getUpdateEndpoint();
        if (e == null) {
            handleException("Required endpoint update endpoint cannot be found");
            return;
        }
        sensor.setUpdateEndpoint(e);
    }

    protected static void handleException(String s, Exception e) {
        log.error(s, e);
        throw new SCException(s, e);
    }

    protected static void handleException(String s) {
        log.error(s);
        throw new SCException(s);
    }
}
