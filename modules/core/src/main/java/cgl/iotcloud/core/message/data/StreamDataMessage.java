package cgl.iotcloud.core.message.data;

import cgl.iotcloud.core.message.DataMessage;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represent a stream data message.
 */
public class StreamDataMessage implements DataMessage {
    private String id = null;

    private InputStream inputStream = null;

    private OutputStream outputStream = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
