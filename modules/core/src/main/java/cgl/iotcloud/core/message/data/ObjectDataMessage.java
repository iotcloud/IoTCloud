package cgl.iotcloud.core.message.data;

import cgl.iotcloud.core.message.DataMessage;

import java.io.Serializable;

public abstract class ObjectDataMessage implements Serializable, DataMessage {
    private static final long serialVersionUID = 1L;
    private long timestamp = 0;

    public ObjectDataMessage() {
        timestamp = System.currentTimeMillis();
    }

    public ObjectDataMessage(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
