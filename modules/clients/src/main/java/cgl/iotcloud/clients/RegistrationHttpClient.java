package cgl.iotcloud.clients;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.sensor.FilterCriteria;
import cgl.iotcloud.core.sensor.SCSensor;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Uses the HTTP API to communicate with the IOT
 */
public class RegistrationHttpClient implements RegistrationClient {
    private Logger log = LoggerFactory.getLogger(RegistrationHttpClient.class);

    private DefaultHttpClient httpClient = new DefaultHttpClient();

    private String hostName =  "";

    private int port;

    private boolean ssl = false;

    private HttpHost target = null;

    public RegistrationHttpClient(String hostName, int port, boolean ssl) {
        this.hostName = hostName;
        this.port = port;
        this.ssl = ssl;

        target = new HttpHost(hostName, port, ssl ? "https" : "http");
    }

    public RegistrationHttpClient(int port, String hostName) {
        this.port = port;
        this.hostName = hostName;
    }

    public List<SCSensor> getSensors() {
        return null;
    }

    public SCSensor getSensor(String id) {
        return null;
    }

    public SCSensor getSensor(String type, FilterCriteria criteria) {
        return null;
    }

    public SCSensor registerClient(String sensorId) {
        return null;
    }

    public void shutdown() {
        httpClient.getConnectionManager().shutdown();
    }

    public InputStream getContent(String url) {
        try {
            HttpGet req = new HttpGet("/");

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

    private void handleException(String msg, Exception e) {
        log.error(msg, e);
        throw new SCException(msg, e);
    }
}
