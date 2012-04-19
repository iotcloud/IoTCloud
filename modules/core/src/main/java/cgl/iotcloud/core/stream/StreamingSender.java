package cgl.iotcloud.core.stream;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.StreamDataMessage;
import cgl.iotcloud.streaming.http.HttpServerException;
import cgl.iotcloud.streaming.http.client.core.HttpCoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * This sender is used for sending a stream message. It uses a Input stream to send the message.
 */
public class StreamingSender implements Sender {
    private Logger log = LoggerFactory.getLogger(StreamingSender.class);

    /** HTTPClient used to send the data */
    private HttpCoreClient client = null;
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
        client = new HttpCoreClient(host, port, path);
        try {
            client.init();
        } catch (HttpServerException e) {
            handleError("Error initializing the client", e);
        }
    }

    public void destroy() {
        // nothing to do for now
        try {
            client.destroy();
        } catch (IOException e) {
            handleError("Error while stopping the streaming client", e);
        }
    }

    public void send(SensorMessage msg) {
        if (!(msg instanceof StreamDataMessage)) {
            throw new IllegalArgumentException("Sensor message should be of type stream message");
        }

        StreamDataMessage message = (StreamDataMessage) msg;
        InputStream inputStream = message.getInputStream();

        if (inputStream == null) {
            handleError("Input stream cannot be null");
        }

        try {
            client.send(inputStream, new DefaultSendCallback());
        } catch (Exception e) {
            handleError("Error sending message to :" + "http://" + host + ":" + port + "/" + path);
        }
    }

    private class DefaultSendCallback implements HttpCoreClient.SendCallBack {
        public void completed() {
        }

        public void failed(Exception e) {
        }

        public void cancelled() {
        }
    }

    private void handleError(String msg) {
        log.error(msg);
        throw new SCException(msg);
    }

    private void handleError(String msg, Exception e) {
        log.error(msg, e);
        throw new SCException(msg, e);
    }
}
