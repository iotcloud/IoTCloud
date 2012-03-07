package cgl.iotcloud.core.stream;

import cgl.iotcloud.core.Control;
import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.message.data.StreamDataMessage;
import cgl.iotcloud.streaming.http.HttpServerException;
import cgl.iotcloud.streaming.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class StreamingSender implements ManagedLifeCycle, Control {
    private Logger log = LoggerFactory.getLogger(StreamingSender.class);

    /** HTTPClient used to send the data */
    private HttpClient client = null;
    /** port to be used */
    private int port = 80;
    /** host name of the server */
    private String host;
    /** The path to be used */
    private String path;

    public StreamingSender(String path, String host, int port) {
        this.path = path;
        this.host = host;
        this.port = port;
    }

    public void start() {

    }

    public void stop() {
    }

    public String getState() {
        return null;
    }

    public void init() {
        client = new HttpClient("http://" + host + ":" + port + "/" + path);
    }

    public void destroy() {
        // nothing to do for now
    }

    public void send(StreamDataMessage message) {
        OutputStream outputStream = message.getOutputStream();

        if (outputStream == null) {
            handleError("Output stream cannot be null");
        }

        try {
            client.send(null);
        } catch (HttpServerException e) {
            handleError("Error sending message to :" + "http://" + host + ":" + port + "/" + path);
        }
    }

    private void handleError(String msg) {
        log.error(msg);
        throw new SCException(msg);
    }
}
