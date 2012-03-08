package cgl.iotcloud.core.stream;

import cgl.iotcloud.core.ManagedLifeCycle;
import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.message.data.StreamDataMessage;
import cgl.iotcloud.streaming.http.client.core.HttpCoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class StreamingSender implements ManagedLifeCycle {
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

    public String getState() {
        return null;
    }

    public void init() {
        client = new HttpCoreClient(host, port, path);
    }

    public void destroy() {
        // nothing to do for now
        try {
            client.destroy();
        } catch (IOException e) {
            handleError("Error while stopping the streaming client", e);
        }
    }

    public void send(StreamDataMessage message) {
        OutputStream outputStream = message.getOutputStream();

        if (outputStream == null) {
            handleError("Output stream cannot be null");
        }

        try {
            client.send(message.getInputStream(), new DefaultSendCallback());
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

    private void handleError(String msg, IOException e) {
        log.error(msg, e);
        throw new SCException(msg, e);
    }
}
